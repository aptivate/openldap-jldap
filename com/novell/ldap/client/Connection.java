/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/Connection.java,v 1.46 2001/03/28 23:23:52 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.client;

import java.io.*;
import java.util.*;
import java.net.Socket;

import com.novell.ldap.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.*;

/**
 * A thread that creates a connection to an LDAP server. After the
 * connection is made, another thread is created that reads data from the
 * connection.
 * The reader thread will multiplex response messages received from the
//?? * server to one of many queues. Each ClientListener which registers with
 * this class will have its own message queue. That message queue may be
 * dedicated to a single LDAP operation, or may be shared among many LDAP
 * operations.
 *
//?? * The applications thread, using an ClientListener, writes data directly
 * to the server using this class. The application thread will then query
//?? * the ClientListener for a response.
 *
 * The reader thread reads data directly from the server as it decodes
 * an LDAPMessage and writes it to a message queue associated with either
 * an LDAPResponseListener, or an LDAPSearchListener. It uses the message
 * ID from the response to determine which listener is expecting the
 * result. It does this by getting a list of message ID's from each
 * listener, and comparing the message ID from the message just received
 * and adding the message to that listeners queue.
 *
 * Note: the listener thread must not be a "selfish" thread, since some
 * operating systems do not time slice.
 *
 */
public final class Connection implements Runnable
{

    private Object bindSemaphore = new Object();
    private int    bindSemaphoreOwner = 0;
    private int    bindSemaphoreCount = 0;
    private BindProperties bindProperties = null;
    // We need a message number for disconnect to grab the semaphore,
    // but may not have one, so we invent a unique one.
    private int fakeId = -1;

    private Thread reader = null; // New thread that reads data from the server.
    private Thread deadReader = null; // Identity of last reader thread
    private IOException deadReaderException = null; // Last exception of reader

    private LBEREncoder encoder = new LBEREncoder();
    private LBERDecoder decoder = new LBERDecoder();

    private Socket socket = null;

    private InputStream in = null;
    private OutputStream out = null;
    // When set to true, app is not notified of connection failures
    private boolean shutdown = false;

	// Tracks server shutdown unsolicited notifications
	private boolean serverShutdownNotification = false;

    // Place to save message information classes
    private MessageVector messages = new MessageVector(5,5);

    // Connection created to follow referral
    private String[] referralList = null;
    private String activeReferral = null;

    // Place to save unsolicited message listeners
    private Vector2 unsolicitedListeners = new Vector2(3,3);

    // The LDAPSocketFactory to be used as the default to create new connections
    static private LDAPSocketFactory socketFactory = null;
    // The LDAPSocketFactory used for this connection
    private LDAPSocketFactory mySocketFactory = socketFactory;
    private String host = null;
    private int port = 0;
    // Number of clones in addition to original LDAPConnection using this connection.
    private int cloneCount = 0;
    // Connection number & name used only for debug
    private String name = "";
    private static Object nameLock = new Object(); // protect connNum
    private static int connNum = 0;

    // These attributes can be retreived using the getProperty
    // method in LDAPConnection.  Future releases might require
    // these to be local variables that can be modified using
    // the setProperty method.
    public static Float sdk = new Float(1.0);
    public static Float protocol = new Float(3.0);
    public static String security = "simple";

    /**
     * Create a new Connection object
     *
     * @param factory specifies the factory to use to produce SSL sockets.
     */
    public Connection( LDAPSocketFactory factory)
    {
        if( factory != null) {
            // save socket factory
            mySocketFactory = factory;
        }
        if( Debug.LDAP_DEBUG) {
            synchronized(nameLock) {
                name = "Connection(" + ++connNum + "): ";
            }
            Debug.trace( Debug.messages, name + "Created");
        }
        return;
    }

    /**
     * Acquire a simple counting semaphore that synchronizes state affecting bind
     * An fake message value is generated.
     *
     * We bind using the message ID because a different thread may unlock
     * the semaphore than the one that set it.  It is cleared when the
     * response to the bind is processed, or when the bind operation times out.
     *
     * Returns when the semaphore is acquired
     *
     * @return the fake message value that identifies the owner of this semaphore
     */
    public int acquireBindSemaphore()
    {
        return acquireBindSemaphore(0);
    }

    /**
     * Acquire a simple counting semaphore that synchronizes state affecting bind
     * The semaphore is held by setting a value in bindSemaphoreOwner.
     *
     * We bind using the message ID because a different thread may unlock
     * the semaphore than the one that set it.  It is cleared when the
     * response to the bind is processed, or when the bind operation times out.
     * Returns when the semaphore is acquired.
     *
     * @param msgId a value that identifies the owner of this semaphore. A
     * value of zero means assign a unique semaphore value.
     *
     * @return the semaphore value used to acquire the lock
     */
    public int acquireBindSemaphore(int msgId)
    {
        int id = msgId;
        synchronized( bindSemaphore) {
            if( id == 0) {
                fakeId = ((fakeId == Integer.MIN_VALUE) ? (fakeId = -1) : --fakeId);
                id = fakeId;
            }
            while( true) {
                if( bindSemaphoreOwner == 0) {
                   // we have acquired the semahpore
                   bindSemaphoreOwner = id;
                   break;
                } else {
                    if( bindSemaphoreOwner == id) {
                        // we already own the semahpore
                        break;
                    }
                    try {
                        // Keep trying for the lock
                        bindSemaphore.wait();
                        continue;
                    } catch( InterruptedException ex) {
                        // Keep trying for the lock
                        continue;
                    }
                }
            }
            bindSemaphoreCount++;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.bindSemaphore, name +
                "Acquired Bind Semaphore(" + id + ") count " + bindSemaphoreCount);
        }
        return id;
    }

    /**
     * Release a simple counting semaphore that synchronizes state affecting bind
     * Frees the semaphore when number of acquires and frees for this thread match
     *
     * @param msgId a value that identifies the owner of this semaphore
     */
    public void freeBindSemaphore(int msgId)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.bindSemaphore, name +
                "Free'd Bind Semaphore(" + msgId + ") count " + (bindSemaphoreCount - 1));
        }
        synchronized( bindSemaphore) {
            if( bindSemaphoreOwner == 0) {
                throw new RuntimeException("Connection.freeBindSemaphore("
                    + msgId + "): semaphore not owned by any thread");
            } else
            if( bindSemaphoreOwner != msgId) {
                throw new RuntimeException("Connection.freeBindSemaphore("
                    + msgId + "): thread does not own the semaphore, owned by "
                    + bindSemaphoreOwner);
            }
            // if all instances of this semaphore for this thread are released,
            // wake up all threads waiting.
            if( --bindSemaphoreCount == 0) {
                bindSemaphoreOwner = 0;
                bindSemaphore.notify();
            }
        }
        return;
    }

    /*
     * Wait until the reader thread ID matches the specified parameter.
     * Null = wait for the reader to terminate
     * Non Null = wait for the reader to start
     * Returns when the ID matches, i.e. reader stopped, or reader starated.
     *
     * @param the thread id to match
     */
    private void waitForReader( Thread thread)
        throws LDAPException
    {
        // wait for previous reader thread to terminate
        while( reader != thread) {
            // Don't initialize connection while previous reader thread still active.
            try {
                if( Debug.LDAP_DEBUG) {
                    if( thread == null) {
                        Debug.trace( Debug.messages, name +
                            "waiting for reader thread to exit");
                    } else {
                        Debug.trace( Debug.messages, name +
                            "waiting for reader thread to start");
                    }
                }
                /*
                 * The reader thread may start and immediately terminate.
                 * To prevent the waitForReader from waiting forever
                 * for the dead to rise, we leave traces of the deceased.
                 * If the thread is already gone, we throw an exception.
                 */
                if( (thread != null) && (thread == deadReader)) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "reader already terminated, throw exception");
                    }
                    IOException lex = deadReaderException;
                    deadReaderException = null;
                    deadReader = null;
                    // Reader thread terminated
                    throw new LDAPException(
				        LDAPExceptionMessageResource.CONNECTION_READER,
                        LDAPException.CONNECT_ERROR, lex);
                }
                synchronized( this) {
                    this.wait(1000);
                }
            } catch ( InterruptedException ex) {
                ;
            }
        }
        deadReaderException = null;
        deadReader = null;
        return;
    }

    /**
    * Constructs a TCP/IP connection to a server specified in host and port.
    * Starts the reader thread.
    *
    * @param host The host to connect to.
    *<br><br>
    * @param host The port on the host to connect to.
    *<br><br>
    * @param LDAPSocketFactory specifies a factory to produce SSL sockets.
    */
    private void connect(String host, int port, int semaphoreId)
      throws LDAPException
    {
        /* Synchronized so all variables are in a consistant state and
         * so that another thread isn't doing a connect, disconnect, or clone
         * at the same time.
         */
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "connect(" + host + "," + port + ")");
        }
        // Wait for active reader to terminate
        waitForReader(null);

		// Clear the server shutdown notification flag.  This should already
		// be false unless of course we are reusing the same Connection object
		// after a server shutdown notification
		serverShutdownNotification = false;

        int semId = acquireBindSemaphore( semaphoreId);

        // Make socket connection to specified host and port
        if( port == 0) {
            port = LDAPConnection.DEFAULT_PORT;
        }

        try {
            if( (in == null) || (out == null) ) {
                if(mySocketFactory != null) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "connect(socketFactory specified)");
                    }
                    socket = mySocketFactory.makeSocket(host, port);
                } else {
                    socket = new Socket(host, port);
                }

                in = new BufferedInputStream(socket.getInputStream());
                out = socket.getOutputStream();
            } else {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "connect(input/out Stream specified)");
                }
            }
        } catch(IOException ioe) {
            // Unable to connect to server host:port
            freeBindSemaphore(semId);
            throw new LDAPException(
              LDAPExceptionMessageResource.CONNECTION_ERROR,
              new Object[] { host, new Integer(port) },
              LDAPException.CONNECT_ERROR);
        }
        // Set host and port
        this.host = host;
        this.port = port;
        // start the reader thread
        Thread r = new Thread(this);
        r.setDaemon(true); // If this is the last thread running, allow exit.
        r.start();
        freeBindSemaphore(semId);
        // Wait for new reader to start
        waitForReader(r);

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + " connect: setup complete");
        }
        shutdown = false; // Allow app to be notified of connection failures
        return;
    }

    /**
     *  Indicates whether clones exist for LDAPConnection
     *
     * @return true if clones exist, false otherwise.
     */
    public boolean isCloned()
    {
        return( cloneCount > 0);
    }

    /**
     *  Indicates that an LDAPConnection clone is being created
     *
     * @return true if other clones exist
     */
    synchronized public void createClone()
    {
        cloneCount++;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "createClone(" + cloneCount + ")");
        }
        return;
    }

    /**
     *  Destroys a clone.  If the object is a clone,
     *  the connection is left untouched and a new
     *  Connection object is returned.  If not a clone,
     *  the current connnection is destroyed and the
     *  existing object is returned.
     *
     * @return a Connection object.
     */
    public Connection destroyClone(boolean how)
        throws LDAPException
    {
        return destroyClone(how, null,0);
    }

    /**
     *  Destroys a clone.  If the object is a clone,
     *  the connection is left untouched and a new
     *  Connection object is returned.  If not a clone,
     *  the current connnection is destroyed and the
     *  existing object is returned.
     *
     *  Only one clone/destroyClone is allowed to run at any one time
     *
     * @return a Connection object.
     */
    synchronized public Connection destroyClone( boolean how, String host, int port)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "destroyClone(" + how + "," + host + "," + port + ")");
        }
        Connection conn = this;

        if( cloneCount > 0) {
            cloneCount--;
            // This is a clone, set a new connection object.
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "destroyClone(" + cloneCount + ") create new connection");
            }
            conn = new Connection( null );
        } else {
            if( in != null) {
                // Not a clone and connected, destroy old connection
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "destroyClone(" + cloneCount + ") destroy old connection");
                }

                // Connection closed
                LocalException notify = new LocalException(
                    (how ? LDAPExceptionMessageResource.CONNECTION_CLOSED :
                           LDAPExceptionMessageResource.CONNECTION_FINALIZED),
                           new Object[] { host, new Integer(port)},
                           LDAPException.CONNECT_ERROR, null, null);
                shutdown("destroy clone", 0, notify);
            } else {
            }
        }
        if( host != null) {
            conn.connect( host, port, 0);
        }
        return conn;
    }

    /**
     * sets the default socket factory
     *
     * @param factory the default factory to set
     */
    public static void setSocketFactory( LDAPSocketFactory factory)
    {
        socketFactory = factory;
        return;
    }

    /**
     * gets the socket factory used for this connection
     *
     * @param factory the default factory to set
     */
    public LDAPSocketFactory getSocketFactory()
    {
        return mySocketFactory;
    }

    /**
     * gets the host used for this connection
     */
    public String getHost()
    {
        return host;
    }

    /**
     * gets the port used for this connection
     */
    public int getPort()
    {
        return port;
    }

    /**
    * Writes an LDAPMessage to the LDAP server over a socket.
    *
    * @param msg the Message containing the message to write.
    */
    /* package */
    void writeMessage(Message info)
        throws LDAPException
    {
        messages.addElement( info);
        // For bind requests, if not connected, attempt to reconnect
        if( info.isBindRequest() && (isConnected() == false) && (host != null)) {
            connect( host, port, info.getMessageID());
        }
        LDAPMessage msg = info.getRequest();
        writeMessage( msg);
        return;
    }


    /**
    * Writes an LDAPMessage to the LDAP server over a socket.
    *
    * @param msg the message to write.
    */
    /* package */
    void writeMessage(LDAPMessage msg)
        throws LDAPException
    {
        int id = msg.getMessageID();
        OutputStream myOut = out;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Writing Message(" + id + ")");
            Debug.trace( Debug.rawInput, name + "RawWrite: " +
                    msg.getASN1Object().toString());
        }
        acquireBindSemaphore(id);
        try {
            if( myOut == null) {
                throw new IOException("Output stream not initialized");
            }
            byte[] ber = msg.getASN1Object().getEncoding(encoder);
            myOut.write(ber, 0, ber.length);
            myOut.flush();
        } catch( IOException ioe) {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "I/O Exception on host" + host + ":" + port +
                    " " + ioe.toString());
            }

			// Could this be due to a server shutdown notification which caused
			// our Connection to quit.  If so we send back a slightly different
			// error message.  We could have checked this a little earlier in the
			// method but that would be an expensive check each time we send out
			// a message.  Since this shutdown request is going to be an infrequent
			// occurence we check for it only when we get an IOException
			if (serverShutdownNotification) {
				throw new LDAPException( LDAPExceptionMessageResource.SERVER_SHUTDOWN_REQ,
					null,
					LDAPException.CONNECT_ERROR,
					ioe);
			}

            // Other I/O Exception on host:port get reported as is
            throw new LDAPException(LDAPExceptionMessageResource.IO_EXCEPTION,
                new Object[] {host, new Integer(port)},
                LDAPException.CONNECT_ERROR, ioe);
        } finally {
            freeBindSemaphore(id);
        }
        return;
    }

    /**
     * Returns the message agent for this msg ID
     */
    public MessageAgent getMessageAgent( int msgId)
        throws NoSuchFieldException
    {
        Message info  = messages.findMessageById( msgId);
        return info.getMessageAgent();
    }

    /**
     * Return whether a bind has been performed on this conneciton.
     */
    public boolean isBound()
    {
        return (bindProperties != null);
    }

    /**
     * Return whether a connection has been made
     */
    public boolean isConnected()
    {
        return (in != null);
    }

    /**
     *  Set the input stream for this connection
     *
     * @param is the InputStream to set.
     */
    public void setInputStream(InputStream is)
    {
        in = is;
    }

    /**
     * Set the output stream for this connection
     *
     * @param os the OutputStream to set.
     */
    public void setOutputStream(OutputStream os)
    {
        out = os;
    }

    /**
     * Gets the input stream for this connection.
     *
     * @return the InputStream for this connection
     */
    public InputStream getInputStream()
    {
        return in;
    }

    /**
     * Gets the output stream for this connection.
     *
     * @return the InputStream for this connection
     */
    public OutputStream getOutputStream()
    {
        return out;
    }

    /**
     * Removes a Message class from the Connection's list
     *
     * @param info the Message class to remove from the list
     */
    public void removeMessage( Message info)
    {
        boolean done = messages.removeElement(info);
        if( Debug.LDAP_DEBUG) {
            if( done) {
                Debug.trace( Debug.messages, name +
                    "Removed Message(" + info.getMessageID() + ")");
            } else {
                Debug.trace( Debug.messages, name +
                    "Removing Message(" + info.getMessageID() + ") - not found");
            }
        }
        return;
    }

    /**
     * Cleans up resources associated with this connection.
     */
    protected void finalize()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "finalize: shutdown connection");
        }
        shutdown("Finalize",0, null);
        return;
    }
    /**
     * Cleans up resources associated with this connection.
     * This method may be called by finalize() for the connection, or it may
     * be called by LDAPConnection.disconnect().
     * Should not have a bindSemaphore lock in place, as deadlock can occur
     * while abandoning connections.
     */
    private void shutdown( String reason, int semaphoreId, LocalException notifyUser)
    {
        Message info = null;
        if( shutdown) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "shutdown: already shut down - " + reason);
            }
            return;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "shutdown: Shutting down connection - " + reason);
        }
        shutdown = true;
        while( true ) {
            // remove messages from connection list and send abandon
            try {
                info = (Message)messages.remove(0);
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "Shutdown removed message(" + info.getMessageID() + ")");
                }
            } catch( ArrayIndexOutOfBoundsException ex) {
                // No more messages
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "Shutdown no messages to remove");
                }
                break;
            }
            info.abandon( null, notifyUser);
        }

        // Now send unbind before closing socket
        int semId = acquireBindSemaphore( semaphoreId);
        if( (bindProperties != null) && (out != null)) {
            try {
                LDAPMessage msg = new LDAPMessage( new RfcUnbindRequest(),null);
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                          "Writing unbind request (" + msg.getMessageID() + ")");
                    Debug.trace( Debug.rawInput, name + "RawWrite: " +
                            msg.getASN1Object().toString());
                }
                byte[] ber = msg.getASN1Object().getEncoding(encoder);
                out.write(ber, 0, ber.length);
                out.flush();
            } catch( IOException ex) {
                ;  // don't worry about error
            }
        }
        bindProperties = null;

        in = null;
        out = null;
        if( socket != null) {
            // Close the socket
            try {
                socket.close();
            } catch(java.io.IOException ie) {
                // ignore problem closing socket
            }
            socket = null;
        }

        freeBindSemaphore( semId);
        return;
    }

    /**
    *
    *  Sets the authentication credentials in the object
    *  and set flag indicating successful bind.
    *
    *
    *<br><br>
    * @param bindProps   The BindProperties object to set.
    */
    public void setBindProperties( BindProperties bindProps)
    {
        bindProperties = bindProps;
        return;
    }

    /**
    *
    *  Sets the authentication credentials in the object
    *  and set flag indicating successful bind.
    *
    *
    *<br><br>
    * @param bindProps   The BindProperties object to set.
    */
    public BindProperties getBindProperties()
    {
        return bindProperties;
    }

    /**
     * This thread decodes and processes RfcLDAPMessage's from the server.
     */
    /*
     * Note: This thread needs a graceful shutdown implementation.
     */
    public void run() {

        String reason = "reader: thread stopping";
        LocalException notify = null;
        Message info = null;
        IOException ioex = null;

        reader = Thread.currentThread();
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "reader: thread starting: " +
                reader.toString());
        }
        try {
            for(;;) {
                // ------------------------------------------------------------
                // Decode an RfcLDAPMessage directly from the socket.
                // ------------------------------------------------------------
                ASN1Identifier asn1ID;
                InputStream myIn;
                /* get current value of in, keep value consistant
                 * though the loop, i.e. even during shutdown
                 */
                myIn = in;
                if( myIn == null) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "reader: thread stopping, connection shut down");
                    }
                    break;
                }
                asn1ID = new ASN1Identifier(myIn);
                int tag = asn1ID.getTag();
                if(asn1ID.getTag() != ASN1Sequence.TAG) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "reader: discarding message with tag " + tag);
                    }
                    continue; // loop looking for an RfcLDAPMessage identifier
                }

                // Turn the message into an RfcMessage class
                ASN1Length asn1Len = new ASN1Length(myIn);

                RfcLDAPMessage msg =
                    new RfcLDAPMessage( decoder, myIn, asn1Len.getLength());
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.rawInput, name + "RawRead: " +
                            msg.toString());
                }

                // ------------------------------------------------------------
                // Process the decoded RfcLDAPMessage.
                // ------------------------------------------------------------
                int msgId = msg.getMessageID();

                // Find the message which requested this response.
                // It is possible to receive a response for a request which
                // has been abandoned. If abandoned, throw it away
                //??               int cnt = ldapListeners.size();
                try {
                    info = messages.findMessageById( msgId);
                    if( Debug.LDAP_DEBUG ) {
                        Debug.trace( Debug.messages, name +
                            "reader: queue message(" + msgId + ")");
                    }
                    info.putReply( msg);   // queue & wake up waiting thread
                } catch ( NoSuchFieldException ex) {

					// We get the NoSuchFieldException when we could not find a matching
					// message id.  First check to see if this is an unsolicited
					// notification (msgID == 0). If it is not we throw it away.
					// If it is we call any unsolicited
					// listeners that might have been registered to listen for these
					// messages.


					// Note the location of this code.  We could have required that
					// message ID 0 be just like other message ID's but since
					// message ID 0 has to be treated specially we have a seperate
					// check for message ID 0.  Also note that this test is after the
					// regular message list has been checked for.  We could have always
					// checked the list of messages after checking if this is an
					// unsolicited notification but that would have inefficient as
					// message ID 0 is a rare event (as of this time).
					if (msgId == 0) {

						if( Debug.LDAP_DEBUG ) {
							Debug.trace( Debug.messages, name + "Received message id 0");
						}

						// Notify any listeners that might have been registered
						notifyAllUnsolicitedListeners(msg);

						// Was this a server shutdown unsolicited notification.  IF so
						// we quit. Actually calling the return will first transfer
						// control to the finally clause which will do the necessary
						// clean up.  Note this check could get expensive once junta
						// starts using unsolcited notifications.  But since not many
						// unsolicited notifications have been defined as of today
						// we are OK.
						if (serverShutdownNotification) {
							notify = new LocalException(
								LDAPExceptionMessageResource.SERVER_SHUTDOWN_REQ,
								null,
								LDAPException.CONNECT_ERROR,
								null, null);

							return;
						}

					}
					else {

						if( Debug.LDAP_DEBUG ) {
							Debug.trace( Debug.messages, name +
								"reader: message(" + msgId +
								") not found, discarding reply");
						}

					}
                }
            }
        } catch( IOException ioe) {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "Connection lost waiting for results from " +
                    host + ":" + port + ", shutdown=" + shutdown +
                    "\n\t" + ioe.toString());
            }

            ioex = ioe;
			if( ! shutdown) {
                // Connection lost waiting for results from host:port
                notify = new LocalException(
                    LDAPExceptionMessageResource.CONNECTION_WAIT,
                            new Object[] { host, new Integer(port)},
                            LDAPException.CONNECT_ERROR,
                            ioe, info);
            }
        } finally {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                "reader: connection shutdown");
            }
            // Notify application of exception, if any
            shutdown(reason, 0, notify);
        }
        deadReaderException = ioex;
        deadReader = reader;
        reader = null;
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name +
            "reader: thread terminated");
        }
        return;
    }
    /**
     * Marks this LDAPConnection as one created to follow a referral
     */
    public void setReferralList( String[] referrals)
    {
        referralList = referrals;
        return;
    }

    /**
     * Returns the referral list if this connection used to follow a referral
     *
     * @return the referral list
     */
    public String[] getReferralList()
    {
        return referralList;
    }

    /**
     * Sets the current referral active on this connection if created to
     * follow referrals.
     */
    public void setActiveReferral( String referral)
    {
        activeReferral = referral;
        return;
    }

    /**
     * Gets the current referral active on this connection if created to
     * follow referrals.
     *
     * @return the active referral url
     */
    public String getActiveReferral()
    {
        return activeReferral;
    }

	/** Add the specific object to the list of listeners that want to be notified when
	 * an unsolicited notification is received.
	 */
	public void addUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
	{
		unsolicitedListeners.add(listener);
	}

	/** Remove the specific object from current list of listeners
	*/
	public void removeUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
	{
		unsolicitedListeners.removeElement(listener);
	}

    /** Inner class defined so that we can spawn off each unsolicited
     *  listener as a seperate thread.  We did not want to call the
     *  unsolicited listener method directly as this would have tied up our
     *  deamon listener thread in the applications unsolicited listener method.
     *  Since we do not know what the application unsolicited listener
     *  might be doing and how long it will take to process the uncoslicited
     *  notification.  We use this class to spawn off the unsolicited
     *  notification as a seperate thread
     */
    private class UnsolicitedListenerThread extends Thread
    {
        private LDAPUnsolicitedNotificationListener listenerObj;
        private LDAPMessage unsolicitedMsg;

        public UnsolicitedListenerThread( LDAPUnsolicitedNotificationListener l,
                                          LDAPMessage m)
        {
            this.listenerObj = l;
            this.unsolicitedMsg = m;
        }

        public void run()
        {
            listenerObj.messageReceived(unsolicitedMsg);
        }

    }

	private void notifyAllUnsolicitedListeners(RfcLDAPMessage message)
	{
		if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name +
            "Calling all Unsolicited Message Listeners");
        }


		// MISSING:  If this is a shutdown notification from the server
		// set a flag in the Connection class so that we can throw an
		// appropriate LDAPException to the application
		LDAPMessage extendedLDAPMessage = new LDAPExtendedResponse(message);
		String notificationOID = ((LDAPExtendedResponse)extendedLDAPMessage).getID();
		if (notificationOID.equals(LDAPConnection.SERVER_SHUTDOWN_OID)) {

			if( Debug.LDAP_DEBUG ) {
				Debug.trace( Debug.messages, name + "Received server shutdown notification!");
			}

			serverShutdownNotification = true;
		}

		int numOfListeners = unsolicitedListeners.size();

		// Cycle through all the listeners
		for(int i = 0; i < numOfListeners; i++ ) {

			// Get next listener
			LDAPUnsolicitedNotificationListener listener = (LDAPUnsolicitedNotificationListener) unsolicitedListeners.get(i);


			// Create a new ExtendedResponse each time as we do not want each listener
			// to have its own copy of the message
			LDAPMessage tempLDAPMessage = new LDAPExtendedResponse(message);

			// Spawn a new thread for each listener to go process the message
			// The reason we create a new thread rather than just call the
			// the messageReceived method directly is beacuse we do not know
			// what kind of processing the notification listener class will
			// do.  We do not want our deamon thread to block waiting for
			// the notification listener method to return.
			UnsolicitedListenerThread u = new UnsolicitedListenerThread(listener, tempLDAPMessage);
            u.start();
		}


        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name + "Done calling all Unsolicited Message Listeners");
        }

	}

    /**
     * Returns the name of this Connection, used for debug only
     *
     * @return the name of this connection
     */
    /*package*/
    String getConnectionName()
    {
        return name;
    }
}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;

import com.novell.ldap.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.*;
import com.novell.ldap.resources.*;

/**
 * The class that creates a connection to the LDAP server. After the
 * connection is made, a thread is created that reads data from the
 * connection.
 * <p>
 * The application's thread sends a request to the MessageAgent class, which
 * creates a Message class.  The Message class calls the writeMessage method
 * of this class to send the request to the server. The application thread
 * will then query the MessageAgent class for a response.
 * <p>
 * The reader thread multiplexes response messages received from the
 * server to the appropriate Message class. Each Message class
 * has its own message queue.
 * <p>
 * Unsolicited messages are process separately, and if the application
 * has registered a handler, a separate thread is created for that
 * application's handler to process the message.
 * <p>
 * Note: the reader thread must not be a "selfish" thread, since some
 * operating systems do not time slice.
 *
 */
public final class Connection implements Runnable
{

    private Object writeSemaphore = new Object();
    private int    writeSemaphoreOwner = 0;
    private int    writeSemaphoreCount = 0;

    // We need a message number for disconnect to grab the semaphore,
    // but may not have one, so we invent a unique one.
    private int fakeId = -1;
    private BindProperties bindProperties = null;

    private Thread reader = null; // New thread that reads data from the server.
    private Thread deadReader = null; // Identity of last reader thread
    private IOException deadReaderException = null; // Last exception of reader

    private LBEREncoder encoder = new LBEREncoder();
    private LBERDecoder decoder = new LBERDecoder();

    /*
     * socket is the current socket being used.
     * nonTLSBackup is the backup socket if startTLS is called.
     * if nonTLSBackup is null then startTLS has not been called,
     * or stopTLS has been called to end TLS protection
     */
    private Socket socket = null;
    private Socket nonTLSBackup = null;

    private InputStream in = null;
    private OutputStream out = null;
    // When set to true, app is not notified of connection failures
    private boolean shutdown = false;

    // Indicates we have received a server shutdown unsolicited notification
    private boolean serverShutdownNotification = false;

    //  LDAP message IDs are all positive numbers so we can use negative
    //  numbers as flags.  This are flags assigned to stopReaderMessageID
    //  to tell the reader what state we are in.
    private final static int CONTINUE_READING = -99;
    private final static int STOP_READING = -98;
    
    //  Stops the reader thread when a Message with the passed-in ID is read.
    //  This parameter is set by stopReaderOnReply and stopTLS
    private int stopReaderMessageID = CONTINUE_READING;


    // Place to save message information classes
    private MessageVector messages = new MessageVector(5,5);

    // Connection created to follow referral
    private ReferralInfo activeReferral = null;

    // Place to save unsolicited message listeners
    private Vector2 unsolicitedListeners = new Vector2(3,3);

    // The LDAPSocketFactory to be used as the default to create new connections
    static private LDAPSocketFactory socketFactory = null;
    // The LDAPSocketFactory used for this connection
    private LDAPSocketFactory mySocketFactory;
    private String host = null;
    private int port = 0;
    // Number of clones in addition to original LDAPConnection using this
    // connection.
    private int cloneCount = 0;
    // Connection number & name used only for debug
    private String name = "";
    private static Object nameLock = new Object(); // protect connNum
    private static int connNum = 0;

    // These attributes can be retreived using the getProperty
    // method in LDAPConnection.  Future releases might require
    // these to be local variables that can be modified using
    // the setProperty method.
    public static String sdk = new String("1.7");
    public static Integer protocol = new Integer(3);
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
        } else {
            mySocketFactory = socketFactory;
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
     * Acquire a simple counting semaphore that synchronizes state affecting
     * bind. This method generates a fake message value (negative number).
     *
     * We bind using the message ID because a different thread may unlock
     * the semaphore than the one that set it.  It is cleared when the
     * response to the bind is processed, or when the bind operation times out.
     *
     * Returns when the semaphore is acquired
     *
     * @return the fake message value that identifies semaphore's owner
     */
    public int acquireWriteSemaphore()
    {
        return acquireWriteSemaphore(0);
    }

    /**
     * Acquire a simple counting semaphore that synchronizes state affecting
     * bind. The semaphore is held by setting a value in writeSemaphoreOwner.
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
    public int acquireWriteSemaphore(int msgId)
    {
        int id = msgId;
        synchronized( writeSemaphore) {
            if( id == 0) {
                fakeId =
                    ((fakeId == Integer.MIN_VALUE) ? (fakeId = -1) : --fakeId);
                id = fakeId;
            }
            while( true) {
                if( writeSemaphoreOwner == 0) {
                   // we have acquired the semahpore
                   writeSemaphoreOwner = id;
                   break;
                } else {
                    if( writeSemaphoreOwner == id) {
                        // we already own the semahpore
                        break;
                    }
                    try {
                        // Keep trying for the lock
                        writeSemaphore.wait();
                        continue;
                    } catch( InterruptedException ex) {
                        // Keep trying for the lock
                        continue;
                    }
                }
            }
            writeSemaphoreCount++;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.bindSemaphore, name +
                "Acquired Bind Semaphore(" + id + ") count " +
                writeSemaphoreCount);
        }
        return id;
    }

    /**
     * Release a simple counting semaphore that synchronizes state affecting
     * bind.  Frees the semaphore when number of acquires and frees for this
     * thread match.
     *
     * @param msgId a value that identifies the owner of this semaphore
     */
    public void freeWriteSemaphore(int msgId)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.bindSemaphore, name +
                "Free'd Bind Semaphore(" + msgId + ") count " +
                (writeSemaphoreCount - 1));
        }
        synchronized( writeSemaphore) {
            if( writeSemaphoreOwner == 0) {
                throw new RuntimeException("Connection.freeWriteSemaphore("
                    + msgId + "): semaphore not owned by any thread");
            } else
            if( writeSemaphoreOwner != msgId) {
                throw new RuntimeException("Connection.freeWriteSemaphore("
                    + msgId + "): thread does not own the semaphore, owned by "
                    + writeSemaphoreOwner);
            }
            // if all instances of this semaphore for this thread are released,
            // wake up all threads waiting.
            if( --writeSemaphoreCount == 0) {
                writeSemaphoreOwner = 0;
                writeSemaphore.notify();
            }
        }
        return;
    }

    /*
     * Wait until the reader thread ID matches the specified parameter.
     * Null = wait for the reader to terminate
     * Non Null = wait for the reader to start
     * Returns when the ID matches, i.e. reader stopped, or reader started.
     *
     * @param the thread id to match
     */
    private void waitForReader( Thread thread)
        throws LDAPException
    {
        // wait for previous reader thread to terminate
        while( reader != thread) {
            // Don't initialize connection while previous reader thread still
            // active.
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
                if( thread == deadReader) {
                    if (thread == null) /* then we wanted a shutdown */
                        return;
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "reader already terminated, throw exception");
                    }
                    IOException lex = deadReaderException;
                    deadReaderException = null;
                    deadReader = null;
                    // Reader thread terminated
                    throw new LDAPException(
                        ExceptionMessages.CONNECTION_READER,
                        LDAPException.CONNECT_ERROR, lex);
                }
                synchronized( this) {
                    this.wait(5);
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

        int semId = acquireWriteSemaphore( semaphoreId);

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
                    socket = mySocketFactory.createSocket(host, port);
                } else {
                    socket = new Socket(host, port);
                }

                in = socket.getInputStream();
                out = socket.getOutputStream();
            } else {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "connect(input/out Stream specified)");
                }
            }
        } catch(IOException ioe) {
            // Unable to connect to server host:port
            freeWriteSemaphore(semId);
            throw new LDAPException(
                  ExceptionMessages.CONNECTION_ERROR,
                  new Object[] { host, new Integer(port) },
                  LDAPException.CONNECT_ERROR, ioe);
        }
        // Set host and port
        this.host = host;
        this.port = port;
        // start the reader thread
        Thread r = new Thread(this);
        r.setDaemon(true); // If this is the last thread running, allow exit.
        r.start();
        freeWriteSemaphore(semId);
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
    synchronized public Connection
        destroyClone( boolean how, String host, int port)
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
                // Not a clone and connected
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "destroyClone(" + cloneCount +
                        ") destroy old connection");
                }
                /*
                 * Either the application has called disconnect or connect
                 * resulting in the current connection being closed. If the
                 * application has any queues waiting on messages, we
                 * need wake these up so the application does not hang.
                 * The boolean flag indicates whether the close came
                 * from an API call or from the objecting being finalized.
                 */
                LocalException notify = new LocalException(
                    (how ? ExceptionMessages.CONNECTION_CLOSED :
                           ExceptionMessages.CONNECTION_FINALIZED),
                           new Object[] { host, new Integer(port)},
                           LDAPException.CONNECT_ERROR, null, null);

                // Destroy old connection
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
        if( info.isBindRequest() && (isConnected() == false) && (host != null)){
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
        acquireWriteSemaphore(id);
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

            /*
             * This could be due to a server shutdown notification which caused
             * our Connection to quit.  If so we send back a slightly different
             * error message.  We could have checked this a little earlier in
             * the method but that would be an expensive check each time we
             * send out a message.  Since this shutdown request is going to be
             * an infrequent occurence we check for it only when we get an
             * IOException
             */
            if (serverShutdownNotification) {
                throw new LDAPException( ExceptionMessages.SERVER_SHUTDOWN_REQ,
                    new Object[] { host, new Integer(port)},
                    LDAPException.CONNECT_ERROR,
                    ioe);
            }

            // Other I/O Exception on host:port get reported as is
            throw new LDAPException(ExceptionMessages.IO_EXCEPTION,
                new Object[] {host, new Integer(port)},
                LDAPException.CONNECT_ERROR, ioe);
        } finally {
            freeWriteSemaphore(id);
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
     * Return whether the application is bound to this connection.
     * Note: an anonymous bind returns false - not bound
     */
    public boolean isBound()
    {
        if( bindProperties != null) {
            String dn = bindProperties.getAuthenticationDN();
            return( (dn != null) && (dn.length() != 0));
        }
        return false;
    }

    /**
     * Return whether a connection has been made
     */
    public boolean isConnected()
    {
        return (in != null);
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
     * Should not have a writeSemaphore lock in place, as deadlock can occur
     * while abandoning connections.
     */
    private void
        shutdown( String reason, int semaphoreId, LocalException notifyUser)
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
        int semId = acquireWriteSemaphore( semaphoreId);
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

        freeWriteSemaphore( semId);
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
     * This tests to see if there are any outstanding messages.  If no messages
     * are in the queue it returns true.  Each message will be tested to
     * verify that it is complete.
     * <I>The writeSemaphore must be set for this method to be reliable!</I>
     *
     * @return true if no outstanding messages
     */
    public boolean areMessagesComplete(){
        Object[] messages = this.messages.getObjectArray();
        int length = messages.length;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.TLS, "startTLS: areMessagesComplete? " +
                    "MessageVector size = " + length);
        }
        if (length == 0){
            return true;
        }

        for(int i=0; i<length; i++){
            if ( Debug.LDAP_DEBUG){
                Debug.trace( Debug.TLS, "startTLS: areMessagesComplete? " +
                        "Message["+i+"].isComplete()=" +
                        ((Message)messages[i]).isComplete());
            }
            if (((Message)messages[i]).isComplete() == false)
                return false;
        }
        return true;
    }

    /**
     * The reader thread will stop when a reply is read with an ID equal
     * to the messageID passed in to this method.  This is used by
     * LDAPConnection.StartTLS.
     */
    public void stopReaderOnReply(int messageID){
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.TLS, "startTLS: stopReaderOnReply of " +
            "message " + messageID);
        }
        this.stopReaderMessageID = messageID;
        return;
    }

    /**
     * Indicates if the connection is using TLS protection.
     *
     * Returns true if using TLS protection.
     */
    public boolean isTLS(){
        return (this.nonTLSBackup != null);
    }

    /**
     * Starts TLS on this connection
     * The writeSemaphore should already have been acquired,
     * the reader thread stopped, checked that no messages are outstanding
     * on this connection.
     */
    public void startTLS()
        throws LDAPException
    {
        if (this.mySocketFactory == null){
            throw new LDAPException( ExceptionMessages.NO_TLS_FACTORY,
                                     LDAPException.TLS_NOT_SUPPORTED  );
        }
        else
        if ( !(this.mySocketFactory instanceof LDAPTLSSocketFactory )) {
            throw new LDAPException( ExceptionMessages.WRONG_FACTORY,
                                     LDAPException.TLS_NOT_SUPPORTED  );
        }

        try {
            /*  We need to wait for the reader to terminate or the reader
             *  will eat up the TLS handshake packets and throw them away.
             *  LDAPConnection called stopReaderOnReply before calling this
             *  method, which should have stopped the reader when the 
             *  response to the startTLS extended request was received..
             */
            waitForReader(null);
            this.nonTLSBackup = this.socket;
            this.socket = ((LDAPTLSSocketFactory)
                        this.mySocketFactory).createTLSSocket( this.socket );
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.TLS, "connection.startTLS, nonTLSBackup:"+
                        nonTLSBackup +", TLSSocket:"+socket+", input:"+ in +","
                        +"output:"+out  );
            }

            // Start Reader Thread
            Thread r = new Thread(this);
            r.setDaemon(true); // If the last thread running, allow exit.
            r.start();
            waitForReader(r);
        }
        catch( java.net.UnknownHostException uhe) {
            this.nonTLSBackup = null;
            throw new LDAPException("The host is unknown",
                LDAPException.CONNECT_ERROR, uhe);
        }
        catch( IOException ioe ) {
            this.nonTLSBackup = null;
            throw new LDAPException("Could not negotiate a secure connection",
                LDAPException.CONNECT_ERROR, ioe);
        }
        return;
    }

    /** stopTLS needs to do the following
     *  1) block writing (acquireWriteSemaphore).
     *  2) check that no messages are outstanding.
     *  3) close the current socket
     *      - This stops the reader thread
     *      - set STOP_READING flag on stopReaderMessageID so that
     *        the reader knows that the IOException is planned.
     *  4) replace the current socket with nonTLSBackup,
     *  5) and set nonTLSBackup to null;
     *  6) reset input and outputstreams
     *  7) restart reader.
     *
     *  Note: Sun's JSSE doesn't allow the nonTLSBackup socket to be
     * used any more, even though autoclose was false: you get an IOException.
     * IBM's JSSE hangs when you close the JSSE socket.
     */
    public void stopTLS() throws LDAPException
    {
        int semaphoreID = this.acquireWriteSemaphore();
        try{
            if (!this.areMessagesComplete()) {
                throw new LDAPException(
                        ExceptionMessages.OUTSTANDING_OPERATIONS,
                        LDAPException.OPERATIONS_ERROR );
            }

            this.stopReaderMessageID = this.STOP_READING;
            this.socket.close();
            waitForReader(null);
            this.socket = this.nonTLSBackup;
            this.in = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.TLS, "connection.stopTLS, nonTLSBackup:"+
                        nonTLSBackup +", TLSSocket:"+socket+", input:"+ in +","
                        +"output:" +out  );
            }
            // Allow the new reader to start
            this.stopReaderMessageID = this.CONTINUE_READING;
            // start the reader thread
            Thread r = new Thread(this);
            r.setDaemon(true); // If the last thread running, allow exit.
            r.start();
            waitForReader(r);
        }catch (IOException ioe){
            throw new LDAPException(ExceptionMessages.STOPTLS_ERROR,
                        LDAPException.CONNECT_ERROR, ioe);
        }finally {
            this.freeWriteSemaphore(semaphoreID);
        }
        return;    
    }

    /**
     * This thread decodes and processes RfcLDAPMessage's from the server.
     *
     * Note: This thread needs a graceful shutdown implementation.
     */
    public void run()
    {

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
                try {
                    info = messages.findMessageById( msgId);
                    if( Debug.LDAP_DEBUG ) {
                        Debug.trace( Debug.messages, name +
                            "reader: queue message(" + msgId + ")");
                    }
                    info.putReply( msg);   // queue & wake up waiting thread
                } catch ( NoSuchFieldException ex) {

                    /*
                     * We get the NoSuchFieldException when we could not find
                     * a matching message id.  First check to see if this is
                     * an unsolicited notification (msgID == 0). If it is not
                     * we throw it away. If it is we call any unsolicited
                     * listeners that might have been registered to listen for these
                     * messages.
                     */


                    /* Note the location of this code.  We could have required
                     * that message ID 0 be just like other message ID's but
                     * since message ID 0 has to be treated specially we have
                     * a separate check for message ID 0.  Also note that
                     * this test is after the regular message list has been
                     * checked for.  We could have always checked the list
                     * of messages after checking if this is an unsolicited
                     * notification but that would have inefficient as
                     * message ID 0 is a rare event (as of this time).
                     */
                    if (msgId == 0) {

                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                    "Received message id 0");
                        }

                        // Notify any listeners that might have been registered
                        notifyAllUnsolicitedListeners(msg);

                        /*
                         * Was this a server shutdown unsolicited notification.
                         * IF so we quit. Actually calling the return will
                         * first transfer control to the finally clause which
                         * will do the necessary clean up.
                         */ 
                        if (serverShutdownNotification) {
                            notify = new LocalException(
                                ExceptionMessages.SERVER_SHUTDOWN_REQ,
                                new Object[] {host, new Integer(port)},
                                LDAPException.CONNECT_ERROR,
                                null, null);

                            return;
                        }
                    } else {

                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "reader: message(" + msgId +
                                ") not found, discarding reply");
                        }

                    }

                }
                if ((this.stopReaderMessageID == msgId) ||
                    (this.stopReaderMessageID == this.STOP_READING)) {
                    // Stop the reader Thread.
                    return;
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
            if((this.stopReaderMessageID != this.STOP_READING ) && ! shutdown ){
                // Connection lost waiting for results from host:port
                notify = new LocalException(
                    ExceptionMessages.CONNECTION_WAIT,
                            new Object[] { host, new Integer(port)},
                            LDAPException.CONNECT_ERROR,
                            ioe, info);
            }
        } finally {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                "reader: connection shutdown");
            }
            /*
             * There can be four states that the reader can be in at this point:
             *  1) We are starting TLS and will be restarting the reader
             *     after we have negotiated TLS.
             *      - Indicated by whether stopReaderMessageID does not
             *        equal CONTINUE_READING.
             *      - Don't call Shutdown.
             *  2) We are stoping TLS and will be restarting after TLS is
             *     stopped.
             *      - Indicated by an IOException AND stopReaderMessageID equals
             *        STOP_READING - in which case notify will be null.
             *      - Don't call Shutdown
             *  3) We receive a Server Shutdown notification.
             *      - Indicated by messageID equal to 0.
             *      - call Shutdown.
             *  4) Another error occured
             *      - Indicated by an IOException AND notify is not NULL
             *      - call Shutdown.
             */
            if (shutdown || (notify != null)) {  //#3 & 4
                shutdown( reason, 0, notify );
            } else {
                this.stopReaderMessageID = this.CONTINUE_READING;
                if( Debug.LDAP_DEBUG ) {       //#1 & #2
                    Debug.trace( Debug.TLS,
                        "reader: Stopping thread, retaining the connection");
                }
            }            
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
     * Sets the current referral active on this connection if created to
     * follow referrals.
     */
    public void setActiveReferral( ReferralInfo referral)
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
    public ReferralInfo getActiveReferral()
    {
        return activeReferral;
    }

    /** Add the specific object to the list of listeners that want to be
     * notified when an unsolicited notification is received.
     */
    public void
      addUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
    {
        unsolicitedListeners.add(listener);
        return;    
    }

    /** Remove the specific object from current list of listeners
    */
    public void
      removeUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
    {
        unsolicitedListeners.removeElement(listener);
        return;    
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
        private LDAPExtendedResponse unsolicitedMsg;

        public UnsolicitedListenerThread( LDAPUnsolicitedNotificationListener l,
                                          LDAPExtendedResponse m)
        {
            this.listenerObj = l;
            this.unsolicitedMsg = m;
            return;    
        }

        public void run()
        {
            listenerObj.messageReceived(unsolicitedMsg);
            return;    
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
                Debug.trace( Debug.messages, name +
                    "Received server shutdown notification!");
            }

            serverShutdownNotification = true;
        }

        int numOfListeners = unsolicitedListeners.size();

        // Cycle through all the listeners
        for(int i = 0; i < numOfListeners; i++ ) {

            // Get next listener
            LDAPUnsolicitedNotificationListener listener =
              (LDAPUnsolicitedNotificationListener) unsolicitedListeners.get(i);


            // Create a new ExtendedResponse each time as we do not want each listener
            // to have its own copy of the message
            LDAPExtendedResponse tempLDAPMessage =
                    new LDAPExtendedResponse(message);

            // Spawn a new thread for each listener to go process the message
            // The reason we create a new thread rather than just call the
            // the messageReceived method directly is beacuse we do not know
            // what kind of processing the notification listener class will
            // do.  We do not want our deamon thread to block waiting for
            // the notification listener method to return.
            UnsolicitedListenerThread u =
                    new UnsolicitedListenerThread(listener, tempLDAPMessage);
            u.start();
        }


        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name +
                "Done calling all Unsolicited Message Listeners");
        }
        return;
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

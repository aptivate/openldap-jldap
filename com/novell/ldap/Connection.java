/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


import com.novell.ldap.asn1.*;
import com.novell.ldap.client.*;
import com.novell.ldap.rfc2251.*;
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
/*package*/
final class Connection
{

    private Object writeSemaphore = new Object();
    private int    writeSemaphoreOwner = 0;
    private int    writeSemaphoreCount = 0;

    // We need a message number for disconnect to grab the semaphore,
    // but may not have one, so we invent a unique one.
    private int ephemeralId = -1;
    private BindProperties bindProperties = null;
    private int bindSemaphoreId = 0; // 0 is never used by to lock a semaphore

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
    // When set to true the client connection is up and running
    private boolean clientActive = true;

    // Indicates we have received a server shutdown unsolicited notification
    private boolean unsolSvrShutDnNotification = false;

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
    private java.util.Vector unsolicitedListeners = new java.util.Vector(3,3);

    // The LDAPSocketFactory to be used as the default to create new connections
    static private LDAPSocketFactory socketFactory = null;
    // The LDAPSocketFactory used for this connection
    private LDAPSocketFactory mySocketFactory = null;

    private int myTimeOut = 0;
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
    /* package */
    static String sdk = new String("4.6");
    /* package */
    static Integer protocol = new Integer(3);
    /* package */
    static String security = "simple";

    /**
     * Create a new Connection object
     *
     * @param factory specifies the factory to use to produce SSL sockets.
     */
    /* package */
    Connection( LDAPSocketFactory factory)
    {
        if( factory != null) {
            /* verify the 'setFactory' permision is set */
            SecurityManager security = System.getSecurityManager();
            if (security != null){
                security.checkSetFactory();
            }

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
     * Copy this Connection object.
     *
     * <p>This is not a true clone, but creates a new object encapsulating
     * part of the connection information from the original object.
     * The new object will have the same default socket factory,
     * designated socket factory, host, port, and protocol version
     * as the original object.
     * The new object is NOT be connected to the host.</p>
     *
     * @return a shallow copy of this object
     */
    /* package */
    Object copy()
    {
        Connection c = new Connection(this.mySocketFactory);
        c.host = this.host;
        c.port = this.port;
        c.protocol = this.protocol;
        return c;
    }

    /**
     * Create a new Connection object
     *
     * @param timeout specifies the socket timeout to be used when the server is stalled.
     */

    Connection( int timeout)
    {
        myTimeOut = timeout;
		mySocketFactory = socketFactory;

        if( Debug.LDAP_DEBUG) {
            synchronized(nameLock) {
                name = "Connection(" + ++connNum + "): ";
            }
            Debug.trace( Debug.messages, name + "Created");
        }
        return;
    }

    /**
     * Copy this Connection object.
     *
     * <p>This is not a true clone, but creates a new object encapsulating
     * part of the connection information from the original object.
     * The new object will have the same default socket factory,
     * designated socket factory, host, port, and protocol version
     * as the original object.
     * The new object is NOT be connected to the host.</p>
     *
     * @return a shallow copy of this object
     */
    /* package */
    Object copy_timeout()
    {
        Connection c = new Connection(this.myTimeOut);
        c.host = this.host;
        c.port = this.port;
        c.protocol = this.protocol;
        return c;
    }


    /**
     * Acquire a simple counting semaphore that synchronizes state affecting
     * bind. This method generates an ephemeral message id (negative number).
     *
     * We bind using the message ID because a different thread may unlock
     * the semaphore than the one that set it.  It is cleared when the
     * response to the bind is processed, or when the bind operation times out.
     *
     * Returns when the semaphore is acquired
     *
     * @return the ephemeral message id that identifies semaphore's owner
     */
    /* package */
    final int acquireWriteSemaphore()
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
    /* package */
    final int acquireWriteSemaphore(int msgId)
    {
        int id = msgId;
        synchronized( writeSemaphore) {
            if( id == 0) {
                ephemeralId = ((ephemeralId == Integer.MIN_VALUE)
                                ? (ephemeralId = -1) : --ephemeralId);
                id = ephemeralId;
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
                "Acquired Socket Write Semaphore(" + id + ") count " +
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
    /* package */
    final void freeWriteSemaphore(int msgId)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.bindSemaphore, name +
                "Free'd Socket Write Semaphore(" + msgId + ") count " +
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
                        LDAPException.CONNECT_ERROR, null, lex);
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
    *
    * @param host The host to connect to.
    *<br><br>
    * @param port The port on the host to connect to.
    */
    /* package */
    void connect(String host, int port)
      throws LDAPException
    {
        connect( host, port, 0);
        return;
    }

    /**
    * Constructs a TCP/IP connection to a server specified in host and port.
    * Starts the reader thread.
    *
    * @param host The host to connect to.
    *<br><br>
    * @param port The port on the host to connect to.
    *<br><br>
    * @param semaphoreId The write semaphore ID to use for the connect
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
        unsolSvrShutDnNotification = false;

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
                	if(myTimeOut > 0)
                	{
                		socket.setSoTimeout(myTimeOut);
                	}
                }


                in = socket.getInputStream();
                out = socket.getOutputStream();
            } else {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "connect(input/out Stream specified)");
                }
            }
        }catch(IOException ioe) {
            // Unable to connect to server host:port
            freeWriteSemaphore(semId);
            throw new LDAPException(
                  ExceptionMessages.CONNECTION_ERROR,
                  new Object[] { host, new Integer(port) },
                  LDAPException.CONNECT_ERROR, null, ioe);
        }
        // Set host and port
        this.host = host;
        this.port = port;
        // start the reader thread
        this.startReader();

        freeWriteSemaphore(semId);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + " connect: setup complete");
        }
        clientActive = true; // Client is up
        return;
    }

    /**
     *  Indicates whether clones exist for LDAPConnection
     *
     * @return true if clones exist, false otherwise.
     */
    /* package */
    final boolean isCloned()
    {
        return( cloneCount > 0);
    }

    /**
     *  Increments the count of cloned connections
     */
    /* package */
    synchronized final void incrCloneCount()
    {
        cloneCount++;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "incrCloneCount(" + cloneCount + ")");
        }
        return;
    }

    /**
     * Destroys a clone of <code>LDAPConnection</code>.
     *
     * <p>This method first determines if only one <code>LDAPConnection</code>
     * object is associated with this connection, i.e. if no clone exists.</p>
     *
     * <p>If no clone exists, the socket is closed, and the current
     * <code>Connection</code> object is returned.</p>
     *
     * <p>If multiple <code>LDAPConnection</code> objects are associated
     * with this connection, i.e. clones exist, a {@link #copy} of the
     * this object is made, but is not connected to any host. This
     * disassociates that clone from the original connection.  The new
     * <code>Connection</code> object is returned.
     *
     * <p>Only one destroyClone instance is allowed to run at any one time.</p>
     *
     * <p>If the connection is closed, any threads waiting for operations
     * on that connection will wake with an LDAPException indicating
     * the connection is closed.</p>
     *
     * @param apiCall <code>true</code> indicates the application is closing the
     *            connection or or creating a new one by calling either the
     *            <code>connect</code> or <code>disconnect</code> methods
     *            of <code>LDAPConnection</code>.  <code>false</code>
     *            indicates that <code>LDAPConnection</code> is being finalized.
     *
     * @return a Connection object or null if finalizing.
     */
    /* package */
    synchronized final Connection destroyClone( boolean apiCall)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "destroyClone(" + apiCall + ")");
        }
        Connection conn = this;

        if( cloneCount > 0) {
            cloneCount--;
            // This is a clone, set a new connection object.
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "destroyClone(" + cloneCount + ") create new connection");
            }
            if( apiCall) {
                conn = (Connection)this.copy();
            } else {
                conn = null;
            }
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
                 * from an API call or from the object being finalized.
                 */
                InterThreadException notify = new InterThreadException(
                    (apiCall ? ExceptionMessages.CONNECTION_CLOSED :
                               ExceptionMessages.CONNECTION_FINALIZED),
                               null, LDAPException.CONNECT_ERROR, null, null);
                // Destroy old connection
                shutdown("destroy clone", 0, notify);
            }
        }
        return conn;
    }

    /**
     * sets the default socket factory
     *
     * @param factory the default factory to set
     */
    /* package */
    final static void setSocketFactory( LDAPSocketFactory factory)
    {
        /* verify the 'setFactory' permision is set */
        SecurityManager security = System.getSecurityManager();
        if (security != null){
            security.checkSetFactory();
        }
        socketFactory = factory;
        return;
    }

    /**
     * gets the socket factory used for this connection
     *
     * @return the default factory for this connection
     */
    /* package */
    final LDAPSocketFactory getSocketFactory()
    {
        return mySocketFactory;
    }

    /**
     * gets the host used for this connection
     */
    /* package */
    final String getHost()
    {
        return host;
    }

    /**
     * gets the port used for this connection
     */
    /* package */
    final int getPort()
    {
        return port;
    }

    /**
     * gets the writeSemaphore id used for active bind operation
     */
    /* package */
    int getBindSemId()
    {
        return bindSemaphoreId;
    }

    /**
     * sets the writeSemaphore id used for active bind operation
     */
    /* package */
    void setBindSemId(int id)
    {
        bindSemaphoreId = id;
        return;
    }

    /**
     * clears the writeSemaphore id used for active bind operation
     */
    /* package */
    void clearBindSemId()
    {
        bindSemaphoreId = 0;
        return;
    }

    /**
     * Gets SocketTimeOut value set.
     *
     * If not set, returns 0.
     *
     */

    final int getSocketTimeOut()
    {
    	return myTimeOut;
    }

    /**
     * Sets the SocketTimeOut value.
     *
     */

    final void setSocketTimeOut(int timeout)
    {
    	try
		{
    		if(socket!=null)
    			socket.setSoTimeout(timeout);
    		myTimeOut = timeout;
		} catch(SocketException e) {}
    	return;
    }


    /**
     * checks if the writeSemaphore id used for active bind operation is clear
     */
    /* package */
    boolean isBindSemIdClear()
    {
        if( bindSemaphoreId == 0) {
            return true;
        }
        return false;
    }

    /**
     * Writes an LDAPMessage to the LDAP server over a socket.
     *
     * @param info the Message containing the message to write.
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
        if(isConnected())
        {
        	LDAPMessage msg = info.getRequest();
        	writeMessage( msg);
        	return;
        }
        else
        	throw new LDAPException(ExceptionMessages.CONNECTION_CLOSED,
                    new Object[] { host, new Integer(port) },
                    LDAPException.CONNECT_ERROR, null,new IOException());
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
        int id;
        // Get the correct semaphore id for bind operations
        if( bindSemaphoreId == 0) {
            // Semaphore id for normal operations
            id = msg.getMessageID();
        } else {
            // Semaphore id for sasl bind operations
            id = bindSemaphoreId;
        }
        OutputStream myOut = out;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Writing Message(" +
                    msg.getMessageID() + ")");
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
             * IOException could be due to a server shutdown notification which
             * caused our Connection to quit.  If so we send back a slightly
             * different error message.  We could have checked this a little
             * earlier in the method but that would be an expensive check each
             * time we send out a message.  Since this shutdown request is
             * going to be an infrequent occurence we check for it only when
             * we get an IOException.  shutdown() will do the cleanup.
             */
            if( clientActive) { // We beliefe the connection was alive
                if (unsolSvrShutDnNotification) { // got server shutdown
                    throw new LDAPException( ExceptionMessages.SERVER_SHUTDOWN_REQ,
                        new Object[] { host, new Integer(port)},
                        LDAPException.CONNECT_ERROR, null,
                        ioe);
                }

                // Other I/O Exceptions on host:port are reported as is
                throw new LDAPException(ExceptionMessages.IO_EXCEPTION,
                    new Object[] {host, new Integer(port)},
                LDAPException.CONNECT_ERROR, null, ioe);
            }
        } finally {
            freeWriteSemaphore(id);
        }
        return;
    }

    /**
     * Returns the message agent for this msg ID
     */
    /* package */
    final MessageAgent getMessageAgent( int msgId)
        throws NoSuchFieldException
    {
        Message info  = messages.findMessageById( msgId);
        return info.getMessageAgent();
    }

    /**
     * Return whether the application is bound to this connection.
     * Note: an anonymous bind returns false - not bound
     */
    /* package */
    final boolean isBound()
    {
        if( bindProperties != null) {
            // Bound if not anonymous
            return( ! bindProperties.isAnonymous());
        }
        return false;
    }

    /**
     * Return whether a connection has been made
     */
    /* package */
    final boolean isConnected()
    {
        return (in != null);
    }

    /**
     * Checks whether a connection is still alive or not by sending data to
     * the server on this connection's socket.If the connection is not alive
     * the send will generate an IOException and the function will return
     * false.
     * @return  true    If connection is alive
     *          false   If connection is not alive.
     */
    final boolean isConnectionAlive()
    {
       boolean isConn=false;
       int id;
       LDAPExtendedOperation op=null;

       if  ( in!= null )      {
              isConn=true;

           op= new LDAPExtendedOperation("0.0.0.0",null);
           LDAPMessage msg =new LDAPExtendedRequest(op, null);
           id = msg.getMessageID();
           acquireWriteSemaphore(id);
           OutputStream myOut = out;
           try          {
               if( myOut == null) {
                   throw new IOException("Output stream not initialized");
               }
               byte[] ber = msg.getASN1Object().getEncoding(encoder);
               myOut.write(ber, 0, ber.length);
               myOut.flush();
               } catch( IOException ioe) {
                   isConn=false;
               }
               finally {
                   freeWriteSemaphore(id);
               }
       }

       return isConn;
   }

    /**
     * Removes a Message class from the Connection's list
     *
     * @param info the Message class to remove from the list
     */
    /* package */
    final void removeMessage( Message info)
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
    private void shutdown( String reason, int semaphoreId, InterThreadException notifyUser)
    {
        Message info = null;
        if( ! clientActive) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "shutdown: already shutdown - " + reason);
            }
            return;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "shutdown: Shutting down connection - " + reason);
        }
        clientActive = false;
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
            info.abandon( null, notifyUser); // also notifies the application
        }

        int semId = acquireWriteSemaphore( semaphoreId);
        // Now send unbind if socket not closed
        if( (bindProperties != null) &&
            (out != null) &&
            (! bindProperties.isAnonymous()))
        {
            try {
                LDAPMessage msg = new LDAPUnbindRequest( null);
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                         "Writing unbind request (" + msg.getMessageID() + ")");
                    Debug.trace( Debug.rawInput, name + "RawWrite: " +
                            msg.getASN1Object().toString());
                }
                byte[] ber = msg.getASN1Object().getEncoding(encoder);
                out.write(ber, 0, ber.length);
                out.flush();
            } catch( Exception ex) {
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

        // wait until reader threads stops completely
        try {
        	if (reader!= Thread.currentThread())
        	     reader.join();

//      	reader.join();
            reader=null;
        }
        catch(InterruptedException iex) {
        	;
        }
        catch(NullPointerException npe) {
        	;
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
    /* package */
    final void setBindProperties( BindProperties bindProps)
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
    * @return  The BindProperties object for this connection.
    */
    /* package */
    final BindProperties getBindProperties()
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
    /* package */
    final boolean areMessagesComplete(){
        Object[] messages = this.messages.getObjectArray();
        int length = messages.length;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.TLS, "startTLS: areMessagesComplete? " +
                    "MessageVector size = " + length +
                    ", bindSemaphoreId=" + bindSemaphoreId);
        }
        // Check if SASL bind in progress
        if( bindSemaphoreId != 0) {
            return false;
        }

        // Check if any messages queued
        if(length == 0) {
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
    /* package */
    final void stopReaderOnReply(int messageID){
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.TLS, "startTLS: stopReaderOnReply of " +
            "message " + messageID);
        }
        this.stopReaderMessageID = messageID;
        return;
    }

    /** startReader
     *  startReader should be called when socket and io streams have been
     *  set or changed.  In particular after client.Connection.startTLS()
     *  It assumes the reader thread is not running.
     */
    /* package */
    final void startReader() throws LDAPException {
        // Start Reader Thread
        Thread r = new Thread(new ReaderThread());
        r.setDaemon(true); // If the last thread running, allow exit.
        r.start();
        waitForReader(r);
        return;
    }

    /**
     * Indicates if the connection is using TLS protection.
     *
     * Returns true if using TLS protection.
     */
    /* package */
    final boolean isTLS(){
        return (this.nonTLSBackup != null);
    }

    /**
     * StartsTLS, in this package, assumes the caller has:
     * 1) Acquired the writeSemaphore
     * 2) Stopped the reader thread
     * 3) checked that no messages are outstanding on this connection.
     *
     * After calling this method upper layers should start the reader
     * by calling startReader()
     *
     * In the client.Connection, StartTLS assumes LDAP.LDAPConnection will
     * stop and start the reader thread.  Connection.StopTLS will stop
     * and start the reader thread.
     */
    /* package */
    final void startTLS()
        throws LDAPException
    {
        if (this.mySocketFactory == null){
            throw new LDAPException( ExceptionMessages.NO_TLS_FACTORY,
                                     LDAPException.TLS_NOT_SUPPORTED, null);
        }
        else
        if ( !(this.mySocketFactory instanceof LDAPTLSSocketFactory )) {
            throw new LDAPException( ExceptionMessages.WRONG_FACTORY,
                                     LDAPException.TLS_NOT_SUPPORTED, null);
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
                        this.mySocketFactory).createSocket( this.socket );
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.TLS, "connection.startTLS, nonTLSBackup:"+
                        nonTLSBackup +", TLSSocket:"+socket+", input:"+ in +","
                        +"output:"+out  );
            }
        }
        catch( java.net.UnknownHostException uhe) {
            this.nonTLSBackup = null;
            throw new LDAPException("The host is unknown",
                LDAPException.CONNECT_ERROR, null, uhe);
        }
        catch( IOException ioe ) {
            this.nonTLSBackup = null;
            throw new LDAPException("Could not negotiate a secure connection",
                LDAPException.CONNECT_ERROR, null, ioe);
        }
        return;
    }

    /*
     * Stops TLS.
     *
     * StopTLS, in this package, assumes the caller has:
     *  1) blocked writing (acquireWriteSemaphore).
     *  2) checked that no messages are outstanding.
     *
     *  StopTLS Needs to do the following:
     *  1) close the current socket
     *      - This stops the reader thread
     *      - set STOP_READING flag on stopReaderMessageID so that
     *        the reader knows that the IOException is planned.
     *  2) replace the current socket with nonTLSBackup,
     *  3) and set nonTLSBackup to null;
     *  4) reset input and outputstreams
     *  5) start the reader thread by calling startReader
     *
    */
    /* package */
    final void stopTLS() throws LDAPException
    {
        try{
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
        }catch (IOException ioe){
            throw new LDAPException(ExceptionMessages.STOPTLS_ERROR,
                        LDAPException.CONNECT_ERROR, null, ioe);
        }finally {
            this.nonTLSBackup = null;
            startReader();
        }
        return;
    }

    public class ReaderThread implements Runnable
    {
        private ReaderThread()
        {
            return;
        }

        /**
         * This thread decodes and processes RfcLDAPMessage's from the server.
         *
         * Note: This thread needs a graceful shutdown implementation.
         */
        public final void run()
        {

            String reason = "reader: thread stopping";
            InterThreadException notify = null;
            Message info = null;
            IOException ioex = null;

            reader = Thread.currentThread();
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name + "reader: thread starting: " +
                    reader.toString());
            }
            try {
                for(;;) {
                    // -------------------------------------------------------
                    // Decode an RfcLDAPMessage directly from the socket.
                    // -------------------------------------------------------
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
                                "reader: queue response to message(" + msgId + ")");
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
                            if (unsolSvrShutDnNotification) {
                                notify = new InterThreadException(
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
                    if ((stopReaderMessageID == msgId) ||
                        (stopReaderMessageID == STOP_READING)) {
                        // Stop the reader Thread.
                        return;
                    }
                }
            } catch( IOException ioe) {
                if( Debug.LDAP_DEBUG ) {
                    Debug.trace( Debug.messages, name +
                        "Connection lost waiting for results from " +
                        host + ":" + port + ", clientActive=" +
                        clientActive + "\n\t" + ioe.toString());
                }

                ioex = ioe;
                if((stopReaderMessageID != STOP_READING ) && clientActive ){
                    // Connection lost waiting for results from host:port
                    notify = new InterThreadException(
                        ExceptionMessages.CONNECTION_WAIT,
                                new Object[] { host, new Integer(port)},
                                LDAPException.CONNECT_ERROR,
                                ioe, info);
                }
                // The connection is no good, don't use it any more
                in = null;
                out = null;
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
                if( (! clientActive) || (notify != null)) { //#3 & 4
                    shutdown( reason, 0, notify );
                } else {
                    stopReaderMessageID = CONTINUE_READING;
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
    } // End class ReaderThread

    /**
     * Sets the current referral active on this connection if created to
     * follow referrals.
     */
    /* package */
    final void setActiveReferral( ReferralInfo referral)
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
    /* package */
    final ReferralInfo getActiveReferral()
    {
        return activeReferral;
    }

    /** Add the specific object to the list of listeners that want to be
     * notified when an unsolicited notification is received.
     */
    /* package */
    final void addUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
    {
        unsolicitedListeners.add(listener);
        return;
    }

    /** Remove the specific object from current list of listeners
    */
    /* package */
    final void removeUnsolicitedNotificationListener(LDAPUnsolicitedNotificationListener listener)
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
     *  notification as a separate thread
     */
    private class UnsolicitedListenerThread extends Thread
    {
        private LDAPUnsolicitedNotificationListener listenerObj;
        private LDAPExtendedResponse unsolicitedMsg;

        /* package */
        UnsolicitedListenerThread( LDAPUnsolicitedNotificationListener l,
                                          LDAPExtendedResponse m)
        {
            this.listenerObj = l;
            this.unsolicitedMsg = m;
            return;
        }

        public final void run()
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

            unsolSvrShutDnNotification = true;
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

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/Connection.java,v 1.27 2000/12/15 22:28:26 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
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

    private LBEREncoder encoder = new LBEREncoder();
    private LBERDecoder decoder = new LBERDecoder();

    private Socket socket = null;

    private InputStream in = null;
    private OutputStream out = null;

    // Place to save message information classes
    private MessageVector messages = new MessageVector(5,5);

    // The LDAPSocketFactory to be used as the default to create new connections
    static private LDAPSocketFactory socketFactory = null;
    // The LDAPSocketFactory used for this connection
    private LDAPSocketFactory mySocketFactory = socketFactory;
    private String host = null;
    private int port = 0;
    // Number of clones in addition to original LDAPConnection using this connection.
    private int cloneCount = 0;
    // Connection number & name used only for debug
    private static Object nameLock = new Object(); // protect connNum
    private static int connNum = 0;
    private String name;
    /**
     * Create a new Connection object
     *
     * @param factory specifies the factory to use to produce SSL sockets.
     */
    public Connection( LDAPSocketFactory factory)
    {
        if( factory == null) {
            mySocketFactory = socketFactory;
        } else {
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
    public void connect(String host, int port)
      throws LDAPException
    {
        connect(host, port, 0);
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
        // Don't initialize connection while previous reader thread still active.
        try {
            // wait for previous reader thread to terminate
            while( reader != null) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "waiting for reader thread to exit");
                }
                synchronized( this) {
                    this.wait(1000);
                }
            }
        } catch ( InterruptedException ex) {
            ;
        }
        int semId = acquireBindSemaphore( semaphoreId);

        // Make socket connection to specified host and port
        if( port == 0) {
            port = LDAPConnection.DEFAULT_PORT;
        }

        try {
            if( (in == null) || (out == null) ) {
                if(socketFactory != null) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "connect(socketFactory specified)");
                    }
                    socket = socketFactory.makeSocket(host, port);
                } else {
                    socket = new Socket(host, port);
                }

                in = new BufferedInputStream(socket.getInputStream());
                out = new BufferedOutputStream(socket.getOutputStream());
            } else {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "connect(input/out Stream specified)");
                }
            }
        } catch(IOException ioe) {
            throw new LDAPException("Unable to connect to server: " + host,
                             LDAPException.CONNECT_ERROR);
        }
        // Set host and port
        this.host = host;
        this.port = port;
        // start the reader thread
        reader = new Thread(this);
        reader.setDaemon(true); // If this is the last thread running, exit.
        reader.start();
        freeBindSemaphore(semId);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + " connect: setup complete");
        }
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
    public void createClone()
    {
        int semId = acquireBindSemaphore();
        cloneCount++;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "createClone(" + cloneCount + ")");
        }
        freeBindSemaphore(semId);
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
    public Connection destroyClone()
        throws LDAPException
    {
        return destroyClone(null,0);
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
    public Connection destroyClone( String host, int port)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "destroyClone(" + host + "," + port + ")");
        }
        Connection conn = this;
        int semId = acquireBindSemaphore();
        
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
                shutdown("Destroy Clone", semId);
            }    
        }
        if( host != null) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "destroyClone(" + cloneCount + ") connect(" + host + "," + port + ")");
            }
            conn.connect( host, port, semId);
        }
        freeBindSemaphore(semId);
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
    /* package */ void writeMessage(Message info)
        throws IOException
    {
        messages.addElement( info);
        LDAPMessage msg = info.getRequest();
        writeMessage( msg);
        return;
    }


    /**
    * Writes an LDAPMessage to the LDAP server over a socket.
    *
    * @param msg the message to write.
    */
    /* package */ void writeMessage(LDAPMessage msg)
        throws IOException
    {
        OutputStream myOut = out;
        if( myOut == null) {
            throw new IOException("Output stream not initialized");
        }
        int id = msg.getMessageID();
        byte[] ber = msg.getASN1Object().getEncoding(encoder);
        acquireBindSemaphore(id);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "Writing Message(" + id + ")");
        }
        myOut.write(ber, 0, ber.length);
        myOut.flush();
        freeBindSemaphore(id);
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
        shutdown("Finalize",0);
        return;
    }
    /**
     * Cleans up resources associated with this connection.
     * This method may be called by finalize() for the connection, or it may
     * be called by LDAPConnection.disconnect().
     *
     */
    private void shutdown( String reason, int semaphoreId)
    {
        Message info = null;
        if( in == null) {
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
                break;
            }
            info.abandon( null );
        }

        // Now send unbind before closing socket
        int semId = acquireBindSemaphore( semaphoreId);
        if( (bindProperties != null) && (out != null)) {
            try {
                LDAPMessage msg = new LDAPMessage( new RfcUnbindRequest(),null);
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                     "Writing unbind request (" + msg.getMessageID() + ")");
                }
                byte[] ber = msg.getASN1Object().getEncoding(encoder);
                if( out != null) {
                    out.write(ber, 0, ber.length);
                    out.flush();
                }
            } catch( IOException ex) {
                ;  // don't worry about error
            }
        }
        bindProperties = null;

        if( in != null) {
            // Close the input stream
            try {
                in.close();
            } catch(java.io.IOException ie) {
                // ignore problem closing input stream
            }
            in = null;
        }        

        if( out != null) {
            // Close the output stream
            try {
                out.close();
            } catch(java.io.IOException ie) {
                // ignore problem closing output stream
            }
            out = null;
        }        

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

        String reason = "reader: thread stopped";

        try {
            for(;;) {
                // ------------------------------------------------------------
                // Decode an RfcLDAPMessage directly from the socket.
                // ------------------------------------------------------------
                Message info;
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
                    if( info.acceptsReplies()) {
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "reader: queue message(" + msgId + ")");
                            Debug.trace( Debug.buffer, name +
                                "reader: message(" + msgId + ")=" +
                                msg.toString());
                        }
                        info.putReply( msg);   // queue & wake up waiting thread
                    } else {
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "reader: message(" + msgId +
                                ") not accepting replies, discarding reply");
                        }        
                    }
                } catch ( NoSuchFieldException ex) {
                    if( Debug.LDAP_DEBUG ) {
                        Debug.trace( Debug.messages, name +
                            "reader: message(" + msgId +
                            ") not found, discarding reply");
                    }        
                }
            }
        } catch( IOException ioe) {
            if( Debug.LDAP_DEBUG ) {
                reason = "reader: Connection lost for " + host + ":" + port + " " + ioe.toString();
                Debug.trace( Debug.messages, name + reason);
            }        
        } finally {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                "reader: connection shutdown");
            }        
            shutdown(reason, 0);
        }
        reader = null;
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name +
            "reader: thread terminated");
        }        
        return;
    }
}

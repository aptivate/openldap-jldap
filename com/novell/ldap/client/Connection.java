/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/Connection.java,v 1.22 2000/11/10 16:50:04 vtag Exp $
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

    private Thread reader; // New thread that reads data from the server.

    private LBEREncoder encoder = new LBEREncoder();
    private LBERDecoder decoder = new LBERDecoder();

    private Socket socket = null;
    private boolean bound = false;

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
    private int protocolVersion = 3;
    private String authenticationPassword = null;
    private String authenticationDN = null;
    private String authenticationMethod = null;
    private Hashtable saslBindProperties = null;
    private Object /* javax.security.auth.callback.CallbackHandler */
                saslBindCallbackHandler = null;
    private boolean keepNotifications = false;
    private int cloneCount = 0;
    // Connection number & name used for debug
    private static int connNum = 0;
    private String name;
    /**
     * Create a new Connection object
     *
     * @param factory specifies the factory to use to produce SSL sockets.
     */
    public Connection( LDAPSocketFactory factory)
    {
        // save socket factory
        mySocketFactory = factory;
        if( Debug.LDAP_DEBUG) {
            synchronized(this) {
                name = "Connection(" + ++connNum + "): ";
            }
            Debug.trace( Debug.messages, name + "Created");
        }
        return;
    }

    /**
     * Create a new Connection object using the default socket factory.
     */
    public Connection( )
    {
        // Use default socket factory
        this( socketFactory);
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
        // Make socket connection to specified host and port
        if( port == 0) {
            port = LDAPConnection.DEFAULT_PORT;
        }

        try {
            if(socketFactory != null) {
                socket = socketFactory.makeSocket(host, port);
            } else {
                socket = new Socket(host, port);
            }

            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
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
     */
    public synchronized void createClone()
    {
        cloneCount++;
        return;
    }

    /**
     *  Indicates that an LDAPConnection clone is being destroyed
     */
    public synchronized void destroyClone()
    {
        cloneCount--;
        return;
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
     * gets the protocol version
     */
    public int getProtocolVersion()
    {
        return protocolVersion;
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
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "Writing request for Message(" + info.getMessageID() + ")");
        }
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
        byte[] ber = msg.getASN1Object().getEncoding(encoder);
        synchronized(this) {
            out.write(ber, 0, ber.length);
            out.flush();
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
        return bound;
    }

    /**
     * Return whether a connection has been made
     */
    public boolean isConnected()
    {
        return (socket != null);
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
     * Gets the authentication password
     *
     * @return the authentication password for this connection
     */
    public String getAuthenticationPassword()
    {
        return authenticationPassword;
    }

    /**
     * Gets the authentication dn
     *
     * @return the authentication dn for this connection
     */
    public String getAuthenticationDN()
    {
        return authenticationPassword;
    }

    /**
     * Gets the authentication method
     *
     * @return the authentication method for this connection
     */
    public String getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    /**
     * Gets the SASL Bind properties
     *
     * @return the sasl bind properties for this connection
     */
    public Hashtable getSaslBindProperties()
    {
        return saslBindProperties;
    }

    /**
     * Gets the SASL callback handler
     *
     * @return the sasl callback handler for this connection
     */
    public Object /* javax.security.auth.callback.CallbackHandler */ getSaslCallbackHandler()
    {
        return saslBindCallbackHandler;
    }

    /**
     * Gets the setting of unsolicited notifications
     *
     * @return true if accepting unsolicited notifications, false if not
     */
    public boolean getUnsolicitedNotifications()
    {
        return keepNotifications;
    }

    /**
     * Sets whether or not to keep unsolicited notifications
     *
     * @param true if to allow unsolicited notifications, false if not
     */
    public void setUnsolicitedNotifications( boolean allow)
    {
        keepNotifications = allow;
        return;
    }

    /**
     * Removes a Message class from the Connection's list
     *
     * @param info the Message class to remove from the list
     */
    public void removeMessage( Message info)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "Removing message(" + info.getMessageID() + ")");
        }
        messages.removeElement(info);
    }

    /**
     * Cleans up resources associated with this connection.
     */
    protected void finalize()
    {
        shutdown(null);
    }
    /**
     * Cleans up resources associated with this connection.
     * This method may be called by finalize() for the connection, or it may
     * be called by LDAPConnection.disconnect().
     *
     */
    public void shutdown( String reason)
    {
        Message info = null;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "Shutting down connection");
        }
        if(socket != null) {
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
            if(bound) {
                bound = false;
                try {
                    LDAPMessage msg = new LDAPMessage( new RfcUnbindRequest(),null);
                    byte[] ber = msg.getASN1Object().getEncoding(encoder);
                    synchronized(this) {
                        out.write(ber, 0, ber.length);
                        out.flush();
                    }
                } catch( IOException ex) {
                    ;  // don't worry about error
                }
            }
            // Close the socket
            try {
                out.flush();
                socket.close();
            } catch(java.io.IOException ie) {
                // ignore problem closing socket
            }
            socket = null;
        }
        clearBound();
    }

    /**
    *
    *  Sets the authentication credentials in the object
    *  and set flag indicating successful bind.
    * 
    *
    * @param version protocol version, either 2 or 3
    *<br><br>
    * @param dn   If non-null and non-empty, specifies that the 
    *              connection and all operations through it should be 
    *              authenticated with the DN as the distinguished name.<br><br>
    *<br><br>
    * @param passwd   If non-null and non-empty, specifies that the
    *                  connection and all operations through it should 
    *                  be authenticated with the dn as the distinguished 
    *                  name and passwd as the password.
    *<br><br>
    * @param method Authentication method.
    *<br><br>
    * @param method Authentication hash for SASL bind.
    *<br><br>
    * @param method Authentication callback handler for SASL bind.
    */


    public synchronized void setBound(   int version,
                                  String dn,
                                  String passwd,
                                  String method,
                                  Hashtable bindProperties,
                                  Object bindCallbackHandler)
    {
        protocolVersion = version;
        authenticationDN = dn;
        authenticationPassword = passwd;
        authenticationMethod = method;
        saslBindProperties = bindProperties;
        saslBindCallbackHandler = bindCallbackHandler;
        bound = true;
        return;
    }

    /**
    *
    *  Clears the authentication credentials in the object
    *  and clears the flag indicating successful bind.
    * 
    *
    *  @param dn   If non-null and non-empty, specifies that the 
    *              connection and all operations through it should be 
    *              authenticated with the DN as the distinguished name.<br><br>
    *<br><br>
    *  @param passwd   If non-null and non-empty, specifies that the
    *                  connection and all operations through it should 
    *                  be authenticated with the dn as the distinguished 
    *                  name and passwd as the password.
    */

    /* Package */ synchronized void clearBound( )
    {
        bound = false;
        protocolVersion = 3;
        authenticationDN = null;
        authenticationPassword = null;
        authenticationMethod = null;
        saslBindProperties = null;
        saslBindCallbackHandler = null;
        return;
    }


    /**
     * This thread decodes and processes RfcLDAPMessage's from the server.
     */
    /*
     * Note: This thread needs a graceful shutdown implementation.
     */
    public void run() {
        String reason = null;
        try {
            for(;;) {
                // ------------------------------------------------------------
                // Decode an RfcLDAPMessage directly from the socket.
                // ------------------------------------------------------------
                Message info;
                ASN1Identifier asn1ID = new ASN1Identifier(in);
                int tag = asn1ID.getTag();
                if(asn1ID.getTag() != ASN1Sequence.TAG) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "discarding message with tag " + tag);
                    }
                    continue; // loop looking for an RfcLDAPMessage identifier
                }

                // Turn the message into an RfcMessage class
                ASN1Length asn1Len = new ASN1Length(in);

                RfcLDAPMessage msg =
                    new RfcLDAPMessage( decoder, in, asn1Len.getLength());

                // ------------------------------------------------------------
                // Process the decoded RfcLDAPMessage.
                // ------------------------------------------------------------
                int msgId = msg.getMessageID();

                if(msgId == 0) {
                    // Process Unsolicited Notification
                } else {
                    // Find the message which requested this response.
                    // It is possible to receive a response for a request which
                    // has been abandoned. If abandoned, throw it away
                    //??               int cnt = ldapListeners.size();
                    try {
                        info = messages.findMessageById( msgId);
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "queue message(" + msgId + ")");
                            Debug.trace( Debug.buffer, name +
                                "message(" + msgId + ")=" +
                                msg.toString());
                        }
                        info.putReply( msg);   // queue & wake up waiting thread
                    } catch ( NoSuchFieldException ex) {
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "discarding message(" + msgId + ")");
                        }        
                    }
                }
            }
        }
        catch(IOException ioe) {
            reason = "Connection lost for " + host + ":" + port + " " + ioe.toString();
            Debug.trace( Debug.messages, name + reason);
        }
        finally {
            shutdown(reason);
        }
    }
}

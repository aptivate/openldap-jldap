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

package org.ietf.ldap;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 *  Represents the central class that encapsulates the connection
 *  to a directory server through the LDAP protocol.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html">
            com.novell.ldap.LDAPLDAPConnection</a>
 */
public class LDAPConnection implements Cloneable
{
    private com.novell.ldap.LDAPConnection conn;
    private java.util.Hashtable listenerQueues = new Hashtable();
    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the base obect.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#SCOPE_BASE">
            com.novell.ldap.LDAPConnection.SCOPE_BASE</a>
     */
    public static final int SCOPE_BASE =
                            com.novell.ldap.LDAPConnection.SCOPE_BASE;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the immediate subordinates of the base obect.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#SCOPE_ONE">
            com.novell.ldap.LDAPConnection.SCOPE_ONE</a>
     */
    public static final int SCOPE_ONE =
                            com.novell.ldap.LDAPConnection.SCOPE_ONE;


    /**
     * Used with search to specify that the scope of entrys to search is to
     * search the base object and all entries within its subtree.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#SCOPE_SUB">
            com.novell.ldap.LDAPConnection.SCOPE_SUB</a>
     */
    public static final int SCOPE_SUB =
                            com.novell.ldap.LDAPConnection.SCOPE_SUB;
    
    /**
     * Used with search to specify that the scope of entrys to search is to
     * search the subordinate subtree object and all entries within it.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#SCOPE_SUBORDINATESUBTREE">
            com.novell.ldap.LDAPConnection.SCOPE_SUBORDINATESUBTREE</a>
     */
    public static final int SCOPE_SUBORDINATESUBTREE =
                            com.novell.ldap.LDAPConnection.SCOPE_SUBORDINATESUBTREE;

    
    /**
     * Used with search instead of an attribute list to indicate that no
     * attributes are to be returned.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#NO_ATTRS">
            com.novell.ldap.LDAPConnection.NO_ATTRS</a>
     */
    public static final String NO_ATTRS =
                            com.novell.ldap.LDAPConnection.NO_ATTRS;

    /**
     * Used with search instead of an attribute list to indicate that all
     * attributes are to be returned.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#ALL_USER_ATTRS">
            com.novell.ldap.LDAPConnection.ALL_USER_ATTRS</a>
     */
    public static final String ALL_USER_ATTRS =
                            com.novell.ldap.LDAPConnection.ALL_USER_ATTRS;

    /**
     * The default port number for LDAP servers.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#DEFAULT_PORT">
            com.novell.ldap.LDAPConnection.DEFAULT_PORT</a>
     */
    public static final int DEFAULT_PORT =
                            com.novell.ldap.LDAPConnection.DEFAULT_PORT;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#LDAP_PROPERTY_SDK">
            com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SDK</a>
     */
    public static final String LDAP_PROPERTY_SDK =
                            com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SDK;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#LDAP_PROPERTY_PROTOCOL">
            com.novell.ldap.LDAPConnection.LDAP_PROPERTY_PROTOCOL</a>
     */
    public static final String LDAP_PROPERTY_PROTOCOL =
                          com.novell.ldap.LDAPConnection.LDAP_PROPERTY_PROTOCOL;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#LDAP_PROPERTY_SECURITY">
            com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SECURITY</a>
     */
    public static final String LDAP_PROPERTY_SECURITY =
                          com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SECURITY;

    /**
     * Constructs a new LDAPConnection object, which represents a connection
     * to an LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#LDAPConnection()">
            com.novell.ldap.LDAPConnection.LDAPConnection()</a>
     */
    public LDAPConnection()
    {
        conn = new com.novell.ldap.LDAPConnection();
        return;
    }

    /**
     * Constructs an LDAPConnection from a com.novell.ldap.LDAPConnection object
     */
    /* package */
    LDAPConnection( com.novell.ldap.LDAPConnection conn)
    {
        this.conn = conn;
        return;
    }

    /**
     * Creates an LDAPConnection from a com.novell.ldap.LDAPConnection object
     */
    /* package */
    com.novell.ldap.LDAPConnection getWrappedObject()
    {
        return conn;
    }

    /**
     * Interface for the SocketFactory wrapper class
     */
    private
    interface SocketFactoryWrapper extends com.novell.ldap.LDAPSocketFactory
    {
        /**
         * Get the org.ietf wrapped LDAPSocketFactory class
         */
        public LDAPSocketFactory getWrappedObject();
    }

    /**
     * Gets an instance of a com.novell.ldap.LDAPSocketFactory
     * wrapping an org.ietf.ldap.LDAPSocketFactory.  This must be
     * static because we need this functionality from both instance methods
     * and static methods within the class LDAPConnection
     */
    private
    static com.novell.ldap.LDAPSocketFactory getSocketImpl(LDAPSocketFactory f)
    {
        class SocketFactoryImpl
                implements SocketFactoryWrapper
        {
            private LDAPSocketFactory factory;
            private SocketFactoryImpl( LDAPSocketFactory factory)
            {
                this.factory = factory;
                return;
            }

            public LDAPSocketFactory getWrappedObject()
            {
                return factory;
            }

            public Socket createSocket(String host, int port)
                      throws IOException, UnknownHostException
            {
                return factory.createSocket( host, port);
            }
        }
        if( f == null) {
                return null;
        }
        return new SocketFactoryImpl(f);
    }

    /**
     * Constructs a new LDAPConnection object, which will use the supplied
     * class factory to construct a socket connection during
     * LDAPConnection.connect method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#LDAPConnection(com.novell.ldap.LDAPSocketFactory)">
            com.novell.ldap.LDAPConnection.LDAPConnection(LDAPSocketFactory)</a>
     */
    public LDAPConnection(LDAPSocketFactory factory)
    {
        if( (factory != null) &&
                (factory instanceof com.novell.ldap.LDAPSocketFactory)) {
            conn = new com.novell.ldap.LDAPConnection(
                                   (com.novell.ldap.LDAPSocketFactory)factory);
        } else {
            conn = new com.novell.ldap.LDAPConnection( getSocketImpl(factory));
        }
        return;
    }

    /**
     * Returns a copy of the object with a private context, but sharing the
     * network connection if there is one.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#clone()">
            com.novell.ldap.LDAPConnection.clone()</a>
     */
    public Object clone()
            throws CloneNotSupportedException
    {
        try {
            Object newObj = super.clone();
            ((LDAPConnection)newObj).conn = (com.novell.ldap.LDAPConnection)conn.clone();
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Closes the connection, if open, and releases any other resources held
     * by the object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#finalize()">
            com.novell.ldap.LDAPConnection.finalize()</a>
     */
    protected void finalize()
        throws LDAPException
    {
        try {
            conn.disconnect();
        } catch ( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Returns the protocol version uses to authenticate
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getProtocolVersion()">
            com.novell.ldap.LDAPConnection.getProtocolVersion()</a>
     */
    public int getProtocolVersion()
    {
        return conn.getProtocolVersion();
    }

    /**
     * Returns the distinguished name (DN) used for authentication by this
     * object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getAuthenticationDN()">
            com.novell.ldap.LDAPConnection.getAuthenticationDN()</a>
     */
    public String getAuthenticationDN()
    {
        return conn.getAuthenticationDN();
    }

    /**
     * Returns the method used to authenticate the connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getAuthenticationMethod()">
            com.novell.ldap.LDAPConnection.getAuthenticationMethod()</a>
     */
    public String getAuthenticationMethod()
    {
        return conn.getAuthenticationMethod();
    }

    /**
     * Returns the properties if any specified on binding with a
     * SASL mechanism.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSaslBindProperties()">
            com.novell.ldap.LDAPConnection.getSaslBindProperties()</a>
     */
    public Map getSaslBindProperties()
    {
        return conn.getSaslBindProperties();
    }

    /**
     * Returns the call back handler if any specified on binding with a
     * SASL mechanism.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSaslBindCallbackHandler()">
            com.novell.ldap.LDAPConnection.getSaslBindCallbackHandler()</a>
     */
    public Object /* javax.security.auth.callback.CallbackHandler */
                     getSaslBindCallbackHandler()
    {
        return conn.getSaslBindCallbackHandler();
    }

    /**
     * Returns a copy of the set of constraints associated with this
     * connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getConstraints()">
            com.novell.ldap.LDAPConnection.getConstraints()</a>
     */
    public LDAPConstraints getConstraints()
    {
        return new LDAPSearchConstraints( conn.getSearchConstraints());
    }

    /**
     * Returns the host name of the LDAP server to which the object is or
     * was last connected, in the format originally specified.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getHost()">
            com.novell.ldap.LDAPConnection.getHost()</a>
     */
    public String getHost()
    {
        return conn.getHost();
    }

    /**
     * Returns the port number of the LDAP server to which the object is or
     * was last connected.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getPort()">
            com.novell.ldap.LDAPConnection.getPort()</a>
     */
    public int getPort()
    {
        return conn.getPort();
    }

    /**
     * Returns a property of a connection object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getProperty(java.lang.String)">
            com.novell.ldap.LDAPConnection.getProperty(String)</a>
     */
    public Object getProperty(String name)
    {
        return conn.getProperty( name);
    }

    /**
     * Returns a copy of the set of search constraints associated with this
     * connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSearchConstraints()">
            com.novell.ldap.LDAPConnection.getSearchConstraints()</a>
     */
    public LDAPSearchConstraints getSearchConstraints()
    {
        return new LDAPSearchConstraints( conn.getSearchConstraints());
    }

    /**
     * Returns the LDAPSocketFactory used to establish this server connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSocketFactory()">
            com.novell.ldap.LDAPConnection.getSocketFactory()</a>
     */
    public LDAPSocketFactory getSocketFactory()
    {
        com.novell.ldap.LDAPSocketFactory factory = conn.getSocketFactory();
        if( factory == null) {
            return null;
        }
        if(factory instanceof LDAPSocketFactory) {
            return (LDAPSocketFactory)factory;
        } else {
            return ((SocketFactoryWrapper)factory).getWrappedObject();
        }
    }

    /**
     * Indicates whether the object has authenticated to the connected LDAP
     * server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#isBound()">
            com.novell.ldap.LDAPConnection.isBound()</a>
     */
    public boolean isBound()
    {
        return conn.isBound();
    }

    /**
     * Indicates whether the connection represented by this object is open
     * at this time.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#isConnected()">
            com.novell.ldap.LDAPConnection.isConnected()</a>
     */
    public boolean isConnected()
    {
        return conn.isConnected();
    }

    /**
     * Indicates if the connection uses TLS.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#isTLS()">
            com.novell.ldap.LDAPConnection.isTLS()</a>
     */
    public boolean isTLS()
    {
        return conn.isTLS();
    }

    /**
     * Sets the constraints that apply to all operations performed through
     * this connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#setConstraints(com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.setConstraints(LDAPConstraints)</a>
     */
    public void setConstraints(LDAPConstraints cons)
    {
        conn.setConstraints( cons.getWrappedObject());
        return;
    }

    /**
     * Establishes the default LDAPSocketFactory used when
     * LDAPConnection objects are constructed.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#setSocketFactory(com.novell.ldap.LDAPSocketFactory)">
            com.novell.ldap.LDAPConnection.setSocketFactory(LDAPSocketFactory)</a>
     */
    public static void setSocketFactory( LDAPSocketFactory factory)
    {
        if( (factory != null) &&
                (factory instanceof com.novell.ldap.LDAPSocketFactory)) {
            com.novell.ldap.LDAPConnection.setSocketFactory(
                                    (com.novell.ldap.LDAPSocketFactory)factory);
        } else {
            com.novell.ldap.LDAPConnection.setSocketFactory(
                                                        getSocketImpl(factory));
        }
        return;
    }

    /**
     * Registers an object to be notified on arrival of an unsolicited
     * message from a server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#addUnsolicitedNotificationListener(com.novell.ldap.LDAPUnsolicitedNotificationListener)">
            com.novell.ldap.LDAPConnection.addUnsolicitedNotificationListener(
                LDAPUnsolicitedNotificationListener)</a>
     */
    public void addUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener listen)
    {
        if (listen != null) {
            conn.addUnsolicitedNotificationListener( new UnsolImpl(listen));
        }
        return;
    }

    /**
     * Class to wrap an application's LDAPUnsolicitedNotificationListener
     */
    private class UnsolImpl
            implements com.novell.ldap.LDAPUnsolicitedNotificationListener
    {
        org.ietf.ldap.LDAPUnsolicitedNotificationListener listen;

        private UnsolImpl( org.ietf.ldap.LDAPUnsolicitedNotificationListener ul)
        {
            listen = ul;
            // Remember this association so we can do remove properly
            synchronized( listenerQueues) {
                listenerQueues.put( ul, this);
            }
            return;
        }

        public void messageReceived( com.novell.ldap.LDAPExtendedResponse msg)
        {
            listen.messageReceived(
                    new LDAPExtendedResponse( msg));
            return;
        }

        private
        org.ietf.ldap.LDAPUnsolicitedNotificationListener getWrappedObject()
        {
            return listen;
        }
    }

    /**
     * Deregisters an object so that it will no longer be notified on
     * arrival of an unsolicited message from a server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#removeUnsolicitedNotificationListener(com.novell.ldap.LDAPUnsolicitedNotificationListener)">
            com.novell.ldap.LDAPConnection.removeUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener)</a>
     */
    public void removeUnsolicitedNotificationListener(
                        LDAPUnsolicitedNotificationListener queue)
    {

        com.novell.ldap.LDAPUnsolicitedNotificationListener ul = null;

        if (queue != null) {
            synchronized( listenerQueues) {
                ul = (com.novell.ldap.LDAPUnsolicitedNotificationListener)
                        listenerQueues.remove( queue);
            }
            if( ul != null) {
                conn.removeUnsolicitedNotificationListener( ul);
            }
        }
        if( ul == null) {
            throw new IllegalArgumentException(
                "removeUnsolicitedNotificationListener: Invalid Parameter");
        }
        return;
    }

    /**
     * Starts Transport Layer Security (TLS) protocol on this connection
     * to enable session privacy.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#startTLS()">
            com.novell.ldap.LDAPConnection.startTLS()</a>
     */
    public void startTLS() throws LDAPException
    {
        try {
            conn.startTLS();
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Stops Transport Layer Security (TLS) protocol on this connection.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#stopTLS()">
            com.novell.ldap.LDAPConnection.stopTLS()</a>
     */
    public void stopTLS() throws LDAPException
    {
        try {
            conn.stopTLS();
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Notifies the server not to send additional results associated with
     * this LDAPSearchResults object, and discards any results already
     * received.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(com.novell.ldap.LDAPSearchResults)">
            com.novell.ldap.LDAPConnection.abandon(LDAPSearchResults)</a>
     */
    public void abandon(LDAPSearchResults results)
        throws LDAPException
    {
        try {
            if( results == null) {
                conn.abandon( (com.novell.ldap.LDAPSearchResults)null);
            } else {
                conn.abandon( results.getWrappedObject());
            }
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Notifies the server not to send additional results associated with
     * this LDAPSearchResults object, and discards any results already
     * received.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(com.novell.ldap.LDAPSearchResults,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.abandon(LDAPSearchResults,
            LDAPConstraints)</a>
     */
    public void abandon(LDAPSearchResults results, LDAPConstraints cons)
        throws LDAPException
    {
        try {
            if( results == null) {
                conn.abandon( (com.novell.ldap.LDAPSearchResults)null,
                              cons.getWrappedObject());
            } else {
                conn.abandon( results.getWrappedObject(),
                              cons.getWrappedObject());
            }
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     *
     *  Abandons an asynchronous operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(int)">
            com.novell.ldap.LDAPConnection.abandon(int)</a>
     */
    public void abandon(int id)
        throws LDAPException
    {
        try {
            conn.abandon( id);
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     *  Abandons a search operation for a queue, using the specified
     *  constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(int, com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.abandon(int, LDAPConstraints)</a>
     */
    public void abandon(int id, LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.abandon( id,
                          cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Abandons all search operations for a queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(com.novell.ldap.LDAPMessageQueue)">
            com.novell.ldap.LDAPConnection.abandon(LDAPMessageQueue)</a>
     */
    public void abandon( LDAPMessageQueue queue)
        throws LDAPException
    {
        try {
            if( queue instanceof LDAPResponseQueue) {
              conn.abandon(((LDAPResponseQueue)queue).getWrappedObject());
            } else
            if( queue instanceof LDAPSearchQueue) {
              conn.abandon(((LDAPSearchQueue)queue).getWrappedObject());
            }
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Abandons all search operations for a queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#abandon(com.novell.ldap.LDAPMessageQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.abandon(LDAPMessageQueue,
            LDAPConstraints)</a>
     */
    public void abandon( LDAPMessageQueue queue, LDAPConstraints cons)
        throws LDAPException
    {
        try {
            if( queue instanceof LDAPResponseQueue) {
              conn.abandon(((LDAPResponseQueue)queue).getWrappedObject(),
                        cons.getWrappedObject());
            } else
            if( queue instanceof LDAPSearchQueue) {
              conn.abandon(((LDAPSearchQueue)queue).getWrappedObject(),
                        cons.getWrappedObject());
            }
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Synchronously adds an entry to the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#add(com.novell.ldap.LDAPEntry)">
            com.novell.ldap.LDAPConnection.add(LDAPEntry)</a>
     */
    public void add(LDAPEntry entry)
        throws LDAPException
    {
        try {
            conn.add( entry.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

     /**
     * Synchronously adds an entry to the directory, using the specified
     * constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#add(com.novell.ldap.LDAPEntry, com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.add(LDAPEntry, LDAPConstraints)</a>
     */
    public void add(LDAPEntry entry,
                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.add( entry.getWrappedObject(),
                      cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Asynchronously adds an entry to the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#add(com.novell.ldap.LDAPEntry,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.add(LDAPEntry,
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue add(LDAPEntry entry,
                                 LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                 conn.add( entry.getWrappedObject(),
                           queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously adds an entry to the directory, using the specified
     * constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#add(com.novell.ldap.LDAPEntry,
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.add(LDAPEntry, LDAPResponseQueue,
            LDAPConstraints)</a>
     */
    public LDAPResponseQueue add(LDAPEntry entry,
                                 LDAPResponseQueue queue,
                                 LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.add( entry.getWrappedObject(),
                             queue.getWrappedObject(),
                             cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name, password, and LDAP version.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(int, java.lang.String, byte[])">
            com.novell.ldap.LDAPConnection.bind(int, String, byte[])</a>
     */
    public void bind(int version,
                     String dn,
                     byte[] passwd)
        throws LDAPException
    {
        try {
            conn.bind( version, dn, passwd);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name, password, LDAP version,
     * and constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(int, java.lang.String, byte[],
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.bind(int, String, byte[],
            LDAPConstraints)</a>
     */
    public void bind(int version,
                     String dn,
                     byte[] passwd,
                     LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.bind( version, dn, passwd,
                        cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, and queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(int, java.lang.String, byte[],
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.bind(int, String, byte[],
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue bind(int version,
                                  String dn,
                                  byte[] passwd,
                                  LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                conn.bind( version, dn, passwd,
                           queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, queue, and constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(int, java.lang.String, byte[],
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.bind(int, String, String,
            LDAPResponseQueue, LDAPConstraints)</a>
     */
    public LDAPResponseQueue bind(int version,
                                  String dn,
                                  byte[] passwd,
                                  LDAPResponseQueue queue,
                                  LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                 conn.bind( version,
                            dn,
                            passwd,
                            queue.getWrappedObject(),
                            cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(java.lang.String, java.lang.String, java.util.Map,
            java.lang.Object)">
            com.novell.ldap.LDAPConnection.bind(String, String, Map, Object)</a>
     */
    public void bind(String dn,
                     String authzid,
                     Map props,
                     Object cbh)/*javax.security.auth.callback.CallbackHandler*/
                     throws LDAPException
    {
        try {
            conn.bind( dn, authzid, props, cbh);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(java.lang.String, java.lang.String, java.util.Map,
            java.lang.Object,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.bind(String, String, Map, Object,
            LDAPConstraints)</a>
     */
    public void bind(String dn,
                     String authzid,
                     Map props,
                     Object cbh,/*javax.security.auth.callback.CallbackHandler*/
                     LDAPConstraints cons)
                     throws LDAPException
    {
        try {
            conn.bind( dn, authzid, props, cbh,
                       cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(java.lang.String, java.lang.String, java.lang.String[],
            java.util.Map, java.lang.Object)">
            com.novell.ldap.LDAPConnection.bind(String, String, String[], Map,
            Object)</a>
     */
    public void bind(String dn,
                     String authzid,
                     String[] mechanisms,
                     Map props,
                     Object cbh)/*javax.security.auth.callback.CallbackHandler*/

                     throws LDAPException
    {
        try {
            conn.bind( dn, authzid, mechanisms, props, cbh);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }
    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#bind(java.lang.String, java.lang.String, java.lang.String[],
            java.util.Map, java.lang.Object, com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.bind(String, String, String[], Map,
            Object, LDAPConstraints)</a>
     */
    public void bind(String dn,
                     String authzid,
                     String[] mechanisms,
                     Map props,
                     Object cbh,/*javax.security.auth.callback.CallbackHandler*/
                     LDAPConstraints cons)
                     throws LDAPException
    {
        try {
            conn.bind( dn, authzid, mechanisms, props, cbh,
                                                    cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Synchronously checks to see if an entry contains an attribute
     * with a specified value.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#compare(java.lang.String, com.novell.ldap.LDAPAttribute)">
            com.novell.ldap.LDAPConnection.compare(String, LDAPAttribute)</a>
     */
    public boolean compare(String dn,
                           LDAPAttribute attr)
        throws LDAPException
    {
        try {
            return conn.compare( dn, attr.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Checks to see if an entry contains an attribute with a specified
     * value, using the specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#compare(java.lang.String, com.novell.ldap.LDAPAttribute,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.compare(String, LDAPAttribute,
            LDAPConstraints)</a>
     */
    public boolean compare(String dn,
                           LDAPAttribute attr,
                           LDAPConstraints cons)
       throws LDAPException
    {
        try {
            return conn.compare( dn,
                                 attr.getWrappedObject(),
                                 cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#compare(java.lang.String, com.novell.ldap.LDAPAttribute,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.compare(String, LDAPAttribute,
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue compare(String dn,
                                     LDAPAttribute attr,
                                     LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
             conn.compare( dn,
                           attr.getWrappedObject(),
                           queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified queue and contraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#compare(java.lang.String, com.novell.ldap.LDAPAttribute,
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.compare(String, LDAPAttribute,
            LDAPResponseQueue, LDAPConstraints)</a>
     */
    public LDAPResponseQueue compare(String dn,
                                     LDAPAttribute attr,
                                     LDAPResponseQueue queue,
                                     LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
               conn.compare( dn,
                             attr.getWrappedObject(),
                             queue.getWrappedObject(),
                             cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     *
     * Connects to the specified host and port
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#connect(java.lang.String, int)">
            com.novell.ldap.LDAPConnection.connect(String, int)</a>
     */
    public void connect(String host, int port)
        throws LDAPException
    {
        try {
            conn.connect( host, port);
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Synchronously deletes the entry with the specified distinguished name
     * from the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#delete(java.lang.String)">
            com.novell.ldap.LDAPConnection.delete(String)</a>
     */
    public void delete(String dn)
        throws LDAPException
    {
        try {
            conn.delete( dn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }


    /**
     * Synchronously deletes the entry with the specified distinguished name
     * from the directory, using the specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#delete(java.lang.String, com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.delete(String, LDAPConstraints)</a>
     */
    public void delete(String dn, LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.delete( dn, cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory and returns the results to the specified queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#delete(java.lang.String, com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.delete(String,
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue delete(String dn,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
              conn.delete( dn,
                           queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory, using the specified contraints and queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#delete(java.lang.String, com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.delete(String, LDAPResponseQueue,
            LDAPConstraints)</a>
     */
    public LDAPResponseQueue delete(String dn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                conn.delete( dn,
                             queue.getWrappedObject(),
                             cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously disconnects from the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#disconnect()">
            com.novell.ldap.LDAPConnection.disconnect()</a>
     */
    public void disconnect()
        throws LDAPException
    {
        try {
            conn.disconnect();
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Synchronously disconnects from the LDAP server, including
     * constraints to send with the unbind request.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#disconnect(com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.disconnect(LDAPConstraints)</a>
     */
    public void disconnect( LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.disconnect( cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Provides a synchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#extendedOperation(com.novell.ldap.LDAPExtendedOperation)">
            com.novell.ldap.LDAPConnection.extendedOperation(
            LDAPExtendedOperation)</a>
     */
    public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op)
        throws LDAPException
    {
        try {
            return new LDAPExtendedResponse(
                    conn.extendedOperation( op.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

     /**
     * Provides a synchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#extendedOperation(com.novell.ldap.LDAPExtendedOperation,
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.extendedOperation(
            LDAPExtendedOperation, LDAPSearchConstraints)</a>
     */

    public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op,
                                                  LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPExtendedResponse(
                        conn.extendedOperation( op.getWrappedObject(),
                                                cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Provides an asynchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#extendedOperation(com.novell.ldap.LDAPExtendedOperation,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.extendedOperation(
            LDAPExtendedOperation, LDAPResponseQueue)</a>
     */

    public LDAPResponseQueue extendedOperation(LDAPExtendedOperation op,
                                     LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                    conn.extendedOperation(
                        op.getWrappedObject(),
                        queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Provides an asynchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#extendedOperation(com.novell.ldap.LDAPExtendedOperation,
            com.novell.ldap.LDAPSearchConstraints,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.extendedOperation(
            LDAPExtendedOperation, LDAPSearchConstraints,
            LDAPResponseQueue)</a>
     */

    public LDAPResponseQueue extendedOperation(LDAPExtendedOperation op,
                                               LDAPSearchConstraints cons,
                                               LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
              conn.extendedOperation( op.getWrappedObject(),
                                      cons.getWrappedSearchObject(),
                                      queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

     /**
     *  Returns the Server Controls associated with the most recent response to
     *  a synchronous request on this connection object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getResponseControls()">
            com.novell.ldap.LDAPConnection.getResponseControls()</a>
     */
    public LDAPControl[] getResponseControls()
    {
        com.novell.ldap.LDAPControl[] controls = conn.getResponseControls();
        if( controls == null) {
            return null;
        }

        LDAPControl[] ietfControls = new LDAPControl[controls.length];

        for( int i=0; i < controls.length; i++) {
            ietfControls[i] = new LDAPControl( controls[i]);
        }
        return ietfControls;
    }

    /**
     * Synchronously makes a single change to an existing entry in the
     * directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification)</a>
     */
    public void modify(String dn, LDAPModification mod)
        throws LDAPException
    {
        try {
            conn.modify( dn, mod.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Synchronously makes a single change to an existing entry in the
     * directory, using the specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification,
            LDAPConstraints)</a>
     */
    public void modify(String dn,
                       LDAPModification mod,
                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.modify( dn,
                         mod.getWrappedObject(),
                         cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Synchronously makes a set of changes to an existing entry in the
     * directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification[]">
            com.novell.ldap.LDAPConnection.modify(String,
            LDAPModification[])</a>
     */
    public void modify(String dn, LDAPModification[] mods)
        throws LDAPException
    {
        try {
            com.novell.ldap.LDAPModification[] lmods =
                            new com.novell.ldap.LDAPModification[mods.length];
            for( int i = 0; i< mods.length; i++) {
                lmods[i] = mods[i].getWrappedObject();
            }
            conn.modify( dn, lmods);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Synchronously makes a set of changes to an existing entry in the
     * directory, using the specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification[],
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification[],
            LDAPConstraints)</a>
     */
    public void modify(String dn,
                       LDAPModification[] mods,
                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            com.novell.ldap.LDAPModification[] lmods =
                            new com.novell.ldap.LDAPModification[mods.length];
            for( int i = 0; i< mods.length; i++) {
                lmods[i] = mods[i].getWrappedObject();
            }
            conn.modify( dn, lmods, cons.getWrappedObject());

        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Asynchronously makes a single change to an existing entry in the
     * directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification,
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification mod,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.modify( dn,
                                mod.getWrappedObject(),
                                queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously makes a single change to an existing entry in the
     * directory, using the specified constraints and queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification,
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification,
            LDAPResponseQueue. LDAPConstraints)</a>
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification mod,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.modify( dn,
                                mod.getWrappedObject(),
                                queue.getWrappedObject(),
                                cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification[],
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification[],
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification[] mods,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            com.novell.ldap.LDAPModification[] lmods =
                            new com.novell.ldap.LDAPModification[mods.length];
            for( int i = 0; i< mods.length; i++) {
                lmods[i] = mods[i].getWrappedObject();
            }
            return new LDAPResponseQueue(
                   conn.modify( dn, lmods, queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory, using the specified constraints and queue.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#modify(java.lang.String, com.novell.ldap.LDAPModification[],
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.modify(String, LDAPModification[],
            LDAPResponseQueue, LDAPConstraints)</a>
     */
    public LDAPResponseQueue modify(String dn,
                                       LDAPModification[] mods,
                                       LDAPResponseQueue queue,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            com.novell.ldap.LDAPModification[] lmods =
                            new com.novell.ldap.LDAPModification[mods.length];
            for( int i = 0; i< mods.length; i++) {
                lmods[i] = mods[i].getWrappedObject();
            }
            return new LDAPResponseQueue(
                   conn.modify( dn,
                                lmods,
                                queue.getWrappedObject(),
                                cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously reads the entry for the specified distiguished name (DN)
     * and retrieves all attributes for the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(java.lang.String)">
            com.novell.ldap.LDAPConnection.read(String)</a>
     */
    public LDAPEntry read(String dn)
        throws LDAPException
    {
        try {
            return new LDAPEntry( conn.read( dn));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously reads the entry for the specified distiguished name (DN),
     * using the specified constraints, and retrieves all attributes for the
     * entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(java.lang.String, com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.read(String,
            LDAPSearchConstraints)</a>
     */
    public LDAPEntry read(String dn,
                          LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPEntry( conn.read(dn, cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     *
     * Synchronously reads the entry for the specified distinguished name (DN)
     * and retrieves only the specified attributes from the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(java.lang.String, java.lang.String[])">
            com.novell.ldap.LDAPConnection.read(String, String[])</a>
     */
    public LDAPEntry read(String dn,
                          String[] attrs)
        throws LDAPException
    {
        try {
            return new LDAPEntry( conn.read( dn, attrs));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     *
     * Synchronously reads the entry for the specified distinguished name (DN),
     * using the specified constraints, and retrieves only the specified
     * attributes from the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(java.lang.String, java.lang.String[],
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.read(String, String[],
            LDAPSearchConstraints)</a>
     */
    public LDAPEntry read(String dn,
                          String[] attrs,
                          LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPEntry( conn.read( dn,attrs,
                                             cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously reads the entry specified by the LDAP URL.
     *

     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(com.novell.ldap.LDAPUrl)">
            com.novell.ldap.LDAPConnection.read(LDAPUrl)</a>
     */
    public static LDAPEntry read(LDAPUrl toGet)
        throws LDAPException
    {
        try {
            return new LDAPEntry( com.novell.ldap.LDAPConnection.read(
                                        toGet.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously reads the entry specified by the LDAP URL, using the
     * specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#read(com.novell.ldap.LDAPUrl,
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.read(LDAPUrl,
            LDAPSearchConstraints)</a>
     */
    public static LDAPEntry read(LDAPUrl toGet,
                                 LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPEntry(
                    com.novell.ldap.LDAPConnection.read(
                                       toGet.getWrappedObject(),
                                       cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously renames an existing entry in the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, boolean)">
            com.novell.ldap.LDAPConnection.rename(String, String, boolean)</a>
     */
    public void rename(String dn,
                       String newRdn,
                       boolean deleteOldRdn)
        throws LDAPException
    {
        try {
            conn.rename( dn, newRdn, deleteOldRdn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Synchronously renames an existing entry in the directory, using the
     * specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, boolean,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.rename(String, String, boolean,
            LDAPConstraints)</a>
     */
    public void rename(String dn,
                       String newRdn,
                       boolean deleteOldRdn,
                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.rename( dn, newRdn, deleteOldRdn,
                         cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Synchronously renames an existing entry in the directory, possibly
     * repositioning the entry in the directory tree.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, java.lang.String, boolean)">
            com.novell.ldap.LDAPConnection.rename(String, String, String,
            boolean)</a>
     */
    public void rename(String dn,
                       String newRdn,
                       String newParentdn,
                       boolean deleteOldRdn)
        throws LDAPException
    {
        try {
            conn.rename( dn, newRdn, newParentdn, deleteOldRdn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     *
     * Synchronously renames an existing entry in the directory, using the
     * specified constraints and possibly repositioning the entry in the
     * directory tree.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, java.lang.String,
            boolean, com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.rename(String, String, String,
            boolean, LDAPConstraints)</a>
     */
    public void rename(String dn,
                       String newRdn,
                       String newParentdn,
                       boolean deleteOldRdn,
                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.rename( dn, newRdn, newParentdn, deleteOldRdn,
                         cons.getWrappedObject());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Asynchronously renames an existing entry in the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, boolean,
            com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.rename(String, String, boolean,
            LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.rename( dn,
                                newRdn,
                                deleteOldRdn,
                                queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously renames an existing entry in the directory, using the
     * specified constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, boolean,
            com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.rename(String, String, boolean,
            LDAPResponseQueue, LDAPConstraints)</a>
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.rename( dn,
                                newRdn,
                                deleteOldRdn,
                                queue.getWrappedObject(),
                                cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously renames an existing entry in the directory, possibly
     * repositioning the entry in the directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, java.lang.String,
            boolean, com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPConnection.rename(String, String, String,
            boolean, LDAPResponseQueue)</a>
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    String newParentdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.rename( dn,
                                newRdn,
                                newParentdn,
                                deleteOldRdn,
                                queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously renames an existing entry in the directory, using the
     * specified constraints and possibily repositioning the entry in the
     * directory.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#rename(java.lang.String, java.lang.String, java.lang.String,
            boolean, com.novell.ldap.LDAPResponseQueue,
            com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPConnection.rename(String, String, String,
            boolean, LDAPResponseQueue, LDAPConstraints)</a>
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    String newParentdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseQueue(
                   conn.rename( dn,
                                newRdn,
                                newParentdn,
                                deleteOldRdn,
                                queue.getWrappedObject(),
                                cons.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously performs the search specified by the parameters.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(java.lang.String, int, java.lang.String,
            java.lang.String[], boolean)">
            com.novell.ldap.LDAPConnection.search(String, int, String,
            String[], boolean)</a>
     */
    public LDAPSearchResults search(String base,
                                    int scope,
                                    String filter,
                                    String[] attrs,
                                    boolean typesOnly)
        throws LDAPException
    {
        try {
            return new LDAPSearchResults(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously performs the search specified by the parameters,
     * using the specified search constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(java.lang.String, int, java.lang.String,
            java.lang.String[], boolean,
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.search(String, int, String,
            String[], boolean, LDAPSearchConstraints)</a>
     */
    public LDAPSearchResults search(String base,
                                    int scope,
                                    String filter,
                                    String[] attrs,
                                    boolean typesOnly,
                                    LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPSearchResults(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly,
                                         cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously performs the search specified by the parameters.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(java.lang.String, int, java.lang.String,
            java.lang.String[], boolean, com.novell.ldap.LDAPSearchQueue)">
            com.novell.ldap.LDAPConnection.search(String, int, String,
            String[], boolean, LDAPSearchQueue)</a>
     */
    public LDAPSearchQueue search(String base,
                                  int scope,
                                  String filter,
                                  String[] attrs,
                                  boolean typesOnly,
                                  LDAPSearchQueue queue)
        throws LDAPException
    {
        try {
            return new LDAPSearchQueue(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly,
                                         queue.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Asynchronously performs the search specified by the parameters,
     * also allowing specification of constraints for the search.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(java.lang.String, int, java.lang.String,
            java.lang.String[], boolean, com.novell.ldap.LDAPSearchQueue,
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.search(String, int, String,
            String[], boolean, LDAPSearchQueue, LDAPSearchConstraints)</a>
     */
    public LDAPSearchQueue search(String base,
                                  int scope,
                                  String filter,
                                  String[] attrs,
                                  boolean typesOnly,
                                  LDAPSearchQueue queue,
                                  LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPSearchQueue(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly,
                                         queue.getWrappedObject(),
                                         cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously performs the search specified by the LDAP URL, returning
     * an enumerable LDAPSearchResults object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(com.novell.ldap.LDAPUrl)">
            com.novell.ldap.LDAPConnection.search(LDAPUrl)</a>
     */
    public static LDAPSearchResults search(LDAPUrl toGet)
        throws LDAPException
    {
        try {
            return new LDAPSearchResults(
                        com.novell.ldap.LDAPConnection.search(
                                            toGet.getWrappedObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Synchronously perfoms the search specified by the LDAP URL, using
     * the specified search constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#search(com.novell.ldap.LDAPUrl,
            com.novell.ldap.LDAPSearchConstraints)">
            com.novell.ldap.LDAPConnection.search(LDAPUrl,
            LDAPSearchConstraints)</a>
     */
    public static LDAPSearchResults search(LDAPUrl toGet,
                                           LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPSearchResults(
                        com.novell.ldap.LDAPConnection.search(
                                            toGet.getWrappedObject(),
                                            cons.getWrappedSearchObject()));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Retrieves the schema associated with a particular schema DN in the
     * Directory server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#fetchSchema(java.lang.String)">
            com.novell.ldap.LDAPConnection.fetchSchema(String)</a>
     */
    public LDAPSchema fetchSchema(String schemaDN) throws LDAPException {
        try{
            return new LDAPSchema( conn.fetchSchema(schemaDN) );
        }
        catch (com.novell.ldap.LDAPException novellException){
            throw new LDAPException(novellException);
        }
    }

    /**
     * Retrieves the DN for the schema at the root DSE of the Directory Server.
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSchemaDN()">
            com.novell.ldap.LDAPConnection.getSchemaDN()</a>
     */
    public String getSchemaDN()
            throws LDAPException
    {
        try{
            return conn.getSchemaDN();
        }
        catch (com.novell.ldap.LDAPException novellException){
            throw new LDAPException(novellException);
        }
    }

    /**
     * Retrieves the DN of the schema associated with a particular entry in
     * the directory.
     * @see <a href="../../../../api/com/novell/ldap/LDAPConnection.html#getSchemaDN(java.lang.String)">
            com.novell.ldap.LDAPConnection.getSchemaDN(String)</a>
     */
    public String getSchemaDN(String entryDN)
            throws LDAPException
    {
        try{
            return conn.getSchemaDN(entryDN);
        }
        catch (com.novell.ldap.LDAPException novellException){
            throw new LDAPException(novellException);
        }
    }
}

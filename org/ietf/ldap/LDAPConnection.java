/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConnection.java,v 1.100 2001/05/29 19:24:57 vtag Exp $
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

package org.ietf.ldap;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 *  Represents the central class that encapsulates the connection
 *  to a directory server through the LDAP protocol.
 *
 * @see com.novell.ldap.LDAPConnection
 */
public class LDAPConnection implements Cloneable
{
    private com.novell.ldap.LDAPConnection conn;
    private java.util.Hashtable listeners = new Hashtable();
    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the base obect.
     *
     * @see com.novell.ldap.LDAPConnection#SCOPE_BASE
     */
    public static final int SCOPE_BASE =
                            com.novell.ldap.LDAPConnection.SCOPE_BASE;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the immediate subordinates of the base obect.
     *
     * @see com.novell.ldap.LDAPConnection#SCOPE_ONE
     */
    public static final int SCOPE_ONE =
                            com.novell.ldap.LDAPConnection.SCOPE_ONE;


    /**
     * Used with search to specify that the scope of entrys to search is to
     * search the base object and all entries within its subtree.
     *
     * @see com.novell.ldap.LDAPConnection#SCOPE_SUB
     */
    public static final int SCOPE_SUB =
                            com.novell.ldap.LDAPConnection.SCOPE_SUB;

    /**
     * Used with search instead of an attribute list to indicate that no
     * attributes are to be returned.
     *
     * @see com.novell.ldap.LDAPConnection#NO_ATTRS
     */
    public static final String NO_ATTRS = 
                            com.novell.ldap.LDAPConnection.NO_ATTRS;

    /**
     * Used with search instead of an attribute list to indicate that all
     * attributes are to be returned.
     *
     * @see com.novell.ldap.LDAPConnection#ALL_USER_ATTRS
     */
    public static final String ALL_USER_ATTRS =
                            com.novell.ldap.LDAPConnection.ALL_USER_ATTRS;

    /**
     * The default port number for LDAP servers.
     *
     * @see com.novell.ldap.LDAPConnection#DEFAULT_PORT
     */
    public static final int DEFAULT_PORT = 
                            com.novell.ldap.LDAPConnection.DEFAULT_PORT;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see com.novell.ldap.LDAPConnection#LDAP_PROPERTY_SDK
     */
    public static final String LDAP_PROPERTY_SDK = 
                            com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SDK;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see com.novell.ldap.LDAPConnection#LDAP_PROPERTY_PROTOCOL
     */
    public static final String LDAP_PROPERTY_PROTOCOL = 
                          com.novell.ldap.LDAPConnection.LDAP_PROPERTY_PROTOCOL;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * @see com.novell.ldap.LDAPConnection#LDAP_PROPERTY_SECURITY
     */
    public static final String LDAP_PROPERTY_SECURITY = 
                          com.novell.ldap.LDAPConnection.LDAP_PROPERTY_SECURITY;

    /**
     * Constructs a new LDAPConnection object, which represents a connection
     * to an LDAP server.
     *
     * @see com.novell.ldap.LDAPConnection#LDAPConnection()
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
                System.out.println("novell Factory class " + this + " wraps " + factory);
                this.factory = factory;
                return;
            }

            public LDAPSocketFactory getWrappedObject()
            {
                System.out.println("novell Factory class " + this + " returns " + factory);
                return factory;
            }
            
            public Socket makeSocket(String host, int port)
                      throws IOException, UnknownHostException
            {
                return factory.makeSocket( host, port);
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
     * @see com.novell.ldap.LDAPConnection#LDAPConnection(LDAPSocketFactory)
     */
    public LDAPConnection(LDAPSocketFactory factory)
    {
        conn = new com.novell.ldap.LDAPConnection( getSocketImpl(factory));
        return;
    }

    /**
     * Returns a copy of the object with a private context, but sharing the
     * network connection if there is one.
     *
     * @see com.novell.ldap.LDAPConnection#clone()
     */
    public Object clone()
    {
        return new LDAPConnection((com.novell.ldap.LDAPConnection)conn.clone());
    }

    /**
     * Closes the connection, if open, and releases any other resources held
     * by the object.
     *
     * @see com.novell.ldap.LDAPConnection#finalize()
     */
    public void finalize()
        throws LDAPException
    {
        try {
            conn.finalize();   
        } catch ( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Returns the protocol version uses to authenticate
     *
     * @see com.novell.ldap.LDAPConnection#getProtocolVersion()
     */
    public int getProtocolVersion()
    {
        return conn.getProtocolVersion();
    }

    /**
     * Returns the distinguished name (DN) used for authentication by this
     * object.
     *
     * @see com.novell.ldap.LDAPConnection#getAuthenticationDN()
     */
    public String getAuthenticationDN()
    {
        return conn.getAuthenticationDN();
    }

    /**
     * Returns the method used to authenticate the connection.
     *
     * @see com.novell.ldap.LDAPConnection#getAuthenticationMethod()
     */
    public String getAuthenticationMethod()
    {
        return conn.getAuthenticationMethod();
    }

    /**
     * Returns the properties if any specified on binding with a
     * SASL mechanism.
     *
     * @see com.novell.ldap.LDAPConnection#getAuthenticationMethod()
     */
    public Hashtable getSaslBindProperties()
    {
        return conn.getSaslBindProperties();
    }

    /**
     * Returns the call back handler if any specified on binding with a
     * SASL mechanism.
     *
     * @see com.novell.ldap.LDAPConnection#getSaslBindCallbackHandler()
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
     * @see com.novell.ldap.LDAPConnection#getConstraints()
     */
    public LDAPConstraints getConstraints()
    {
        return new LDAPSearchConstraints( conn.getSearchConstraints());
    }

    /**
     * Returns the host name of the LDAP server to which the object is or
     * was last connected, in the format originally specified.
     *
     * @see com.novell.ldap.LDAPConnection#getHost()
     */
    public String getHost()
    {
        return conn.getHost();
    }

    /**
     * Returns the stream used by the connection object for receiving data
     * from the LDAP server.
     *
     * @see com.novell.ldap.LDAPConnection#getInputStream()
     */
    public InputStream getInputStream()
    {
        return conn.getInputStream();
    }

    /**
     * Returns the stream used by the connection object to send data to the
     * LDAP server.
     *
     * @see com.novell.ldap.LDAPConnection#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        return conn.getOutputStream();
    }

    /**
     * Returns the port number of the LDAP server to which the object is or
     * was last connected.
     *
     * @see com.novell.ldap.LDAPConnection#getPort()
     */
    public int getPort()
    {
        return conn.getPort();
    }

    /**
     * Returns a property of a connection object.
     *
     * @see com.novell.ldap.LDAPConnection#getProperty(String)
     */
    public Object getProperty(String name)
        throws LDAPException
    {
        try {
            return conn.getProperty( name);
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
    }

    /**
     * Returns a copy of the set of search constraints associated with this
     * connection.
     *
     * @see com.novell.ldap.LDAPConnection#getSearchConstraints
     */
    public LDAPSearchConstraints getSearchConstraints()
    {
        return new LDAPSearchConstraints( conn.getSearchConstraints());
    }

    /**
     * Returns the LDAPSocketFactory used to establish this server connection.
     *
     * @see com.novell.ldap.LDAPConnection#getSocketFactory()
     */
    public LDAPSocketFactory getSocketFactory()
    {
        com.novell.ldap.LDAPSocketFactory factory = conn.getSocketFactory();
        if( factory == null) {
            return null;                            
        }
        return ((SocketFactoryWrapper)factory).getWrappedObject();
    }

    /**
     * Indicates whether the object has authenticated to the connected LDAP
     * server.
     *
     * @see com.novell.ldap.LDAPConnection#isBound()
     */
    public boolean isBound()
    {
        return conn.isBound();
    }

    /**
     * Indicates whether the connection represented by this object is open
     * at this time.
     *
     * @see com.novell.ldap.LDAPConnection#isConnected()
     */
    public boolean isConnected()
    {
        return conn.isConnected();
    }

    /**
     * Indicates if the connection uses TLS, i.e. startTLS has completed.
     *
     * @see com.novell.ldap.LDAPConnection#isTLS()
     */
    public boolean isTLS()
    {
        return conn.isTLS();
    }

    /**
     * Sets the constraints that apply to all operations performed through
     * this connection.
     *
     * @see com.novell.ldap.LDAPConnection#setConstraints(LDAPConstraints)
     */
    public void setConstraints(LDAPConstraints cons)
    {
        conn.setConstraints( cons.getWrappedObject());
        return;
    }

    /**
     * Sets the stream used by the connection object for receiving data from
     * the LDAP server.
     *
     * @see com.novell.ldap.LDAPConnection#setInputStream(InputStream)
     */
    public void setInputStream(InputStream stream)
    {
        conn.setInputStream(stream);
        return;
    }

    /**
     * Sets the stream used by the connection object to send data to the
     * LDAP server.
     *
     * @see com.novell.ldap.LDAPConnection#setOutputStream(OutputStream)
     */
    public void setOutputStream(OutputStream stream)
    {
        conn.setOutputStream(stream);
        return;
    }

    /**
     * Sets a property of a connection object.
     *
     * @see com.novell.ldap.LDAPConnection#setProperty(String,Object)
     */
    public void setProperty(String name, Object value)
        throws LDAPException
    {
        try {
            conn.setProperty( name, value);
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException(ex);
        }
        return;
    }

    /**
     * Establishes the default LDAPSocketFactory used when
     * LDAPConnection objects are constructed.
     *
     * @see com.novell.ldap.LDAPConnection#setSocketFactory(LDAPSocketFactory)
     */
    public static void setSocketFactory( LDAPSocketFactory factory)
    {
        com.novell.ldap.LDAPConnection.setSocketFactory(getSocketImpl(factory));
        return;
    }
    
    /**
     * Registers an object to be notified on arrival of an unsolicited
     * message from a server.
     *
     * @see com.novell.ldap.LDAPConnection#addUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener)
     */
    public void addUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener listener)
    {
        if (listener != null) {
            conn.addUnsolicitedNotificationListener( new UnsolImpl(listener));
        }
        return;
    }

    /**
     * Class to wrap an application's LDAPUnsolicitedNotificationListener
     */
    private class UnsolImpl
            implements com.novell.ldap.LDAPUnsolicitedNotificationListener
    {
        org.ietf.ldap.LDAPUnsolicitedNotificationListener listener;

        private UnsolImpl( org.ietf.ldap.LDAPUnsolicitedNotificationListener ul)
        {
            listener = ul;
            // Remember this association so we can do remove properly
            synchronized( listeners) {
                listeners.put( ul, this);
            }
            return;
        }

        public void messageReceived( com.novell.ldap.LDAPExtendedResponse msg)
        {
            listener.messageReceived( 
                    new LDAPExtendedResponse( msg));
            return;
        }

        private
        org.ietf.ldap.LDAPUnsolicitedNotificationListener getWrappedObject()
        {
            return listener;
        }
    }

    /**
     * Deregisters an object so that it will no longer be notified on
     * arrival of an unsolicited message from a server.
     *
     *@see com.novell.ldap.LDAPConnection#removeUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener)
     */
    public void removeUnsolicitedNotificationListener(
                        LDAPUnsolicitedNotificationListener listener)
    {

        com.novell.ldap.LDAPUnsolicitedNotificationListener ul = null;

        if (listener != null) {
            synchronized( listeners) {
                ul = (com.novell.ldap.LDAPUnsolicitedNotificationListener)
                        listeners.remove( listener);
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
     * @see com.novell.ldap.LDAPConnection#startTLS()
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
     * Notifies the server not to send additional results associated with
     * this LDAPSearchResults object, and discards any results already
     * received.
     *
     * @see com.novell.ldap.LDAPConnection#abandon(LDAPSearchResults)
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
     * @see com.novell.ldap.LDAPConnection#abandon(LDAPSearchResults,
                LDAPConstraints)
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
     * @see com.novell.ldap.LDAPConnection#abandon(int)
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
     *  Abandons a search operation for a listener, using the specified
     *  constraints.
     *
     * @see com.novell.ldap.LDAPConnection#abandon(int)
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
     * Abandons all search operations for a listener.
     *
     * @see com.novell.ldap.LDAPConnection#abandon(LDAPListener)
     */
    public void abandon( LDAPListener listener)
        throws LDAPException
    {
        try {
            if( listener instanceof LDAPResponseListener) {
              conn.abandon(((LDAPResponseListener)listener).getWrappedObject());
            } else
            if( listener instanceof LDAPSearchListener) {
              conn.abandon(((LDAPSearchListener)listener).getWrappedObject());
            }
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Abandons all search operations for a listener.
     *
     *@see com.novell.ldap.LDAPConnection#abandon(LDAPListener,LDAPConstraints)
     */
    public void abandon( LDAPListener listener, LDAPConstraints cons)
        throws LDAPException
    {
        try {
            if( listener instanceof LDAPResponseListener) {
              conn.abandon(((LDAPResponseListener)listener).getWrappedObject(),
                        cons.getWrappedObject());
            } else
            if( listener instanceof LDAPSearchListener) {
              conn.abandon(((LDAPSearchListener)listener).getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#add(LDAPEntry)
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
     * @see com.novell.ldap.LDAPConnection#add(LDAPEntry, LDAPConstraints)
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
     * @see com.novell.ldap.LDAPConnection#add(LDAPEntry, LDAPResponseListener)
     */
    public LDAPResponseListener add(LDAPEntry entry,
                                    LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                 conn.add( entry.getWrappedObject(),
                           listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#add(LDAPEntry,
                    LDAPResponseListener,LDAPConstraints)
     */
    public LDAPResponseListener add(LDAPEntry entry,
                                    LDAPResponseListener listener,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.add( entry.getWrappedObject(),
                             listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#bind(int,String,String)
     */
    public void bind(int version,
                     String dn,
                     String passwd)
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
     * @see com.novell.ldap.LDAPConnection#bind(
                        int,String,String,LDAPConstraints)
     */
    public void bind(int version,
                     String dn,
                     String passwd,
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
     * version, and listener.
     *
     * @see com.novell.ldap.LDAPConnection#bind(
                        int,String,String,LDAPResponseListener)
     */
    public LDAPResponseListener bind(int version,
                                     String dn,
                                     String passwd,
                                     LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                conn.bind( version, dn, passwd,
                           listener.getWrappedObject()));
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
     * version, listener, and constraints.
     *
     * @see com.novell.ldap.LDAPConnection#bind(
                        int,String,String,LDAPResponseListener,LDAPConstraints)
     */
    public LDAPResponseListener bind(int version,
                                     String dn,
                                     String passwd,
                                     LDAPResponseListener listener,
                                     LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                 conn.bind( version,
                            dn,
                            passwd,
                            listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#bind( String, Hashtable, Object)
     */
    public void bind(String dn,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh)
                     throws LDAPException
    {
        try {
            conn.bind( dn, props, cbh);
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
     * @see com.novell.ldap.LDAPConnection#bind( String, Hashtable, Object,
                    LDAPConstraints)
     */
    public void bind(String dn,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        try {
            conn.bind( dn, props, cbh,
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
     * @see com.novell.ldap.LDAPConnection#bind( String, String[],
                    Hashtable, Object)
     */
    public void bind(String dn,
                     String[] mechanisms,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh)

                     throws LDAPException
    {
        try {
            conn.bind( dn, mechanisms, props, cbh);
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
     * @see com.novell.ldap.LDAPConnection#bind( String, String[],
                    Hashtable, Object, LDAPConstraints)
     */
    public void bind(String dn,
                     String[] mechanisms,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        try {
            conn.bind( dn, mechanisms, props, cbh, cons.getWrappedObject());
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
     * @see com.novell.ldap.LDAPConnection#compare(String, LDAPAttribute)
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
     * @see com.novell.ldap.LDAPConnection#compare(String, LDAPAttribute,
                LDAPConstraints)
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
     * using the specified listener.
     * @see com.novell.ldap.LDAPConnection#compare(String, LDAPAttribute,
                LDAPResponseListener)
     */
    public LDAPResponseListener compare(String dn,
                                        LDAPAttribute attr,
                                        LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
             conn.compare( dn,
                           attr.getWrappedObject(),
                           listener.getWrappedObject()));
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
     * using the specified listener and contraints.
     *
     * @see com.novell.ldap.LDAPConnection#compare(String, LDAPAttribute,
                LDAPResponseListener, LDAPConstraints)
     */
    public LDAPResponseListener compare(String dn,
                                        LDAPAttribute attr,
                                        LDAPResponseListener listener,
                                        LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
               conn.compare( dn,
                             attr.getWrappedObject(),
                             listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#connect(String,int)
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
     * @see com.novell.ldap.LDAPConnection#delete(String)
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
     * @see com.novell.ldap.LDAPConnection#delete(String, LDAPConstraints)
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
     * from the directory and returns the results to the specified listener.
     *
     * @see com.novell.ldap.LDAPConnection#delete(String, LDAPResponseListener)
     */
    public LDAPResponseListener delete(String dn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
              conn.delete( dn,
                           listener.getWrappedObject()));
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
     * from the directory, using the specified contraints and listener.
     *
     * @see com.novell.ldap.LDAPConnection#delete(String, LDAPResponseListener,
                LDAPConstraints)
     */
    public LDAPResponseListener delete(String dn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                conn.delete( dn,
                             listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#disconnect()
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
     * @see com.novell.ldap.LDAPConnection#disconnect(LDAPConstraints)
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
     * @see com.novell.ldap.LDAPConnection#extendedOperation(
            LDAPExtendedOperation)
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
     * @see com.novell.ldap.LDAPConnection#extendedOperation(
            LDAPExtendedOperation, LDAPSearchConstraints)
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
     * @see com.novell.ldap.LDAPConnection#extendedOperation(
            LDAPExtendedOperation, LDAPResponseListener)
     */

    public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                     LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                    conn.extendedOperation(
                        op.getWrappedObject(),
                        listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#extendedOperation(
            LDAPExtendedOperation, LDAPSearchConstraints, LDAPResponseListener)
     */

    public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                                  LDAPSearchConstraints cons,
                                                  LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
              conn.extendedOperation( op.getWrappedObject(),
                                      cons.getWrappedSearchObject(),
                                      listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#getResponseControls()
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
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModification)
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
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModification,
            LDAPConstraints)
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
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModificationSet)
     */
    public void modify(String dn, LDAPModificationSet mods)
        throws LDAPException
    {
        try {
            conn.modify( dn, mods.getWrappedObject());
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
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModificationSet,
                LDAPConstraints)
     */
    public void modify(String dn,
                       LDAPModificationSet mods,
                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            conn.modify( dn, 
                         mods.getWrappedObject(),
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
     * Asynchronously makes a single change to an existing entry in the
     * directory.
     *
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModification,
                LDAPResponseListener)
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModification mod,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.modify( dn,
                                mod.getWrappedObject(),
                                listener.getWrappedObject()));
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
     * directory, using the specified constraints and listener.
     *
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModification,
                LDAPResponseListener)
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModification mod,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.modify( dn,
                                mod.getWrappedObject(),
                                listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModificationSet,
                LDAPResponseListener)
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModificationSet mods,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.modify( dn,
                                mods.getWrappedObject(),
                                listener.getWrappedObject()));
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
     * directory, using the specified constraints and listener.
     *
     * @see com.novell.ldap.LDAPConnection#modify(String, LDAPModificationSet,
                LDAPResponseListener, LDAPConstraints)
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModificationSet mods,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.modify( dn,
                                mods.getWrappedObject(),
                                listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#read(String)
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
     * @see com.novell.ldap.LDAPConnection#read(String)
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
     * @see com.novell.ldap.LDAPConnection#read(String, String[])
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
     * @see com.novell.ldap.LDAPConnection#read(String, String[],
                    LDAPSearchConstraints)
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
     * @see com.novell.ldap.LDAPConnection#read(LDAPUrl)
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
     * @see com.novell.ldap.LDAPConnection#read(LDAPUrl,
                    LDAPSearchConstraints)
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,boolean)
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,boolean,
                LDAPConstraints)
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,String,boolean)
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,String,boolean)
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,boolean,
                    LDAPResponseListener)
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.rename( dn,
                                newRdn,
                                deleteOldRdn,
                                listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,boolean,
                    LDAPResponseListener, LDAPConstraints)
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.rename( dn,
                                newRdn,
                                deleteOldRdn,
                                listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,String,boolean,
                    LDAPResponseListener)
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       String newParentdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.rename( dn,
                                newRdn,
                                newParentdn,
                                deleteOldRdn,
                                listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#rename(String,String,String,boolean,
                    LDAPResponseListener, LDAPConstraints)
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       String newParentdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPResponseListener(
                   conn.rename( dn,
                                newRdn,
                                newParentdn,
                                deleteOldRdn,
                                listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#search(String,int,String,
            String[],boolean)
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
     * @see com.novell.ldap.LDAPConnection#search(String,int,String,
            String[],boolean,LDAPSearchConstraints)
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
     * @see com.novell.ldap.LDAPConnection#search(String,int,String,
            String[],boolean,LDAPSearchListener)
     */
    public LDAPSearchListener search(String base,
                                     int scope,
                                     String filter,
                                     String[] attrs,
                                     boolean typesOnly,
                                     LDAPSearchListener listener)
        throws LDAPException
    {
        try {
            return new LDAPSearchListener(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly,
                                         listener.getWrappedObject()));
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
     * @see com.novell.ldap.LDAPConnection#search(String,int,String,
            String[],boolean,LDAPSearchListener,LDAPSearchConstraints)
     */
    public LDAPSearchListener search(String base,
                                     int scope,
                                     String filter,
                                     String[] attrs,
                                     boolean typesOnly,
                                     LDAPSearchListener listener,
                                     LDAPSearchConstraints cons)
        throws LDAPException
    {
        try {
            return new LDAPSearchListener(
                            conn.search( base,
                                         scope,
                                         filter,
                                         attrs,
                                         typesOnly,
                                         listener.getWrappedObject(),
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
     * @see com.novell.ldap.LDAPConnection#search(LDAPUrl)
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
     * @see com.novell.ldap.LDAPConnection#search(LDAPUrl,LDAPSearchConstraints)
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
}


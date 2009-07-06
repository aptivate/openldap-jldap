/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2003 Novell, Inc. All Rights Reserved.
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

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import com.novell.security.sasl.*;
import javax.security.auth.callback.CallbackHandler;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.*;
import com.novell.ldap.client.BindProperties;
import com.novell.ldap.client.Debug;
import com.novell.ldap.client.ReferralInfo;
import com.novell.ldap.rfc2251.RfcBindRequest;
import com.novell.ldap.rfc2251.RfcBindResponse;

//import com.novell.ldap.message.*;
import com.novell.ldap.resources.ExceptionMessages;

/**
 * The central class that encapsulates the connection
 * to a directory server through the LDAP protocol.
 * LDAPConnection objects are used to perform common LDAP
 * operations such as search, modify and add.
 *
 * <p>In addition, LDAPConnection objects allow you to bind to an
 * LDAP server, set connection and search constraints, and perform
 * several other tasks.
 *
 * <p>An LDAPConnection object is not connected on
 * construction and can only be connected to one server at one
 * port. Multiple threads may share this single connection, typically
 * by cloning the connection object, one for each thread. An
 * application may have more than one LDAPConnection object, connected
 * to the same or different directory servers.</p>
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/Search.java.html">Search.java</p>
 *
 */
public class LDAPConnection implements Cloneable
{
    private LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
    private LDAPControl[] responseCtls = null;

    // Synchronization Object used to synchronize access to responseCtls
    private Object responseCtlSemaphore = new Object();

    private Connection conn = null;

    private static Object nameLock = new Object(); // protect agentNum
    private static int lConnNum = 0;  // Debug, LDAPConnection number
    private String name;             // String name for debug

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the base obect.
     *
     *<p>SCOPE_BASE = 0</p>
     */
    public static final int SCOPE_BASE   = 0;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the immediate subordinates of the base obect.
     *
     *<p>SCOPE_ONE = 1</p>
     */
    public static final int SCOPE_ONE    = 1;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search the base object and all entries within its subtree.
     *
     *<p>SCOPE_ONE = 2</p>
     */
    public static final int SCOPE_SUB    = 2;
    
    /**
     * Used with search to specify that the scope of entries to search is to
     * search the subordinate subtree object and all entries within it.
     *
     *<p>SCOPE_SUBORDINATESUBTREE = 4</p>
     */
    public static final int SCOPE_SUBORDINATESUBTREE = 4;

    /**
     * Used with search instead of an attribute list to indicate that no
     * attributes are to be returned.
     *
     *<p>NO_ATTRS = "1.1"</p>
     */
    public static final String NO_ATTRS = "1.1";

    /**
     * Used with search instead of an attribute list to indicate that all
     * attributes are to be returned.
     *
     *<p>ALL_USER_ATTRS = "*"</p>
     */
    public static final String ALL_USER_ATTRS = "*";

    /**
     * Specifies the LDAPv3 protocol version when performing a bind operation.
     *
     * <p>Specifies LDAP version V3 of the protocol, and is specified
     * when performing bind operations.
     * <p>You can use this identifier in the version parameter
     * of the bind method to specify an LDAPv3 bind.
     * LDAP_V3 is the default protocol version</p>
     *
     *<p>LDAP_V3 = 3</p>
     *
     * @see #bind(int, String, byte[])
     * @see #bind(int, String, byte[], LDAPConstraints)
     * @see #bind(int, String, byte[], LDAPResponseQueue)
     * @see #bind(int, String, byte[], LDAPResponseQueue, LDAPConstraints)
     */
    public static final int LDAP_V3 = 3;

    /**
     * The default port number for LDAP servers.
     *
     * <p>You can use this identifier to specify the port when establishing
     * a clear text connection to a server.  This the default port.</p>
     *
     *<p>DEFAULT_PORT = 389</p>
     *
     * @see #connect(String, int)
     */
    public static final int DEFAULT_PORT = 389;


    /**
     * The default SSL port number for LDAP servers.
     *
     *<p>DEFAULT_SSL_PORT = 636</p>
     *
     * <p>You can use this identifier to specify the port when establishing
     * a an SSL connection to a server.</p>.
     */
    public static final int DEFAULT_SSL_PORT = 636;

    /**
     * A string that can be passed in to the getProperty method.
     *
     *<p>LDAP_PROPERTY_SDK = "version.sdk"</p>
     *
     * <p>You can use this string to request the version of the SDK</p>.
     */
    public static final String LDAP_PROPERTY_SDK = "version.sdk";

    /**
     * A string that can be passed in to the getProperty method.
     *
     *<p>LDAP_PROPERTY_PROTOCOL = "version.protocol"</p>
     *
     * <p>You can use this string to request the version of the
     * LDAP protocol</p>.
     */
    public static final String LDAP_PROPERTY_PROTOCOL = "version.protocol";

    /**
     * A string that can be passed in to the getProperty method.
     *
     *<p>LDAP_PROPERTY_SECURITY = "version.security"</p>
     *
     * <p>You can use this string to request the type of security
     * being used</p>.
     */
    public static final String LDAP_PROPERTY_SECURITY = "version.security";

    /**
     * A string that corresponds to the server shutdown notification OID.
     * This notification may be used by the server to advise the client that
     * the server is about to close the connection due to an error
     * condition.
     *
     *<p>SERVER_SHUTDOWN_OID = "1.3.6.1.4.1.1466.20036"
     */
    public static final String SERVER_SHUTDOWN_OID = "1.3.6.1.4.1.1466.20036";

    /**
     * The OID string that identifies a StartTLS request and response.
     */
    private static final String START_TLS_OID = "1.3.6.1.4.1.1466.20037";

    /*
     * Constructors
     */

    /**
     * Constructs a new LDAPConnection object, which represents a connection
     * to an LDAP server.
     *
     * <p>Calling the constructor does not actually establish the connection.
     * To connect to the LDAP server, use the connect method.</p>
     *
     * @see #connect(String, int)
     */
    public LDAPConnection()
    {
        this( null);
        return;
    }

    /**
     * Constructs a new LDAPConnection object, which will use the supplied
     * class factory to construct a socket connection during
     * LDAPConnection.connect method.
     *
     *  @param factory     An object capable of producing a Socket.
     *
     * @see #connect(String, int)
     * @see #getSocketFactory()
     * @see #setSocketFactory( LDAPSocketFactory)
     */
    public LDAPConnection(LDAPSocketFactory factory)
    {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConnection(" + ++lConnNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name + "Created");
        }
        conn = new Connection( factory );
        return;
    }

    /**
     * Constructs a new LDAPConnection object, which will use the supplied
     * timeout value to construct a socket connection during
     * LDAPConnection.connect method with the specified socket 
     * connect timeout value.
     *
     *  @param timeout     An object capable of producing a Socket 
     *                     with the specified Socket Connect timeout value.
     *
     * @see #getSocketTimeOut()
     * @see #setSocketTimeOut(int)
     */
    
    public LDAPConnection(int timeout)
    {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConnection(" + ++lConnNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name + "Created");
        }
        conn = new Connection( timeout );
        return;
    }
    /*
     * The following are methods that affect the operation of
     * LDAPConnection, but are not LDAP requests.
     */

    /**
     * Returns a copy of the object with a private context, but sharing the
     * network connection if there is one.
     *
     * <p>The network connection remains open until all clones have
     * disconnected or gone out of scope. Any connection opened after
     * cloning is private to the object making the connection.</p>
     *
     * <p>The clone can issue requests and freely modify options and search
     * constraints, and , without affecting the source object or other clones.
     * If the clone disconnects or reconnects, it is completely dissociated
     * from the source object and other clones. Reauthenticating in a clone,
     * however, is a global operation which will affect the source object
     * and all associated clones, because it applies to the single shared
     * physical connection. Any request by an associated object after one
     * has reauthenticated will carry the new identity.</p>
     *
     * @return A of the object.
     */
    public Object clone()
    {
        LDAPConnection newClone;
        Object newObj;
        try {
            newObj = super.clone();
            newClone = (LDAPConnection)newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
        newClone.conn = conn;   // same underlying connection
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "clone()");
        }

        //now just duplicate the defSearchCons and responseCtls
        if (defSearchCons != null){
            newClone.defSearchCons=(LDAPSearchConstraints)defSearchCons.clone();
        }
        else{
            newClone.defSearchCons = null;
        }
        if (responseCtls != null){
            newClone.responseCtls = new LDAPControl[responseCtls.length];
            for(int i=0; i < responseCtls.length; i++){
                newClone.responseCtls[i] = (LDAPControl)responseCtls[i].clone();
            }
        }
        else {
            newClone.responseCtls = null;
        }
        conn.incrCloneCount();     // Increment the count of clones
        return newObj;
    }

    /**
     * Closes the connection, if open, and releases any other resources held
     * by the object.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     *
     * @see #disconnect
     */
    protected void finalize()
        throws LDAPException
    {
        // Disconnect did not come from user API call
        disconnect(defSearchCons, false);
        return;
    }

    /**
     * Returns the protocol version uses to authenticate.
     *
     * <p> 0 is returned if no authentication has been performed.</p>
     *
     * @return The protol version used for authentication or 0
     * not authenticated.
     *
     * @see #bind( int, String, String)
     */
    public int getProtocolVersion()
    {
        BindProperties prop = conn.getBindProperties();
        if( prop == null) {
            return LDAP_V3;
        }
        return prop.getProtocolVersion();
    }

    /**
     * Returns the distinguished name (DN) used for as the bind name during
     * the last successful bind operation.  <code>null</code> is returned
     * if no authentication has been performed or if the bind resulted in
     * an aonymous connection.
     *
     * @return The distinguished name if authenticated; otherwise, null.
     *
     * @see #bind( String, String)
     * @see #isBound()
     */
    public String getAuthenticationDN()
    {
        BindProperties prop = conn.getBindProperties();
        if( prop == null) {
            return null;
        }
        if( prop.isAnonymous()) {
            return null;
        }
        return prop.getAuthenticationDN();
    }

    /**
     * Returns the method used to authenticate the connection. The return
     * value is one of the following:
     *
     *  <ul>
     *  <li>"none" indicates the connection is not authenticated.</li>
     *
     *
     *  <li>"simple" indicates simple authentication was used or that a null
     *                 or empty authentication DN was specified.</li>
     *
     *  <li>"sasl" indicates that a SASL mechanism was used to authenticate</li>
     *  </ul>
     *
     * @return The method used to authenticate the connection.
     */
    public String getAuthenticationMethod()
    {
        BindProperties prop = conn.getBindProperties();
        if( prop == null) {
            return "simple";
        }
        return conn.getBindProperties().getAuthenticationMethod();
    }

    /**
     * Returns the properties if any specified on binding with a
     * SASL mechanism.
     *
     * <p> Null is returned if no authentication has been performed
     * or no authentication Map is present.</p>
     *
     * @return The bind properties Map Object used for SASL bind or null if
     * the connection is not present or not authenticated.
     *
     * @see #bind( String, String, String[], Map, Object )
     */
    public Map getSaslBindProperties()
    {
        BindProperties prop = conn.getBindProperties();
        if( prop == null) {
            return null;
        }
        return conn.getBindProperties().getSaslBindProperties();
    }

    /**
     * Returns the call back handler if any specified on binding with a
     * SASL mechanism.
     *
     * <p> Null is returned if no authentication has been performed
     * or no authentication call back handler is present.</p>
     *
     * @return The call back handler used for SASL bind or null if the
     * object is not present or not authenticated.
     *
     * @see #bind( String, String, String[], Map, Object )
     */
    public Object /* javax.security.auth.callback.CallbackHandler */
                     getSaslBindCallbackHandler()
    {
        BindProperties prop = conn.getBindProperties();
        if( prop == null) {
            return null;
        }
        return conn.getBindProperties().getSaslCallbackHandler();
    }

    /**
     * Returns a copy of the set of constraints associated with this
     * connection. These constraints apply to all operations performed
     * through this connection (unless a different set of constraints is
     * specified when calling an operation method).
     *
     * @return The set of default contraints that apply to this connection.
     *
     * @see #setConstraints(LDAPConstraints)
     */
    public LDAPConstraints getConstraints()
    {
        return (LDAPConstraints)(this.defSearchCons).clone();
    }

    /**
     * Returns the host name of the LDAP server to which the object is or
     * was last connected, in the format originally specified.
     *
     * @return The host name of the LDAP server to which the object last
     * connected or null if the object has never connected.
     *
     * @see #connect( String, int)
     */
    public String getHost()
    {
        return conn.getHost();
    }

    /**
     * Returns the port number of the LDAP server to which the object is or
     * was last connected.
     *
     * @return The port number of the LDAP server to which the object last
     * connected or -1 if the object has never connected.
     *
     * @see #connect( String, int)
     */
    public int getPort()
    {
        return conn.getPort();
    }

    /**
     * Returns a property of a connection object.
     *
     *  @param name   Name of the property to be returned.
     *
     * <p>The following read-only properties are available
     *  for any given connection:</p>
     *  <ul>
     *  <li>LDAP_PROPERTY_SDK returns the version of this SDK,
     *                        as a Float data type.</li>
     *
     *  <li>LDAP_PROPERTY_PROTOCOL returns the highest supported version of
     *                          the LDAP protocol, as a Float data type.</li>
     *
     *  <li>LDAP_PROPERTY_SECURITY returns a comma-separated list of the
     *                             types of authentication supported, as a
     *                             string.
     *  </ul>
     *
     *  <p>A deep copy of the property is provided where applicable; a
     *  client does not need to clone the object received.</p>
     *
     *  @return The object associated with the requested property,
     *  or null if the property is not defined.
     *
     * @see LDAPConstraints#getProperty(String)
     * @see LDAPConstraints#setProperty(String, Object)
     */
    public Object getProperty(String name)
    {
        if (name.equalsIgnoreCase(LDAP_PROPERTY_SDK))
            return conn.sdk;
        else if (name.equalsIgnoreCase(LDAP_PROPERTY_PROTOCOL))
            return conn.protocol;
        else if (name.equalsIgnoreCase(LDAP_PROPERTY_SECURITY))
            return conn.security;
        else {
            return null;
        }
    }

    /**
     * Returns a copy of the set of search constraints associated with this
     * connection. These constraints apply to search operations performed
     * through this connection (unless a different set of
     * constraints is specified when calling the search operation method).
     *
     * @return The set of default search contraints that apply to
     * this connection.
     *
     * @see #setConstraints
     * @see #search( String, int, String, String[], boolean, LDAPSearchConstraints)
     */
    public LDAPSearchConstraints getSearchConstraints()
    {
        return (LDAPSearchConstraints)this.defSearchCons.clone();
    }

    /**
     * Returns the LDAPSocketFactory used to establish this server connection.
     *
     * @return The LDAPSocketFactory used to establish a connection.
     *
     * @see #LDAPConnection( LDAPSocketFactory)
     * @see #setSocketFactory( LDAPSocketFactory)
     */
    public LDAPSocketFactory getSocketFactory()
    {
        return conn.getSocketFactory();
    }

    /**
     * Indicates whether the object has authenticated to the connected LDAP
     * server.
     *
     * @return True if the object has authenticated; false if it has not
     * authenticated.
     *
     * @see #bind( String, String)
     */
    public boolean isBound()
    {
        return conn.isBound();
    }

    /**
     * Indicates whether the connection represented by this object is open
     * at this time.
     *
     * @return  True if connection is open; false if the connection is closed.
     */
    public boolean isConnected()
    {
        return conn.isConnected();
    }

    /**
     * Checks whether the connection represented by this object is still
     * alive or not.
     *
     * @return  True if connection is alive; false if the connection is closed.
     */
    public boolean isConnectionAlive()
    {
           return conn.isConnectionAlive();
    }
    /**
     * Indicatates if the connection is protected by TLS.
     *
     * @return If startTLS has completed this method returns true.
     * If stopTLS has completed or start tls failed, this method returns false.
     *
     * @return  True if the connection is protected by TLS.
     *
     * @see #startTLS
     * @see #stopTLS
     */
    public boolean isTLS()
    {
       return conn.isTLS();
    }

    /**
     * Returns the SocketTimeOut value set.
     *
     * @return Returns the SocketTimeOut value if set, else 0
     *
     *@see #setSocketTimeOut(int)
     */
    
    public int getSocketTimeOut()
    {
    	return conn.getSocketTimeOut();
    }

    /**
     * Sets the SocketTimeOut value.
     *
     *@see #getSocketTimeOut()
     */

    public void setSocketTimeOut(int timeout)
    {
    	conn.setSocketTimeOut(timeout);
    	return;
    }
	 
    /**
     * Sets the constraints that apply to all operations performed through
     * this connection (unless a different set of constraints is specified
     * when calling an operation method).  An LDAPSearchConstraints object
     * which is passed to this method sets all constraints, while an
     * LDAPConstraints object passed to this method sets only base constraints.
     *
     * @param cons  An LDAPConstraints or LDAPSearchConstraints Object
     * containing the contstraint values to set.
     *
     * @see #getConstraints()
     * @see #getSearchConstraints()
     */
    public void setConstraints(LDAPConstraints cons)
    {
        // Set all constraints, replace the object with a new one
        if( cons instanceof LDAPSearchConstraints) {
            defSearchCons = (LDAPSearchConstraints)cons.clone();
            return;
        }

        // We set the constraints this way, so a thread doesn't get an
        // conconsistant view of the referrals.
        LDAPSearchConstraints newCons =
                    (LDAPSearchConstraints)defSearchCons.clone();
        newCons.setHopLimit(cons.getHopLimit());
        newCons.setTimeLimit(cons.getTimeLimit());
        newCons.setReferralHandler(cons.getReferralHandler());
        newCons.setReferralFollowing(cons.getReferralFollowing());
        LDAPControl[] lsc = cons.getControls();
        if( lsc != null) {
            newCons.setControls( lsc);
        }
        Hashtable lp = newCons.getProperties();
        if( lp != null) {
            newCons.setProperties( lp);
        }
        defSearchCons = newCons;
        return;
    }

    /**
     * Establishes the default LDAPSocketFactory used when
     * LDAPConnection objects are constructed unless an
     * LDAPSocketFactory is specified in the LDAPConnection
     * object constructor.
     *
     * <p>This method sets the default LDAPSocketFactory used for
     * all subsequent LDAPConnection objects constructed.  If called
     * after LDAPConnection objects are created, those already created are not
     * affected even if they disconnect and establish a new connection.
     * It affects LDAPConnection objects only as they are constructed.</p>
     *
     *
     * <p>The following code snippet provides a typical usage example:
     * <pre><code>
     *   if (usingTLS) {
     *       LDAPConnection.setSocketFactory(myTLSFactory);
     *   }
     *   ...
     *   LDAPConnection conn = new LDAPConnection();
     *   conn.connect(myHost, myPort);
     * </code></pre></p>
     *
     * <p>In this example, connections are constructed with the default
     * LDAPSocketFactory.  At application start-up time, the default may be
     * set to use a particular provided TLS socket factory.</p>
     *
     * @param factory  A factory object which can construct socket
     *                 connections for an LDAPConnection.
     *
     * @see #LDAPConnection( LDAPSocketFactory)
     */
    public static void setSocketFactory( LDAPSocketFactory factory)
    {
        Connection.setSocketFactory( factory);
        return;
    }

    /**
     * Registers an object to be notified on arrival of an unsolicited
     * message from a server.
     *
     * <p>An unsolicited message has the ID 0. A new thread is created and
     * the method "messageReceived" in each registered object is called in
     * turn.</p>
     *
     *  @param listener  An object to be notified on arrival of an
     *         unsolicited message from a server.  This object must
     *           implement the LDAPUnsolicitedNotificationListener interface.
     *
     */
    public void addUnsolicitedNotificationListener(
            LDAPUnsolicitedNotificationListener listener)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
             "addUnsolicitedNOtificationListener()");
        }
        if (listener != null)
            conn.addUnsolicitedNotificationListener(listener);
    }



    /**
     * Deregisters an object so that it will no longer be notified on
     * arrival of an unsolicited message from a server. If the object is
     * null or was not previously registered for unsolicited notifications,
     * the method does nothing.
     *
     *
     *  @param listener  An object to no longer be notified on arrival of
     *                   an unsolicited message from a server.
     *
     */
    public void removeUnsolicitedNotificationListener(
                        LDAPUnsolicitedNotificationListener listener)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "removeUnsolicitedNOtificationListener()");
        }

        if (listener != null)
            conn.removeUnsolicitedNotificationListener(listener);
    }

    /**
     * Starts Transport Layer Security (TLS) protocol on this connection
     * to enable session privacy.
     *
     * <p>This affects the LDAPConnection object and all cloned objects. A
     * socket factory that implements LDAPTLSSocketFactory must be set on the
     * connection.</p>
     *
     * @exception LDAPException Thrown if TLS cannot be started.  If a
     * SocketFactory has been specified that does not implement
     * LDAPTLSSocketFactory an LDAPException is thrown.
     *
     * @see #isTLS
     * @see #stopTLS
     * @see #setSocketFactory
     */
    public void startTLS() throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name + "startTLS()");
        }


        LDAPMessage startTLS = makeExtendedOperation(
                new LDAPExtendedOperation( this.START_TLS_OID, null ), null);

        int tlsID = startTLS.getMessageID();

        conn.acquireWriteSemaphore( tlsID );
        try {
            if (!conn.areMessagesComplete()){
                throw new LDAPLocalException(
                        ExceptionMessages.OUTSTANDING_OPERATIONS,
                        LDAPException.OPERATIONS_ERROR );
            }

            // Stop reader when response to startTLS request received
            conn.stopReaderOnReply( tlsID );

            // send tls message
            LDAPResponseQueue queue =
                    sendRequestToServer(
                        startTLS,
                        defSearchCons.getTimeLimit(), null, null );

            LDAPExtendedResponse response =
                    (LDAPExtendedResponse) queue.getResponse();
            response.chkResultCode();

            conn.startTLS();
        }
        finally {
            //Free this semaphore no matter what exceptions get thrown
            conn.startReader();
            conn.freeWriteSemaphore( tlsID ) ;
        }
        return;
    }

    /**
     * Stops Transport Layer Security(TLS) on the LDAPConnection and reverts
     * back to an anonymous state.
     *
     * @throws LDAPException This can occur for the following reasons: <UL>
     *          <LI>StartTLS has not been called before stopTLS
     *          <LI>There exists outstanding messages that have not received all
     *          responses
     *          <LI>The sever was not able to support the operation</UL>
     *
     * <p>Note: The Sun and IBM implementions of JSSE do not currently allow
     * stopping TLS on an open Socket.  In order to produce the same results
     * this method currently disconnects the socket and reconnects, giving
     * the application an anonymous connection to the server, as required
     * by StopTLS.</p>
     *
     * @see #startTLS
     * @see #isTLS
     */
    public void stopTLS() throws LDAPException {

        if (!isTLS()){
            throw new LDAPLocalException(ExceptionMessages.NO_STARTTLS,
                    LDAPException.OPERATIONS_ERROR );
        }

        int semaphoreID = conn.acquireWriteSemaphore();
        try{
            if (!conn.areMessagesComplete()) {
                throw new LDAPLocalException(
                        ExceptionMessages.OUTSTANDING_OPERATIONS,
                        LDAPException.OPERATIONS_ERROR );
            }
            //stopTLS stops and starts the reader thread for us.
            conn.stopTLS();
        }
        finally {
            conn.freeWriteSemaphore(semaphoreID);
        }
        return;
    }
    //*************************************************************************
    // Below are all of the LDAP protocol operation methods
    //*************************************************************************

    //*************************************************************************
    // abandon methods
    //*************************************************************************

    /**
     *
     *
     * Notifies the server not to send additional results associated with
     * this LDAPSearchResults object, and discards any results already
     * received.
     *
     *  @param results   An object returned from a search.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     */
    public void abandon(LDAPSearchResults results)
        throws LDAPException
    {
        abandon( results, defSearchCons );
        return;
    }

    /**
     *
     *
     * Notifies the server not to send additional results associated with
     * this LDAPSearchResults object, and discards any results already
     * received.
     *
     *  @param results   An object returned from a search.
     *<br><br>
     *  @param cons     The contraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     */
    public void abandon(LDAPSearchResults results, LDAPConstraints cons)
        throws LDAPException
    {
        results.abandon();
        return;
    }

    /**
     *
     *  Abandons an asynchronous operation.
     *
     *  @param id      The ID of the asynchronous operation to abandon. The ID
     *                 can be obtained from the response queue for the
     *                 operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon(int id)
        throws LDAPException
    {
        abandon(id, defSearchCons);
        return;
    }

    /**
     *  Abandons an asynchronous operation, using the specified
     *  constraints.
     *
     *  @param id The ID of the asynchronous operation to abandon.
     *            The ID can be obtained from the search
     *            queue for the operation.
     *<br><br>
     *  @param cons The contraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon(int id, LDAPConstraints cons)
        throws LDAPException
    {
        // We need to inform the Message Agent which owns this messageID to
        // remove it from the queue.
        try {
            MessageAgent agent = conn.getMessageAgent(id);
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                "abandon(" + id + ")");
            }
            agent.abandon(id, cons);
            return;
        } catch( NoSuchFieldException ex) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                "abandon(" + id + "), agent not found");
            }
            return; // Ignore error
        }
    }

    /**
     * Abandons all outstanding operations managed by the queue.
     *
     * <p>All operations in progress, which are managed by the specified queue,
     * are abandoned.</p>
     *
     *  @param queue     The queue returned from an asynchronous request.
     *                   All outstanding operations managed by the queue
     *                   are abandoned, and the queue is emptied.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon( LDAPMessageQueue queue)
        throws LDAPException
    {
        abandon( queue, defSearchCons);
        return;
    }

    /**
     * Abandons all outstanding operations managed by the queue.
     *
     * <p>All operations in progress, which are managed by the specified
     * queue, are abandoned.</p>
     *
     *  @param queue     The queue returned from an asynchronous request.
     *                   All outstanding operations managed by the queue
     *                   are abandoned, and the queue is emptied.
     *<br><br>
     *  @param cons     The contraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon( LDAPMessageQueue queue, LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "abandon(queue)");
        }
        if(queue != null) {
            MessageAgent agent;
            if( queue instanceof LDAPSearchQueue) {
                agent = queue.getMessageAgent();
            } else {
                agent = queue.getMessageAgent();
            }
            int[] msgIds = agent.getMessageIDs();
            for(int i=0; i<msgIds.length; i++) {
                agent.abandon(msgIds[i], cons);
            }
        }
        return;
    }

    //*************************************************************************
    // add methods
    //*************************************************************************

    /**
     * Synchronously adds an entry to the directory.
     *
     * @param entry    LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void add(LDAPEntry entry)
        throws LDAPException
    {
        add(entry, defSearchCons);
        return;
    }

     /**
     *
     * Synchronously adds an entry to the directory, using the specified
     * constraints.
     *
     *  @param entry   LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.
     *<br><br>
     *  @param cons    Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public void add(LDAPEntry entry,
                    LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseQueue queue = add(entry, null, cons);

        // Get a handle to the add response
        LDAPResponse addResponse = (LDAPResponse)(queue.getResponse());

        // Set local copy of responseControls synchronously if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = addResponse.getControls();
        }
        chkResultCode( queue, cons, addResponse);
        return;
    }

    /**
     * Asynchronously adds an entry to the directory.
     *
     *  @param entry   LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.
     *<br><br>
     *  @param queue   Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 queue object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue add(LDAPEntry entry, LDAPResponseQueue queue)
        throws LDAPException
    {
        return add(entry, queue, defSearchCons);
    }

    /**
     * Asynchronously adds an entry to the directory, using the specified
     * constraints.
     *
     *  @param entry   LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.
     *<br><br>
     *  @param queue  Handler for messages returned from a server in
     *                response to this request. If it is null, a
     *                queue object is created internally.
     *<br><br>
     *  @param cons   Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue add(LDAPEntry entry,
                                 LDAPResponseQueue queue,
                                 LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "add()");
        }
        if(cons == null)
            cons = defSearchCons;

        // error check the parameters
        if(entry == null ) {
            throw new IllegalArgumentException("The LDAPEntry parameter" +
                        " cannot be null");
        }
        if( entry.getDN() == null) {
            throw new IllegalArgumentException("The DN value must be present" +
                        " in the LDAPEntry object");
        }

        LDAPMessage msg = new LDAPAddRequest( entry, cons.getControls());

        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    //*************************************************************************
    // bind methods
    //*************************************************************************

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) as an LDAPv3 bind, using the specified name and
     * password.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *                 Note: the application should use care in the use
     *                 of String password objects.  These are long lived
     *                 objects, and may expose a security risk, especially
     *                 in objects that are serialized.  The LDAPConnection
     *                 keeps no long lived instances of these objects.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @deprecated replaced by {@link #bind(int, String, byte[])}
     */
    public void bind(String dn,
                     String passwd)
        throws LDAPException
    {
        bind( LDAP_V3, dn, passwd, defSearchCons);
        return;
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password,
     * and LDAP version.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     *<br><br>
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *                 Note: the application should use care in the use
     *                 of String password objects.  These are long lived
     *                 objects, and may expose a security risk, especially
     *                 in objects that are serialized.  The LDAPConnection
     *                 keeps no long lived instances of these objects.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @deprecated replaced by {@link #bind(int, String, byte[])}
     */
    public void bind(int version,
                     String dn,
                     String passwd)
        throws LDAPException
    {
        bind( version, dn, passwd, defSearchCons);
        return;
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) as an LDAPv3 bind, using the specified name,
     * password, and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *                 Note: the application should use care in the use
     *                 of String password objects.  These are long lived
     *                 objects, and may expose a security risk, especially
     *                 in objects that are serialized.  The LDAPConnection
     *                 keeps no long lived instances of these objects.
     *<br><br>
     * @param cons     Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @deprecated replaced by {@link #bind(int, String, byte[], LDAPConstraints)}
     */
    public void bind(String dn,
                     String passwd,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        bind( LDAP_V3, dn, passwd, cons);
        return;
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP version,
     * and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     *<br><br>
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *                 Note: the application should use care in the use
     *                 of String password objects.  These are long lived
     *                 objects, and may expose a security risk, especially
     *                 in objects that are serialized.  The LDAPConnection
     *                 keeps no long lived instances of these objects.
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @deprecated replaced by
     * {@link #bind(int, String, byte[], LDAPConstraints)}
     */
    public void bind(int version,
                     String dn,
                     String passwd,
                     LDAPConstraints cons)
        throws LDAPException
    {
        byte[] pw = null;
        if( passwd != null) {
            try {
                pw = passwd.getBytes("UTF8");
                passwd = null;  // Keep no reference to String object
            } catch( UnsupportedEncodingException ex) {
                passwd = null;  // Keep no reference to String object
                throw new RuntimeException( ex.toString());
            }
        }
        bind(version, dn, pw, cons);
        return;
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password,
     * and LDAP version.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The version of the LDAP protocol to use
     *                  in the bind, use LDAP_V3.  LDAP_V2 is not supported.
     *<br><br>
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(int version,
                     String dn,
                     byte[] passwd)
        throws LDAPException
    {
        bind(version, dn, passwd, defSearchCons);
        return;
    }

    /**
     *
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP version,
     * and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     *<br><br>
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(int version,
                     String dn,
                     byte[] passwd,
                     LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseQueue queue =
            bind(version, dn, passwd, null, cons);
        LDAPResponse res = (LDAPResponse)queue.getResponse();
        if( res != null) {
            // Set local copy of responseControls synchronously if any
            synchronized (responseCtlSemaphore) {
                responseCtls = res.getControls();
            }

            chkResultCode( queue, cons, res);
        }
        return;
    }

    /**
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, and queue.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     * <br><br>
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *  @param queue   Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 queue object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue bind(int version,
                                  String dn,
                                  byte[] passwd,
                                  LDAPResponseQueue queue)
        throws LDAPException
    {
        return bind(version, dn, passwd, queue, defSearchCons);
    }

    /**
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, queue, and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * had already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     * <br><br>
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *<br><br>
     *  @param queue   Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 queue object is created internally.
     *<br><br>
     *  @param cons      Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue bind(int version,
                                  String dn,
                                  byte[] passwd,
                                  LDAPResponseQueue queue,
                                  LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "bind(\"" + dn + "\")");
        }
        int msgId;
        BindProperties bindProps;
        if(cons == null)
            cons = defSearchCons;

        boolean anonymous = false;
        if(dn == null || dn.length()==0) {
            dn = "";
            anonymous = true; // anonymous, passwd length zero with simple bind
            passwd = null;
        } else {
            dn = dn.trim();
        }

        if(passwd == null)
            passwd = new byte[] {};

        
        if( passwd.length == 0) {
            anonymous = true; // anonymous, passwd length zero with simple bind
            dn = "";          // set to null if anonymous
        }
        LDAPMessage msg = new LDAPBindRequest( version, dn, passwd, cons.getControls());

        msgId = msg.getMessageID();
        bindProps = new BindProperties( version, dn, "simple",
                                        anonymous, null, null);

        // For bind requests, if not connected, attempt to reconnect
        if( ! conn.isConnected()) {
            if( conn.getHost() != null) {
                conn.connect( conn.getHost(), conn.getPort());
            } else {
                throw new LDAPException( ExceptionMessages.CONNECTION_IMPOSSIBLE,
                    LDAPException.CONNECT_ERROR, null);
            }
        }

        // The semaphore is released when the bind response is queued.
        conn.acquireWriteSemaphore( msgId);

        return sendRequestToServer( msg,cons.getTimeLimit(), queue, bindProps);
    }

    //*************************************************************************
    // SASL bind methods
    //*************************************************************************

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * <p>If none of the requested SASL mechanisms is available, an
     * exception is thrown.  If the object has been disconnected from an
     * LDAP server, this method attempts to reconnect to the server. If the
     * object has already authenticated, the old authentication is
     * discarded. If mechanisms is null, or if the first version of the
     * method is called, the LDAP server will be interrogated for its
     * supportedSaslMechanisms attribute of its root DSE. See RFC 2251 for a
     * discussion of the SASL classes. </p>
     *
     *
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *<br><br>
     *  @param authzId  If not null and not empty, specifies an LDAP authzId to
     *                  pass to the SASL layer.  If null or empty, the authzId
     *                  will be treated as an empty string and processed
     *                  as per RFC 2222.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the SASL client
     *                  implementation to obtain additional information
     *                  required, such as additional credentials.
     *                  If cbh is not of type
     *                  javax.security.auth.callback.CallbackHandler, a
     *                  RuntimeException will be thrown.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(String dn,
                     String authzId,
                     Map props,
                     Object cbh)/*javax.security.auth.callback.CallbackHandler*/
                     throws LDAPException
    {
        bind( dn, authzId, props, cbh, defSearchCons);
        return;
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * <p>If none of the requested SASL mechanisms is available, an
     * exception is thrown.  If the object has been disconnected from an
     * LDAP server, this method attempts to reconnect to the server. If the
     * object has already authenticated, the old authentication is
     * discarded. If mechanisms is null, or if the first version of the
     * method is called, the LDAP server will be interrogated for its
     * supportedSaslMechanisms attribute of its root DSE. See RFC 2251 for a
     * discussion of the SASL classes. </p>
     *
     *
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *<br><br>
     *  @param authzId  If not null and not empty, specifies an LDAP authzId to
     *                  pass to the SASL layer.  If null or empty, the authzId
     *                  will be treated as an empty string and processed
     *                  as per RFC 2222.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the SASL client
     *                  implementation to obtain additional information
     *                  required, such as additional credentials.
     *                  If cbh is not of type
     *                  javax.security.auth.callback.CallbackHandler, a
     *                  RuntimeException will be thrown.
     *<br><br>
     *  @param cons     Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(String dn,
                     String authzId,
                     Map props,
                     Object cbh,/*javax.security.auth.callback.CallbackHandler*/
                     LDAPConstraints cons)
                     throws LDAPException
    {
        //"LDAPConnection.bind(with mechanisms) is not Implemented."
        throw new LDAPLocalException(ExceptionMessages.NOT_IMPLEMENTED,
                new Object[] {"LDAPConnection.bind(with mechanisms)"},
                LDAPException.LDAP_NOT_SUPPORTED);
    }

    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * <p>If none of the requested SASL mechanisms is available, an
     * exception is thrown.  If the object has been disconnected from an
     * LDAP server, this method attempts to reconnect to the server. If the
     * object has already authenticated, the old authentication is
     * discarded. If mechanisms is null, or if the first version of the
     * method is called, the LDAP server will be interrogated for its
     * supportedSaslMechanisms attribute of its root DSE. See RFC 2251 for a
     * discussion of the SASL classes. </p>
     *
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *  @param authzId  If not null and not empty, specifies an LDAP authzId to
     *                  pass to the SASL layer.  If null or empty, the authzId
     *                  will be treated as an empty string and processed
     *                  as per RFC 2222.
     *<br><br>
     *  @param mechanisms    An array of IANA-registered SASL mechanisms which
     *                       the client is willing to use for authentication.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the SASL client
     *                  implementation to obtain additional information
     *                  required, such as additional credentials.
     *                  If cbh is not of type
     *                  javax.security.auth.callback.CallbackHandler, a
     *                  RuntimeException will be thrown.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(String dn,
                     String authzId,
                     String[] mechanisms,
                     Map props,
                     Object cbh)/*javax.security.auth.callback.CallbackHandler*/
                     throws LDAPException
    {
        bind( dn, authzId, mechanisms, props, cbh, defSearchCons);
        return;
    }
    /**
     * Synchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name and the specified set of
     * mechanisms.
     *
     * <p>If none of the requested SASL mechanisms is available, an
     * exception is thrown.  If the object has been disconnected from an
     * LDAP server, this method attempts to reconnect to the server. If the
     * object has already authenticated, the old authentication is
     * discarded. If mechanisms is null, or if the first version of the
     * method is called, the LDAP server will be interrogated for its
     * supportedSaslMechanisms attribute of its root DSE. See RFC 2251 for a
     * discussion of the SASL classes. </p>
     *
     *  @param dn       If non-null and non-empty, specifies that the
     *                  connection and all operations through it should
     *                  be authenticated with dn as the distinguished
     *                  name.
     *  @param authzId  If not null and not empty, specifies an LDAP authzId to
     *                  pass to the SASL layer.  If null or empty, the authzId
     *                  will be treated as an empty string and processed
     *                  as per RFC 2222.
     *<br><br>
     *  @param mechanisms    An array of IANA-registered SASL mechanisms which
     *                       the client is willing to use for authentication.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the SASL client
     *                  implementation to obtain additional information
     *                  required, such as additional credentials.
     *                  If cbh is not of type
     *                  javax.security.auth.callback.CallbackHandler, a
     *                  RuntimeException will be thrown.
     *<br><br>
     *  @param cons     Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void bind(String dn,
                     String authzId,
                     String[] mechanisms,
                     Map props,
                     Object cbh,/*javax.security.auth.callback.CallbackHandler*/
                     LDAPConstraints cons)
                     throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "saslBind(" + dn + ")");
        }

        if(cons == null)
            cons = defSearchCons;

        if(dn == null)
            dn = "";

        if (authzId == null)
            authzId = "";

        try
        {
            SaslClient saslClient = Sasl.createSaslClient(mechanisms,
                                                     authzId,
                                                     "ldap",
                                                     this.getHost(),
                                                     props,
                                                     (CallbackHandler)cbh);
            if (saslClient == null)
            {
                throw new LDAPException(
                      "Unsupported SASL mechanism(s) and/or properties",
                      LDAPException.AUTH_METHOD_NOT_SUPPORTED, null);
            }


            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.saslBind, name +
                    "saslClient created, mechanism " +
                    saslClient.getMechanismName());
            }

            byte[]  clientResponse = null;
            boolean anonymous = false;
            BindProperties bindProps = new BindProperties(LDAP_V3,
                                                         null,
                                                         "sasl",
                                                         anonymous,
                                                         null,
                                                         null);

            // Acquire the bind semaphore and communicate its value to
            // the corresponding Connection class.
            int id = conn.acquireWriteSemaphore();
            conn.setBindSemId( id);

            if (saslClient.hasInitialResponse())
            {
                clientResponse = saslClient.evaluateChallenge(new
                                                              byte[0]);
            }
            while(!saslClient.isComplete())
            {
                try
                {
                    byte[] replyBuf = LDAPTransport(
                                          clientResponse,
                                          saslClient.getMechanismName(),
                                          bindProps);

                    if(replyBuf!=null)
                    {
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.saslBind, name +
                                "saslClient response " + replyBuf.length +
                                " bytes");
                        }
                        clientResponse = saslClient.evaluateChallenge(
                                                             replyBuf);
                    }
                    else
                    {
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.saslBind, name +
                                "saslClient response empty");
                        }
                        clientResponse = saslClient.evaluateChallenge(
                                                          new byte[0]);
                    }
                }
                catch(SaslException e)
                {
                    // This call will free any client resources.
                    saslClient.dispose();
                    throw new LDAPException("Unexpected SASL error.",
                                             LDAPException.OTHER, null, e);
                }
                catch(LDAPException lde)
                {
                    // This call will free any client resources.
                    saslClient.dispose();
                    throw lde;
                }
            }
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.saslBind, name + "saslBind Complete");
            }
        }
        catch (SaslException eSasl)
        {
            throw new LDAPException("SASL Bind Error.",
                                     LDAPException.OTHER,
                                     null,
                                     eSasl.getCause());
        }

        //LDAP Bind complete
        return;
    }

    /**
     * Implements a LDAP Transport method which recives requests from the
     * saslClient and send Data to the LDAP server and returns the response
     * back to the saslClient
     *
     * @param toWrite   request received from saslClient in a byte array
     * @param mechanism Name of the saslClient  Mechanism
     * @param bindProps Bindproperties
     *
     * @return  Response reived from the LDAP server in a byte array
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    private byte[] LDAPTransport( byte[] toWrite,
                                  String mechanism,
                                  BindProperties bindProps)
            throws LDAPException
    {

        LDAPMessage     msg;
        RfcBindResponse bindResponse;
        LDAPConstraints cons;
        ASN1OctetString serverCreds;

        cons = defSearchCons;
        msg = new LDAPMessage(LDAPMessage.BIND_REQUEST,
                               new RfcBindRequest(LDAP_V3,
                                                  "",
                                                  mechanism,
                                                  toWrite),
                               cons.getControls());

        LDAPResponseQueue queue = sendRequestToServer(msg,
                                                     cons.getTimeLimit(),
                                                     null,
                                                     bindProps);
        LDAPResponse ldapResponse = (LDAPResponse)queue.getResponse();
        bindResponse = (RfcBindResponse)ldapResponse.getASN1Object().get(1);
        serverCreds = bindResponse.getServerSaslCreds();
        int resultCode = ldapResponse.getResultCode();

        byte[] replyBuf = null;
        if ((resultCode == LDAPException.SASL_BIND_IN_PROGRESS) ||
            (resultCode == LDAPException.SUCCESS))
        {
            if (serverCreds != null)
            {
                replyBuf = serverCreds.byteValue();
            }
        }
        else {
           ldapResponse.chkResultCode();
           throw new LDAPException("SASL Bind Error.",
                                    resultCode,
                                    null);
        }
        return replyBuf;
    }

    //*************************************************************************
    // compare methods
    //*************************************************************************

    /**
     *
     * Synchronously checks to see if an entry contains an attribute
     * with a specified value.
     *
     *  @param dn      The distinguished name of the entry to use in the
     *                 comparison.
     *<br><br>
     *  @param attr    The attribute to compare against the entry. The
     *                 method checks to see if the entry has an
     *                 attribute with the same name and value as this
     *                 attribute.
     *
     *  @return True if the entry has the value,
     *          and false if the entry does not
     *          have the value or the attribute.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public boolean compare(String dn,
                           LDAPAttribute attr)
        throws LDAPException
    {
        return compare(dn, attr, defSearchCons);
    }

    /**
     *
     * Synchronously checks to see if an entry contains an attribute with a
     * specified value, using the specified constraints.
     *
     *  @param dn      The distinguished name of the entry to use in the
     *                 comparison.
     *<br><br>
     *  @param attr    The attribute to compare against the entry. The
     *                 method checks to see if the entry has an
     *                 attribute with the same name and value as this
     *                 attribute.
     *<br><br>
     *  @param cons    Constraints specific to the operation.
     *
     *  @return True if the entry has the value,
     *          and false if the entry does not
     *          have the value or the attribute.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     */
    public boolean compare(String dn,
                           LDAPAttribute attr,
                           LDAPConstraints cons)
       throws LDAPException
    {
        boolean ret = false;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "compare(" + dn + ") if value");
        }
        LDAPResponseQueue queue = compare(dn, attr, null, cons);

        LDAPResponse res = (LDAPResponse)queue.getResponse();

        // Set local copy of responseControls synchronously - if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = res.getControls();
        }

        if(res.getResultCode() == LDAPException.COMPARE_TRUE) {
            ret = true;
        } else
        if(res.getResultCode() == LDAPException.COMPARE_FALSE) {
            ret = false;
        } else {
            chkResultCode( queue, cons, res);
        }
        return ret;
    }

    /**
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified queue.
     * <p>
     * Please note that a successful completion of this command results in
     * one of two status codes: LDAPException.COMPARE_TRUE if the entry
     * has the value, and LDAPException.COMPARE_FALSE if the entry
     * does not have the value or the attribute.
     *<br><br>
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.
     *<br><br>
     *  @param attr    An attribute to compare.
     *<br><br>
     *  @param queue   The queue for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    queue object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @see LDAPException#COMPARE_TRUE
     * @see LDAPException#COMPARE_FALSE
     */
    public LDAPResponseQueue compare(String dn,
                                        LDAPAttribute attr,
                                        LDAPResponseQueue queue)
        throws LDAPException
    {
        return compare(dn, attr, queue, defSearchCons);
    }

    /**
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified queue and contraints.
     * <p>
     * Please note that a successful completion of this command results in
     * one of two status codes: LDAPException.COMPARE_TRUE if the entry
     * has the value, and LDAPException.COMPARE_FALSE if the entry
     * does not have the value or the attribute.
     *<br><br>
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.
     *<br><br>
     *  @param attr    An attribute to compare.
     *<br><br>
     *  @param queue     Handler for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   queue object is created internally.
     *<br><br>
     *  @param cons      Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     * @see LDAPException#COMPARE_TRUE
     * @see LDAPException#COMPARE_FALSE
     */
    public LDAPResponseQueue compare(String dn,
                                     LDAPAttribute attr,
                                     LDAPResponseQueue queue,
                                     LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "compare(" + dn + ") compare value");
        }

        if( attr.size() != 1) {
            throw new IllegalArgumentException("compare: Exactly one value " +
                    "must be present in the LDAPAttribute");
        }

        if(dn == null ) {
            // Invalid parameter
            throw new IllegalArgumentException("compare: DN cannot be null");
        }

        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPCompareRequest( dn,
                                                  attr.getName(),
                                                  attr.getByteValue(),
                                                  cons.getControls());

        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    //*************************************************************************
    // connect methods
    //*************************************************************************

    /**
     *
     *  Connects to the specified host and port.
     *
     *  <p>If this LDAPConnection object represents an open connection, the
     *  connection is closed first before the new connection is opened.
     *  At this point, there is no authentication, and any operations are
     *  conducted as an anonymous client.</p>
     *
     *  <p> When more than one host name is specified, each host is contacted
     *  in turn until a connection can be established.</p>
     *
     *  @param host A host name or a dotted string representing the IP address
     *              of a host running an LDAP server. It may also
     *              contain a list of host names, space-delimited. Each host
     *              name can include a trailing colon and port number.
     *<br><br>
     *  @param port The TCP or UDP port number to connect to or contact.
     *              The default LDAP port is 389. The port parameter is
     *              ignored for any host hame which includes a colon and
     *              port number.
     *<br><br>
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     *
     */
    public void connect(String host, int port)
        throws LDAPException
    {
        // connect doesn't affect other clones
        // If not a clone, destroys old connection.
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "connect(" + host + ", " + port + ")");
        }

        // Step through the space-delimited list
        StringTokenizer hostList = new StringTokenizer(host," ");
        String address = null;

        int specifiedPort;
		  int bracketIndex; //Specific to IPv6 - after ']' is colon followed by port
        int colonIndex; //after the colon is the port
        while (hostList.hasMoreTokens()) {
            try{
                specifiedPort=port;
                address = hostList.nextToken();
					 bracketIndex = address.indexOf(']');
					 if(bracketIndex == -1)
					 {
                	colonIndex = address.indexOf((int)':');  //IPv4
					 }
					 else
					 {
						colonIndex = address.indexOf((int)':', bracketIndex); //IPv6
					 }
                if (colonIndex != -1 && colonIndex+1 != address.length()){
                    //parse Port out of address
                    try{
                        specifiedPort = Integer.parseInt(
                                    address.substring(colonIndex+1));
                        address =   address.substring(0, colonIndex);
                    }catch (Exception e){
                          throw new IllegalArgumentException(
                                     ExceptionMessages.INVALID_ADDRESS);
                    }
                }
                // This may return a different conn object
                // Disassociate this clone with the underlying connection.
                conn = conn.destroyClone( true);
                conn.connect( address, specifiedPort);
                break;
            }catch (LDAPException LE){
                if (!hostList.hasMoreTokens())
                    throw LE;
            }
        }
        return;
    }

    //*************************************************************************
    // delete methods
    //*************************************************************************

    /**
     *
     * Synchronously deletes the entry with the specified distinguished name
     * from the directory.
     *
     * <p>Note: A Delete operation will not remove an entry that contains
     * subordinate entries, nor will it dereference alias entries. </p>
     *
     *  @param dn      The distinguished name of the entry to delete.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void delete(String dn)
        throws LDAPException
    {
        delete(dn, defSearchCons);
        return;
    }


    /**
     * Synchronously deletes the entry with the specified distinguished name
     * from the directory, using the specified constraints.
     *
     * <p>Note: A Delete operation will not remove an entry that contains
     * subordinate entries, nor will it dereference alias entries. </p>
     *
     *  @param dn      The distinguished name of the entry to delete.
     *<br><br>
     *  @param cons    Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     */
    public void delete(String dn, LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseQueue queue =
            delete(dn, null, cons);

        // Get a handle to the delete response
        LDAPResponse deleteResponse = (LDAPResponse)(queue.getResponse());

        // Set local copy of responseControls synchronously - if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = deleteResponse.getControls();
        }
        chkResultCode( queue, cons, deleteResponse);
        return;
    }

    /**
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory and returns the results to the specified queue.
     *
     * <p>Note: A Delete operation will not remove an entry that contains
     * subordinate entries, nor will it dereference alias entries. </p>
     *
     *  @param dn      The distinguished name of the entry to modify.
     *<br><br>
     *  @param queue     The queue for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   queue object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public LDAPResponseQueue delete(String dn, LDAPResponseQueue queue)
        throws LDAPException
    {
        return delete(dn, queue, defSearchCons);
    }

    /**
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory, using the specified contraints and queue.
     *
     * <p>Note: A Delete operation will not remove an entry that contains
     * subordinate entries, nor will it dereference alias entries. </p>
     *
     *  @param dn      The distinguished name of the entry to delete.
     *<br><br>
     *  @param queue      The queue for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    queue object is created internally.
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public LDAPResponseQueue delete(String dn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "delete(" + dn + ")");
        }
        if(dn == null) {
            // Invalid DN parameter
            throw new IllegalArgumentException(
                                     ExceptionMessages.DN_PARAM_ERROR);
        }

        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPDeleteRequest( dn, cons.getControls());

        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    //*************************************************************************
    // disconnect method
    //*************************************************************************

    /**
     *
     * Synchronously disconnects from the LDAP server.
     *
     * <p>Before the object can perform LDAP operations again, it must
     * reconnect to the server by calling connect.</p>
     *
     * <p>The disconnect method abandons any outstanding requests, issues an
     * unbind request to the server, and then closes the socket.</p>
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public void disconnect()
        throws LDAPException
    {
        // disconnect from API call
        disconnect( defSearchCons, true);
        return;
    }

    /**
     * Synchronously disconnects from the LDAP server.
     *
     * <p>Before the object can perform LDAP operations again, it must
     * reconnect to the server by calling connect.</p>
     *
     * <p>The disconnect method abandons any outstanding requests, issues an
     * unbind request to the server, and then closes the socket.</p>
     *
     * @param cons LDPConstraints to be set with the unbind request
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void disconnect( LDAPConstraints cons)
        throws LDAPException
    {
        // disconnect from API call
        disconnect(cons, true);
        return;
    }

    /**
     * Synchronously disconnect from the server
     *
     * @param how true if application call disconnect API, false if finalize.
     */
    private void disconnect(LDAPConstraints cons, boolean how)
        throws LDAPException
    {
        // disconnect doesn't affect other clones
        // If not a clone, distroys connection
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            (how?"disconnect()":"finalize()"));
        }
        conn = conn.destroyClone(how);
        return;
    }

    //*************************************************************************
    // extendedOperation methods
    //*************************************************************************

    /**
     * Provides a synchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to and (2)
     *            an operation-specific sequence of octet strings
     *            or BER-encoded values.
     *
     * @return An operation-specific object, containing an ID and either an octet
     * string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op)
        throws LDAPException
    {
        return extendedOperation(op, defSearchCons);
    }

    /*
     *  Synchronous LDAP extended request with SearchConstraints
     */

     /**
     *
     * Provides a synchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to and (2) an
     *            operation-specific sequence of octet strings
     *            or BER-encoded values.
     *<br><br>
     * @param cons The constraints specific to the operation.
     *
     * @return An operation-specific object, containing an ID and either an
     * octet string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op,
                                                  LDAPConstraints cons)
        throws LDAPException
    {

        // Call asynchronous API and get back handler to reponse queue
        LDAPResponseQueue queue = extendedOperation(op, cons, null);
        LDAPExtendedResponse response =
                            (LDAPExtendedResponse) queue.getResponse();

        // Set local copy of responseControls synchronously - if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = response.getControls();
        }

        chkResultCode( queue, cons, response);
        return response;
    }


    /*
     * Asynchronous LDAP extended request
     */

    /**
     * Provides an asynchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to and (2) an
     *            operation-specific sequence of octet strings
     *            or BER-encoded values.
     *<br><br>
     * @param queue     The queue for messages returned from a server in
     *                  response to this request. If it is null, a queue
     *                  object is created internally.
     *
     * @return An operation-specific object, containing an ID and either an octet
     * string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPResponseQueue extendedOperation(LDAPExtendedOperation op,
                                     LDAPResponseQueue queue)
        throws LDAPException
    {

        return extendedOperation(op, defSearchCons, queue);
    }


    /*
     *  Asynchronous LDAP extended request with SearchConstraints
     */

    /**
     * Provides an asynchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to and (2) an operation-
     *         specific sequence of octet strings or BER-encoded values.
     *<br><br>
     * @param queue     The queue for messages returned from a server in
     *                  response to this request. If it is null, a queue
     *                  object is created internally.
     *<br><br>
     * @param cons      The constraints specific to this operation.
     *
     * @return An operation-specific object, containing an ID and either an
     * octet string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPResponseQueue extendedOperation(LDAPExtendedOperation op,
                                               LDAPConstraints cons,
                                               LDAPResponseQueue queue)
        throws LDAPException
    {
        // Use default constraints if none-specified
        if(cons == null)
            cons = defSearchCons;
        LDAPMessage msg = makeExtendedOperation(op, cons);
        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    /**
     * Formulates the extended operation, constraints into an
     * LDAPMessage and returns the LDAPMessage.  This is used by
     * extendedOperation and startTLS which needs the LDAPMessage to
     * get the MessageID.
     */
    protected LDAPMessage makeExtendedOperation(LDAPExtendedOperation op,
                                                LDAPConstraints cons)
        throws LDAPException
    {
        // Use default constraints if none-specified
        if(cons == null)
            cons = defSearchCons;

        // error check the parameters
        if (op.getID() == null) {
            // Invalid extended operation parameter, no OID specified
            throw new IllegalArgumentException(
                                     ExceptionMessages.OP_PARAM_ERROR);
        }

        return new LDAPExtendedRequest(op, cons.getControls());
    }

    //*************************************************************************
    // getResponseControls method
    //*************************************************************************

     /**
     *  Returns the Server Controls associated with the most recent response
     *  to a synchronous request on this connection object, or null
     *  if the latest response contained no Server Controls. The method
     *  always returns null for asynchronous requests. For asynchronous
     *  requests, the response controls are available in LDAPMessage.
     *
     *  @return The server controls associated with the most recent response
     *  to a synchronous request or null if the response contains no server
     *  controls.
     *
     * @see LDAPMessage#getControls()
     */
    public LDAPControl[] getResponseControls()
    {
        if( responseCtls == null) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                "getResponseControls() returns null");
            }
            return null;
        }

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "getResponseControls()");
        }

        // We have to clone the control just in case
        // we have two client threads that end up retreiving the
        // same control.
        LDAPControl [] clonedControl = new LDAPControl [responseCtls.length];

        // Also note we synchronize access to the local response
        // control object just in case another message containing controls
        // comes in from the server while we are busy duplicating
        // this one.
        synchronized (responseCtlSemaphore) {
            for(int i = 0; i < responseCtls.length; i++) {
                   clonedControl[i] = (LDAPControl) (responseCtls[i]).clone();
            }
        }

        // Return the cloned copy.  Note we have still left the
        // control in the local responseCtls variable just in case
        // somebody requests it again.
        return clonedControl;
    }

    //*************************************************************************
    // modify methods
    //*************************************************************************

    /**
     * Synchronously makes a single change to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method changes the value of an attribute,
     * adds a new attribute value, or removes an existing attribute value. </p>
     *
     * <p>The LDAPModification object specifies both the change to be made and
     * the LDAPAttribute value to be changed.</p>
     *
     * <p>If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modification.</p>
     *
     *  @param dn     The distinguished name of the entry to modify.
     *<br><br>
     *  @param mod    A single change to be made to the entry.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify( String dn,
                        LDAPModification mod)
        throws LDAPException
    {
        modify(dn, mod, defSearchCons);
        return;
    }

    /**
     *
     * Synchronously makes a single change to an existing entry in the
     * directory, using the specified constraints.
     *
     * <p>For example, this modify method changes the value of an attribute,
     * adds a new attribute value, or removes an existing attribute value.</p>
     *
     * <p>The LDAPModification object specifies both the change to be
     * made and the LDAPAttribute value to be changed.</p>
     *
     * <p>If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modification.</p>
     *
     *  @param dn       The distinguished name of the entry to modify.
     *<br><br>
     *  @param mod      A single change to be made to the entry.
     *<br><br>
     *  @param cons     The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify( String dn,
                        LDAPModification mod,
                        LDAPConstraints cons)
        throws LDAPException
    {
        LDAPModification[] mods = new LDAPModification[1];
        mods[0] = mod;
        modify(dn, mods, cons);
        return;
    }

    /**
     *
     * Synchronously makes a set of changes to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method changes attribute values, adds
     * new attribute values, or removes existing attribute values.</p>
     *
     * <p>Because the server applies all changes in an LDAPModification array
     * atomically, the application can expect that no changes
     * have been performed if an error is returned.
     * If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modifications.</p>
     *
     *  @param dn     Distinguished name of the entry to modify.
     *<br><br>
     *  @param mods   The changes to be made to the entry.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify( String dn,
                        LDAPModification[] mods)
        throws LDAPException
    {
        modify(dn, mods, defSearchCons);
        return;
    }

    /**
     * Synchronously makes a set of changes to an existing entry in the
     * directory, using the specified constraints.
     *
     * <p>For example, this modify method changes attribute values, adds new
     * attribute values, or removes existing attribute values.</p>
     *
     * <p>Because the server applies all changes in an LDAPModification array
     * atomically, the application can expect that no changes
     * have been performed if an error is returned.
     * If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modifications.</p>
     *
     *  @param dn      The distinguished name of the entry to modify.
     *<br><br>
     *  @param mods    The changes to be made to the entry.
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an
     *                          error message and an LDAP error code.
     */
    public void modify( String dn,
                        LDAPModification[] mods,
                        LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseQueue queue =
            modify(dn, mods, null, cons);

        // Get a handle to the modify response
        LDAPResponse modifyResponse = (LDAPResponse)(queue.getResponse());

        // Set local copy of responseControls synchronously - if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = modifyResponse.getControls();
        }

        chkResultCode( queue, cons, modifyResponse);

        return;
    }

    /**
     * Asynchronously makes a single change to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method can change the value of an attribute,
     * add a new attribute value, or remove an existing attribute value.</p>
     *
     * <p>The LDAPModification object specifies both the change to be made and
     * the LDAPAttribute value to be changed.</p>
     *
     * <p>If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modification.</p>
     *
     *  @param dn         Distinguished name of the entry to modify.
     *<br><br>
     *  @param mod        A single change to be made to the entry.
     *<br><br>
     *  @param queue      Handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    queue object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification mod,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        return modify(dn, mod, queue, defSearchCons);
    }

    /**
     * Asynchronously makes a single change to an existing entry in the
     * directory, using the specified constraints and queue.
     *
     * <p>For example, this modify method can change the value of an attribute,
     * add a new attribute value, or remove an existing attribute value.</p>
     *
     * <p>The LDAPModification object specifies both the change to be made
     * and the LDAPAttribute value to be changed.</p>
     *
     * <p>If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modification.</p>
     *
     *  @param dn          Distinguished name of the entry to modify.
     *<br><br>
     *  @param mod         A single change to be made to the entry.
     *<br><br>
     *  @param queue       Handler for messages returned from a server in
     *                     response to this request. If it is null, a
     *                     queue object is created internally.
     *<br><br>
     *  @param cons        Constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification mod,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        LDAPModification[] mods = new LDAPModification[1];
        mods[0] = mod;
        return modify(dn, mods, queue, cons);
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method can change attribute values, add new
     * attribute values, or remove existing attribute values.</p>
     *
     * <p>Because the server applies all changes in an LDAPModification array
     * atomically, the application can expect that no changes
     * have been performed if an error is returned.
     * If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modifications.</p>
     *
     *  @param dn         The distinguished name of the entry to modify.
     *<br><br>
     *  @param mods       The changes to be made to the entry.
     *<br><br>
     *  @param queue      The queue for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    queue object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification[] mods,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        return modify(dn, mods, queue, defSearchCons);
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory, using the specified constraints and queue.
     *
     * <p>For example, this modify method can change attribute values, add new
     * attribute values, or remove existing attribute values.</p>
     *
     * <p>Because the server applies all changes in an LDAPModification array
     * atomically, the application can expect that no changes
     * have been performed if an error is returned.
     * If the request fails with {@link LDAPException#CONNECT_ERROR},
     * it is indeterminate whether or not the server made the modifications.</p>
     *
     *  @param dn         The distinguished name of the entry to modify.
     *<br><br>
     *  @param mods       The changes to be made to the entry.
     *<br><br>
     *  @param queue      The queue for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    queue object is created internally.
     *<br><br>
     *  @param cons       Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseQueue modify(String dn,
                                    LDAPModification[] mods,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "modify(" + dn + "), " + mods.length + " modifications");
        }
        if(dn == null) {
            // Invalid DN parameter
            throw new IllegalArgumentException(
                                     ExceptionMessages.DN_PARAM_ERROR);
        }

        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPModifyRequest( dn, mods, cons.getControls());

        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    //*************************************************************************
    // read methods
    //*************************************************************************

    /**
     * Synchronously reads the entry for the specified distiguished name (DN)
     * and retrieves all attributes for the entry.
     *
     *  @param dn        The distinguished name of the entry to retrieve.
     *
     *  @return the LDAPEntry read from the server.
     *
     *  @exception LDAPException if the object was not found
     */
    public LDAPEntry read(String dn)
        throws LDAPException
    {
        return read(dn, defSearchCons);
    }


    /**
     *
     * Synchronously reads the entry for the specified distiguished name (DN),
     * using the specified constraints, and retrieves all attributes for the
     * entry.
     *
     *  @param dn         The distinguished name of the entry to retrieve.
     *<br><br>
     *  @param cons       The constraints specific to the operation.
     *
     *  @return the LDAPEntry read from the server
     *
     *  @exception LDAPException if the object was not found
     */
    public LDAPEntry read(String dn,
                          LDAPSearchConstraints cons)
        throws LDAPException
    {
        return read(dn, null, cons);
    }

    /**
     *
     * Synchronously reads the entry for the specified distinguished name (DN)
     * and retrieves only the specified attributes from the entry.
     *
     *  @param dn         The distinguished name of the entry to retrieve.
     *<br><br>
     *  @param attrs      The names of the attributes to retrieve.
     *
     *  @return the LDAPEntry read from the server
     *
     *  @exception LDAPException if the object was not found
     */
    public LDAPEntry read(String dn,
                          String[] attrs)
        throws LDAPException
    {
        return read(dn, attrs, defSearchCons);
    }

    /**
     * Synchronously reads the entry for the specified distinguished name (DN),
     * using the specified constraints, and retrieves only the specified
     * attributes from the entry.
     *
     *  @param dn       The distinguished name of the entry to retrieve.
     *<br><br>
     *  @param attrs    The names of the attributes to retrieve.
     *<br><br>
     *  @param cons     The constraints specific to the operation.
     *
     *  @return the LDAPEntry read from the server
     *
     *  @exception LDAPException if the object was not found
     */
    public LDAPEntry read(String dn,
                          String[] attrs,
                          LDAPSearchConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "read(" + dn + ")");
        }
        LDAPSearchResults sr = search(dn, SCOPE_BASE,
                                      null,
                                      attrs, false, cons);

        LDAPEntry ret = null;
        if( sr.hasMore()) {
            ret = sr.next();
            if( sr.hasMore()) {
                // "Read response is ambiguous, multiple entries returned"
                throw new LDAPLocalException(ExceptionMessages.READ_MULTIPLE,
                                    LDAPException.AMBIGUOUS_RESPONSE);
            }
        }
        return ret;
    }

    /**
     * Synchronously reads the entry specified by the LDAP URL.
     *
     * <p>When this read method is called, a new connection is created
     * automatically, using the host and port specified in the URL. After
     * finding the entry, the method closes the connection (in other words,
     * it disconnects from the LDAP server).</p>
     *
     * <p>If the URL specifies a filter and scope, they are not used. Of the
     * information specified in the URL, this method only uses the LDAP host
     * name and port number, the base distinguished name (DN), and the list
     * of attributes to return.</p>
     *
     *  @param toGet           LDAP URL specifying the entry to read.
     *
     *  @return The entry specified by the base DN.
     *
     *  @exception LDAPException if the object was not found
     */
    public static LDAPEntry read(LDAPUrl toGet)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read(" + toGet.toString() + ")");
        }
        LDAPConnection lconn = new LDAPConnection();
        lconn.connect(toGet.getHost(),toGet.getPort());
        LDAPEntry toReturn = lconn.read(toGet.getDN(),toGet.getAttributeArray());
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        lconn.disconnect();
        return toReturn;
    }

    /**
     * Synchronously reads the entry specified by the LDAP URL, using the
     * specified constraints.
     *
     * <p>When this method is called, a new connection is created
     * automatically, using the host and port specified in the URL. After
     * finding the entry, the method closes the connection (in other words,
     * it disconnects from the LDAP server).</p>
     *
     * <p>If the URL specifies a filter and scope, they are not used. Of the
     * information specified in the URL, this method only uses the LDAP host
     * name and port number, the base distinguished name (DN), and the list
     * of attributes to return.</p>
     *
     * @return The entry specified by the base DN.
     *
     * @param toGet       LDAP URL specifying the entry to read.
     *<br><br>
     * @param cons       Constraints specific to the operation.
     *
     *  @exception LDAPException if the object was not found
     */
    public static LDAPEntry read(LDAPUrl toGet,
                                 LDAPSearchConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read(" + toGet.toString() + ")");
        }
        LDAPConnection lconn = new LDAPConnection();
        lconn.connect(toGet.getHost(),toGet.getPort());
        LDAPEntry toReturn = lconn.read(toGet.getDN(),
                                    toGet.getAttributeArray(), cons);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        lconn.disconnect();
        return toReturn;
    }

    //*************************************************************************
    // rename methods
    //*************************************************************************

    /**
     *
     * Synchronously renames an existing entry in the directory.
     *
     *  @param dn       The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn   The new relative distinguished name for the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void rename(String dn,
                       String newRdn,
                       boolean deleteOldRdn)
        throws LDAPException
    {
        rename(dn, newRdn, deleteOldRdn, defSearchCons);
        return;
    }

    /**
     *
     * Synchronously renames an existing entry in the directory, using the
     * specified constraints.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public void rename(String dn,
                       String newRdn,
                       boolean deleteOldRdn,
                       LDAPConstraints cons)
        throws LDAPException
    {
        // null for newParentdn means that this is originating as an LDAPv2 call
        rename(dn, newRdn, null, deleteOldRdn, cons);
        return;
    }

    /**
     * Synchronously renames an existing entry in the directory, possibly
     * repositioning the entry in the directory tree.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param newParentdn    The distinguished name of an existing entry which
     *                        is to be the new parent of the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *
     * @exception LDAPException A general exception which includes an error
     *                          message and an LDAP error code.
     */
    public void rename(String dn,
                       String newRdn,
                       String newParentdn,
                       boolean deleteOldRdn)
        throws LDAPException
    {
        rename(dn, newRdn, newParentdn, deleteOldRdn, defSearchCons);
        return;
    }

    /**
     *
     * Synchronously renames an existing entry in the directory, using the
     * specified constraints and possibly repositioning the entry in the
     * directory tree.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param newParentdn    The distinguished name of an existing entry which
     *                        is to be the new parent of the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public void rename(String dn,
                       String newRdn,
                       String newParentdn,
                       boolean deleteOldRdn,
                       LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseQueue queue =
            rename(dn, newRdn, newParentdn, deleteOldRdn, null, cons);

        // Get a handle to the rename response
        LDAPResponse renameResponse = (LDAPResponse)(queue.getResponse());

        // Set local copy of responseControls synchronously - if there were any
        synchronized (responseCtlSemaphore) {
            responseCtls = renameResponse.getControls();
        }

        chkResultCode( queue, cons, renameResponse);
        return;
    }

    /*
     * rename
     */

    /**
     * Asynchronously renames an existing entry in the directory.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param queue          The queue for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        return rename(dn, newRdn, deleteOldRdn, queue, defSearchCons);
    }

    /**
     * Asynchronously renames an existing entry in the directory, using the
     * specified constraints.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param queue          The queue for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        return rename(dn, newRdn, null, deleteOldRdn, queue, cons);
    }

    /**
     * Asynchronously renames an existing entry in the directory, possibly
     * repositioning the entry in the directory.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param newParentdn    The distinguished name of an existing entry which
     *                        is to be the new parent of the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param queue          The queue for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    String newParentdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "rename(" + dn + "," + newRdn + "," + newParentdn + ")");
        }
        return rename(dn, newRdn, newParentdn,
                      deleteOldRdn, queue, defSearchCons);
    }

    /**
     * Asynchronously renames an existing entry in the directory, using the
     * specified constraints and possibily repositioning the entry in the
     * directory.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param newParentdn    The distinguished name of an existing entry which
     *                        is to be the new parent of the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     *  @param queue          The queue for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseQueue rename(String dn,
                                    String newRdn,
                                    String newParentdn,
                                    boolean deleteOldRdn,
                                    LDAPResponseQueue queue,
                                    LDAPConstraints cons)
        throws LDAPException
    {
        if(dn == null || newRdn == null) {
            // Invalid DN or RDN parameter
            throw new IllegalArgumentException(
                                     ExceptionMessages.RDN_PARAM_ERROR);
        }

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "rename(" + dn + "," + newRdn + "," + newParentdn + ")");
        }
        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPModifyDNRequest( dn, newRdn, newParentdn,
                                deleteOldRdn, cons.getControls());

        return sendRequestToServer(msg, cons.getTimeLimit(), queue, null);
    }

    //*************************************************************************
    // search methods
    //*************************************************************************

    /**
     *
     * Synchronously performs the search specified by the parameters.
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                           within its subtree
     *</ul><br><br>
     *  @param filter         Search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          Names of attributes to retrieve.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found. If false, returns the
     *                        names and values for attributes found.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPSearchResults search(String base,
                                    int scope,
                                    String filter,
                                    String[] attrs,
                                    boolean typesOnly)
        throws LDAPException
    {
        return search(base, scope, filter, attrs, typesOnly, defSearchCons);
    }

    /**
     *
     * Synchronously performs the search specified by the parameters,
     * using the specified search constraints (such as the
     * maximum number of entries to find or the maximum time to wait for
     * search results).
     *
     * <p>As part of the search constraints, the method allows specifying
     * whether or not the results are to be delivered all at once or in
     * smaller batches. If specified that the results are to be delivered in
     * smaller batches, each iteration blocks only until the next batch of
     * results is returned.</p>
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                          within its subtree
     *</ul><br><br>
     *  @param filter         The search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          The names of attributes to retrieve.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found.  If false, returns the
     *                        names and values for attributes found.
     *<br><br>
     *  @param cons           The constraints specific to the search.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPSearchResults search(String base,
                                    int scope,
                                    String filter,
                                    String[] attrs,
                                    boolean typesOnly,
                                    LDAPSearchConstraints cons)
        throws LDAPException
    {
        LDAPSearchQueue queue =
            search(base, scope, filter, attrs, typesOnly, null, cons);

        if( cons == null )
            cons = defSearchCons;
        return new LDAPSearchResults(this, queue, cons);
    }

    /**
     * Asynchronously performs the search specified by the parameters.
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                          within its subtree
     *</ul><br><br>
     *  @param filter         Search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          Names of attributes to retrieve.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found.  If false, returns the
     *                        names and values for attributes found.
     *<br><br>
     *  @param queue          Handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPSearchQueue search(String base,
                                  int scope,
                                  String filter,
                                  String[] attrs,
                                  boolean typesOnly,
                                  LDAPSearchQueue queue)
        throws LDAPException
    {
        return search(base, scope, filter, attrs, typesOnly,
                      queue, defSearchCons);
    }

    /**
     * Asynchronously performs the search specified by the parameters,
     * also allowing specification of constraints for the search (such
     * as the maximum number of entries to find or the maximum time to
     * wait for search results).
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                           within its subtree
     *</ul><br><br>
     *  @param filter         The search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          The names of attributes to retrieve.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found.  If false, returns the
     *                        names and values for attributes found.
     * <br><br>
     *  @param queue          The queue for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        queue object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the search.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
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
        if( filter == null) {
            filter = "objectclass=*";
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "search(\"" + base + "\"," + scope + ",\"" + filter + "\")");
        }
        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPSearchRequest( base, scope, filter,
                                                 attrs, cons.getDereference(),
                                                 cons.getMaxResults(),
                                                 cons.getServerTimeLimit(),
                                                 typesOnly, cons.getControls());
        MessageAgent agent;
        LDAPSearchQueue myqueue = queue;
        if(myqueue == null) {
            agent = new MessageAgent();
            myqueue = new LDAPSearchQueue( agent );
        } else {
            agent = queue.getMessageAgent();
        }

        try {
            agent.sendMessage( conn, msg, cons.getTimeLimit(), myqueue, null);
        } catch(LDAPException lex) {
            throw lex;
        }
        return myqueue;
    }

    /*
     * LDAP URL search
     */

    /**
     * Synchronously performs the search specified by the LDAP URL, returning
     * an enumerable LDAPSearchResults object.
     *
     * @param toGet The LDAP URL specifying the entry to read.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public static LDAPSearchResults search(LDAPUrl toGet)
        throws LDAPException
    {
        // Get a clone of default search constraints, method alters batchSize
        return search( toGet, null);
    }

    /*
     * LDAP URL search
     */

    /**
     * Synchronously perfoms the search specified by the LDAP URL, using
     * the specified search constraints (such as the maximum number of
     * entries to find or the maximum time to wait for search results).
     *
     * <p>When this method is called, a new connection is created
     * automatically, using the host and port specified in the URL. After
     * all search results have been received from the server, the method
     * closes the connection (in other words, it disconnects from the LDAP
     * server).</p>
     *
     * <p>As part of the search constraints, a choice can be made as to whether
     * to have the results delivered all at once or in smaller batches. If
     * the results are to be delivered in smaller batches, each iteration
     * blocks only until the next batch of results is returned.</p>
     *
     *
     *  @param toGet          LDAP URL specifying the entry to read.
     *<br><br>
     *  @param cons           The constraints specific to the search.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public static LDAPSearchResults search(LDAPUrl toGet,
                                           LDAPSearchConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests,
                    "LDAPConnection.search(" + toGet.toString() + ")");
        }
        LDAPConnection lconn = new LDAPConnection();
        lconn.connect(toGet.getHost(),toGet.getPort());
        if( cons == null) {
            // This is a clone, so we already have our own copy
            cons = lconn.getSearchConstraints();
        } else {
            // get our own copy of user's constraints because we modify it
            cons = (LDAPSearchConstraints)cons.clone();
        }
        cons.setBatchSize(0); // Must wait until all results arrive
        LDAPSearchResults toReturn = lconn.search(toGet.getDN(),
                toGet.getScope(), toGet.getFilter(), toGet.getAttributeArray(),
                false, cons);
        lconn.disconnect();
        return toReturn;
    }

    /**
     * Sends an LDAP request to a directory server.
     *
     * <p>The specified the LDAP request is sent to the directory server
     * associated with this connection using default constraints. An LDAP
     * request object is a subclass {@link LDAPMessage} with the operation
     * type set to one of the request types. You can build a request by using
     * the request classes found in this package</p>
     *
     * <p>You should note that, since LDAP requests sent to the server
     * using sendRequest are asynchronous, automatic referral following
     * does not apply to these requests.</p>
     *
     * @param request The LDAP request to send to the directory server.
     * @param queue    The queue for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 queue object is created internally.
     * @exception     LDAPException A general exception which includes an error
     *                message and an LDAP error code.
     *
     * @see LDAPMessage#getType()
     * @see LDAPMessage#isRequest()
     */
    public LDAPMessageQueue sendRequest( LDAPMessage request,
                                         LDAPMessageQueue queue)
            throws LDAPException
    {
        return sendRequest( request, queue, null);
    }

    /**
     * Sends an LDAP request to a directory server.
     *
     * <p>The specified the LDAP request is sent to the directory server
     * associated with this connection. An LDAP request object is an
     * {@link LDAPMessage} with the operation type set to one of the request
     * types. You can build a request by using the request classes found in this
     * package</p>
     *
     * <p>You should note that, since LDAP requests sent to the server
     * using sendRequest are asynchronous, automatic referral following
     * does not apply to these requests.</p>
     *
     * @param request The LDAP request to send to the directory server.
     * @param queue    The queue for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 queue object is created internally.
     * @param cons    The constraints that apply to this request
     * @exception     LDAPException A general exception which includes an error
     *                message and an LDAP error code.
     *
     * @see LDAPMessage#getType()
     * @see LDAPMessage#isRequest()
     */
    public LDAPMessageQueue sendRequest( LDAPMessage request,
                                         LDAPMessageQueue queue,
                                         LDAPConstraints cons)
            throws LDAPException
    {

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "sendRequest(" + request.toString() + ")");
        }

        if( ! request.isRequest() ) {
            throw new RuntimeException( "Object is not a request message");
        }

        if(cons == null) {
            cons = defSearchCons;
        }

        // Get the correct queue for a search request
        MessageAgent agent;
        LDAPMessageQueue myqueue = queue;
        if(myqueue == null) {
            agent = new MessageAgent();
            if( request.getType() == LDAPMessage.SEARCH_REQUEST) {
                myqueue = new LDAPSearchQueue( agent );
            } else {
                myqueue = new LDAPResponseQueue( agent );
            }
        } else {
            if( request.getType() == LDAPMessage.SEARCH_REQUEST) {
                agent = queue.getMessageAgent();
            } else {
                agent = queue.getMessageAgent();
            }
        }

        try {
            agent.sendMessage( conn, request, cons.getTimeLimit(), myqueue, null);
        } catch(LDAPException lex) {
            throw lex;
        }
        return myqueue;
    }

    //*************************************************************************
    // helper methods
    //*************************************************************************

    /**
     * Locates the appropriate message agent and sends
     * the LDAP request to a directory server.
     *
     * @param msg the message to send
     *<br><br>
     * @param timeout the timeout value
     *<br><br>
     * @param queue the response queue or null
     *
     * @return the LDAPResponseQueue for this request
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    private LDAPResponseQueue sendRequestToServer( LDAPMessage msg,
                                           int timeout,
                                           LDAPResponseQueue queue,
                                           BindProperties bindProps)
            throws LDAPException
    {
        MessageAgent agent;
        if(queue == null) {
            agent = new MessageAgent();
            queue = new LDAPResponseQueue( agent);
        } else {
            agent = queue.getMessageAgent();
        }

        agent.sendMessage( conn, msg, timeout, queue, bindProps);
        return queue;
    }

    /**
     * Return the Connection object associated with this LDAPConnection
     *
     * @return the Connection object
     */
    /* package */
    Connection getConnection( )
    {
        return conn;
    }

    /**
     * Return the Connection object name associated with this LDAPConnection
     *
     * @return the Connection object name
     */
    /* package */
    String getConnectionName( )
    {
        return name;
    }

    /**
     * get an LDAPConnection object so that we can follow a referral.
     * This function is never called if cons.getReferralFollowing() returns
     * false.
     *
     * @param referrals the array of referral strings
     *<br><br>
     *
     * @return The referralInfo object
     *
     *  @exception LDAPReferralException A general exception which includes
     *  an error message and an LDAP error code.
     */
    private ReferralInfo getReferralConnection( String[] referrals)
                    throws LDAPReferralException
    {
        ReferralInfo refInfo = null;
        Throwable ex = null;
        LDAPConnection rconn = null;
        LDAPReferralHandler rh = defSearchCons.getReferralHandler();
        int i = 0;
        // Check if we use LDAPRebind to get authentication credentials
        if( (rh == null) ||      (rh instanceof LDAPAuthHandler)) {
            for( i = 0; i < referrals.length; i++) {
                // dn, pw are null in the default case (anonymous bind)
                String dn = null;
                byte[] pw = null;
                try {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.referrals,   name +
                                                    "getReferralConnection: " +
                                                    "url=" + referrals[i]);
                    }
                    rconn = new LDAPConnection( conn.getSocketFactory());
                    rconn.setConstraints( defSearchCons);
                    LDAPUrl url = new LDAPUrl(referrals[i]);
                    rconn.connect(url.getHost(),url.getPort());
                    if( rh != null) {
                        if( rh instanceof LDAPAuthHandler) {
                            // Get application supplied dn and pw
                            LDAPAuthProvider ap =
                                  ((LDAPAuthHandler)rh).getAuthProvider(
                                        url.getHost(),url.getPort());
                            dn = ap.getDN();
                            pw = ap.getPassword();
                        }
                    }
                    rconn.bind( LDAP_V3, dn, pw);
                    ex = null;
                    refInfo = new ReferralInfo(rconn, referrals, url);
                    // Indicate this connection created to follow referral
                    rconn.getConnection().setActiveReferral( refInfo);
                    break;
                } catch( Throwable lex) {
                    if( rconn != null) {
                        try {
                            if( Debug.LDAP_DEBUG) {
                                Debug.trace( Debug.referrals, name +
                                    "getReferralConnection, exception " +
                                    "binding for referral" + lex.toString());
                            }
                            rconn.disconnect();
                            rconn = null;
                            ex = lex;
                        } catch( LDAPException e) {
                            ; // ignore
                        }
                    }
                }

            }
        }
        // Check if application gets connection and does bind
        else { //  rh instanceof LDAPBind
            try {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals, name +
                                                "getReferralConnection: " +
                                                "Call LDAPBind.bind()");
                }
                rconn = ((LDAPBindHandler)rh).bind( referrals, this);
                if( rconn == null) {
                    LDAPReferralException rex = new LDAPReferralException(
                        ExceptionMessages.REFERRAL_ERROR);
                    rex.setReferrals( referrals);
                    throw rex;
                }
                // Figure out which Url belongs to the connection
                for( int idx = 0; idx < referrals.length; idx++) {
                    try {
                        LDAPUrl url = new LDAPUrl( referrals[idx]);
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.referrals, name +
                                        "getReferralConnection: " +
                                        "Compare host port " +
                                        url.getHost() + "-" + rconn.getHost() +
                                        " & " +
                                        url.getPort() + "-" + rconn.getPort());
                        }
                        if( url.getHost().equalsIgnoreCase(rconn.getHost()) &&
                                (url.getPort() == rconn.getPort())) {
                            refInfo = new ReferralInfo(rconn, referrals, url);
                            break;
                        }
                    } catch ( Exception e) {
                        ; // ignore
                    }
                }
                if( refInfo == null) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.referrals, name +
                                    "getReferralConnection: " +
                                    "LDAPBind.bind(): " +
                                    " could not match connection with URL" +
                                    referrals.toString());
                    }
                    // Could not match LDAPBind.bind() connecction with URL list
                    ex = new LDAPLocalException(
                            ExceptionMessages.REFERRAL_BIND_MATCH,
                            LDAPException.CONNECT_ERROR);
                }
            } catch( Throwable lex) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals, name +
                                "getReferralConnection: " +
                                "Exception from LDAPBind.bind(): " +
                                lex.toString());
                }
                rconn = null;
                ex = lex;
            }
        }
        if( ex != null) {
            // Could not connect to any server, throw an exception
            LDAPException ldapex;
            if( ex instanceof LDAPReferralException) {
                throw (LDAPReferralException)ex;
            } else
            if( ex instanceof LDAPException) {
                ldapex = (LDAPException)ex;
            } else {
                ldapex = new LDAPLocalException(
                    ExceptionMessages.SERVER_CONNECT_ERROR,
                    new Object[] { conn.getHost() },
                    LDAPException.CONNECT_ERROR, ex);
            }
            // Error attempting to follow a referral
            LDAPReferralException rex = new LDAPReferralException(
                    ExceptionMessages.REFERRAL_ERROR,
                    ldapex);
            rex.setReferrals(referrals);
            // Use last URL string for the failed referral
            rex.setFailedReferral( referrals[referrals.length-1]);
            throw rex;
        }

        // We now have an authenticated connection
        // to be used to follow the referral.
        return refInfo;
    }

    /**
     * Check the result code and throw an exception if needed.
     *
     * <p>If referral following is enabled, checks if we need to
     * follow a referral</p>
     *
     * @param queue - the message queue of the current response
     *
     * @param cons - the constraints that apply to the request
     *
     * @param response - the LDAPResponse to check
     */
    private void chkResultCode( LDAPMessageQueue queue,
                                LDAPConstraints cons,
                                LDAPResponse response)
                throws LDAPException
     {
        if( (response.getResultCode() == LDAPException.REFERRAL) &&
                                    cons.getReferralFollowing()) {
            // Perform referral following and return
            ArrayList refConn = null;
            try {
                chaseReferral( queue, cons, response,
                        response.getReferrals(), 0, false, null );
            } finally {
                releaseReferralConnections( refConn);
            }
        } else {
            // Throws exception for non success result
            response.chkResultCode();
        }
        return;
     }

    /**
     * Follow referrals if necessary referral following enabled.
     * This function is called only by synchronous requests.
     * Search responses come here only if referral following is
     * enabled and if we are processing a SearchResultReference
     * or a Response with a status of REFERRAL, i.e. we are
     * going to follow a referral.
     *
     * This functions recursively follows a referral until a result
     * is returned or until the hop limit is reached.
     *
     * @param queue The LDAPResponseQueue for this request
     * <br><br>
     * @param cons The constraints that apply to the request
     * <br><br>
     * @param msg The referral or search reference response message
     * <br><br>
     * @param initialReferrals The referral array returned from the
     *                        initial request.
     * <br><br>
     * @param hopCount the number of hops already used while
     *                 following this referral
     * <br><br>
     * @param searchReference true if the message is a search reference
     * <br><br>
     * @param connectionList An optional array list used to store
     *        the LDAPConnection objects used in following the referral.
     *
     * @return The array list used to store the all LDAPConnection objects
     *        used in following the referral.  The list will be empty
     *        if there were none.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    /* package */
    ArrayList chaseReferral( LDAPMessageQueue queue,
                                LDAPConstraints cons,
                                LDAPMessage msg,
                                String[] initialReferrals,
                                int hopCount,
                                boolean searchReference,
                                ArrayList connectionList)
                    throws LDAPException
    {
        ArrayList connList = connectionList;
        LDAPConnection rconn = null; // new conn for following referral
        ReferralInfo rinfo = null;   // referral info
        LDAPMessage origMsg;

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.referrals,   name +
                "Check for referrals, reference = " + searchReference);
        }
        // Get a place to store new connections
        if( connList == null) {
            connList = new ArrayList( cons.getHopLimit());
        }
        // Following referrals or search reference
        String [] refs;             // referral list
        if( initialReferrals != null) {
            // Search continuation reference from a search request
            refs = initialReferrals;
            origMsg = msg.getRequestingMessage();
        } else { // Not a search request
            LDAPResponse resp = (LDAPResponse)queue.getResponse();
            if( resp.getResultCode() != LDAPException.REFERRAL) {
                // Not referral result,throw Exception if nonzero result
                resp.chkResultCode();
                return connList;
            }
            // We have a referral response
            refs = resp.getReferrals();
            origMsg = resp.getRequestingMessage();
        }
        LDAPUrl refUrl;             // referral represented as URL
        try {
            // increment hop count, check max hops
            if( hopCount++ > cons.getHopLimit()) {
                throw new LDAPLocalException("Max hops exceeded",
                    LDAPException.REFERRAL_LIMIT_EXCEEDED);
            }
            // Get a connection to follow the referral
            rinfo = getReferralConnection( refs);
            rconn = rinfo.getReferralConnection();
            refUrl = rinfo.getReferralUrl();
            connList.add( rconn);

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.referrals,   name +
                        (searchReference?"Following search reference URL " :
                        "Following referral URL ") + refUrl.toString());
            }

            // rebuild msg into new msg changing msgID,dn,scope,filter
            LDAPMessage newMsg = rebuildRequest( origMsg,
                                                 refUrl, searchReference);

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.referrals,   name +
                    "following referral for " + refUrl.toString());
                Debug.trace( Debug.referrals,   name +
                    "request " + newMsg.toString());
            }

            // Send new message on new connection
            try {
                MessageAgent agent;
                if( queue instanceof LDAPResponseQueue) {
                    agent=queue.getMessageAgent();
                } else {
                    agent=queue.getMessageAgent();
                }
                agent.sendMessage( rconn.getConnection(), newMsg,
                        defSearchCons.getTimeLimit(), queue, null);
            } catch(InterThreadException ex) {
                // Error ending request to referred server
                LDAPReferralException rex = new LDAPReferralException(
                     ExceptionMessages.REFERRAL_SEND,
                     LDAPException.CONNECT_ERROR, null, ex);
                rex.setReferrals( initialReferrals);
                ReferralInfo ref=rconn.getConnection().getActiveReferral();
                rex.setFailedReferral( ref.getReferralUrl().toString());
                throw rex;
            }

            if( initialReferrals == null) {
                // For operation results, when all responses are complete,
                // the stack unwinds back to the original and returns
                // to the application.
                // An exception is thrown for an error
                connList = chaseReferral( queue, cons, null, null,
                            hopCount, false, connList);
            } else {
                // For search, just return to LDAPSearchResults object
                return connList;
            }
        } catch (Exception ex) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.referrals, name +
                        "Throw exception " + ex.toString());
            }

            if( ex instanceof LDAPReferralException) {
                throw (LDAPReferralException)ex;
            } else {

                // Set referral list and failed referral
                LDAPReferralException rex = new LDAPReferralException(
                    ExceptionMessages.REFERRAL_ERROR, ex);
                rex.setReferrals( refs);
                if( rinfo != null) {
                    rex.setFailedReferral( rinfo.getReferralUrl().toString());
                } else {
                    rex.setFailedReferral( refs[refs.length - 1]);
                }
                throw rex;
            }
        }
        return connList;
    }

    /**
     * Builds a new request replacing dn, scope, and filter where approprate
     *
     * @param msg the original LDAPMessage to build the new request from
     * <br><br>
     * @param url the referral url
     *
     * @return a new LDAPMessage with appropriate information replaced
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    private
    LDAPMessage rebuildRequest( LDAPMessage msg, LDAPUrl url, boolean reference)
            throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.referrals, name +
                "rebuildRequest: original request = " +
                "message(" + msg.getMessageID() + "), request type " +
                msg.getType());
        }

        String dn = url.getDN(); // new base
        String filter = null;

        switch( msg.getType()) {
            case LDAPMessage.SEARCH_REQUEST:
                if( reference) {
                    filter = url.getFilter();
                }
                break;
            // We are allowed to get a referral for the following
            case LDAPMessage.ADD_REQUEST:
            case LDAPMessage.BIND_REQUEST:
            case LDAPMessage.COMPARE_REQUEST:
            case LDAPMessage.DEL_REQUEST:
            case LDAPMessage.EXTENDED_REQUEST:
            case LDAPMessage.MODIFY_RDN_REQUEST:
            case LDAPMessage.MODIFY_REQUEST:
                break;
            // The following return no response
            case LDAPMessage.ABANDON_REQUEST:
            case LDAPMessage.UNBIND_REQUEST:
            default:
                throw new LDAPLocalException(
                     // "Referral doesn't make sense for command"
                    ExceptionMessages.IMPROPER_REFERRAL,
                    new Object[] {
                        new Integer( msg.getType())
                    },
                    LDAPException.LOCAL_ERROR);
        }

        return msg.clone( dn, filter, reference);
    }

    /*
     * Release connections acquired by following referrals
     *
     * @param list the list of the connections
     */
    /* package */
   void releaseReferralConnections( ArrayList list)
   {
        if( list == null) {
            return;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.referrals, name +
                "Release referal connections");
        }
        // Release referral connections
        for( int i = list.size()-1; i >= 0; i--) {
            LDAPConnection rconn = null;
            try {
                rconn = (LDAPConnection)list.remove(i);
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals, "\t" + name +
                        "Disconnecting " +
                        rconn.getConnectionName());
                }
                rconn.disconnect();
            } catch( ArrayIndexOutOfBoundsException ex) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals, "\t" + name +
                        "Failed to get conn at index " + i);
                }
                continue;
            } catch( LDAPException lex) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals, "\t" + name +
                        "Disconnect failed for " + rconn.getConnectionName());
                }
                continue;
            }
        }
        return;
    }

    //*************************************************************************
    // Schema Related methods
    //*************************************************************************

    /**
     * Retrieves the schema associated with a particular schema DN in the
     * directory server.
     * <p>The schema DN for a particular entry is obtained by calling the
     * getSchemaDN method of LDAPConnection</p>
     *
     * @param    schemaDN The schema DN used to fetch the schema.
     *
     * @return    An LDAPSchema entry containing schema attributes.  If the
     * entry contains no schema attributes then the returned LDAPSchema object
     * will be empty.
     *
     * @exception LDAPException     This exception occurs if the schema entry
     *          cannot be retrieved with this connection.
     * @see #getSchemaDN()
     * @see #getSchemaDN(String)
     */
    public LDAPSchema fetchSchema ( String schemaDN ) throws LDAPException {
        /* Read the schema definitions.  If no entry is found an
         * Exception is thrown */
        LDAPEntry ent = read(schemaDN, LDAPSchema.schemaTypeNames);
        return new LDAPSchema(ent);
    }

    /**
     * Retrieves the Distiguished Name (DN) for the schema advertised in the
     * root DSE of the Directory Server.
     *
     * <p>The DN can be used with the methods fetchSchema and modify to retreive
     * and extend schema definitions.  The schema entry is located by reading
     * subschemaSubentry attribute of the root DSE.  This is equivalent to
     * calling {@link #getSchemaDN(String) } with the DN parameter as an empty
     * string: <code>getSchemaDN("")</code>.
     * </p>
     *
     *  @return     Distinguished Name of a schema entry in effect for the
     *              Directory.
     *  @exception LDAPException     This exception occurs if the schema DN
     * cannot be retrieved, or if the subschemaSubentry attribute associated
     * with the root DSE contains multiple values.
     *
     *  @see #fetchSchema
     *  @see #modify
     */
    public String getSchemaDN() throws LDAPException {
        return getSchemaDN("");
    }

    /**
     * Retrieves the Distiguished Name (DN) of the schema associated with a
     * entry in the Directory.
     *
     * <p>The DN can be used with the methods fetchSchema and modify to retreive
     * and extend schema definitions.  Reads the subschemaSubentry of the entry
     * specified.<p>
     *
     * @param dn     Distinguished Name of any entry.  The subschemaSubentry
     *               attribute is queried from this entry.
     *
     * @return      Distinguished Name of a schema entry in effect for the entry
     *               identified by <code>dn</code>.
     *
     * @exception LDAPException     This exception occurs if a null or empty
     * value is passed as dn, if the subschemasubentry attribute cannot
     * be retrieved, or the subschemasubentry contains multiple values.
     *
     *  @see #fetchSchema
     *  @see #modify
     */
    public String getSchemaDN( String dn )
        throws LDAPException
    {
        String attrSubSchema[] = { "subschemaSubentry" };

        /* Read the entries subschemaSubentry attribute. Throws an exception if
         * no entries are returned. */
        LDAPEntry ent = this.read( dn, attrSubSchema );

        LDAPAttribute attr = ent.getAttribute( attrSubSchema[0] );
        String values[] = attr.getStringValueArray();
        if( values == null || values.length < 1 ) {
            throw new LDAPLocalException(
                ExceptionMessages.NO_SCHEMA,
                new Object[] { dn },
                LDAPException.NO_RESULTS_RETURNED);
        }
        else if( values.length > 1 ) {
            throw new LDAPLocalException(
                ExceptionMessages.MULTIPLE_SCHEMA,
                new Object[] { dn },
                LDAPException.CONSTRAINT_VIOLATION);
        }
        return values[0];
   }
   
}

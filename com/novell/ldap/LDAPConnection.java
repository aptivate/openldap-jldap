/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConnection.java,v 1.77 2001/02/16 18:58:47 javed Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ***************************************************************************/

package com.novell.ldap;

import com.novell.ldap.client.ArrayList;
import java.io.*;
import java.util.*;
import java.net.MalformedURLException;

import com.novell.ldap.*;
import com.novell.ldap.client.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 *
 *  Represents the central class that encapsulates the connection
 *  to a directory server through the LDAP protocol.
 *
 *  An LDAPConnection object is not connected on
 *  construction and can only be connected to one server at one
 *  port. Multiple threads may share this single connection, typically
 *  by cloning the connection object, one for each thread. An
 *  application may have more than one LDAPConnection object, connected
 *  to the same or different directory servers.</p>
 *
 */
public class LDAPConnection implements Cloneable
{
    private LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
    private LDAPControl[] responseCtls = null;
    private Connection conn = null;

    private static Object nameLock = new Object(); // protect agentNum
    private static int lConnNum = 0;  // Debug, LDAPConnection number
    private String name;             // String name for debug
    private LDAPUrl referralURL = null; // Url used to follow a referral

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the base obect.
     */
    public static final int SCOPE_BASE   = 0;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search only the immediate subordinates of the base obect.
     */
    public static final int SCOPE_ONE    = 1;

    /**
     * Used with search to specify that the scope of entrys to search is to
     * search the base object and all entries within its subtree.
     */
    public static final int SCOPE_SUB    = 2;

    /**
     * Used with search instead of an attribute list to indicate that no
     * attributes are to be returned.
     */
    public static final String NO_ATTRS = "1.1";

    /**
     * Used with search instead of an attribute list to indicate that all
     * attributes are to be returned.
     */
    public static final String ALL_USER_ATTRS = "*";

    /**
     * Specifies the LDAPv2 protocol version when performing a bind operation.
     *
     * <p>You can use this identifier in the version parameter
     * of the bind method to specify an LDAPv2 bind.
     * The default protocol version is LDAP_V3</p>
     *
     * @see #bind(int, String, String)
     * @see #bind(int, String, String, LDAPConstraints)
     * @see #bind(int, String, String, LDAPResponseListener)
     * @see #bind(int, String, String, LDAPResponseListener, LDAPConstraints)
     */
    public static final int LDAP_V2 = 2;

    /**
     * Specifies the LDAPv3 protocol version when performing a bind operation.
     *
     * <p>You can use this identifier in the version parameter
     * of the bind method to specify an LDAPv3 bind.
     * LDAP_V3 is the default protocol version</p>
     *
     * @see #bind(int, String, String)
     * @see #bind(int, String, String, LDAPConstraints)
     * @see #bind(int, String, String, LDAPResponseListener)
     * @see #bind(int, String, String, LDAPResponseListener, LDAPConstraints)
     */
    public static final int LDAP_V3 = 3;

    /**
     * The default port number for LDAP servers.
     *
     * <p>You can use this identifier to specify the port when establishing
     * a clear text connection to a server.  This the default port.</p>
     *
     * @see #connect(String, int)
     */
    public static final int DEFAULT_PORT = 389;


    /**
     * The default SSL port number for LDAP servers.
     *
     * <p>You can use this identifier to specify the port when establishing
     * a an SSL connection to a server.</p>.
     */
    public static final int DEFAULT_SSL_PORT = 636;

    /**
     * A string that can be passed in to the getProperty method.
     *
     * <p>You can use this string to request the version of the SDK</p>.
     */


    public static final String LDAP_PROPERTY_SDK = "version.sdk";
    /**
     * A string that can be passed in to the getProperty method.
     *
     * <p>You can use this string to request the version of the
     * LDAP protocol</p>.
     */


    public static final String LDAP_PROPERTY_PROTOCOL = "version.protocol";

    /**
     * A string that can be passed in to the getProperty method.
     *
     * <p>You can use this string to request the type of security
     * being used</p>.
     */


    public static final String LDAP_PROPERTY_SECURITY = "version.security";

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
        this( (LDAPSocketFactory)null);
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
        LDAPConnection newClone = new LDAPConnection();
        newClone.conn = conn;   // same connection
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "clone()");
        }

        //now just duplicate the defSearchCons and responseCtls
        if (defSearchCons != null){
            newClone.defSearchCons = (LDAPSearchConstraints)defSearchCons.clone();
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
        conn.createClone();     // Tell the Connection object a clone exists
        return (Object) newClone;
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
    public void finalize()
        throws LDAPException
    {
        // Disconnect did not come from user API call
        disconnect(false);
        return;
    }

    /**
     * Returns the protocol version uses to authenticate
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
        return conn.getBindProperties().getProtocolVersion();
    }

    /**
     * Returns the distinguished name (DN) used for authentication by this
     * object. Null is returned if no authentication has been performed.
     *
     * @return The distinguished name if object is authenticated; otherwise,
     * null.
     *
     * @see #bind( String, String)
     */
    public String getAuthenticationDN()
    {
        return conn.getBindProperties().getAuthenticationDN();
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
        return conn.getBindProperties().getAuthenticationMethod();
    }

    /**
     * Returns the password used for simple authentication by this object.
     *
     * <p> null is returned if no simple authentication has been performed.</p>
     *
     * @return The password used for simple authentication or null if the
     * object is not authenticated.
     *
     * @see #bind( String, String)
     */
    public String getAuthenticationPassword()
    {
        return conn.getBindProperties().getAuthenticationPassword();
    }

    /**
     * Returns the properties if any specified on binding with a
     * SASL mechanism.
     *
     * <p> Null is returned if no authentication has been performed
     * or no authentication Hashtable is present.</p>
     *
     * @return The Hashtable used for SASL bind or null if the
     * object is not present or not authenticated. The object returned can
     * be either of type Hashtable or Properties.
     *
     * @see #bind( String, String[], Hashtable, Object )
     */
    public Hashtable getSaslBindProperties()
    {
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
     * @see #bind( String, String[], Hashtable, Object )
     */
    public Object /* javax.security.auth.callback.CallbackHandler */
                     getSaslBindCallbackHandler()
    {
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
        return (LDAPConstraints)((LDAPConstraints)this.defSearchCons).clone();
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
     * Returns the stream used by the connection object for receiving data
     * from the LDAP server.
     *
     * @return The stream the connection object used for receiving data,
     * or null if the object has never connected.
     *
     * @see #setInputStream
     */
    public InputStream getInputStream()
    {
        return conn.getInputStream();
    }

    /**
     * Returns the stream used by the connection object to send data to the
     * LDAP server.
     *
     * @return The stream the connection object used for sending data
     * or null if the object has never connected.
     *
     * @see #setOutputStream
     */
    public OutputStream getOutputStream()
    {
        return conn.getOutputStream();
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
     *  @return The requested property.
     *
     *  @exception LDAPException Thrown if the requested property is not
     *  available.
     *
     * @see #setProperty( String, Object)
     */
    public Object getProperty(String name)
        throws LDAPException
    {
        if (name.equals(LDAP_PROPERTY_SDK))
            return conn.sdk;
        else if (name.equals(LDAP_PROPERTY_PROTOCOL))
            return conn.protocol;
        else if (name.equals(LDAP_PROPERTY_SECURITY))
            return conn.security;
        else {
            // Requested property not available.
            throw new LDAPException(LDAPExceptionMessageResource.NO_PROPERTY,
                    LDAPException.PARAM_ERROR);
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
     * @see #setSearchConstraints
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
     * Indicates if the connection uses TLS, i.e. startTLS has completed.
     *
     * @see #startTLS
     */
    public boolean isTLS()
    {
        return false;
    }

    /**
     * Sets the constraints that apply to all operations performed through
     * this connection (unless a different set of constraints is specified
     * when calling an operation method).
     *
     * @param cons  Object containint the contraint values to set
     *
     * @see #getConstraints()
     */
    public void setConstraints(LDAPConstraints cons)
    {
        // We set the constraints this way, so a thread doesn't get an
        // conconsistant view of the referrals.
        LDAPSearchConstraints newCons = (LDAPSearchConstraints)defSearchCons.clone();
        newCons.setHopLimit(cons.getHopLimit());
        newCons.setTimeLimit(cons.getTimeLimit());
        newCons.setReferralHandler(cons.getReferralHandler());
        newCons.setReferralFollowing(cons.getReferralFollowing());
        LDAPControl[] lcc = cons.getClientControls();
        if( lcc != null) {
            newCons.setClientControls((LDAPControl)lcc.clone());
        }
        LDAPControl[] lsc = cons.getServerControls();
        if( lsc != null) {
            newCons.setServerControls((LDAPControl)lsc.clone());
        }
        defSearchCons = newCons;
        return;
    }

    /**
     * Sets the stream used by the connection object for receiving data from
     * the LDAP server.
     *
     * @param stream The input stream for receiving data.
     *
     * @see #getInputStream()
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
     * @param stream The output stream for sending data.
     *
     * @see #getOutputStream()
     */
    public void setOutputStream(OutputStream stream)
    {
        conn.setOutputStream(stream);
        return;
    }

    /**
     * Sets a property of a connection object.
     *
     * <p>No property names which can be set have been defined at this time. </p>
     *
     *
     * @param name    Name of the property to set.<br><br>
     *<br><br>
     * @param value   Value to assign to the property.
     *
     * @exception LDAPException Thrown if the specified
     *                 property is not supported.
     *
     * @see #getProperty( String )
     */
    public void setProperty(String name, Object value)
        throws LDAPException
    {
        // Requested property is not supported
        throw new LDAPException(LDAPExceptionMessageResource.NO_SUP_PROPERTY,
                LDAPException.PARAM_ERROR);
    }

    /**
     * Sets the constraints that apply to all search operations performed
     * through this connection (unless a different set of constraints is
     * specified when calling a search operation method).
     *
     * <p>Typically, the setSearchConstraints method is used to create a
     * slightly different set of search constraints to apply to a particular
     * search.</p>
     *
     * @param cons The search constraints to set.
     *
     * @see #getSearchConstraints()
     */
    public void setSearchConstraints(LDAPSearchConstraints cons)
    {
        defSearchCons = (LDAPSearchConstraints)cons.clone();
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
	 *		   implement the LDAPUnsolicitedNotificationListener interface.
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
     * This affects the connection characteristics and thus will affect
     * the source object and all clone objects.
     *
     * @see #isTLS()
     */
    public void startTLS()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "startTLS()");
        }
        throw new RuntimeException("Method LDAPConnection.startTLS not implemented");
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
     *                 can be obtained from the response listener for the
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
     *  Abandons a search operation for a listener, using the specified
     *  constraints.
     *
     *  @param id The ID of the asynchronous operation to abandon.
     *            The ID can be obtained from the search
     *            listener for the operation.<br><br>
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
     * Abandons all search operations for a listener.
     *
     * <p>All operations in progress, which are managed by the specified listener,
     * are abandoned.</p>
     *
     *  @param listener  The handler returned for messages returned on a
     *                   search request.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon( LDAPListener listener)
        throws LDAPException
    {
        abandon( listener, defSearchCons);
        return;
    }

    /**
     * Abandons all search operations for a listener.
     *
     * <p>All operations in progress, which are managed by the specified listener,
     * are abandoned.</p>
     *
     *  @param listener  The handler returned for messages returned on a
     *                   search request.
     *<br><br>
     *  @param cons     The contraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void abandon( LDAPListener listener, LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "abandon(listener)");
        }
        if(listener != null) {
            MessageAgent agent;
            if( listener instanceof LDAPSearchListener) {
                agent = ((LDAPSearchListener)listener).getMessageAgent();
            } else {
                agent = ((LDAPResponseListener)listener).getMessageAgent();
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
     *                 name and attributes of the new entry.<br><br>
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
        LDAPResponseListener listener =
            add(entry, (LDAPResponseListener)null, cons);
        ((LDAPResponse)(listener.getResponse())).chkResultCode();
        //checkForReferral( listener, 0, 0); // Search for referrals
        return;
    }

    /**
     *
     * Asynchronously adds an entry to the directory.
     *
     *  @param entry   LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.<br><br>
     *<br><br>
     *  @param listener  Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 listener object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener add(LDAPEntry entry,
                                    LDAPResponseListener listener)
        throws LDAPException
    {
        return add(entry, listener, defSearchCons);
    }

    /**
     * Asynchronously adds an entry to the directory, using the specified
     * constraints.
     *
     *  @param entry   LDAPEntry object specifying the distinguished
     *                 name and attributes of the new entry.<br><br>
     *<br><br>
     *  @param listener  Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 listener object is created internally.<br><br>
     *<br><br>
     *  @param cons   Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener add(LDAPEntry entry,
                                    LDAPResponseListener listener,
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
        if(entry == null || entry.getDN() == null) {
            // Invalid Entry parameter
            throw new LDAPException(
                    LDAPExceptionMessageResource.ENTRY_PARAM_ERROR,
                    LDAPException.PARAM_ERROR);
        }

        // convert Java-API LDAPEntry to RFC2251 AttributeList
        RfcAttributeList attrList = new RfcAttributeList();
        LDAPAttributeSet attrSet = entry.getAttributeSet();
        Enumeration enum = attrSet.getAttributes();
        while(enum.hasMoreElements()) {
            LDAPAttribute attr = (LDAPAttribute)enum.nextElement();
            ASN1SetOf vals = new ASN1SetOf();
            Enumeration attrEnum = attr.getByteValues();
            while(attrEnum.hasMoreElements()) {
                vals.add(new RfcAttributeValue((byte[])attrEnum.nextElement()));
            }
            attrList.add(new RfcAttributeTypeAndValues(
                new RfcAttributeDescription(attr.getName()), vals));
        }

        LDAPMessage msg =
                new LDAPMessage(
                    new RfcAddRequest(
                        new RfcLDAPDN(entry.getDN()),
                        attrList),
                    cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
    }

    //*************************************************************************
    // bind methods
    //*************************************************************************

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) as an LDAPv3 bind, using the specified name and
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
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public void bind(String dn,
                     String passwd)
        throws LDAPException
    {
        bind(LDAP_V3, dn, passwd, defSearchCons); // call bind() w/version 3
        return;
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) as an LDAPv3 bind, using the specified name,
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
     *<br><br>
     * @param cons     Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public void bind(String dn,
                     String passwd,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        bind(LDAP_V3, dn, passwd, cons); // call LDAPv3 bind()
        return;
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) as an LDAPv3 bind, using the specified name,
     * password, and listener.
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
     * @param listener Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 listener object is created internally. It is
     *                 recommended that the client blocks
     *                 until the listener returns a response.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     *
     */
    public LDAPResponseListener bind(String dn,
                                     String passwd,
                                     LDAPResponseListener listener)
                                     throws LDAPException
    {
        return bind(LDAP_V3, dn, passwd, listener, defSearchCons);
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) as an LDAPv3 bind, using the specified name,
     * password, listener, and constraints.
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
     * @param listener Handler for messages returned from a server in
     *                 response to this request. If it is null, a
     *                 listener object is created internally. It is
     *                 recommended that the client blocks
     *                 until the listener returns a response.
     *<br><br>
     * @param cons     Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public LDAPResponseListener bind(String dn,
                                     String passwd,
                                     LDAPResponseListener listener,
                                     LDAPConstraints cons)
                                     throws LDAPException
    {
        return bind(LDAP_V3, dn, passwd, listener, cons); // call LDAPv3 bind()
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name, password, and LDAP version.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The version of the LDAP protocol to use
     *                  in the bind, either LDAP_V2 or LDAP_V3.
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
     *
     */
    public void bind(int version,
                     String dn,
                     String passwd)
        throws LDAPException
    {
        bind(version, dn, passwd, defSearchCons);
        return;
    }

    /**
     *
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name, password, LDAP version,
     * and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, either LDAP_V2 or LDAP_V3.
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
                     String passwd,
                     LDAPConstraints cons)
        throws LDAPException
    {
        int msgId;
        Connection conn;
        LDAPResponseListener listener =
            bind(version, dn, passwd, (LDAPResponseListener)null, cons);
        // There can be only one msgId on this listener, find our Connection object
        // We make sure it is the right one, not one changed by clone/disconnect
        msgId = listener.getMessageIDs()[0];
        try {
            conn = listener.getMessageAgent().getMessage( msgId).getConnection();
        } catch( NoSuchFieldException ex) {
            throw new RuntimeException("Internal error, wrong messageID on bind");
        }
        LDAPResponse res = (LDAPResponse)listener.getResponse();
        if( res != null) {
            res.chkResultCode();
        }
        return;
    }

    /**
     *
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, and listener.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * has already authenticated, the old authentication is discarded.</p>
     *
     *
     *  @param version  The LDAP protocol version, either LDAP_V2 or LDAP_V3.
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
     *  @param listener  Handler for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   listener object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener bind(int version,
                                     String dn,
                                     String passwd,
                                     LDAPResponseListener listener)
        throws LDAPException
    {
        return bind(version, dn, passwd, listener, defSearchCons);
    }

    /**
     * Asynchronously authenticates to the LDAP server (that the object is
     * currently connected to) using the specified name, password, LDAP
     * version, listener, and constraints.
     *
     * <p>If the object has been disconnected from an LDAP server,
     * this method attempts to reconnect to the server. If the object
     * had already authenticated, the old authentication is discarded.</p>
     *
     *  @param version  The LDAP protocol version, either LDAP_V2 or LDAP_V3.
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
     *  @param listener  Handler for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   listener object is created internally.
     *<br><br>
     *  @param cons      Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener bind(int version,
                                     String dn,
                                     String passwd,
                                     LDAPResponseListener listener,
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

        if(dn == null)
            dn = "";

        if(passwd == null)
            passwd = "";

        LDAPMessage msg =
                new LDAPMessage(
                    new RfcBindRequest(
                        new ASN1Integer(version),
                        new RfcLDAPDN(dn),
                        new RfcAuthenticationChoice(
                            new ASN1Tagged(
                                new ASN1Identifier(ASN1Identifier.CONTEXT, false, 0),
                                new ASN1OctetString(passwd),
                                false))), // implicit tagging
                    cons.getServerControls());

        msgId = msg.getMessageID();
        bindProps = new BindProperties( version, dn, passwd, "simple", null, null);

        // The semaphore is released when the bind response is queued.
        conn.acquireBindSemaphore( msgId);

        LDAPResponseListener listen=sendRequest(msg,cons.getTimeLimit(),listener, bindProps);

        return listen;
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
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
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the Mechanism
     *                  Driver to obtain additional information required,
     *                  such as additional credentials.
     */
    public void bind(String dn,
                     Properties props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh)
                     throws LDAPException
    {
        bind( dn, props, cbh, defSearchCons);
        return;
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
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
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the Mechanism
     *                  Driver to obtain additional information required,
     *                  such as additional credentials.
     *<br><br>
     *  @param cons      Constraints specific to the operation.
     */
    public void bind(String dn,
                     Properties props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        throw new RuntimeException("LDAPConnection.bind(with mechanisms) is not Implemented.");
    }

    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
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
     *<br><br>
     *  @param mechanisms    An array of IANA-registered SASL mechanisms which
     *                       the client is willing to use for authentication.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the Mechanism
     *                  Driver to obtain additional information required,
     *                  such as additional credentials.
     */
    public void bind(String dn,
                     String[] mechanisms,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh)

                     throws LDAPException
    {
        bind( dn, mechanisms, props, cbh, defSearchCons);
        return;
    }
    /**
     * Authenticates to the LDAP server (that the object is currently
     * connected to) using the specified name and the specified set of
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
     *<br><br>
     *  @param mechanisms    An array of IANA-registered SASL mechanisms which
     *                       the client is willing to use for authentication.
     *<br><br>
     *  @param props    The optional qualifiers for the authentication
     *                  session.
     *<br><br>
     *  @param cbh      A class which may be called by the Mechanism
     *                  Driver to obtain additional information required,
     *                  such as additional credentials.
     */
    public void bind(String dn,
                     String[] mechanisms,
                     Hashtable props,
                     /*javax.security.auth.callback.CallbackHandler*/ Object cbh,
                     LDAPConstraints cons)
                     throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "saslBind(" + dn + ")");
        }
        int i;
        boolean found = false;

        for( i=0; i < mechanisms.length; i++) {
            if( mechanisms[i].equalsIgnoreCase("simple")) {
                found = true;
                break;
            }
        }

        if( found ) {
            String password = null;
            if( props != null) {
                password = (String)props.get("password");
            }
            bind( LDAP_V3, dn, password, defSearchCons);
        } else {
            throw new RuntimeException("LDAPConnection.bind(with mechanisms) is not Implemented.");
        }
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
     *                 comparison.<br><br>
     *<br><br>
     *  @param attr    The attribute to compare against the entry. The
     *                 method checks to see if the entry has an
     *                 attribute with the same name and value as this
     *                 attribute.
     *
     *  @return True if the entry has the value, and false if the
     *  entry does not have the value or the attribute.
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
     * Checks to see if an entry contains an attribute with a specified
     * value, using the specified constraints.
     *
     *  @param dn      The distinguished name of the entry to use in the
     *                 comparison.<br><br>
     *<br><br>
     *  @param attr    The attribute to compare against the entry. The
     *                 method checks to see if the entry has an
     *                 attribute with the same name and value as this
     *                 attribute.<br><br>
     *<br><br>
     *  @param cons    Constraints specific to the operation.
     *
     *  @return  Returns true if the entry has the value, and false if the
     *  entry does not have the value or the attribute.
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
        LDAPResponseListener listener =
            compare(dn, attr, (LDAPResponseListener)null, cons);
        LDAPResponse res = (LDAPResponse)listener.getResponse();

        if(res.getResultCode() == LDAPException.COMPARE_TRUE) {
            ret = true;
        }
        else if(res.getResultCode() == LDAPException.COMPARE_FALSE) {
            ret = false;
        }
        else {
            res.chkResultCode();
        }

        return ret;
    }

    /**
     *
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified listener.
     *
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.<br><br>
     *<br><br>
     *  @param attr    An attribute to compare.<br><br>
     *<br><br>
     *  @param listener   The handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    listener object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener compare(String dn,
                                        LDAPAttribute attr,
                                        LDAPResponseListener listener)
        throws LDAPException
    {
        return compare(dn, attr, listener, defSearchCons);
    }

    /**
     * Asynchronously compares an attribute value with one in the directory,
     * using the specified listener and contraints.
     *
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.<br><br>
     *<br><br>
     *  @param attr    An attribute to compare.<br><br>
     *<br><br>
     *  @param listener  Handler for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   listener object is created internally.<br><br>
     *<br><br>
     *  @param cons      Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener compare(String dn,
                                        LDAPAttribute attr,
                                        LDAPResponseListener listener,
                                        LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "compare(" + dn + ") compare value");
        }
        if(cons == null)
            cons = defSearchCons;

        String type = attr.getName();
        String value = attr.getStringValueArray()[0]; // get first value

        if(dn == null || type == null || value == null) {
            // Invalid parameter
            throw new LDAPException(LDAPExceptionMessageResource.PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);
        }

        LDAPMessage msg =
            new LDAPMessage(
                new RfcCompareRequest(
                    new RfcLDAPDN(dn),
                    new RfcAttributeValueAssertion(
                        new RfcAttributeDescription(type),
                        new RfcAssertionValue(value))),
                cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
    }

    //*************************************************************************
    // connect methods
    //*************************************************************************

    /**
     *
     *  Connects to the specified host and port
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
     *              name can include a trailing colon and port number.<br><br>
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
        conn = conn.destroyClone( true, host, port);
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
     *  @param dn      The distinguished name of the entry to delete.<br><br>
     *<br><br>
     *  @param cons    Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
     */
    public void delete(String dn, LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseListener listener =
            delete(dn, (LDAPResponseListener)null, cons);
        ((LDAPResponse)(listener.getResponse())).chkResultCode();
        return;
    }

    /**
     *
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory and returns the results to the specified listener.
     *
     *  @param dn      The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param listener  The handler for messages returned from a server in
     *                   response to this request. If it is null, a
     *                   listener object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public LDAPResponseListener delete(String dn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        return delete(dn, listener, defSearchCons);
    }

    /**
     * Asynchronously deletes the entry with the specified distinguished name
     * from the directory, using the specified contraints and listener.
     *
     *  @param dn      The distinguished name of the entry to delete.<br><br>
     *<br><br>
     *  @param listener   The handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    listener object is created internally.<br><br>
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     *
     */
    public LDAPResponseListener delete(String dn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "delete(" + dn + ")");
        }
        if(dn == null) {
            // Invalid DN parameter
            throw new LDAPException(LDAPExceptionMessageResource.DN_PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);
        }

        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg =
            new LDAPMessage(
                new RfcDelRequest(dn),
                cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
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
        disconnect(true);
        return;
    }

    /**
     * Disconnect from server
     *
     * @param how true if application call disconnect API, false if finalize.
     *
     */
    private void disconnect(boolean how)
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
     *
     * Provides a synchronous means to access extended, non-mandatory
     * operations offered by a particular LDAPv3 compliant server.
     *
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to and (2) an operation-specific
     *            sequence of octet strings or BER-encoded values.
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
     *            server this client is connected to and (2) an operation-specific
     *            sequence of octet strings or BER-encoded values.<br><br>
     *<br><br>
     * @param cons The constraints specific to the operation.
     *
     * @return An operation-specific object, containing an ID and either an octet
     * string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op,
                                                   LDAPSearchConstraints cons)
        throws LDAPException
    {

        // Call asynchronous API and get back handler to reponse listener
        LDAPResponseListener listener = extendedOperation(op, cons, (LDAPResponseListener)null);
        LDAPExtendedResponse response = (LDAPExtendedResponse) listener.getResponse();
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
     *            server this client is connected to and (2) an operation-specific
     *            sequence of octet strings or BER-encoded values.<br><br>
     *<br><br>
     * @param listener  The handler for messages returned from a server in
     *                  response to this request. If it is null, a listener
     *                  object is created internally.
     *
     * @return An operation-specific object, containing an ID and either an octet
     * string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                     LDAPResponseListener listener)
        throws LDAPException
    {

        return extendedOperation(op, defSearchCons, listener);
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
     *         specific sequence of octet strings or BER-encoded values.<br><br>
     *<br><br>
     * @param listener  The handler for messages returned from a server in
     *                  response to this request. If it is null, a listener
     *                  object is created internally.<br><br>
     *<br><br>
     * @param cons      The constraints specific to this operation.
     *
     * @return An operation-specific object, containing an ID and either an octet
     * string or BER-encoded values.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */

    public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                                  LDAPSearchConstraints cons,
                                                  LDAPResponseListener listener)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "extendedOperation(" + op.getValue() + ")");
        }
        // Use default constraints if none-specified
        if(cons == null)
            cons = defSearchCons;

        // error check the parameters
        if (op.getID() == null) {
            // Invalid extended operation parameter, no OID specified
            throw new LDAPException(LDAPExceptionMessageResource.OP_PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);
        }

        ASN1OctetString value =
            (op.getValue() != null) ? new ASN1OctetString(op.getValue()) : null;

        RfcExtendedRequest er = new RfcExtendedRequest(new RfcLDAPOID(op.getID()),
                                                value);

        LDAPMessage msg = new LDAPMessage(er, cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
    }

    //*************************************************************************
    // getResponseControls method
    //*************************************************************************

     /**
     *  Returns the Server Controls associated with the most recent response to
     *  a synchronous request on this connection object, or null
     *  if the latest response contained no Server Controls. The method
     *  always returns null for asynchronous requests. For asynchronous
     *  requests, the response controls are available in LDAPMessage.
     *
     *  @return The server controls associated with the most recent response to a
     *  synchronous request or null if the response contains no server controls.
     *
     * @see LDAPMessage#getControls()
     */
    public LDAPControl[] getResponseControls()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "getResponseControls()");
        }
        return responseCtls;
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
     *  @param dn     The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mod    A single change to be made to the entry.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify(String dn, LDAPModification mod)
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
     *  @param dn       The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mod      A single change to be made to the entry.<br><br>
     *<br><br>
     *  @param cons     The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify(String dn,
                       LDAPModification mod,
                       LDAPConstraints cons)
        throws LDAPException
    {
        LDAPModificationSet mods = new LDAPModificationSet();
        mods.add(mod);
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
     *  @param dn     Distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mods   A set of changes to be made to the entry.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public void modify(String dn, LDAPModificationSet mods)
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
     *  @param dn      The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mods    A set of changes to be made to the entry.<br><br>
     *<br><br>
     *  @param cons    The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an
     *                          error message and an LDAP error code.
     */
    public void modify(String dn,
                       LDAPModificationSet mods,
                       LDAPConstraints cons)
        throws LDAPException
    {
        LDAPResponseListener listener =
            modify(dn, mods, (LDAPResponseListener)null, cons);
        ((LDAPResponse)(listener.getResponse())).chkResultCode();
        return;
    }

    /**
     *
     * Asynchronously makes a single change to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method can change the value of an attribute,
     * add a new attribute value, or remove an existing attribute value.</p>
     *
     * <p>The LDAPModification object specifies both the change to be made and
     * the LDAPAttribute value to be changed.</p>
     *
     *  @param dn         Distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mod        A single change to be made to the entry.<br><br>
     *<br><br>
     *  @param listener   Handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    listener object is created internally.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModification mod,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        return modify(dn, mod, listener, defSearchCons);
    }

    /**
     * Asynchronously makes a single change to an existing entry in the
     * directory, using the specified constraints and listener.
     *
     * <p>For example, this modify method can change the value of an attribute,
     * add a new attribute value, or remove an existing attribute value.</p>
     *
     * <p>The LDAPModification object specifies both the change to be made
     * and the LDAPAttribute value to be changed.</p>
     *
     *  @param dn          Distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mod         A single change to be made to the entry.<br><br>
     *<br><br>
     *  @param listener    Handler for messages returned from a server in
     *                     response to this request. If it is null, a
     *                     listener object is created internally.<br><br>
     *<br><br>
     *  @param cons        Constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModification mod,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        LDAPModificationSet mods = new LDAPModificationSet();
        mods.add(mod);
        return modify(dn, mods, listener, cons);
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory.
     *
     * <p>For example, this modify method can change attribute values, add new
     * attribute values, or remove existing attribute values.</p>
     *
     *  @param dn         The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mods       A set of changes to be made to the entry.<br><br>
     *<br><br>
     *  @param listener   The handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    listener object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModificationSet mods,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        return modify(dn, mods, listener, defSearchCons);
    }

    /**
     * Asynchronously makes a set of changes to an existing entry in the
     * directory, using the specified constraints and listener.
     *
     * <p>For example, this modify method can change attribute values, add new
     * attribute values, or remove existing attribute values.</p>
     *
     *  @param dn         The distinguished name of the entry to modify.<br><br>
     *<br><br>
     *  @param mods       A set of changes to be made to the entry.<br><br>
     *<br><br>
     *  @param listener   The handler for messages returned from a server in
     *                    response to this request. If it is null, a
     *                    listener object is created internally.<br><br>
     *<br><br>
     *  @param cons       Constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPResponseListener modify(String dn,
                                       LDAPModificationSet mods,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "modify(" + dn + ")");
        }
        if(dn == null) {
            // Invalid DN parameter
            throw new LDAPException(LDAPExceptionMessageResource.DN_PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);
        }

        if(cons == null)
            cons = defSearchCons;

        // Convert Java-API LDAPModificationSet to RFC2251 SEQUENCE OF SEQUENCE
        ASN1SequenceOf rfcMods = new ASN1SequenceOf();
        for(int i=0; i<mods.size(); i++) {
            LDAPModification mod = mods.elementAt(i);
            LDAPAttribute attr = mod.getAttribute();

            // place modification attribute values in ASN1SetOf
            ASN1SetOf vals = new ASN1SetOf();
            Enumeration attrEnum = attr.getByteValues();
            while(attrEnum.hasMoreElements()) {
                vals.add(new RfcAttributeValue((byte[])attrEnum.nextElement()));
            }

            // create SEQUENCE containing mod operation and attr type and vals
            ASN1Sequence rfcMod = new ASN1Sequence();
            rfcMod.add(new ASN1Enumerated(mod.getOp()));
            rfcMod.add(new RfcAttributeTypeAndValues(
                new RfcAttributeDescription(attr.getName()), vals));

            // place SEQUENCE into SEQUENCE OF
            rfcMods.add(rfcMod);
        }

        LDAPMessage msg =
            new LDAPMessage(
                new RfcModifyRequest(
                    new RfcLDAPDN(dn),
                    rfcMods),
                cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
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
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
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
     *  @param dn         The distinguished name of the entry to retrieve.<br><br>
     *<br><br>
     *  @param cons       The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPEntry read(String dn,
                          LDAPSearchConstraints cons)
        throws LDAPException
    {
        return read(dn, (String[]) null, cons);
    }

    /**
     *
     * Synchronously reads the entry for the specified distinguished name (DN)
     * and retrieves only the specified attributes from the entry.
     *
     *  @param dn         The distinguished name of the entry to retrieve.<br><br>
     *<br><br>
     *  @param attrs      The names of the attributes to retrieve.
     *
     *  @exception LDAPException A general exception which includes an error
     *                            message and an LDAP error code.
     */
    public LDAPEntry read(String dn,
                          String[] attrs)
        throws LDAPException
    {
        return read(dn, attrs, defSearchCons);
    }

    /**
     *
     * Synchronously reads the entry for the specified distinguished name (DN),
     * using the specified constraints, and retrieves only the specified
     * attributes from the entry.
     *
     *  @param dn       The distinguished name of the entry to retrieve.<br><br>
     *<br><br>
     *  @param attrs    The names of the attributes to retrieve.<br><br>
     *<br><br>
     *  @param cons     The constraints specific to the operation.
     *
     *  @exception LDAPException A general exception which includes an error
     *                           message and an LDAP error code.
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

        return (sr.hasMoreElements()) ? sr.next() : null;
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
     *  @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public static LDAPEntry read(LDAPUrl toGet)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read(" + toGet.toString() + ")");
        }
        LDAPConnection conn = new LDAPConnection();
        conn.connect(toGet.getHost(),toGet.getPort());
        LDAPEntry toReturn = conn.read(toGet.getDN(), toGet.getAttributeArray());
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        conn.disconnect();
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
     * @param toGet       LDAP URL specifying the entry to read.<br><br>
     *<br><br>
     * @param cons       Constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public static LDAPEntry read(LDAPUrl toGet,
                                 LDAPSearchConstraints cons)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read(" + toGet.toString() + ")");
        }
        LDAPConnection conn = new LDAPConnection();
        conn.connect(toGet.getHost(),toGet.getPort());
        LDAPEntry toReturn = conn.read(toGet.getDN(), toGet.getAttributeArray(), cons);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        conn.disconnect();
        return toReturn;
    }

    //*************************************************************************
    // rename methods
    //*************************************************************************

    /**
     *
     * Synchronously renames an existing entry in the directory.
     *
     *  @param dn       The current distinguished name of the entry.<br><br>
     *<br><br>
     *  @param newRdn   The new relative distinguished name for the entry.<br><br>
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
        LDAPResponseListener listener =
            rename(dn, newRdn, newParentdn, deleteOldRdn,
                (LDAPResponseListener)null, cons);
        ((LDAPResponse)(listener.getResponse())).chkResultCode();
        return;
    }

    /*
     * rename
     */

    /**
     *
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
     *  @param listener       The handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        return rename(dn, newRdn, deleteOldRdn, listener, defSearchCons);
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
     *  @param listener       The handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        return rename(dn, newRdn, null, deleteOldRdn, listener, cons);
    }

    /**
     *
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
     *  @param listener       The handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       String newParentdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "rename(" + dn + "," + newRdn + "," + newParentdn + ")");
        }
        return rename(dn, newRdn, newParentdn,
                      deleteOldRdn, listener, defSearchCons);
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
     *  @param listener       The handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the operation.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPResponseListener rename(String dn,
                                       String newRdn,
                                       String newParentdn,
                                       boolean deleteOldRdn,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
        throws LDAPException
    {
        if(dn == null || newRdn == null) {
            // Invalid DN or RDN parameter
            throw new LDAPException(LDAPExceptionMessageResource.RDN_PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);
        }

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "rename(" + dn + "," + newRdn + "," + newParentdn + ")");
        }
        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg =
            new LDAPMessage(
                new RfcModifyDNRequest(
                    new RfcLDAPDN(dn),
                    new RfcRelativeLDAPDN(newRdn),
                    new ASN1Boolean(deleteOldRdn),
                    (newParentdn != null) ?
                        new RfcLDAPDN(newParentdn) : null),
                cons.getServerControls());

        return sendRequest(msg, cons.getTimeLimit(), listener, null);
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
        LDAPSearchListener listener =
            search(base, scope, filter, attrs, typesOnly,
                    (LDAPSearchListener)null, cons);

        if( cons == null )
            cons = defSearchCons;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                "search cons " + cons + "defSearchCons " + defSearchCons); 
        }
        return new LDAPSearchResults(this, listener, cons);
    }

    /**
     *
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
     *  @param listener       Handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
     */
    public LDAPSearchListener search(String base,
                                     int scope,
                                     String filter,
                                     String[] attrs,
                                     boolean typesOnly,
                                     LDAPSearchListener listener)
        throws LDAPException
    {
        return search(base, scope, filter, attrs, typesOnly,
                      listener, defSearchCons);
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
     *  @param listener       The handler for messages returned from a server in
     *                        response to this request. If it is null, a
     *                        listener object is created internally.
     *<br><br>
     *  @param cons           The constraints specific to the search.
     *
     * @exception LDAPException A general exception which includes an error
     * message and an LDAP error code.
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
        if( filter == null) {
            filter = "objectclass=*";
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
            "search(\"" + base + "\"," + scope + ",\"" + filter + "\")");
        }
        if(cons == null)
            cons = defSearchCons;

        LDAPMessage msg = new LDAPMessage(
            new RfcSearchRequest(
            new RfcLDAPDN(base),
            new ASN1Enumerated(scope),
            new ASN1Enumerated(cons.getDereference()),
            new ASN1Integer(cons.getMaxResults()),
            new ASN1Integer(cons.getServerTimeLimit()),
            new ASN1Boolean(typesOnly),
            new RfcFilter(filter),
            new RfcAttributeDescriptionList(attrs)),
        cons.getServerControls());

        MessageAgent agent;
        LDAPSearchListener listen = listener;
        if(listen == null) {
            agent = new MessageAgent();
            listen = new LDAPSearchListener( agent );
        } else {
            agent = listener.getMessageAgent();
        }

        try {
            agent.sendMessage( conn, msg, cons.getTimeLimit(), listen, null);
        } catch(LDAPException lex) {
            throw lex;
        }
        return listen;
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
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "search(" + toGet.toString() + ")");
        }
        LDAPConnection conn = new LDAPConnection();
        conn.connect(toGet.getHost(),toGet.getPort());
        LDAPSearchResults toReturn = conn.search(toGet.getDN(), toGet.getScope(),
        toGet.getFilter(), toGet.getAttributeArray(), false);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        conn.disconnect();
        return toReturn;
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
        LDAPConnection conn = new LDAPConnection();
        conn.connect(toGet.getHost(),toGet.getPort());
        LDAPSearchResults toReturn = conn.search(toGet.getDN(), toGet.getScope(),
            toGet.getFilter(), toGet.getAttributeArray(), false, cons);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "read: disconnect()");
        }
        conn.disconnect();
        return toReturn;
    }

    //*************************************************************************
    // helper methods
    //*************************************************************************

    /**
     * get an LDAPResponseListener for this request and send request
     *
     * @param msg the message to send
     *<br><br>
     * @param msg the timeout value
     *<br><br>
     * @param listen the response listener or null
     *
     * @return the LDAPResponseListener for this request
     */
    private LDAPResponseListener sendRequest(
                                        LDAPMessage msg,
                                        int timeout,
                                        LDAPResponseListener listener,
                                        BindProperties bindProps)
            throws LDAPException
    {
        MessageAgent agent;
        LDAPResponseListener listen = listener;
        if(listen == null) {
            agent = new MessageAgent();
            listen = new LDAPResponseListener( agent);
        } else {
            agent = listener.getMessageAgent();
        }

        agent.sendMessage( conn, msg, timeout, listen, bindProps);
        return listen;
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
     * Save the URL used to follow a referral for this connection
     *
     * @param url the referral URL
     */
    /* package */
    void setReferralURL( LDAPUrl url)
    {
        referralURL = url;
        return;
    }

    /**
     * Get the URL used to follow a referral for this connection
     *
     * @return the referral URL
     */
    /* package */
    LDAPUrl getReferralURL( )
    {
        return referralURL;
    }


    /**
     * get an LDAPConnection object so that we can follow a referral.
     * This function is never called if cons.getReferralFollowing() returns false.
     *
     * @param refs the array of referral strings
     *<br><br>
     * @param search true if a search operation
     *<br><br>
     * @param returnUrl the LDAPUrl that was connected to
     *
     * @return the new LDAPConnection object
     */
    /* package*/
    LDAPConnection getReferralConnection( String[] referrals,
                                          boolean search)
                    throws LDAPReferralException
    {
        LDAPConnection rconn = null;
        Throwable ex = null;
        LDAPReferralHandler rh = defSearchCons.getReferralHandler();
        int i = 0;
        // Check if we use LDAPRebind to get authentication credentials
        if( (rh == null) || (rh instanceof LDAPRebind)) {
            for( i = 0; i < referrals.length; i++) {
                // dn, pw are null in the default case (anonymous bind)
                String dn = null;
                String pw = null;
                try {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.referrals,   name +
                                                    "getReferralConnection: " +
                                                    "url=" + referrals[i]);
                    }
                    rconn = new LDAPConnection( conn.getSocketFactory());
                    // This connection created to follow a referral
                    rconn.setConstraints( defSearchCons);
                    if( search) {
                        rconn.setSearchConstraints( defSearchCons);
                    }
                    LDAPUrl url = new LDAPUrl(referrals[i]);
                    rconn.connect(url.getHost(),url.getPort());
                    if( rh != null) {
                        // Get application supplied dn and pw
                        LDAPRebindAuth ra = ((LDAPRebind)rh).getRebindAuthentication(
                            url.getHost(),url.getPort());
                        dn = ra.getDN();
                        pw = ra.getPassword();
                    }
                    rconn.bind( dn, pw);
                    ex = null;
                    rconn.setReferralURL( url);
                    rconn.setReferralList( referrals);
                    rconn.setActiveReferral( referrals[i]);
                    break;
                } catch( Exception lex) {
                    if( rconn != null) {
                        try {
                            if( Debug.LDAP_DEBUG) {
                                Debug.trace( Debug.referrals, name +
                                "getReferralConnection, exception binding for referral" +
                                ex.toString());
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
                    Debug.trace( Debug.referrals,   name +
                                                "getReferralConnection: " +
                                                "Call LDAPBind.bind()");
                }
                rconn = ((LDAPBind)rh).bind( referrals, this);
                // This connection created to follow a referral
                rconn.setReferralList( referrals);
            } catch( LDAPReferralException lex) {
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
                ldapex = new LDAPException(
                    LDAPExceptionMessageResource.SERVER_CONNECT_ERROR,
                    new Object[] { conn.getHost() },
                    LDAPException.CONNECT_ERROR, ex);
            }
            // Error attempting to follow a referral
            LDAPReferralException rex = new LDAPReferralException(
                    LDAPExceptionMessageResource.REFERRAL_ERROR,
                    ldapex);
            rex.setReferrals(referrals); 
            // Use last URL string for the failed referral
            rex.setFailedReferral( referrals[referrals.length-1]);
            throw rex;
        }

        /*
         * We had no errors, but just could not connect and authenticate
         * I'm not sure how we can get this
         */
        if( rconn == null) {
                // Could not create any connection to follow referral
                LDAPReferralException rex = new LDAPReferralException(
                        LDAPExceptionMessageResource.NO_CONNECT,
                        LDAPException.LOCAL_ERROR, null);
            rex.setReferrals(referrals); 
            // Use last URL string for the failed referral
            rex.setFailedReferral( referrals[referrals.length-1]);
            throw rex;
        }
                
        // We now have an authenticated connection to be used to follow the referral.
        return rconn;
    }

    /**
     * Check the result code and follow referrals if necessary.
     * This function is called only by synchronous requests.
     * Search responses only come here if only referral following is
     * enabled and if we are processing a SearchResultReference
     * or a SearchResponse with a status of REFERRAL, i.e. we are
     * going to follow a referral.
     *
     * @param listen The LDAPResponseListener for this request
     * <br><br>
     * @param hopCount the maximum hops configured for referrals
     * <br><br>
     * @param referral The referral string from a search response
     * <br><br>
     * @param The current hop count
     * <br><br>
     * @param The response message
     * <br><br>
     * @param An optional array list used to store the LDAPConnection objects
     *        used in following the referral.
     *
     * @return The array list used to store the LDAPConnection objects
     *        used in following the referral.  The list will be empty
     *        if there were none.
     */
    /* package */
    ArrayList checkForReferral( LDAPListener listen,
                                LDAPConstraints cons,
                                LDAPMessage msg,
                                String[] searchReferral,
                                int hopCount,
                                boolean searchReference,
                                ArrayList referralList)
                    throws LDAPException
    {
        ArrayList refList = referralList;
        LDAPConnection rconn = null; // new conn for following referral
        LDAPMessage origMsg;
        
        // Get a place to store new connections
        if( refList == null) {
            refList = new ArrayList( cons.getHopLimit()); 
        }
        if( ! cons.getReferralFollowing()) {
            if( Debug.LDAP_DEBUG) {
                if( (searchReferral != null) || (msg != null)) {
                    // If referral following is off, should never get here
                    // LDAPCOnnection: checkForReferral: internal error
                    throw new LDAPReferralException(
                        LDAPExceptionMessageResource.REFERRAL_INTERNAL,
                        LDAPException.LOCAL_ERROR, null);
                }
            }
            /* Only come here for synchronous requests
             * If we come here, this is NOT a search response.
             * A search response comes here only if following a referral.
             * Note: there is never more than one outstanding response
             * at any one time, for a non search.
             */
            LDAPResponse resp = (LDAPResponse)listen.getResponse();
            // Throws an Exception for any nonzero result code
            resp.chkResultCode();
        } else {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.referrals,   name +
                    "Check for referrals, reference = " + searchReference);
            }        
            // Following referrals or search reference
            String [] refs;             // referral list
            if( (searchReferral != null)) {
                // Search continuation reference from a search request
                refs = searchReferral;
                origMsg = msg.getASN1Object().getRequestingMessage();
            } else {
                // Not a search request
                LDAPResponse resp = (LDAPResponse)listen.getResponse();
                if( resp.getResultCode() != LDAPException.REFERRAL) {
                    // Not referral result,throw Exception if nonzero result
                    resp.chkResultCode();
                    return refList;
                }
                // We have a referral response
                refs = resp.getReferrals();
                origMsg = resp.getASN1Object().getRequestingMessage();
            }
            LDAPUrl refUrl;             // referral represented as URL
            try {
                // increment hop count, check max hops
                if( hopCount++ > cons.getHopLimit()) {
                    throw new LDAPException("Max hops exceeded",
                        LDAPException.REFERRAL_LIMIT_EXCEEDED);
                }
                // Get a connection to follow the referral
                rconn = getReferralConnection( refs, searchReference);
                refUrl = rconn.getReferralURL();
                refList.add( rconn);
                
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.referrals,   name +
                        listen.getMessageIDs().length + "Referral URL " + refUrl.toString());
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
                    if( listen instanceof LDAPResponseListener) {
                        agent = ((LDAPResponseListener)listen).getMessageAgent();
                    } else {
                        agent = ((LDAPSearchListener)listen).getMessageAgent();
                    }
                    agent.sendMessage( rconn.getConnection(), newMsg,
                            defSearchCons.getTimeLimit(), listen, null);
                } catch(LocalException ex) {
                    // Error ending request to referred server
                    LDAPReferralException rex = new LDAPReferralException(
                         LDAPExceptionMessageResource.REFERRAL_SEND,
                         LDAPException.CONNECT_ERROR, null, ex);
                    rex.setReferrals( searchReferral);
                    rex.setFailedReferral( rconn.getConnection().getActiveReferral());
                    throw rex;                     
                }

                if( searchReferral == null) {
                    // For non searches, When all responses are complete,
                    // the stack unwinds, back to the original and returns
                    // to the application.
                    // An exception is thrown for an error
                    refList = checkForReferral( listen, cons, null, null,
                                hopCount, false, refList);
                } else {
                    // For search, just return to LDAPSearchResults object
                    return refList;
                }
            } catch (Exception ex) {
                if( ex instanceof LDAPReferralException) {
                    throw (LDAPReferralException)ex;
                } else {
                    // Connecting to referred server
                    LDAPReferralException rex = new LDAPReferralException(
                        LDAPExceptionMessageResource.REFERRAL_ERROR,
                        (LDAPException)ex);
                    rex.setReferrals( refs);
                    if( rconn != null) {
                        rex.setFailedReferral( rconn.getConnection().getActiveReferral());
                    } else {
                        rex.setFailedReferral( refs[refs.length - 1]);
                    }
                    throw rex;        
                }
            }
            return refList;
        }
        return refList;
    }

    /**
     * Builds a new request replacing dn, scope, and filter where approprate
     *
     * @param msg the original LDAPMessage to build the new request from
     * <br><br>
     * @param url the referral url
     *
     * @return a new LDAPMessage with appropriate information replaced
     */

    private
    LDAPMessage rebuildRequest( LDAPMessage msg, LDAPUrl url, boolean reference)
            throws LDAPException, CloneNotSupportedException
    {
        RfcLDAPMessage rfcMsg = msg.getASN1Object();
        ASN1Identifier id = rfcMsg.getProtocolOp().getIdentifier();
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.referrals, name +
            "rebuildRequest: original request = " + id.getTag());
        }

        String dn = url.getDN(); // new base
        String filter = null;
        Integer scope = null;

        switch( id.getTag()) {
            case RfcProtocolOp.SEARCH_REQUEST:
                if( reference) {
                    filter = url.getFilter();
                }
                // scope ??????? Fix scope here
                break;
            case RfcProtocolOp.MODIFY_REQUEST:
            case RfcProtocolOp.ADD_REQUEST:
            case RfcProtocolOp.DEL_REQUEST:
            case RfcProtocolOp.MODIFY_DN_REQUEST:
            case RfcProtocolOp.COMPARE_REQUEST:
                break;
            case RfcProtocolOp.EXTENDED_REQUEST:
                dn = null;  // base doesn't make sense here
                break;
            case RfcProtocolOp.ABANDON_REQUEST:
            case RfcProtocolOp.BIND_REQUEST:
            case RfcProtocolOp.UNBIND_REQUEST:
            default:
                throw new LDAPException(
                     // "Referral doesn't make sense for command"
                    LDAPExceptionMessageResource.IMPROPER_REFERRAL,
                    new Object[] {
                        new Integer(rfcMsg.getProtocolOp().getIdentifier().getTag())
                    },
                    LDAPException.LOCAL_ERROR);
        }

        RfcLDAPMessage newRfcMsg = (RfcLDAPMessage)rfcMsg.dupMessage( dn, filter, scope);
        
        return new LDAPMessage( newRfcMsg);
    }

    /**
     * Marks this LDAPConnection as one created to follow a referral
     */
    /*package*/
    void setReferralList( String[] referrals)
    {
        conn.setReferralList( referrals);
        return;
    }

    /**
     * Indiciates if this LDAPConnection as one created to follow a referral
     *
     * @return true if LDAPConnection created to follow a referral on a
     * synchronous request.
     */
    /*package*/
    String[] getReferralConnection()
    {
        return conn.getReferralList();
    }

    /**
     * Sets the active referral for this connection
     */
    /*package*/
    void setActiveReferral( String referral)
    {
        conn.setActiveReferral( referral);
        return;
    }
    
    /*
     * Release referral connections
     *
     * @param list the list of the connections
     *
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
}

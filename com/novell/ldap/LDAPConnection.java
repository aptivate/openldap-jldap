/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConnection.java,v 1.42 2000/10/04 17:15:51 smerrill Exp $
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

import java.io.*;
import java.util.*;

import com.novell.ldap.client.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.protocol.*;

/*
 * 4.6 public class LDAPConnection
 *                implements LDAPv3, Cloneable
 */
 

/**
 *
 *  Represents the central class that encapsulates the connection
 *  to a directory server through the LDAP protocol. 
 *
 *  <p>The LDAPConnection class implements the LDAPv2 and LDAPv3
 *  interfaces. An LDAPConnection object is not connected on 
 *  construction and can only be connected to one server at one 
 *  port. Multiple threads may share this single connection, and an
 *  application may have more than one LDAPConnection object, connected
 *  to the same or different directory servers.</p>
 *
 */
public class LDAPConnection implements
   LDAPv3, Cloneable {

   private Connection conn = null;
   private LDAPSocketFactory socketFactory = null;
   private String host = null;
   private int port = 0;
   private LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
   private int authenticationVersion = 3;
   private String authenticationPassword = null;
   private String authenticationDN = null;
   private String authenticationMethod = "none";
   private Hashtable authenticationHash = null;

  /**
   * A setOption key that specifies whether aliases are dereferenced.
   *
   * <p>Can be set to the following values:</p>
   *<ul>
   *<li>DEREF_NEVER = 0</li>
   *<li>DEREF_SEARCHING = 1</li>
   *<li>DEREF_FINDING = 2</li>
   *<li>DEREF_ALWAYS = 3</li>
   *</ul>
   * <p> Default value: DEREF = 0 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #DEREF_NEVER
   * @see #DEREF_FINDING
   * @see #DEREF_SEARCHING
   * @see #DEREF_ALWAYS
   */
   public static final int DEREF = 2;

   /**
   * A setOption key, the corresponding value
   * of which controls the the constraint specifying
   * if aliases are dereferenced.
   *
   * <p> DEREF_NEVER = 0 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #DEREF
   */
   public static final int DEREF_NEVER  = 0;
   
   /**
   * A setOption key value that specifies the constraint
   * that aliases are dereferenced when
   * searching the entries beneath the starting point but not when 
   * searching for the starting entry.
   *
   * <p> DEREF_SEARCHING = 1 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #DEREF
   */
   public static final int DEREF_SEARCHING = 1;
   
   /**
   * A setOption key value that specifies the constraint
   * that aliases are dereferenced when
   * searching for the starting entry but are not dereferenced when
   * searching the entries beneath the starting point.
   *
   * <p> DEREF_FINDING = 2 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #DEREF
   */
   public static final int DEREF_FINDING = 2;
   
  /**
   * A setOption key value that specifies the constraint
   * that aliases are dereferenced always 
   * (when searching for the starting entry and when
   * searching the entries beneath the starting point).
   *
   * <p> DEREF_ALWAYS = 3 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #DEREF
   */
   public static final int DEREF_ALWAYS = 3;
   
  /**
   * A setOption key which
   * specifies the maximum number of search results to
   * return.
   *
   * <p>If this option is set to 0, there is no maximum limit.
   *
   * <p> The value must be an Integer.</p>
   * <p>Default value: 1000</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int SIZELIMIT = 3;

  /**
   * A setOption key which
   * specifies the maximum number of milliseconds the client
   * waits for an operation to complete. 
   *
   * <p>If this option is set to 0, there is no maximum time limit.</p>
   *
   * <p> The value must be an Integer.</p>
   * Default value: 0
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int TIMELIMIT = 4;
   
  /**
   * A setOption key which
   * specifies the maximum number of seconds the server 
   * is to wait when returning search results.
   *
   * <p>This option is only recoginzed on search operations.</p>
   * <p> If this option is set to 0, there is no maximum time limit.</p>
   * <p> The value must be an Integer.</p>
   * <p>Default value: 0 
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int SERVER_TIMELIMIT = 5;
   
  /**
   * A setOption key which specifies the 
   * encoding format for strings.
   *
   * <p>Can be set to the following values:</p>
   * <ul>
   *   <li>UTF8</li>
   *   <li>T61</li>
   * </ul>
   * <p> Default value: UTF8
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #UTF8
   * @see #T61
   */
   public static final int STRING_FORMAT = 6;

  /**
   * A setOption key value which specifies that
   * strings are encoded in UTF8 format.
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #T61
   * @see #STRING_FORMAT
   */
   public static final int UTF8 = 0;

  /**
   * A setOption key which specifies that
   * strings are encoded in T61 format.
   *
   * <p>T61 is LDAPv2 encoding. NDS does not support this type of encoding.</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   * @see #UTF8
   * @see #STRING_FORMAT
   */
   public static final int T61 = 1;
   
  /**
   * A setOption key which
   * specifies whether or not the client follows referrals
   * automatically.
   *
   * <p> If false, an LDAPReferralException is raised when a referral is
   * detected. If true, referrals are followed automatically.</p>
   *
   * <p> Referrals of any type other than to an LDAP server (for example, 
   * a referral URL other than ldap://something) are ignored on automaic
   * referral following.</p>
   *
   * <p> The value must be a Boolean.</p>
   * <p>Default value: false</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int REFERRALS = 8;
   
  /**
   * A setOption key which
   * specifies an object that implements the LDAPRebind
   * interface.
   *
   * <p>A user of the class library must define the LDAPRebind class 
   * and the getREbindAuthentication method that will be used to get
   * the distinguished name and the password to use for authentication.</p>
   *
   * <p>If this value is null and REFERRALS is true, referrals are followed
   * with an anonymous bind (no authentication).</p>
   *
   * <p> The value must be an LDAPRebind object.</p>
   * <p>Default value: null</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int REFERRALS_REBIND_PROC = 9;
   
   /**
   * A setOption key which
   * specifies the maximum number of referrals in a sequence
   * that the client will follow.
   *
   * <p>For example, if REFERRALS_HOP_LIMIT is set to 5, the client follows
   * no more than 5 referrals in a row when resolving a single LDAP 
   * request.</p>
   *
   * <p> The value must be an Integer.</p>
   * <p>Default value: 10
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int REFERRALS_HOP_LIMIT = 10;
   
   /**
   * A setOption key which
   * specifies an object that can process an authentication
   * request, overriding the default authentication behavior.
   *
   * <p> The object is typically used for processing authentication when
   * following referrals.</p>
   *
   * <p> The value must be an LDAPBind object.</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int BIND = 13;
   
   /**
   * A setOption key which
   * specifies the number of search results to return at a
   * time.
   *
   * <p>For example, if BATCHSIZE is 1, enumerating an LDAPSearchResults
   * blocks only until one entry is available. If it is set to 0, enumerating
   * blocks until all entries have been retrieved from the server.</p>
   *
   * <p> The value must be an Integer.</p>
   * <p>Default value: 1 </p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int BATCHSIZE = 20;

  /**
   * Specifies the LDAPv2 protocol version.
   *
   * <p>You can use this identifier with the LDAPConnection.bind or
   * LDAPConnection.connect methods
   * in the version parameter to specify an LDAPv2 bind.</p>
   *
   * @see LDAPv2#setOption(int, java.lang.Object)
   * @see LDAPv2#getOption(int)
   */
   public static final int LDAP_V2 = 2;
   
  /**
   * Specifies the LDAPv3 protocol version.
   *
   * <p>You can use this identifier with the LDAPConnection.bind or
   * LDAPConnection.connect methods
   * in the version parameter to specify an LDAPv3 bind.</p>
   */
   public static final int LDAP_V3 = 3;

  /**
   *The default port number for LDAP servers.
   *
   * <p>You can use this identifier to specify the port when using the
   * LDAPConnection.connect method to connect to an LDAP server running 
   * on port 389.</p>
   */
   public static final int DEFAULT_PORT = 389;


  /**
   *The default SSL port number for LDAP servers.
   *
   * <p>You can use this identifier to specify the port when using the
   * LDAPConnection.connect method to connect to an LDAP server listening 
   * on port 636 for SSL connections.</p>
   */
   public static final int DEFAULT_SSL_PORT = 636;

   /*
    * Constructors
    */

   /**
    * Constructs a new LDAPConnection object, which represents a connection
    * to an LDAP server.
    *
    * <p>Calling the constructor does not actually establish the connection.
    * To connect to the LDAP server, use the connect method.</p>
    */
   public LDAPConnection()
   {
      return;
   }

   /**
    * Constructs a new LDAPConnection object, which will use the supplied
    * class factory to construct a socket connection during
    * LDAPConnection.connect method.
    *
    *  @param factory     An object capable of producing a Socket.
    */
   public LDAPConnection(LDAPSocketFactory factory)
   {
      socketFactory = factory;
      return;
   }

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
    * @return A copy of the object.
    */
   public Object clone()
   {
      throw new RuntimeException("Method LDAPConnection.clone not implemented");
   }

   /*
    * 4.6.3 finalize
    */

   /**
    * Closes the connection, if open, and releases any other resources held
    * by the object.
    *
    * @exception LDAPException A general exception which includes an error
    * message and an LDAP error code.
    */
   public void finalize()
      throws LDAPException
   {
      disconnect();
      return;
   }

   /*
    * 4.6.4 getAuthenticationDN
    */

   /**
    * Returns the distinguished name (DN) used for authentication by this
    * object. Null is returned if no authentication has been performed.
    *
    * @return The distinguished name if object is authenticated; otherwise,
    * null.
    */
   public String getAuthenticationDN()
   {
      return authenticationDN;
   }

   /*
    * 4.6.5 getAuthenticationMethod
    */

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
      return( authenticationMethod);
   }

   /**
    * Returns the authentication properties for SASL authentication.
    *
    * <p> Null is returned if no authentication has been performed
    * or no authentication Hashtable is present.</p>
    *
    * @return The Hashtable used for authentication information or null if the 
    * object is not present or not authenticated. The object returned can
        * be either of type Hashtable or Properties.
    */
   public Hashtable getAuthenticationQualifiers()
   {
      return authenticationHash;
   }

   /**
    * Returns the protocol version uses to authenticate
    *
    * <p> 0 is returned if no authentication has been performed.</p>
    *
    * @return The protol version used for authentication or 0 
    * not authenticated.
    */
   public int getAuthenticationVersion()
   {
      return authenticationVersion;
   }

   /**
    * Returns the password used for simple authentication by this object.
    *
    * <p> Null is returned if no authentication has been performed.</p>
    *
    * @return The password used for simple authentication or null if the 
    * object is not authenticated.
    */
   public String getAuthenticationPassword()
   {
      return authenticationPassword;
   }

   /**
    * Returns the host name of the LDAP server to which the object is or
    * was last connected, in the format originally specified.
    *
    * @return The host name of the LDAP server to which the object last
    * connected or null if the object has never connected.
    */
   public String getHost()
   {
      return this.host;
   }

   /**
    * Returns the stream used by the connection object for receiving data
    * from the LDAP server.
    *
    * @return The stream the connection object used for receiving data,
    * or null if the object has never connected.
    */
   public InputStream getInputStream()
   {
      return isConnected() ? conn.getInputStream() : null;
   }

   /**
    * Returns the stream used by the connection object to send data to the
    * LDAP server.
    *
    * @return The stream the connection object used for sending data
    * or null if the object has never connected.
    
    */
   public OutputStream getOutputStream()
   {
      return isConnected() ? conn.getOutputStream() : null;
   }

   /**
    * Returns the port number of the LDAP server to which the object is or
    * was last connected.
    *
    * @return The port number of the LDAP server to which the object last
    * connected or -1 if the object has never connected.
    */
   public int getPort()
   {
      return this.port;
   }

   /**
    * Returns a property of a connection object.
    *
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
    *
    *  <p>A deep copy of the property is provided where applicable; a
    *  client does not need to clone the object received.</p>
    *
    *  @return The requested property.
    *
    *  @exception LDAPException Thrown if the requested property is not
    *  available.
    */
   public Object getProperty(String name)
      throws LDAPException
   {
      throw new RuntimeException("Method LDAPConnection.getProperty not implemented");
   }

   /**
    * Returns the set of constraints that apply to all operations performed
    * through this connection (unless a different set of constraints is
    * specified when calling an operation method).
    *
    * <p> The getOption method can be used to get individual
    * constraints (rather than getting the entire set of constraints).</p>
    *
    * @return The set of default contraints that apply to this connection.
    *
    * @see #setConstraints(LDAPConstraints)
    * @see #getOption(int)
    */
   public LDAPConstraints getConstraints()
   {
      return this.defSearchCons;
   }

   /**
    * Returns the set of constraints that apply to search operations
    * performed through this connection (unless a different set of
    * constraints is specified when calling the search operation method).
    *
    * @return The set of default search contraints that apply to 
    * this connection.
    */
   public LDAPSearchConstraints getSearchConstraints()
   {
      return this.defSearchCons;
   }

   /**
    * Returns the LDAPSocketFactory used to establish a connection to a
    * server.
    *
    * @return The LDAPSocketFactory used to establish a connection.
    */
   public LDAPSocketFactory getSocketFactory()
   {
      return socketFactory;
   }

   /**
    * Indicates whether the object has authenticated to the connected LDAP
    * server.
    *
    * @return True if the object has authenticated; false if it has not
    * authenticated.
    */
   public boolean isBound()
   {
      return isConnected() ? conn.isBound() : false;
   }

   /**
    * Indicates whether the connection represented by this object is open
    * at this time.
    *
    * @return  True if connection is open; false if the connection is closed.
    */
   public boolean isConnected()
   {
      return conn != null;
   }

   /**
    * Sets the constraints that apply to all operations performed through
    * this connection (unless a different set of constraints is specified
    * when calling an operation method).
    *
    * <p> The setOption method can be used to set individual
    * constraints (rather than setting the entire set of constraints).</p>
    *
    * @param cons  Contraints to set
    *
    * @see #getConstraints()
    * @see #setOption(int, Object)
    */
   public void setConstraints(LDAPConstraints cons)
   {
      this.defSearchCons.setHopLimit(cons.getHopLimit());
      this.defSearchCons.setBindProc(cons.getBindProc());
      this.defSearchCons.setRebindProc(cons.getRebindProc());
      this.defSearchCons.setReferrals(cons.getReferrals());
      this.defSearchCons.setTimeLimit(cons.getTimeLimit());
      this.defSearchCons.setClientControls(cons.getClientControls());
      this.defSearchCons.setServerControls(cons.getServerControls());
      return;
   }

   /**
    * Sets the stream used by the connection object for receiving data from
    * the LDAP server.
    *
    * @param stream The input stream for receiving data.
    */
   public void setInputStream(InputStream stream)
   {
      if(isConnected()) {
         conn.setInputStream(stream);
      }
      return;
   }

   /**
    * Sets the stream used by the connection object to send data to the
    * LDAP server.
    *
    * @param stream The output stream for sending data.
    *
    */
   public void setOutputStream(OutputStream stream)
   {
      if(isConnected()) {
         conn.setOutputStream(stream);
      }
      return;
   }

   /**
    * Sets a property of a connection object.
    *
    * <p>No property names which can be set have been defined at this time. </p>
    * 
    * 
    * @param name    Name of the property to set.<br><br>
    *
    * @param value   Value to assign to the property.
    *
    * @exception LDAPException Thrown if the specified
    *                 property is not supported.
    */
   public void setProperty(String name, Object value)
      throws LDAPException
   {
      throw new RuntimeException("Method LDAPConnection.setProperty not implemented");
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
    */
   public void setSearchConstraints(LDAPSearchConstraints cons)
   {
      this.defSearchCons.setBatchSize( cons.getBatchSize() );
      this.defSearchCons.setDereference( cons.getDereference() );
      this.defSearchCons.setMaxResults( cons.getMaxResults() );
      this.defSearchCons.setServerTimeLimit( cons.getServerTimeLimit() );
      return;
   }

   /**
    * Establishes the default LDAPSocketFactory used to establish a
    * connection to a server.
    *
    * <p>The setSocketFactory method is implemented as once-only. 
    * It should be called before the first connect method. If called 
    * (for the first time) after connecting, the new factory will not
    * be used until or unless a new connection is attempted 
    * with the object.</p>
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
    */
   public void setSocketFactory(LDAPSocketFactory factory)
   {
      socketFactory = factory;
      return;
   }

    //*************************************************************************
    // Below follows all of the LDAP protocol operation methods
    //*************************************************************************

    //*************************************************************************
    // abandon methods
    //*************************************************************************

   /*
    * See LDAPv2 Interface
    */
   public void abandon(LDAPSearchResults results)
      throws LDAPException
   {
      results.abandon();
      return;
   }
   
   /*
   * 4.39.1 abandon
   */

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
   
   /*
   *(Not yet in the draft)
   */

   /**
    *  Abandons a search operation for a listener, using the specified
    *  constraints.
    *
    *  @param id The ID of the asynchronous operation to abandon. 
    *            The ID can be obtained from the search
    *            listener for the operation.<br><br>
    *
    *  @param cons The contraints specific to the operation. 
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(int id, LDAPConstraints cons)
      throws LDAPException
   {
      validateConn();

//    try {
//       conn.writeMessage(new AbandonRequest(conn.getMessageID(), id,
//                                            cons.getClientControls(), ldapv3
//                                            ).getLber());
//    }
//    catch(IOException ioe) {
//       throw new LDAPException("Communication error.",
//                               LDAPException.OTHER);
//    }

      // We need to inform the LDAPListener which owns this messageID to
      // remove it from the queue.
      conn.abandon(id);
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
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(LDAPSearchListener listener)
      throws LDAPException
   {
      if(listener != null) {
         int[] msgIds = listener.getClientListener().getMessageIDs();
         for(int i=0; i<msgIds.length; i++) {
            abandon(msgIds[i], defSearchCons);
         }
      }
   }

   /**
    * Abandons all non-search operations for a listener.
    *
    * <p>All operations in progress which are managed by the listener 
    * are abandoned.</p>
    *
    *  @param listener  The handler returned for messages from a
    *                   search request. 
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(LDAPResponseListener listener)
      throws LDAPException
   {
      if(listener != null) {
         int[] msgIds = listener.getClientListener().getMessageIDs();
         for(int i=0; i<msgIds.length; i++) {
            abandon(msgIds[i], defSearchCons);
         }
      }
   }

    //*************************************************************************
    // add methods
    //*************************************************************************

   /*
   * See LDAPv2 Interface
   */
   public void add(LDAPEntry entry)
      throws LDAPException
   {
      add(entry, defSearchCons);
      return;
   }

   /*
   * See LDAPv2 Interface
   */
   public void add(LDAPEntry entry,
                   LDAPConstraints cons)
      throws LDAPException
   {
      LDAPResponseListener listener =
         add(entry, (LDAPResponseListener)null, cons);
      listener.getResponse().chkResultCode();
      return;
   }
   
   /*
   * 4.39.2 add
   */

   /**
    *
    * Asynchronously adds an entry to the directory.
    *
    *  @param entry   LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.<br><br>
    *
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
    *
    *  @param listener  Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally.<br><br>
    *
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
      validateConn();
      LDAPListener clientListener;

      if(cons == null)
         cons = defSearchCons;

      // error check the parameters
      if(entry == null || entry.getDN() == null)
         throw new LDAPException("Invalid parameter",
                                 LDAPException.PARAM_ERROR);

      // should we make sure that entry has attributes that have values
      // before trying to encode them?
//    if(attr.size() == 0) {
//       throw new LDAPException(attr.getName() + ": has no values.",
//                               LDAPException.CONSTRAINT_VIOLATION);
//    }

//    LDAPRequest req = new AddRequest(entry, conn.getMessageID(),
//                                     cons.getClientControls(), ldapv3);

      // convert Java-API LDAPEntry to RFC2251 AttributeList
      AttributeList attrList = new AttributeList();
      LDAPAttributeSet attrSet = entry.getAttributeSet();
      Enumeration enum = attrSet.getAttributes();
      while(enum.hasMoreElements()) {
         LDAPAttribute attr = (LDAPAttribute)enum.nextElement();
         ASN1SetOf vals = new ASN1SetOf();
         Enumeration attrEnum = attr.getByteValues();
         while(attrEnum.hasMoreElements()) {
            vals.add(new AttributeValue((byte[])attrEnum.nextElement()));
         }
         attrList.add(new AttributeTypeAndValues(
            new AttributeDescription(attr.getName()), vals));
      }

      LDAPMessage msg =
            new LDAPMessage(
                new AddRequest(
                    new com.novell.ldap.protocol.LDAPDN(entry.getDN()),
                    attrList),
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

      try {
         clientListener.writeMessage(msg, cons.getTimeLimit());
      }
      catch(IOException ioe) {
         // do we need to remove message id here?

         throw new LDAPException("Communication error.",
                                 LDAPException.OTHER);
      }

      return listener;
   }

    //*************************************************************************
    // bind methods
    //*************************************************************************

    /*
    * See LDAPv2 Interface
    */
   public void bind(String dn,
                    String passwd)
      throws LDAPException
   {
      bind(LDAP_V2, dn, passwd, defSearchCons); // call LDAPv2 bind()
      return;
   }

    /*
    * See LDAPv2 Interface
    */
   public void bind(String dn,
                    String passwd,
                    LDAPConstraints cons)
                    throws LDAPException
   {
      bind(LDAP_V2, dn, passwd, cons); // call LDAPv2 bind()
      return;
   }

    /*
    * See LDAPv2 Interface
    */
   public LDAPResponseListener bind(String dn,
                                    String passwd,
                                    LDAPResponseListener listener)
                                    throws LDAPException
   {
      return bind(LDAP_V2, dn, passwd, listener, defSearchCons); // call LDAPv2 bind()
   }

    /*
    * See LDAPv2 Interface
    */
   public LDAPResponseListener bind(String dn,
                                    String passwd,
                                    LDAPResponseListener listener,
                                    LDAPConstraints cons)
                                    throws LDAPException
   {
      return bind(LDAP_V2, dn, passwd, listener, cons); // call LDAPv2 bind()
   }

   /*
    * See LDAPV3 (4.40.1)
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
      LDAPResponseListener listener =
         bind(version, dn, passwd, (LDAPResponseListener)null, cons);
      LDAPResponse res = listener.getResponse();

      if(res.getResultCode() == LDAPException.SUCCESS) {
          conn.setBound();
      }

      res.chkResultCode();
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
      LDAPListener clientListener;
      validateConn();

      if(cons == null)
         cons = defSearchCons;

      if(dn == null)
         dn = "";

      if(passwd == null)
         passwd = "";

      switch(version) {
         case LDAP_V3:
            authenticationVersion = version;
            break;
         case LDAP_V2:
            authenticationVersion = version;
            break;
         default:
            throw new LDAPException("Protocol version " + version +
                                    " not supported",
                                    LDAPException.PROTOCOL_ERROR);
      }

      LDAPMessage msg =
            new LDAPMessage(
                new BindRequest(
                    new ASN1Integer(version),
                    new com.novell.ldap.protocol.LDAPDN(dn),
                    new AuthenticationChoice(
                        new ASN1Tagged(
                            new ASN1Identifier(ASN1Identifier.CONTEXT, false, 0),
                            new ASN1OctetString(passwd),
                            false))), // implicit tagging
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

      try {
         clientListener.writeMessage(msg, cons.getTimeLimit());
      }
      catch(IOException ioe) {
         // do we need to remove message id here?

         throw new LDAPException("Communication error.",
                                 LDAPException.OTHER);
      }

      setAuthenticationInfo( dn, passwd, "simple", null);
//    if(passwd != null) {
//       req.getLber().reset(); // clear copy of passwd
//    }

      return listener;
   }

   /*
    * See LDAPv3
    */
   public void bind(String dn,
                    Properties props,
                    /*javax.security.auth.callback.CallbackHandler*/ Object cbh)
                    throws LDAPException
   {
         throw new LDAPException(    "Not Implemented.",
                                    LDAPException.LDAP_NOT_SUPPORTED);
   }

   /*
    * See LDAPv3
    */
   public void bind(String dn,
                    String[] mechanisms,
                    Hashtable props,
                    /*javax.security.auth.callback.CallbackHandler*/ Object cbh)
                    throws LDAPException
   {
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
             throw new LDAPException(    "Mechanism Not Implemented.",
                                    LDAPException.LDAP_NOT_SUPPORTED);
        }
   }

    //*************************************************************************
    // compare methods
    //*************************************************************************

   /*
    * See LDAPv2 Interface
    */
     public boolean compare(String dn,
                          LDAPAttribute attr)
         throws LDAPException
   {
      return compare(dn, attr, defSearchCons);
   }
   
    /*
     * See LDAPv2 Interface
     */
   public boolean compare(String dn,
                          LDAPAttribute attr,
                          LDAPConstraints cons)
      throws LDAPException
   {
      boolean ret = false;

      LDAPResponseListener listener =
         compare(dn, attr, (LDAPResponseListener)null, cons);
      LDAPResponse res = listener.getResponse();

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
   /*
    * 4.1.4 compare
    */

   /**
    *
    * Asynchronously compares an attribute value with one in the directory,
    * using the specified listener.
    *
    *  @param dn      The distinguished name of the entry containing an
    *                 attribute to compare.<br><br>
    *
    *  @param attr    An attribute to compare.<br><br>
    *
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
    *
    *  @param attr    An attribute to compare.<br><br>
    *
    *  @param listener  Handler for messages returned from a server in
    *                   response to this request. If it is null, a
    *                   listener object is created internally.<br><br>
    *
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
      LDAPListener clientListener;
      validateConn();

      if(cons == null)
         cons = defSearchCons;

      String type = attr.getName();
      String value = attr.getStringValueArray()[0]; // get first value

      if(dn == null || type == null || value == null)
         throw new LDAPException("Invalid parameter.",
                                 LDAPException.PARAM_ERROR);

      LDAPMessage msg =
            new LDAPMessage(
                new CompareRequest(
                    new com.novell.ldap.protocol.LDAPDN(dn),
                    new AttributeValueAssertion(
                        new AttributeDescription(type),
                        new AssertionValue(value))),
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn); 
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

        try {
            clientListener.writeMessage(msg, cons.getTimeLimit());
        }
        catch(IOException ioe) {
              // do we need to remove message id here?

            throw new LDAPException("Communication error.",
                                            LDAPException.OTHER);
        }

      return listener;
   }

    //*************************************************************************
    // connect methods
    //*************************************************************************

   /*
    * See LDAPv2 Interface
    */    
   public void connect(String host, int port)
      throws LDAPException
   {
      connect(host, port, null, null);
      return;
   }

   /*
    * See LDAPv2 Interface
    */
   public void connect(String host, int port, String dn, String passwd)
      throws LDAPException
   {
      // call LDAPv3 method
      connect(LDAP_V2, host, port, dn, passwd);
      return;
   }

   /*
    * See LDAPv3, 4.40.2
    */
    

   public void connect(int version, String host, int port, String dn,
                       String passwd)
      throws LDAPException
   {
      // if already connected, disconnect first
      if(conn != null) {
         disconnect();
      }

      conn = new Connection(host, port, socketFactory);

      if( (dn != null) || (passwd != null))
          bind(version, dn, passwd);

      this.setConnectionInfo( host, port, socketFactory);
      this.setAuthenticationInfo( dn, passwd, "simple", null);
      return;
   }

   /**
    *
    *  Sets the specified host, & port
    *  in the object without connecting or authenticating.
    *
    *  @param host A host name or a dotted string representing the IP address
    *              of a host running an LDAP server to connect to. It may also
    *              contain a list of host names, space-delimited. Each host 
    *              name can include a trailing colon and port number.<br><br>
    *
    *  @param port The TCP or UDP port number to connect to or contact. 
    *              The default LDAP port is 389. The port parameter is 
    *              ignored for any host hame which includes a colon and 
    *              port number.<br><br>
    */

    /* Package */ void setConnectionInfo(    String host, int port,
                                            LDAPSocketFactory factory)
    {
        this.host = host;
        this.port = port;
        this.socketFactory = factory;
        return;
    }
    /**
    *
    *  Sets the specified dn, and password
    *  in the object without connecting or authenticating.
    *
    *  @param dn   If non-null and non-empty, specifies that the 
    *              connection and all operations through it should be 
    *              authenticated with the DN as the distinguished name.<br><br>
    *
    *  @param passwd   If non-null and non-empty, specifies that the
    *                  connection and all operations through it should 
    *                  be authenticated with the dn as the distinguished 
    *                  name and passwd as the password.
    */

    /* Package */ void setAuthenticationInfo( String dn, String passwd,
                                            String method, Hashtable hash)
    {
        authenticationDN = dn;
        authenticationPassword = passwd;
        authenticationMethod = method;
        authenticationHash = hash;
        return;
    }

    //*************************************************************************
    // delete methods
    //*************************************************************************

   /*
    * See LDAPv2
    */
   public void delete(String dn)
      throws LDAPException
   {
      delete(dn, defSearchCons);
      return;
   }
   
   /*
    * See LDAPv2
    */
   public void delete(String dn, LDAPConstraints cons)
      throws LDAPException
   {
      LDAPResponseListener listener =
         delete(dn, (LDAPResponseListener)null, cons);
      listener.getResponse().chkResultCode();
      return;
   }
   
   /*
    * 4.1.5 delete
    */

   /**
    *
    * Asynchronously deletes the entry with the specified distinguished name
    * from the directory and returns the results to the specified listener.
    *
    *  @param dn      The distinguished name of the entry to modify.<br><br>
    *
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
    *
    *  @param listener   The handler for messages returned from a server in
    *                    response to this request. If it is null, a
    *                    listener object is created internally.<br><br>
    *
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
      LDAPListener clientListener;
      validateConn();

      if(dn == null)
         throw new LDAPException("Invalid parameter.",
                                 LDAPException.PARAM_ERROR);

      if(cons == null)
         cons = defSearchCons;

      LDAPMessage msg =
            new LDAPMessage(
                new DelRequest(dn),
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

      try {
         clientListener.writeMessage(msg, cons.getTimeLimit());
      }
      catch(IOException ioe) {
         // do we need to remove message id here?

         throw new LDAPException("Communication error.",
                                 LDAPException.OTHER);
      }

      return listener;
   }

    //*************************************************************************
    // disconnect method
    //*************************************************************************
   /*
    * See LDAPv2
    */
   public void disconnect()
      throws LDAPException
   {
      if(conn != null) {
         conn.shutdown((LDAPControl[])null);
         conn = null;
         setAuthenticationInfo( null, null, "none", null);
      }
      else {
         throw new LDAPException("Not connected.",
                                 LDAPException.CONNECT_ERROR);
      }
      return;
   }

    //*************************************************************************
    // extendedOperation methods
    //*************************************************************************

   /*
    * See LDAPv3, 4.40.3 extendedOperation 
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
    *
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
      LDAPResponseListener listener = extendedOperation(op, (LDAPResponseListener)null, cons);
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
    *
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

      return extendedOperation(op, listener, defSearchCons);
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
    *
    * @param listener  The handler for messages returned from a server in 
    *                  response to this request. If it is null, a listener 
    *                  object is created internally.<br><br>
    *
    * @param cons      The constraints specific to this operation.
    *
    * @return An operation-specific object, containing an ID and either an octet 
    * string or BER-encoded values.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */

   public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                       LDAPResponseListener listener,
                                                  LDAPSearchConstraints cons)
        throws LDAPException
    {

      LDAPListener clientListener;
      // Validate our connection structure
      validateConn();

      // Use default constraints if none-specified
      if(cons == null)
         cons = defSearchCons;

      // error check the parameters
      if (op.getID() == null)
         throw new LDAPException("Invalid parameter",
                                 LDAPException.PARAM_ERROR);
   
      // Ber encode the request
//    LDAPRequest req = new ExtendedRequest(op, conn.getMessageID(),
//                                     cons.getClientControls(), ldapv3);

      ASN1OctetString value = 
         (op.getValue() != null) ? new ASN1OctetString(op.getValue()) : null;

      ExtendedRequest er = new ExtendedRequest(new LDAPOID(op.getID()),
                                               value);

      LDAPMessage msg = new LDAPMessage(er, cons.getServerControls());

      // Create a listener if we do not have one already
      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

      try {
         clientListener.writeMessage(msg, cons.getTimeLimit());
      }
      catch(IOException ioe) {
         throw new LDAPException("Communication error.",
                                 LDAPException.OTHER);
      }

      return listener;
   }

    //*************************************************************************
    // getOption method
    //*************************************************************************

   /*
    * See LDAPv2 (4.39.8 getOption)
    */
   public Object getOption(int option)
   throws LDAPException {
      switch(option) {
         case LDAPConnection.DEREF:
            return new Integer(defSearchCons.getDereference());
         case LDAPConnection.SIZELIMIT:
            return new Integer(defSearchCons.getMaxResults());
         case LDAPConnection.SERVER_TIMELIMIT:
            return new Integer(defSearchCons.getServerTimeLimit());
         case LDAPConnection.TIMELIMIT:
            return new Integer(defSearchCons.getTimeLimit());
         case LDAPConnection.REFERRALS:
            return new Boolean(defSearchCons.getReferrals());
         case LDAPConnection.REFERRALS_REBIND_PROC:
            return defSearchCons.getRebindProc();
         case LDAPConnection.BIND:
            return defSearchCons.getBindProc();
         case LDAPConnection.REFERRALS_HOP_LIMIT:
            return new Integer(defSearchCons.getHopLimit());
         case LDAPConnection.BATCHSIZE:
            return new Integer(defSearchCons.getBatchSize());
         default:
            throw new LDAPException("Invalid option ",
                                    LDAPException.PARAM_ERROR);
      }
   }

    //*************************************************************************
    // getResponseControls method
    //*************************************************************************

   /*
    * See LDAPv3, 4.40.4 getResponseControls
    */
   public LDAPControl[] getResponseControls()
    {
      LDAPControl[] controls = null;

      return controls;
   }

    //*************************************************************************
    // modify methods
    //*************************************************************************

   /*
    * See LDAPv2 (4.39.9 modify)
    */
    public void modify(String dn, LDAPModification mod)
      throws LDAPException
   {
      modify(dn, mod, defSearchCons);
      return;
   }

  /*
   * See LDAPv2
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

  /*
   * See LDAPv2
   */
   public void modify(String dn, LDAPModificationSet mods)
      throws LDAPException
   {
      modify(dn, mods, defSearchCons);
      return;
   }

  /*
   * See LDAPv2
   */
   public void modify(String dn,
                      LDAPModificationSet mods,
                      LDAPConstraints cons)
      throws LDAPException
   {
      LDAPResponseListener listener =
         modify(dn, mods, (LDAPResponseListener)null, cons);
      listener.getResponse().chkResultCode();
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
    *
    *  @param mod        A single change to be made to the entry.<br><br>
    *
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
    *
    *  @param mod         A single change to be made to the entry.<br><br>
    *
    *  @param listener    Handler for messages returned from a server in
    *                     response to this request. If it is null, a
    *                     listener object is created internally.<br><br>
    *
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
    *
    *  @param mods       A set of changes to be made to the entry.<br><br>
    *
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
    *
    *  @param mods       A set of changes to be made to the entry.<br><br>
    *
    *  @param listener   The handler for messages returned from a server in
    *                    response to this request. If it is null, a
    *                    listener object is created internally.<br><br>
    *
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
      LDAPListener clientListener;
      validateConn();

      if(dn == null)
         throw new LDAPException("Invalid parameter.",
                                 LDAPException.PARAM_ERROR);

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
                vals.add(new AttributeValue((byte[])attrEnum.nextElement()));
            }

            // create SEQUENCE containing mod operation and attr type and vals
            ASN1Sequence rfcMod = new ASN1Sequence();
            rfcMod.add(new ASN1Enumerated(mod.getOp()));
            rfcMod.add(new AttributeTypeAndValues(
                new AttributeDescription(attr.getName()), vals));

            // place SEQUENCE into SEQUENCE OF
            rfcMods.add(rfcMod);
        }

      LDAPMessage msg =
            new LDAPMessage(
                new ModifyRequest(
                    new com.novell.ldap.protocol.LDAPDN(dn),
                    rfcMods),
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

        try {
            clientListener.writeMessage(msg, cons.getTimeLimit());
        }
        catch(IOException ioe) {
              // do we need to remove message id here?

            throw new LDAPException("Communication error.",
                                            LDAPException.OTHER);
        }

      return listener;
   }

    //*************************************************************************
    // read methods
    //*************************************************************************

   /*
    * See LDAPv2
    */
   public LDAPEntry read(String dn)
      throws LDAPException
   {
      return read(dn, defSearchCons);
   }

   /*
    * See LDAPv2
    */
   public LDAPEntry read(String dn,
                         LDAPSearchConstraints cons)
      throws LDAPException
   {
      return read(dn, (String[]) null, cons);
   }

   /*
    * See LDAPv2
    */
   public LDAPEntry read(String dn,
                         String attrs[])
      throws LDAPException
   {
      return read(dn, attrs, defSearchCons);
   }

   /*
    * See LDAPv2
    */    
   public LDAPEntry read(String dn,
                         String attrs[],
                         LDAPSearchConstraints cons)
      throws LDAPException
   {
        LDAPSearchResults sr = search(dn, LDAPv2.SCOPE_BASE,
                                            "objectclass=*",
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
      LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
         return read( toGet, defSearchCons);
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
    *
    * @param cons       Constraints specific to the operation.
    *
    * @exception LDAPException A general exception which includes an error 
    * message and an LDAP error code.
    */
   public static LDAPEntry read(LDAPUrl toGet,
                                LDAPSearchConstraints cons)
      throws LDAPException
   {
      throw new RuntimeException("Method LDAPConnection.read(LDAPUrl) not implemented");
   }

    //*************************************************************************
    // rename methods
    //*************************************************************************

   /*
    * See LDAPv2 (4.39.11 rename)
    */
   public void rename(String dn,
                      String newRdn,
                      boolean deleteOldRdn)
      throws LDAPException
   {
      rename(dn, newRdn, deleteOldRdn, defSearchCons);
      return;
   }

   /*
    * See LDAPv2 (4.39.11 rename)
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

   /*
    * See LDAPv3, 4.40.5
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

   /*
    * See LDAPv3
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
      listener.getResponse().chkResultCode();
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
   
   /* 
    * LDAPv3, 4.40.5
    */

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

   /*
    * LDAPv3 version, 4.40.5
    */
    
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
      return rename(dn, newRdn, newParentdn,
                    deleteOldRdn, listener, defSearchCons);
   }

   /*
    * LDAPv3 version, 4.40.5
    */
    
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
      LDAPListener clientListener;
      validateConn();

      if(dn == null || newRdn == null)
         throw new LDAPException("Invalid parameter.",
                                 LDAPException.PARAM_ERROR);

      if(cons == null)
         cons = defSearchCons;

      LDAPMessage msg =
            new LDAPMessage(
                new ModifyDNRequest(
                    new com.novell.ldap.protocol.LDAPDN(dn),
                    new RelativeLDAPDN(newRdn),
                    new ASN1Boolean(deleteOldRdn),
                    (newParentdn != null) ?
                        new com.novell.ldap.protocol.LDAPDN(newParentdn) : null),
                cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPResponseListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

        try {
            clientListener.writeMessage(msg, cons.getTimeLimit());
        }
        catch(IOException ioe) {
              // do we need to remove message id here?

            throw new LDAPException("Communication error.",
                                            LDAPException.OTHER);
        }

      return listener;
   }

    //*************************************************************************
    // search methods
    //*************************************************************************

   /*
    * See LDAPv2 (4.39.12 search)
    */
   public LDAPSearchResults search(String base,
                                   int scope,
                                   String filter,
                                   String attrs[],
                                   boolean typesOnly)
      throws LDAPException
   {
      return search(base, scope, filter, attrs, typesOnly, defSearchCons);
   }

   /*
    * See LDAPv2 (4.39.12 search)
    */
   public LDAPSearchResults search(String base,
                                   int scope,
                                   String filter,
                                   String attrs[],
                                   boolean typesOnly,
                                   LDAPSearchConstraints cons)
      throws LDAPException
   {
      LDAPSearchListener listener =
         search(base, scope, filter, attrs, typesOnly,
               (LDAPSearchListener)null, cons);

      if( cons == null )
        cons = defSearchCons;
      return new LDAPSearchResults(cons.getBatchSize(), listener);
   }

   /*
    * 4.39.12 search
    */
    
   /**
    *
    * Asynchronously performs the search specified by the parameters.
    *
    *  @param base           The base distinguished name to search from.
    *<br><br>
    *  @param scope          The scope of the entries to search. The following
    *                        are the valid options:
    *<ul> 
    *   <li>LDAPv2.SCOPE_BASE - searches only the base DN
    *
    *   <li>LDAPv2.SCOPE_ONE - searches only entries under the base DN
    *                                  
    *   <li>LDAPv2.SCOPE_SUB - searches the base DN and all entries
    *                          within its subtree
    *</ul><br><br>
    *  @param filter         Search filter specifying the search criteria, as
    *                        defined in RFC 1960.
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
                                    String attrs[],
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
    *   <li>LDAPv2.SCOPE_BASE - searches only the base DN
    *
    *   <li>LDAPv2.SCOPE_ONE - searches only entries under the base DN
    *                                  
    *   <li>LDAPv2.SCOPE_SUB - searches the base DN and all entries
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
                                    String attrs[],
                                    boolean typesOnly,
                                    LDAPSearchListener listener,
                                    LDAPSearchConstraints cons)
      throws LDAPException
   {
      LDAPListener clientListener;
      validateConn();

      if(cons == null)
         cons = defSearchCons;

      LDAPMessage msg = new LDAPMessage(
         new SearchRequest(
            new com.novell.ldap.protocol.LDAPDN(base),
            new ASN1Enumerated(scope),
            new ASN1Enumerated(cons.getDereference()),
            new ASN1Integer(cons.getMaxResults()),
            new ASN1Integer(cons.getServerTimeLimit()),
            new ASN1Boolean(typesOnly),
            new Filter(filter),
            new AttributeDescriptionList(attrs)),
         cons.getServerControls());

      if(listener == null) {
         clientListener = new LDAPListener(conn);
         listener = new LDAPSearchListener( clientListener );
      } else {
         clientListener = listener.getClientListener();
      }

    try {
       clientListener.writeMessage(msg, cons.getTimeLimit());
    }
    catch(IOException ioe) {
         // do we need to remove message id here?

       throw new LDAPException("Communication error.",
                               LDAPException.OTHER);
    }

      return listener;
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
      throw new RuntimeException("Method LDAPConnection.search(LDAPUrl) not implemented");
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
      throw new RuntimeException("Method LDAPConnection.search(LDAPUrl) not implemented");
   }

    //*************************************************************************
    // setOption methods
    //*************************************************************************

   /*
    * See LDAPv2 (4.39.13 setOption)
    */
    
   public void setOption(int option, Object value)
      throws LDAPException
   {
      switch(option) {
         case LDAPConnection.DEREF:
            int deref = ((Integer)value).intValue();
            switch(deref) {
               case DEREF_NEVER:
               case DEREF_SEARCHING:
               case DEREF_FINDING:
               case DEREF_ALWAYS:
                  defSearchCons.setDereference(deref);
                  break;
               default:
                  throw new LDAPException("Invalid value ",
                                          LDAPException.PARAM_ERROR);
            }
            break;
         case LDAPConnection.SIZELIMIT:
            defSearchCons.setMaxResults(((Integer)value).intValue());
            break;
         case LDAPConnection.SERVER_TIMELIMIT:
            defSearchCons.setServerTimeLimit(((Integer)value).intValue());
            break;
         case LDAPConnection.TIMELIMIT:
            defSearchCons.setTimeLimit(((Integer)value).intValue());
            break;
         case LDAPConnection.REFERRALS:
            defSearchCons.setReferrals(((Boolean)value).booleanValue());
            break;
         case LDAPConnection.REFERRALS_REBIND_PROC:
            defSearchCons.setRebindProc((LDAPRebind)value);
            break;
         case LDAPConnection.BIND:
            defSearchCons.setBindProc((LDAPBind)value);
            break;
         case LDAPConnection.REFERRALS_HOP_LIMIT:
            defSearchCons.setHopLimit(((Integer)value).intValue());
            break;
         case LDAPConnection.BATCHSIZE:
            defSearchCons.setBatchSize(((Integer)value).intValue());
            break;
         default:
            throw new LDAPException("Invalid option ",
                                    LDAPException.PARAM_ERROR);
      }
      return;
   }

    //*************************************************************************
    // helper methods
    //*************************************************************************

    /**
     *
     */
   private void validateConn()
      throws LDAPException
   {
      if(conn == null) {
         new LDAPException("Not connected", LDAPException.CONNECT_ERROR);
      }
      return;
   }
}

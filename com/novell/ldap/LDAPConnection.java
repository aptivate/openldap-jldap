/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConnection.java,v 1.49 2000/11/03 17:54:13 vtag Exp $
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

   static private LDAPSocketFactory socketFactory = null;
   private LDAPSocketFactory mySocketFactory = socketFactory;
   private Connection conn = null;
   private String host = null;
   private int port = 0;
   private LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
   private int protocolVersion = 3;
   private String authenticationPassword = null;
   private String authenticationDN = null;
   private String authenticationMethod = "none";
   private Hashtable authenticationHash = null;

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
      socketFactory = factory;
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
      throw new RuntimeException("Method LDAPConnection.clone not implemented");
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
      disconnect();
      return;
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
      return authenticationDN;
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
      return( authenticationMethod);
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
      return authenticationPassword;
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
      return this.host;
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
      return isConnected() ? conn.getInputStream() : null;
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
      return isConnected() ? conn.getOutputStream() : null;
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
      return this.port;
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
      throw new RuntimeException("Method LDAPConnection.getProperty not implemented");
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
      return protocolVersion;
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
      return authenticationHash;
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
      throw new RuntimeException("Method LDAPConnection.getSaslBindCallbackHandler not implemented");
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
    * Returns the LDAPSocketFactory used to establish a connection to a
    * server.
    *
    * @return The LDAPSocketFactory used to establish a connection.
    *
    * @see #LDAPConnection( LDAPSocketFactory)
    * @see #setSocketFactory( LDAPSocketFactory)
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
    *
    * @see #bind( String, String)
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
      this.defSearchCons.setHopLimit(cons.getHopLimit());
      this.defSearchCons.setTimeLimit(cons.getTimeLimit());
      this.defSearchCons.setReferralHandler(cons.getReferralHandler());
      this.defSearchCons.setReferrals(cons.getReferrals());
      this.defSearchCons.setClientControls((LDAPControl)cons.getClientControls().clone());
      this.defSearchCons.setServerControls((LDAPControl)cons.getServerControls().clone());
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
    * @see #getOutputStream()
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
   public static void setSocketFactory(LDAPSocketFactory factory)
   {
      socketFactory = factory;
      return;
   }
  
   /**
    * Enables or disables the processing of unsolicited messages from the 
    * server.
    *
    * <p>An unsolicited message has the ID 0. If unsolicited 
    * notifications are enabled, unsolicited messages can be queried and 
    * retrieved from a response listener with isResponseReceived(0) and 
    * getResponse(0). The default is for unsolicited messages to be 
    * discarded.</p>
    *   
    * @param allow If true, keep unsolicited notifications and make 
    *              them available as message ID 0. 
    *
    */
   public void setUnsolicitedNotifications (boolean allow) 
   {
      throw new RuntimeException("Method LDAPConnection.setUnsolicitedNotifications not implemented");
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
      throw new RuntimeException("Method LDAPConnection.startTKS not implemented");
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
      validateConn();

      // We need to inform the ClientListener which owns this messageID to
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
      if(listener != null) {
         ClientListener clientListener;
         if( listener instanceof LDAPSearchListener ) {
            clientListener = ((LDAPSearchListener)listener).getClientListener();
         } else {
            clientListener = ((LDAPResponseListener)listener).getClientListener();
         }
         int[] msgIds = clientListener.getMessageIDs();
         for(int i=0; i<msgIds.length; i++) {
            abandon(msgIds[i], cons);
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
      validateConn();
      ClientListener clientListener;

      if(cons == null)
         cons = defSearchCons;

      // error check the parameters
      if(entry == null || entry.getDN() == null)
         throw new LDAPException("Invalid parameter",
                                 LDAPException.PARAM_ERROR);

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
         clientListener = new ClientListener(conn);
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
      LDAPResponseListener listener =
         bind(version, dn, passwd, (LDAPResponseListener)null, cons);
      LDAPResponse res = (LDAPResponse)listener.getResponse();

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
      ClientListener clientListener;
      validateConn();

      if(cons == null)
         cons = defSearchCons;

      if(dn == null)
         dn = "";

      if(passwd == null)
         passwd = "";

      switch(version) {
         case LDAP_V3:
            protocolVersion = version;
            break;
         case LDAP_V2:
            protocolVersion = version;
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
         clientListener = new ClientListener(conn);
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
      ClientListener clientListener;
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
         clientListener = new ClientListener(conn); 
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
      // if already connected, disconnect first
      if(conn != null) {
         disconnect();
      }

      conn = new Connection(host, port, mySocketFactory);

      this.host = host;
      this.port = port;
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
    *<br><br>
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
      ClientListener clientListener;
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
         clientListener = new ClientListener(conn);
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

      ClientListener clientListener;
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
//                                     cons.getClientControls(), LDAPv3);

      ASN1OctetString value = 
         (op.getValue() != null) ? new ASN1OctetString(op.getValue()) : null;

      ExtendedRequest er = new ExtendedRequest(new LDAPOID(op.getID()),
                                               value);

      LDAPMessage msg = new LDAPMessage(er, cons.getServerControls());

      // Create a listener if we do not have one already
      if(listener == null) {
         clientListener = new ClientListener(conn);
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
      LDAPControl[] controls = null;

      return controls;
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
      ClientListener clientListener;
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
         clientListener = new ClientListener(conn);
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
        LDAPSearchResults sr = search(dn, SCOPE_BASE,
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
      throw new RuntimeException("Method LDAPConnection.read(LDAPUrl) not implemented");
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
      ClientListener clientListener;
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
         clientListener = new ClientListener(conn);
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
                                   String attrs[],
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
                                    String attrs[],
                                    boolean typesOnly,
                                    LDAPSearchListener listener,
                                    LDAPSearchConstraints cons)
      throws LDAPException
   {
      ClientListener clientListener;
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
         clientListener = new ClientListener(conn);
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

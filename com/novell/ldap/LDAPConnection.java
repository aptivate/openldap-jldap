/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPConnection.java,v 1.21 2000/08/24 19:18:05 smerrill Exp $
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
 
package org.ietf.ldap;

import java.io.*;
import java.util.*;

import com.novell.ldap.client.*;
import com.novell.asn1.*;
import com.novell.asn1.ldap.*;

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
   LDAPv3, LDAPAsynchronousConnection, Cloneable {

   private Connection conn = null;
   private LDAPSocketFactory socketFactory = null;
   private LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
   private boolean ldapv3 = true;

   public static final int DEREF_NEVER  = 0;
   public static final int DEREF_SEARCHING = 1;
   public static final int DEREF_FINDING = 2;
   public static final int DEREF_ALWAYS = 3;

   public static final int DEREF = 1;
   public static final int SIZELIMIT = 2;
   public static final int SERVER_TIMELIMIT = 3;
   public static final int TIMELIMIT = 4;
   public static final int REFERRALS = 5;
   public static final int REFERRALS_REAUTHENTICATION = 6;
   public static final int BIND = 7;
   public static final int REFERRALS_HOP_LIMIT = 8;
   public static final int BATCHSIZE = 9;

   public static final int LDAP_V2 = 2;
   public static final int LDAP_V3 = 3;

	public static final int DEFAULT_PORT = 389;

   /*
    * Constructors
    */

   /**
    * Constructs a new LDAPConnection object, which represents a connection
    * to an LDAP server.
    *
    * <p>Calling the constructor does not actually establish the connection.
    * To connect to the LDAP server, use the connect method. </p>
    */
   public LDAPConnection()
   {
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
   }

   /**
    * Returns a copy of the object with a private context, but sharing the
    * network connection if there is one. 
    *
    * <p>The network connection remains open until all clones have 
    * disconnected or gone out of scope. Any connection opened after
    * cloning is private to the object making the connection.<\p>
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
      return null;
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
      return null;
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
      if(isBound()) {
         return "simple";
      }
      else {
         return "none";
      }
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
      return null;
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
      return isConnected() ? conn.getHost() : null;
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
    * @return The stream the connection object used for sending data,
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
      return isConnected() ? conn.getPort() : -1;
   }

   /**
    * Gets a property of a connection object.
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
    *  <p>Other properties may be available in particular implementations
    *  of the class, and used to modify operations such as search.</p>
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
      return null;
   }

   /**
    * Returns the set of constraints that apply to all operations performed
    * through this connection (unless a different set of constraints is
    * specified when calling an operation method).
    *
    * <p> The getOption method can be used to get individual
    * constraints (rather than getting the entire set of constraints). </p>
    *
    * @return The set of default contraints that apply to this connection.
    */
   public LDAPConstraints getConstraints()
   {
      return (LDAPConstraints)getSearchConstraints();
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
      return (LDAPSearchConstraints)defSearchCons.clone();
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
    * @return
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
    */
   public void setConstraints(LDAPConstraints cons)
   {
      defSearchCons = (LDAPSearchConstraints)cons.clone();
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
   }

   /**
    * Sets a property of a connection object.
    *
    * <p>No property names have been defined at this time, but the mechanism
    * is in place in order to support revisional as well as dynamic
    * extensions to operation modifiers. </p>
    * 
    * 
    * @param name    Name of the property to set.
    *
    * @param value   Value to assign to the property.
    *
    * @exception LDAPException Thrown if the specified
    *                 property is not supported.
    */
   public void setProperty(String name, Object value)
      throws LDAPException
   {
   }

   /**
    * Sets the constraints that apply to all search operations performed
    * through this connection (unless a different set of constraints is
    * specified when calling a search operation method).
    *
    * <p>Typically, the setSearchConstraints method is used to create a
    * slightly different set of search constraints to apply to a particular
    * search. </p>
    *
    * @param cons The search constraints to set.
    */
   public void setSearchConstraints(LDAPSearchConstraints cons)
   {
      this.defSearchCons = cons;
   }

   /**
    * Establishes the default LDAPSocketFactory used to establish a
    * connection to a server.
    *
    * <p>The setSocketFactory method is implemented as once-only. 
    * It should be called before the first connect method. If called 
    * (for the first time) after connecting, the new factory will not
    * be used until or unless a new connection is attempted 
    * with the object. </p>
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
   }

	//*************************************************************************
	// Below follows all of the LDAP protocol operation methods
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
    *  message and an LDAP error code.
    */
   public void abandon(LDAPSearchResults results)
      throws LDAPException
   {
      results.abandon();
   }
   
   /*
   * 4.39.1 abandon
   */

   /**
    *
    * Abandons one search operation for a listener.
    *
    *  @param id      The ID of the asynchrous operation to abandon. The ID 
    *                 can be obtained from the search listener for the
    *                 operation.
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(int id)
      throws LDAPException
   {
      abandon(id, defSearchCons);
   }
   
   /*
   *(Not yet in the draft)
   */

   /**
    * Abandons a search operation for a listener.
    *
    * @param id The ID of the asynchronous operation to abandon. The ID can 
    *           be obtained from the search listern for the operation.
    *
    * @param cons The contraints specific to the operation 
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
   }

   /**
    * Abandons all search operations for a listener.
    *
    * <p>All operations in progress which are managed by the listener 
    * are abandoned.</p>
    *
    *  @param listener  Handler returned for messages returned on a
    *                   search request. 
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(LDAPSearchListener listener)
      throws LDAPException
   {
      abandon(listener, defSearchCons);
   }

   /*
   * Not in the draft
   */
   
   /**
    * Abandons all messages in progress hosted by the LDAPListener.
    *
    *  @param listener  Handler returned for messages returned on a
    *                   search request.
    *
    *  @param cons The contraints specific to the request.  
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void abandon(LDAPSearchListener listener, LDAPConstraints cons)
      throws LDAPException
   {
      if(listener != null) {
         int[] msgIds = listener.getMessageIDs();
         for(int i=0; i<msgIds.length; i++) {
            abandon(msgIds[i], cons);
         }
      }
   }

	//*************************************************************************
	// add methods
	//*************************************************************************

   /*
   * add (LDAPv2)
   */
   
   /**
    * Adds an entry to the directory.
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
   }

   /*
   * add (LDAPv2)
   */

   /**
    *
    * Adds an entry to the directory.
    *
    *  @param entry   LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.
    *
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
      listener.getResponse().chkResultCode();
   }
   
   /*
   * 4.39.2 add
   */

   /**
    *
    * Adds an entry to the directory.
    *
    *  @param entry   LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.
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
    * Adds an entry to the directory.
    *
    *  @param entry   LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.
    *
    *  @param listener  Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally.
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
					new com.novell.asn1.ldap.LDAPDN(entry.getDN()),
					attrList),
				cons.getServerControls());

      if(listener == null)
         listener = new LDAPResponseListener(conn);

      try {
         listener.writeMessage(msg, cons.getTimeLimit());
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
    * 4.39.3 bind, LDAPv2, synchronous
    */

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  
    *
    * If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.
    *
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
      bind(LDAP_V2, dn, passwd); // call LDAPv3 bind()
   }

   /**
	*
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    *
	* The LDAPv3 bind method adds the version parameter.
    *
    *  @param version  The version of the LDAP protocol to use 
    *                  in the bind. 
    *
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.
    *
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
   }

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    *
    *
    * The LDAPv3 bind method adds the version parameter.
    *
    *  @param version  The version of the LDAP protocol to use 
    *                   in the bind. 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
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
   }

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    *
    * The LDAPv3 bind method adds the version parameter.
    *
    *  @param version  The version of the LDAP protocol to use 
    *                  in the bind. 
    *                  
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
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
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * The LDAPv3 bind method adds the version parameter.
    *
    *  @param version  The version of the LDAP protocol to use 
    *                  in the bind. 
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
    *  @param listener  Handler for messages returned from a server in
    *                   response to this request. If it is null, a
    *                   listener object is created internally.
    *
    *  @param cons      Constraints specific to the operation.
    */
   public LDAPResponseListener bind(int version,
                                    String dn,
                                    String passwd,
                                    LDAPResponseListener listener,
                                    LDAPConstraints cons)
      throws LDAPException
   {
      validateConn();

      if(cons == null)
         cons = defSearchCons;

		if(dn == null)
			dn = "";

		if(passwd == null)
			passwd = "";

      switch(version) {
         case LDAP_V3:
            ldapv3 = true;
            break;
         case LDAP_V2:
            ldapv3 = false;
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
					new com.novell.asn1.ldap.LDAPDN(dn),
					new AuthenticationChoice(
						new ASN1Tagged(
							new ASN1Identifier(ASN1Identifier.CONTEXT, false, 0),
							new ASN1OctetString(passwd),
							false))), // implicit tagging
				cons.getServerControls());

      if(listener == null)
         listener = new LDAPResponseListener(conn);

      try {
         listener.writeMessage(msg, cons.getTimeLimit());
      }
      catch(IOException ioe) {
         // do we need to remove message id here?

         throw new LDAPException("Communication error.",
                                 LDAPException.OTHER);
      }

//    if(passwd != null) {
//       req.getLber().reset(); // clear copy of passwd
//    }

      return listener;
   }

   /*
    *
    */
/*
   public void bind(String dn,
                    Properties props,
                    javax.security.auth.callback.CallbackHandler cbh)
                    throws LDAPException {
   }
*/

   /*
    *
    */
/*
   public void bind(String dn,
                    String[] mechanisms,
                    Hashtable props,
                    javax.security.auth.callback.CallbackHandler cbh)
                    throws LDAPException {
   }
*/

	//*************************************************************************
	// compare methods
	//*************************************************************************

   /**
    * 4.39.4 compare, LDAPv2, synchronous
    *
    * Checks to see if an entry contains an attribute with a specified
    * value.  Returns true if the entry has the value, and false if the
    * entry does not have the value or the attribute.
    *
    * Parameters are:
    *
    *  dn             The distinguished name of the entry to use in the
    *                 comparison.
    *
    *  attr           The attribute to compare against the entry. The
    *                 method checks to see if the entry has an
    *                 attribute with the same name and value as this
    *                 attribute.
    */
   public boolean compare(String dn,
                          LDAPAttribute attr)
      throws LDAPException
   {
      return compare(dn, attr, defSearchCons);
   }

   /**
    * compare (LDAPv2)
    *
    * Checks to see if an entry contains an attribute with a specified
    * value.  Returns true if the entry has the value, and false if the
    * entry does not have the value or the attribute.
    *
    * Parameters are:
    *
    *  dn             The distinguished name of the entry to use in the
    *                 comparison.
    *
    *  attr           The attribute to compare against the entry. The
    *                 method checks to see if the entry has an
    *                 attribute with the same name and value as this
    *                 attribute.
    *
    *  cons           Constraints specific to the operation.
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

   /**
    * 4.1.4 compare
    *
    * Compare an attribute value with one in the directory.
    *
    * Parameters are:
    *  dn             The distinguished name of the entry containing an
    *                 attribute to compare.
    *
    *  attr           An attribute to compare.
    *
    *  listener       Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally.
    */
   public LDAPResponseListener compare(String dn,
                                       LDAPAttribute attr,
                                       LDAPResponseListener listener)
      throws LDAPException
   {
      return compare(dn, attr, listener, defSearchCons);
   }

   /**
    * Compare an attribute value with one in the directory.
    *
    * Parameters are:
    *  dn             The distinguished name of the entry containing an
    *                 attribute to compare.
    *
    *  attr           An attribute to compare.
    *
    *  listener       Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
    */
   public LDAPResponseListener compare(String dn,
                                       LDAPAttribute attr,
                                       LDAPResponseListener listener,
                                       LDAPConstraints cons)
      throws LDAPException
   {
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
					new com.novell.asn1.ldap.LDAPDN(dn),
					new AttributeValueAssertion(
						new AttributeDescription(type),
						new AssertionValue(value))),
				cons.getServerControls());

      if(listener == null)
         listener = new LDAPResponseListener(conn);

		try {
			listener.writeMessage(msg, cons.getTimeLimit());
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
    * LDAPv2
    */
   public void connect(String host, int port)
      throws LDAPException
   {
      connect(host, port, null, null);
   }

   /**
    * LDAPv2
    */
   public void connect(String host, int port, String dn, String passwd)
      throws LDAPException
   {
      // call LDAPv3 method
      connect(LDAP_V2, host, port, dn, passwd);
   }

   /**
    * LDAPv3
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

      bind(version, dn, passwd);
   }

	//*************************************************************************
	// delete methods
	//*************************************************************************

   /**
    * 4.28.6 delete
    *
    * Deletes the entry for the specified DN from the directory.
    *
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    */
   public void delete(String dn)
      throws LDAPException
   {
      delete(dn, defSearchCons);
   }

   /**
    * 4.28.6 delete
    *
    * Deletes the entry for the specified DN from the directory.
    *
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  cons           Constraints specific to the operation.
    */
   public void delete(String dn, LDAPConstraints cons)
      throws LDAPException
   {
      LDAPResponseListener listener =
         delete(dn, (LDAPResponseListener)null, cons);
      listener.getResponse().chkResultCode();
   }

   /**
    * 4.1.5 delete
    *
    * Deletes the entry for the specified DN from the directory.
    *
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    */
   public LDAPResponseListener delete(String dn,
                                      LDAPResponseListener listener)
      throws LDAPException
   {
      return delete(dn, listener, defSearchCons);
   }

   /**
    * Deletes the entry for the specified DN from the directory.
    *
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
    */
   public LDAPResponseListener delete(String dn,
                                      LDAPResponseListener listener,
                                      LDAPConstraints cons)
      throws LDAPException
   {
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

      if(listener == null)
         listener = new LDAPResponseListener(conn);

      try {
         listener.writeMessage(msg, cons.getTimeLimit());
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
    * 4.39.7 disconnect, LDAPv2, synchronous
    *
    * Disconnects from the LDAP server. Before the object can perform LDAP
    * operations again, it must reconnect to the server by calling connect.
    *
    * Will abandon any outstanding requests, issue an unbind request to the
    * server, and then close the socket.
	 *
	 * @throws LDAPException
    */
   public void disconnect()
      throws LDAPException
   {
      if(conn != null) {
         conn.shutdown((LDAPControl[])null);
         conn = null;
      }
      else {
         throw new LDAPException("Not connected.",
                                 LDAPException.CONNECT_ERROR);
      }
   }

	//*************************************************************************
	// extendedOperation methods
	//*************************************************************************

   /**
    * 4.40.3 extendedOperation, LDAPv3, synchronous LDAP extended request 
    */
   public LDAPExtendedResponse extendedOperation(LDAPExtendedOperation op)
      throws LDAPException
   {
      return extendedOperation(op, defSearchCons);
   }

   /**
    *  Synchronous LDAP extended request with SearchConstraints
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


   /**
    * ASynchronous LDAP extended request 
    */
   public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                    LDAPResponseListener listener)
		throws LDAPException
	{

      return extendedOperation(op, listener, defSearchCons);
   }

   /**
    *  ASynchronous LDAP extended request with SearchConstraints
    */
   public LDAPResponseListener extendedOperation(LDAPExtendedOperation op,
                                       LDAPResponseListener listener,
                                                  LDAPSearchConstraints cons)
		throws LDAPException
	{

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
      if(listener == null)
         listener = new LDAPResponseListener(conn);

      try {
         listener.writeMessage(msg, cons.getTimeLimit());
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

   /**
    * 4.28.8 getOption
    *
    * Returns the value of the specified option for this object.
    *
    * Parameters are:
    *
    *  option         See LDAPConnection.setOption for a description of
    *                 valid options.
	 *
	 * @throws LDAPException
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
         case LDAPConnection.REFERRALS_REAUTHENTICATION:
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

   /**
    * 4.40.4 getResponseControls, LDAPv3, synchronous
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
    * 4.39.9 modify, LDAPv2, synchronous
    *
    * Makes a single change to an existing entry in the directory (for
    * example, changes the value of an attribute, adds a new attribute
    * value, or removes an existing attribute value).
    *
    * The LDAPModification object specifies both the change to be made and
    * the LDAPAttribute value to be changed.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mod            A single change to be made to the entry.
    */
   public void modify(String dn, LDAPModification mod)
      throws LDAPException
   {
      modify(dn, mod, defSearchCons);
   }

   /**
    * modify
    *
    * Makes a single change to an existing entry in the directory (for
    * example, changes the value of an attribute, adds a new attribute
    * value, or removes an existing attribute value).
    *
    * The LDAPModification object specifies both the change to be made and
    * the LDAPAttribute value to be changed.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mod            A single change to be made to the entry.
    *
    *  cons           Constraints specific to the operation.
    */
   public void modify(String dn,
                      LDAPModification mod,
                      LDAPConstraints cons)
      throws LDAPException
   {
      LDAPModificationSet mods = new LDAPModificationSet();
      mods.add(mod);
      modify(dn, mods, cons);
   }

   /**
    * modify
    *
    * Makes a set of changes to an existing entry in the directory (for
    * example, changes attribute values, adds new attribute values, or
    * removes existing attribute values).
    * 
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mods           A set of changes to be made to the entry.
    */
   public void modify(String dn, LDAPModificationSet mods)
      throws LDAPException
   {
      modify(dn, mods, defSearchCons);
   }

   /**
    * Makes a set of changes to an existing entry in the directory (for
    * example, changes attribute values, adds new attribute values, or
    * removes existing attribute values).
    * 
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mods           A set of changes to be made to the entry.
    *
    *  cons           Constraints specific to the operation.
    */
   public void modify(String dn,
                      LDAPModificationSet mods,
                      LDAPConstraints cons)
      throws LDAPException
   {
      LDAPResponseListener listener =
         modify(dn, mods, (LDAPResponseListener)null, cons);
      listener.getResponse().chkResultCode();
   }

   /**
    * modify
    *
    * Makes a single change to an existing entry in the directory (for
    * example, changes the value of an attribute, adds a new attribute
    * value, or removes an existing attribute value).
    *
    * The LDAPModification object specifies both the change to be made and
    * the LDAPAttribute value to be changed.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mod            A single change to be made to the entry.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    */
   public LDAPResponseListener modify(String dn,
                                      LDAPModification mod,
                                      LDAPResponseListener listener)
      throws LDAPException
   {
      return modify(dn, mod, listener, defSearchCons);
   }

   /**
    * Makes a single change to an existing entry in the directory (for
    * example, changes the value of an attribute, adds a new attribute
    * value, or removes an existing attribute value).
    *
    * The LDAPModification object specifies both the change to be made and
    * the LDAPAttribute value to be changed.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mod            A single change to be made to the entry.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
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
    * Makes a set of changes to an existing entry in the directory (for
    * example, changes attribute values, adds new attribute values, or
    * removes existing attribute values).
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mods           A set of changes to be made to the entry.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    */
   public LDAPResponseListener modify(String dn,
                                      LDAPModificationSet mods,
                                      LDAPResponseListener listener)
      throws LDAPException
   {
      return modify(dn, mods, listener, defSearchCons);
   }

   /**
    * Makes a set of changes to an existing entry in the directory (for
    * example, changes attribute values, adds new attribute values, or
    * removes existing attribute values).
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  mods           A set of changes to be made to the entry.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
    */
   public LDAPResponseListener modify(String dn,
                                      LDAPModificationSet mods,
                                      LDAPResponseListener listener,
                                      LDAPConstraints cons)
      throws LDAPException
   {
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
					new com.novell.asn1.ldap.LDAPDN(dn),
					rfcMods),
				cons.getServerControls());

      if(listener == null)
         listener = new LDAPResponseListener(conn);

		try {
			listener.writeMessage(msg, cons.getTimeLimit());
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
    * read (LDAPv2)
    *
    * Reads the entry for the specified distiguished name (DN) and
    * retrieves all attributes for the entry.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to retrieve.
    */
   public LDAPEntry read(String dn)
      throws LDAPException
   {
      return read(dn, defSearchCons);
   }

   /**
    * read (LDAPv2)
    *
    * Reads the entry for the specified distiguished name (DN) and
    * retrieves all attributes for the entry.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to retrieve.
    *
    *  cons           Constraints specific to the operation.
    */
   public LDAPEntry read(String dn,
                         LDAPSearchConstraints cons)
      throws LDAPException
   {
      return read(dn, (String[]) null, cons);
   }

   /**
    * read (LDAPv2)
    *
    * Reads the entry for the specified distinguished name (DN) and
    * retrieves only the specified attributes from the entry.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to retrieve.
    *
    *  attrs          Names of attributes to retrieve.
    */
   public LDAPEntry read(String dn,
                         String attrs[])
      throws LDAPException
   {
      return read(dn, attrs, defSearchCons);
   }

   /**
    * read (LDAPv2)
    *
    * Reads the entry for the specified distinguished name (DN) and
    * retrieves only the specified attributes from the entry.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to retrieve.
    *
    *  attrs          Names of attributes to retrieve.
    *
    *  cons           Constraints specific to the operation.
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
    * Reads the entry specified by the LDAP URL.
    *
    * When this method is called, a new connection is created
    * automatically, using the host and port specified in the URL. After
    * finding the entry, the method closes the connection (in other words,
    * it disconnects from the LDAP server).
    *
    * If the URL specifies a filter and scope, these are not used. Of the
    * information specified in the URL, this method only uses the LDAP host
    * name and port number, the base distinguished name (DN), and the list
    * of attributes to return.
    *
    * The method returns the entry specified by the base DN.
    *
    * Parameters are:
    *
    *  toGet           LDAP URL specifying the entry to read.
    */
   public static LDAPEntry read(LDAPUrl toGet)
      throws LDAPException
   {
      LDAPSearchConstraints defSearchCons = new LDAPSearchConstraints();
         return read( toGet, defSearchCons);
   }

   /**
    * Reads the entry specified by the LDAP URL.
    *
    * When this method is called, a new connection is created
    * automatically, using the host and port specified in the URL. After
    * finding the entry, the method closes the connection (in other words,
    * it disconnects from the LDAP server).
    *
    * If the URL specifies a filter and scope, these are not used. Of the
    * information specified in the URL, this method only uses the LDAP host
    * name and port number, the base distinguished name (DN), and the list
    * of attributes to return.
    *
    * The method returns the entry specified by the base DN.
    *
    * Parameters are:
    *
    *    toGet           LDAP URL specifying the entry to read.
    *
    *    cons           Constraints specific to the operation.
    */
   public static LDAPEntry read(LDAPUrl toGet,
                                LDAPSearchConstraints cons)
      throws LDAPException
   {
      return null;
   }

	//*************************************************************************
	// rename methods
	//*************************************************************************

   /**
    * 4.39.11 rename, LDAPv2, synchronous
    *
    * Renames an existing entry in the directory.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    */
   public void rename(String dn,
                      String newRdn,
                      boolean deleteOldRdn)
      throws LDAPException
   {
      rename(dn, newRdn, deleteOldRdn, defSearchCons);
   }

   /**
    * rename
    *
    * Renames an existing entry in the directory.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  cons           Constraints specific to the operation.
    */
   public void rename(String dn,
                      String newRdn,
                      boolean deleteOldRdn,
                      LDAPConstraints cons)
      throws LDAPException
   {
      // null for newParentdn means that this is originating as an LDAPv2 call
      rename(dn, newRdn, null, deleteOldRdn, cons);
   }

   /**
    * rename
    *
    * Renames an existing entry in the directory, possibly repositioning it
    * in the directory tree.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  newParentdn    Distinguished name of the existing entry which is
    *                 to be the new parent of the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                 attribute value.
    */
   public void rename(String dn,
                      String newRdn,
                      String newParentdn,
                      boolean deleteOldRdn)
      throws LDAPException
   {
      rename(dn, newRdn, newParentdn, deleteOldRdn, defSearchCons);
   }

   /**
    * 4.29.5 rename
    *
    * Renames an existing entry in the directory, possibly repositioning it
    * in the directory tree.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  newParentdn    Distinguished name of the existing entry which is
    *                 to be the new parent of the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                 attribute value.
    *
    *  cons           Constraints specific to the operation.
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
   }

   /**
    * 4.1.7 rename
    * 
    * Renames an existing entry in the directory.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
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
    * Renames an existing entry in the directory.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
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
    * LDAPv3 version Not in the draft yet.
    * 
    * Renames an existing entry in the directory.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
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
    * LDAPv3 version Not in the draft yet.
    *
    * Parameters are:
    *
    *  dn             Current distinguished name of the entry.
    *
    *  newRdn         New relative distinguished name for the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the operation.
    */
   public LDAPResponseListener rename(String dn,
                                      String newRdn,
                                      String newParentdn,
                                      boolean deleteOldRdn,
                                      LDAPResponseListener listener,
                                      LDAPConstraints cons)
      throws LDAPException
   {
      validateConn();

      if(dn == null || newRdn == null)
         throw new LDAPException("Invalid parameter.",
                                 LDAPException.PARAM_ERROR);

      if(cons == null)
         cons = defSearchCons;

      LDAPMessage msg =
			new LDAPMessage(
				new ModifyDNRequest(
					new com.novell.asn1.ldap.LDAPDN(dn),
					new RelativeLDAPDN(newRdn),
					new ASN1Boolean(deleteOldRdn),
					(newParentdn != null) ?
						new com.novell.asn1.ldap.LDAPDN(newParentdn) : null),
				cons.getServerControls());

      if(listener == null)
         listener = new LDAPResponseListener(conn);

		try {
			listener.writeMessage(msg, cons.getTimeLimit());
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
    * 4.28.12 search
    *
    * Performs the search specified by the parameters.
    *
    * Parameters are:
    *
    *  base           The base distinguished name to search from.
    *
    *  scope          The scope of the entries to search. The following
    *                  are the valid options:
    *
    *     LDAPv2.SCOPE_BASE            Search only the base DN
    *
    *     LDAPv2.SCOPE_ONE             Search only entries under the
    *                                  base DN
    *
    *     LDAPv2.SCOPE_SUB             Search the base DN and all
    *                                  entries
    *                                   within its subtree
    *
    *  filter         Search filter specifying the search criteria, as
    *                  defined in [3].
    *
    *  attrs          Names of attributes to retrieve.
    *
    *  typesOnly      If true, returns the names but not the values of
    *                  the attributes found.  If false, returns the
    *                  names and values for attributes found.
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
    * 4.28.12 search
    *
    * Performs the search specified by the parameters, also allowing
    * specification of constraints for the search (such as the maximum
    * number of entries to find or the maximum time to wait for search
    * results).
    *
    * As part of the search constraints, the function allows specifying
    * whether or not the results are to be delivered all at once or in
    * smaller batches. If specified that the results are to be delivered in
    * smaller batches, each iteration blocks only until the next batch of
    * results is returned.
    *
    * Parameters are:
    *
    *  base           The base distinguished name to search from.
    *
    *  scope          The scope of the entries to search. The following
    *                 are the valid options:
    *
    *     LDAPv2.SCOPE_BASE            Search only the base DN
    *
    *     LDAPv2.SCOPE_ONE             Search only entries under the
    *                                  base DN
    *
    *     LDAPv2.SCOPE_SUB             Search the base DN and all
    *                                  entries within its subtree
    *
    *  filter         Search filter specifying the search criteria, as
    *                 defined in [3].
    *
    *  attrs          Names of attributes to retrieve.
    *
    *  typesOnly      If true, returns the names but not the values of
    *                 the attributes found.  If false, returns the
    *                 names and values for attributes found.
    *
    *  cons           Constraints specific to the search.
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

      return new LDAPSearchResults(cons.getBatchSize(), listener);
   }

   /**
    * 4.1.8 search
    *
    * Performs the search specified by the parameters.
    *
    * Parameters are:
    *
    *  base           The base distinguished name to search from.
    *
    *  scope          The scope of the entries to search. The following
    *                  are the valid options:
    *
    *     LDAPv2.SCOPE_BASE            Search only the base DN
    *
    *     LDAPv2.SCOPE_ONE             Search only entries under the
    *                                  base DN
    *
    *     LDAPv2.SCOPE_SUB             Search the base DN and all
    *                                  entries
    *                                   within its subtree
    *
    *  filter         Search filter specifying the search criteria, as
    *                  defined in [FILTERS].
    *
    *  attrs          Names of attributes to retrieve.
    *
    *  typesOnly      If true, returns the names but not the values of
    *                  the attributes found.  If false, returns the
    *                  names and values for attributes found.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
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
    * Performs the search specified by the parameters, also allowing
    * specification of constraints for the search (such as the maximum
    * number of entries to find or the maximum time to wait for search
    * results).
    *
    * Parameters are:
    *
    *  base           The base distinguished name to search from.
    *
    *  scope          The scope of the entries to search. The following
    *                  are the valid options:
    *
    *     LDAPv2.SCOPE_BASE            Search only the base DN
    *
    *     LDAPv2.SCOPE_ONE             Search only entries under the
    *                                  base DN
    *
    *     LDAPv2.SCOPE_SUB             Search the base DN and all
    *                                  entries
    *                                   within its subtree
    *
    *  filter         Search filter specifying the search criteria, as
    *                  defined in [FILTERS].
    *
    *  attrs          Names of attributes to retrieve.
    *
    *  typesOnly      If true, returns the names but not the values of
    *                  the attributes found.  If false, returns the
    *                  names and values for attributes found.
    *
    *  listener       Handler for messages returned from a server in
    *                  response to this request. If it is null, a
    *                  listener object is created internally.
    *
    *  cons           Constraints specific to the search.
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
      validateConn();

      if(cons == null)
         cons = defSearchCons;

      LDAPMessage msg = new LDAPMessage(
         new SearchRequest(
            new com.novell.asn1.ldap.LDAPDN(base),
            new ASN1Enumerated(scope),
            new ASN1Enumerated(cons.getDereference()),
            new ASN1Integer(cons.getMaxResults()),
            new ASN1Integer(cons.getServerTimeLimit()),
            new ASN1Boolean(typesOnly),
            new Filter(filter),
            new AttributeDescriptionList(attrs)),
         cons.getServerControls());

      if(listener == null)
         listener = new LDAPSearchListener(conn);

    try {
       listener.writeMessage(msg, cons.getTimeLimit());
    }
    catch(IOException ioe) {
         // do we need to remove message id here?

       throw new LDAPException("Communication error.",
                               LDAPException.OTHER);
    }

      return listener;
   }

   /**
    * Performs the search specified by the LDAP URL, returning an
    * enumerable LDAPSearchResults object.
    */
   public static LDAPSearchResults search(LDAPUrl toGet)
      throws LDAPException
   {
      return null;
   }

   /**
    * Perfoms the search specified by the LDAP URL. This method also allows
    * specifying constraints for the search (such as the maximum number of
    * entries to find or the maximum time to wait for search results).
    *
    * When this method is called, a new connection is created
    * automatically, using the host and port specified in the URL. After
    * all search results have been received from the server, the method
    * closes the connection (in other words, it disconnects from the LDAP
    * server).
    *
    * As part of the search constraints, a choice can be made as to whether
    * to have the results delivered all at once or in smaller batches. If
    * the results are to be delivered in smaller batches, each iteration
    * blocks only until the next batch of results is returned.
    *
    * Parameters are:
    *
    *  toGet          LDAP URL specifying the entry to read.
    *
    *  cons           Constraints specific to the search.
    */
   public static LDAPSearchResults search(LDAPUrl toGet,
                                          LDAPSearchConstraints cons)
      throws LDAPException
   {
      return null;
   }

	//*************************************************************************
	// setOption methods
	//*************************************************************************

   /**
    * 4.28.13 setOption
    *
    * Sets the value of the specified option for this object.
    *
    * These options represent the default search constraints for the
    * current connection. Some of these options are also propagated through
    * the LDAPConstraints, which can be obtained from the connection object
    * with the getSearchConstraints method.
    *
    * The option that is set here applies to all subsequent searches
    * performed through the current connection, unless it is overridden
    * with an LDAPConstraints at the time of search.
    *
    * To set a constraint only for a particular search, create an
    * LDAPConstraints object with the new constraints and pass it to the
    * LDAPConnection.search method.
    *
    * Parameters are:
    *
    *
    *  option         One of the following options:
    *
    *     Option                       Type       Description
    *
    *     LDAPConnection.DEREF         Integer    Specifies under what
    *                                             circumstances the
    *                                             object dereferences
    *                                             aliases. By default,
    *                                             the value of this
    *                                             option is
    *                                             LDAPConnection.DEREF_NEVER.
    *
    *                 Legal values for this option are:
    *
    *                 LDAPConnection.DEREF_NEVER   Aliases are never
    *                                              dereferenced.
    *
    *
    *                 LDAPConnection.DEREF_FINDING aliases are
    *                                              dereferenced when
    *                                              finding the starting
    *                                              point for the search
    *                                              (but not when
    *                                              searching under that
    *                                              starting entry).
    *
    *
    *                 LDAPConnection.DEREF_SEARCHING  Aliases are
    *                                              dereferenced when
    *                                              searching the entries
    *                                              beneath the starting
    *                                              point of the search
    *                                              (but not when finding
    *                                              the starting entry).
    *
    *
    *                 LDAPConnection.DEREF_ALWAYS  Aliases are always
    *                                              dereferenced (both
    *                                              when finding the
    *                                              starting point for
    *                                              the search and when
    *                                              searching under that
    *                                              starting entry).
    *
    *
    *     LDAPConnection.SIZELIMIT     Integer    Specifies the maximum
    *                                              number of search
    *                                              results to return. If
    *                                              this option is set to
    *                                              0, there is no
    *                                              maximum limit.
    *
    *                                              By default, the value
    *                                              of this option is
    *                                              1000.
    *
    *
    *     LDAPConnection.SERVER_TIMELIMIT          Integer      Sets the
    *                                              maximum number of
    *                                              seconds that the
    *                                              server is to wait
    *                                              when returning search
    *                                              results. The
    *                                              parameter is only
    *                                              recognized on search
    *                                              operations.
    *
    *                                              By default, the value
    *                                              of this option is 0.
    *
    *
    *     LDAPConnection.TIMELIMIT     Integer    Specifies the maximum
    *                                              number of
    *                                              milliseconds to wait
    *                                              for results before
    *                                              timing out. If this
    *                                              option is set to 0,
    *                                              there is no maximum
    *                                              time limit. The
    *                                              actual granularity of
    *                                              the timeout depends
    *                                              on the
    *                                              implementation.
    *
    *                                              By default, the value
    *                                              of this option is 0.
    *
    *
    *     LDAPConnection.REFERRALS     Boolean    Specifies whether or
    *                                              not the client
    *                                              follows referrals
    *                                              automatically. If
    *                                              true, the client
    *                                              follows referrals
    *                                              automatically.  If
    *                                              false, an
    *                                              LDAPReferralException
    *                                              is raised when a
    *                                              referral is detected.
    *
    *                                              Referrals of any type
    *                                              other to an LDAP
    *                                              server (i.e. a
    *                                              referral URL other
    *                                              than
    *                                              ldap://something) are
    *                                              ignored on automatic
    *                                              referral following.
    *
    *                                              By default, the value
    *                                              of this option is
    *                                              false.
    *
    *
    *     LDAPConnection.REFERRALS_REAUTHENTICATION LDAPRebind
    *                                              Specifies an object
    *                                              that implements the
    *                                              LDAPRebind interface.
    *                                              A user of the class
    *                                              library must define
    *                                              this class and the
    *                                              getRebindAuthentica-
    *                                              tion method that will
    *                                              be used to get the
    *                                              distinguished name
    *                                              and password to use
    *                                              for authentication.
    *                                              If this value is null
    *                                              and REFERRALS is
    *                                              true, referrals will
    *                                              be followed with
    *                                              anonymous (= no)
    *                                              authentication.
    *
    *                                              By default, the value
    *                                              of this option is
    *                                              null.
    *
    *
    *     LDAPConnection.BIND          LDAPBind   Specifies an object
    *                                              that can process an
    *                                              authentication
    *                                              request, overriding
    *                                              the default
    *                                              authentication
    *                                              behavior. This is
    *                                              typically used for
    *                                              processing
    *                                              authentication during
    *                                              referral following.
    *
    *     LDAPConnection.REFERRALS_HOP_LIMIT  Integer  Specifies the
    *                                              maximum number of
    *                                              referrals in a
    *                                              sequence that the
    *                                              client will follow.
    *                                              For example, if
    *                                              REFERRALS_HOP_LIMIT
    *                                              is 5, the client will
    *                                              follow no more than 5
    *                                              referrals in a row
    *                                              when resolving a
    *                                              single LDAP request.
    *
    *                                              The default value of
    *                                              this option is 10.
    *
    *
    *     LDAPConnection.BATCHSIZE     Integer    Specifies the number
    *                                              of search results to
    *                                              return at a time. For
    *                                              example, if BATCHSIZE
    *                                              is 1, enumerating an
    *                                              LDAPSearchResults
    *                                              will block only until
    *                                              one entry is
    *                                              available. If it is
    *                                              0, enumerating will
    *                                              block until all
    *                                              entries have been
    *                                              retrieved from the
    *                                              server.
    *
    *                                              The default value of
    *                                              this option is 1.
    *
    *
    *  value          The value to assign to the option. The value must
    *                 be the java.lang object wrapper for the
    *                 appropriate parameter (e.g. boolean->Boolean,
    *                 int->Integer).
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
         case LDAPConnection.REFERRALS_REAUTHENTICATION:
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
   }

}


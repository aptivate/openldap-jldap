/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPv2.java,v 1.8 2000/09/14 20:06:15 judy Exp $
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
 
package com.novell.ldap;
 
/*
 * public interface LDAPv2
 */
 
/**
 *
 *  As a mechanism to support planned and future LDAP protocol
 *  extensions, the functionality defined in the LDAPv2 interface
 *  corresponds to version 2 of the LDAP protocol. The LDAPConnection 
 *  class implements LDAPv2 and LDAPv3.  Applications can test 
 *  for support of these protocol levels in a given package with
 *  the instanceof operator.
 */
public interface LDAPv2 {

   /*
    * Defines (always static and final)
    */
  
  /**
  * Searches only the base obect.
  */
   public int SCOPE_BASE   = 0;
   
 /**
  * Searches only the immediate subordinates of the base obect. 
  */
   public int SCOPE_ONE    = 1;
   
 /**
  * Searches the base object and all entries within its subtree.
  */
   public int SCOPE_SUB    = 2;

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
   public void abandon(LDAPSearchResults results) throws LDAPException;

   /**
    * Synchronously adds an entry to the directory.
    *
    * @param entry    LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void add(LDAPEntry entry) throws LDAPException;

   /**
    *
    * Asynchronously adds an entry to the directory, using the specified
    * constraints.
    *
    *  @param entry   LDAPEntry object specifying the distinguished
    *                 name and attributes of the new entry.<br><br>
    *
    *  @param cons    Constraints specific to the operation.
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void add(LDAPEntry entry,
                   LDAPConstraints cons)
                   throws LDAPException;

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) as an LDAPv2 bind, using the specified name and 
    * password.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.<br><br>
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
                    throws LDAPException;

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) as an LDAPv2 bind, using the specified name, 
    * password, and constraints.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.<br><br>
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
    * @param cons     Constraints specific to the operation.
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    *
    */
   public void bind(String dn,
                    String passwd,
                    LDAPConstraints cons)
                    throws LDAPException;

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) as an LDAPv2 bind, using the specified name,  
    * password, and listener.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.<br><br>
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
    * @param listener Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally. It is
    *                 recommended that either the synchronous version
    *                 of this method is used or that the client blocks
    *                 until the listener returns a response.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    *
    */
   public LDAPResponseListener bind(String dn,
				                    String passwd,
							  LDAPResponseListener listener)
				                    throws LDAPException;

   /**
    *
    * Authenticates to the LDAP server (that the object is currently
    * connected to) as an LDAPv2 bind, using the specified name, 
    * password, listener, and constraints.  
    *
    * <p>If the object has been disconnected from an LDAP server,
    * this method attempts to reconnect to the server. If the object
    * has already authenticated, the old authentication is discarded.</p>
    * 
    *  @param dn      If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name.<br><br>
    *
    *  @param passwd  If non-null and non-empty, specifies that the
    *                 connection and all operations through it should
    *                 be authenticated with dn as the distinguished
    *                 name and passwd as password.
    *
    * @param listener Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally. It is
    *                 recommended that either the synchronous version
    *                 of this method is used or that the client blocks
    *                 until the listener returns a response.
    *
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
				                throws LDAPException;

   /**
    *
    * Synchronously checks to see if an entry contains an attribute 
    * with a specified value. 
    * 
    *  @param dn      The distinguished name of the entry to use in the
    *                 comparison.<br><br>
    *
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
                          throws LDAPException;

   /**
    *
    * Checks to see if an entry contains an attribute with a specified
    * value, using the specified constraints. 
    *
    *  @param dn      The distinguished name of the entry to use in the
    *                 comparison.<br><br>
    *
    *  @param attr    The attribute to compare against the entry. The
    *                 method checks to see if the entry has an
    *                 attribute with the same name and value as this
    *                 attribute.<br><br>
    *
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
                          throws LDAPException;

   /**
    *
    *  Connects to the specified host and port as an LDAPv2 connection. 
    *
    *  <p>If this LDAPConnection object represents an open connection, the
    *  connection is colosed first before the new connection is opened. 
    *  At this point, there is no authentication, and any operations are
    *  conducted as an anonymous client.</p>
    *
    *  <p> When more than one host name is specified, each host is contacted
    *  in turn until a connection can be established.</p>
    *
    *  @param host A host name or a dotted string representing the IP address
    *              of a host running an LDAP server to connect to. It may also
    *              contain a list of host names, space-delimited. Each host 
    *              name can include a trailing colon and port number.<br><br>
    *
    *  @param port The TCP or UDP port number to connect to or contact. 
    *              The default LDAP port is 389. The port parameter is 
    *              ignored for any host hame which includes a colon and 
    *              port number.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    *
    */
   public void connect(String host,
                       int port)
                       throws LDAPException;

  /**
    *
    *  Connects to the specified host and port, using the specified name
    *  and password, as an LDAPv2 connection. 
    *
    *  <p>If this LDAPConnection object represents an open connection, the
    *  connection is colosed first before the new connection is opened. 
    *  This is equivalent to connect (host, port) followed by bind (dn, 
    *  passwd).</p>
    *
    *  <p> When more than one host name is specified, each host is contacted
    *  in turn until a connection can be established.</p>
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
    *
    *  @param dn   If non-null and non-empty, specifies that the 
    *              connection and all operations through it should be 
    *              authenticated with the dn as the distinguished name.<br><br>
    *
    *  @param passwd   If non-null and non-empty, specifies that the
    *                  connection and all operations through it should 
    *                  be authenticated with the DN as the distinguished 
    *                  name and passwd as the password.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    *
    */
   public void connect(String host,
                       int port,
                       String dn,
                       String passwd)
                       throws LDAPException;

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
   public void delete(String dn) throws LDAPException;

   /**
    *
    * Synchronously deletes the entry with the specified distinguished name 
    * from the directory, using the specified constraints.
    *
    *  @param dn      The distinguished name of the entry to delete.<br><br>
    *
    *  @param cons    Constraints specific to the operation.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public void delete(String dn,
                      LDAPConstraints cons)
                      throws LDAPException;

   /**
    *
    * Synchronously disconnects from the LDAP server. 
    *
    * <p>Before the object can perform LDAP operations again, it must </p>
    * reconnect to the server by calling connect.
    *
    * The disconnect method abandons any outstanding requests, issues an 
    * unbind request to the server, and then close the socket.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    *
    */
   public void disconnect() throws LDAPException;

   /**
    * 
    * Returns the value of the specified option for this object.
    *
    * @param option   The option whose value is to be returned (the See Also
    *                 section contains the possible options).
    *
    * @return The value of the specified option.
    *
    * @exception LDAPException A general exception which includes an error 
    *                          message and an LDAP error code.
    *
    * @see #setOption(int, Object)
    * @see LDAPConnection#BATCHSIZE
    * @see LDAPConnection#BIND
	* @see LDAPv3#CLIENTCONTROLS
    * @see LDAPConnection#DEREF
    * @see LDAPConnection#REFERRALS
    * @see LDAPConnection#REFERRALS_HOP_LIMIT
    * @see LDAPConnection#REFERRALS_REBIND_PROC
	* @see LDAPv3#SERVERCONTROLS
    * @see LDAPConnection#SERVER_TIMELIMIT
    * @see LDAPConnection#SIZELIMIT
    * @see LDAPConnection#STRING_FORMAT
    */
   public Object getOption(int option) throws LDAPException;

   /**
    * Synchronously makes a single change to an existing entry in the 
    * directory.
    *
    * <p>For example, changes the value of an attribute, adds 
    * a new attribute value, or removes an existing attribute value. </p>
    *
    * <p>The LDAPModification object specifies both the change to be made and
    * the LDAPAttribute value to be changed.</p>
    *
    *  @param dn     The distinguished name of the entry to modify.<br><br>
    *
    *  @param mod    A single change to be made to the entry.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void modify(String dn,
                      LDAPModification mod)
                      throws LDAPException;

   /**
    *
    * Synchronously makes a single change to an existing entry in the  
    * directory, using the specified constraints.
    *
    * <p>For example, changes the value of an attribute, adds a new 
    * attribute value, or removes an existing attribute value.</p>
    *
    * <p>The LDAPModification object specifies both the change to be 
    * made and the LDAPAttribute value to be changed.</p>
    *
    *  @param dn       The distinguished name of the entry to modify.<br><br>
    *
    *  @param mod      A single change to be made to the entry.<br><br>
    *
    *  @param cons     The constraints specific to the operation.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void modify(String dn,
                      LDAPModification mod,
                      LDAPConstraints cons)
                      throws LDAPException;

   /**
    *
    * Synchronously makes a set of changes to an existing entry in the 
    * directory.
    *
    * <p>For example, changes attribute values, adds new attribute 
    * values, or removes existing attribute values).</p>
    * 
    *  @param dn     Distinguished name of the entry to modify.<br><br>
    *
    *  @param mods   A set of changes to be made to the entry.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public void modify(String dn,
                      LDAPModificationSet mods)
                      throws LDAPException;

   /**
    * Synchronously makes a set of changes to an existing entry in the 
    * directory, using the specified constraints.
    *
    * <p>For example, changes attribute values, adds new attribute values, 
    * or removes existing attribute values.</p>
    * 
    *  @param dn      The distinguished name of the entry to modify.<br><br>
    *
    *  @param mods    A set of changes to be made to the entry.<br><br>
    *
    *  @param cons    The constraints specific to the operation.
    *
    * @exception LDAPException A general exception which includes an 
    *                          error message and an LDAP error code.
    */
   public void modify(String dn,
                      LDAPModificationSet mods,
                      LDAPConstraints cons)
                      throws LDAPException;

   /**
    * Synchronously reads the entry for the specified distiguished name (DN) 
    * and retrieves all attributes for the entry.
    *
    *  @param dn        The distinguished name of the entry to retrieve.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public LDAPEntry read(String dn) throws LDAPException;

   /**
    *
    * Synchronously reads the entry for the specified distiguished name (DN), 
    * using the specified constraints, and retrieves all attributes for the
    * entry.
    *
    *  @param dn         The distinguished name of the entry to retrieve.<br><br>
    *
    *  @param cons       The constraints specific to the operation.
    *
    *  @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
   public LDAPEntry read(String dn,
                         LDAPSearchConstraints cons)
                         throws LDAPException;

   /**
    *
    * Synchronously reads the entry for the specified distinguished name (DN) 
    * and retrieves only the specified attributes from the entry.
    *
    *  @param dn         The distinguished name of the entry to retrieve.<br><br>
    *
    *  @param attrs      The names of the attributes to retrieve.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                            message and an LDAP error code.
    */
   public LDAPEntry read(String dn,
                         String attrs[])
                         throws LDAPException;

   /**
    *
    * Synchronously reads the entry for the specified distinguished name (DN),
    * using the specified constraints, and retrieves only the specified 
    * attributes from the entry.
    *
    *  @param dn       The distinguished name of the entry to retrieve.<br><br>
    *
    *  @param attrs    The names of the attributes to retrieve.<br><br>
    *
    *  @param cons     The constraints specific to the operation.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public LDAPEntry read(String dn,
                         String attrs[],
		             LDAPSearchConstraints cons)
                         throws LDAPException;

   /**
    *
    * Synchronously renames an existing entry in the directory.
    *
    *  @param dn       The current distinguished name of the entry.<br><br>
    *
    *  @param newRdn   The new relative distinguished name for the entry.<br><br>
    *
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
                      throws LDAPException;

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
                      throws LDAPException;

   /**
    *
    * Synchronously performs the search specified by the parameters.
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
      throws LDAPException;

   /**
    *
    * Synchronously performs the search specified by the parameters, also
    * allowing specification of constraints for the search (such as the 
    * maximum number of entries to find or the maximum time to wait for 
    * search results).
    *
    * <p>As part of the search constraints, the function allows specifying
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
    *   <li>LDAPv2.SCOPE_BASE - searches only the base DN
    *
    *   <li>LDAPv2.SCOPE_ONE - searches only entries under the base DN
    *                                  
    *   <li>LDAPv2.SCOPE_SUB - searches the base DN and all entries
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
      throws LDAPException;

   /**
    *
    * Sets the value of the specified option for this object.
    *
    * <p>These options represent the default search constraints for the
    * current connection. Some of these options are also propagated through
    * the LDAPConstraints, which can be obtained from the connection object
    * with the getSearchConstraints method.</p>
    *
    * <p>The option that is set here applies to all subsequent searches
    * performed through the current connection, unless it is overridden
    * with an LDAPConstraints at the time of search.</p>
    *
    * <p>To set a constraint only for a particular search, create an
    * LDAPConstraints object with the new constraints and pass it to the
    * LDAPConnection.search method.</p>
    *
    *
    *  @param option         The name of the option to set (the See Also
    *                        section lists their names).
    *<br><br>
    *  @param value          The value to assign to the option. The value must
    *                        be the java.lang object wrapper for the
    *                        appropriate parameter (for example, boolean->
    *                        Boolean, int->Integer).
    *
    * @exception LDAPException A general exception which includes an error 
    * message and an LDAP error code.
    *
    * @see #getOption(int)
    * @see LDAPConnection#BATCHSIZE
    * @see LDAPConnection#BIND
	* @see LDAPv3#CLIENTCONTROLS
    * @see LDAPConnection#DEREF
    * @see LDAPConnection#REFERRALS
    * @see LDAPConnection#REFERRALS_HOP_LIMIT
    * @see LDAPConnection#REFERRALS_REBIND_PROC
	* @see LDAPv3#SERVERCONTROLS
    * @see LDAPConnection#SERVER_TIMELIMIT
    * @see LDAPConnection#SIZELIMIT
    * @see LDAPConnection#STRING_FORMAT
    * @see LDAPConnection#TIMELIMIT
    */
   public void setOption(int option,
                         Object value)
                         throws LDAPException;

}

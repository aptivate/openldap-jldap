/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPv2.java,v 1.5 2000/08/28 22:18:59 vtag Exp $
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
 
/**
 * public interface LDAPv2
 *
 *  As a mechanism to support planned and future LDAP protocol
 *  extensions, functionality is defined in an interface - LDAPv2,
 *  corresponding to version 2 of the LDAP protocol. LDAPConnection must
 *  implement at least LDAPv2, and may implement LDAPv3.  Applications
 *  can test for support of these protocol levels in a given package with
 *  the instanceof operator.
 */
public interface LDAPv2 {

   /*
    * Defines (always static and final)
    */

   public int SCOPE_BASE   = 0;
   public int SCOPE_ONE    = 1;
   public int SCOPE_SUB    = 2;

   public int LDAP_DEREF_NEVER      = 0; // default
   public int LDAP_DEREF_SEARCHING  = 1;
   public int LDAP_DEREF_FINDING    = 2;
   public int LDAP_DEREF_ALWAYS     = 3;

   /**
    * Notifies the server to not send additional results associated with
    * this LDAPSearchResults object, and discards any results already
    * received.
    *
    * Parameters are:
    *
    *  results        An object returned from a search.
    */
   public void abandon(LDAPSearchResults results) throws LDAPException;

   /**
    * Adds an entry to the directory.
    *
    * Parameters are:
    *
    *  entry          LDAPEntry object specifying the distinguished
    *                  name and attributes of the new entry.
    */
   public void add(LDAPEntry entry) throws LDAPException;

   /**
    * Adds an entry to the directory.
    *
    * Parameters are:
    *
    *  entry          LDAPEntry object specifying the distinguished
    *                  name and attributes of the new entry.
    *
    *  cons           Constraints specific to the operation.
    */
   public void add(LDAPEntry entry,
                   LDAPConstraints cons)
                   throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * Parameters are:
    *
    *  dn             If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
    */
   public void bind(String dn,
                    String passwd)
                    throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * Parameters are:
    *
    *  dn             If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
    *
    * cons           Constraints specific to the operation.
    */
   public void bind(String dn,
                    String passwd,
                    LDAPConstraints cons)
                    throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * Parameters are:
    *
    *  dn             If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
    * listener       Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally. It is
    *                 recommended that either the synchronous version
    *                 of this method is used or that the client blocks
    *                 until the listener returns a response.
    */
   public LDAPResponseListener bind(String dn,
				                    String passwd,
									LDAPResponseListener listener)
				                    throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * Parameters are:
    *
    *  dn             If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
	*
    * listener       Handler for messages returned from a server in
    *                 response to this request. If it is null, a
    *                 listener object is created internally. It is
    *                 recommended that either the synchronous version
    *                 of this method is used or that the client blocks
    *                 until the listener returns a response.
	*
    * cons           Constraints specific to the operation.
    */
   public LDAPResponseListener bind(String dn,
				                    String passwd,
									LDAPResponseListener listener,
				                    LDAPConstraints cons)
				                    throws LDAPException;

   /**
    * Checks to see if an entry contains an attribute with a specified
    * value.  Returns true if the entry has the value, and false if the
    * entry does not have the value or the attribute.
    *
    * Parameters are:
    *
    *  dn             The distinguished name of the entry to use in the
    *                  comparison.
    *
    *  attr           The attribute to compare against the entry. The
    *                  method checks to see if the entry has an
    *                  attribute with the same name and value as this
    *                  attribute.
    */
   public boolean compare(String dn,
                          LDAPAttribute attr)
                          throws LDAPException;

   /**
    * Checks to see if an entry contains an attribute with a specified
    * value.  Returns true if the entry has the value, and false if the
    * entry does not have the value or the attribute.
    *
    * Parameters are:
    *
    *  dn             The distinguished name of the entry to use in the
    *                  comparison.
    *
    *  attr           The attribute to compare against the entry. The
    *                  method checks to see if the entry has an
    *                  attribute with the same name and value as this
    *                  attribute.
    *
    *  cons           Constraints specific to the operation.
    */
   public boolean compare(String dn,
                          LDAPAttribute attr,
                          LDAPConstraints cons)
                          throws LDAPException;

   /**
    * Connects to the specified host and port. If this LDAPConnection
    * object represents an open connection, the connection is closed first
    * before the new connection is opened.  At this point there is no
    * authentication, and any operations will be conducted as an anonymous
    * client.
    */
   public void connect(String host,
                       int port)
                       throws LDAPException;

   /**
    * Connects to the specified host and port and uses the specified DN and
    * password to authenticate to the server. If this LDAPConnection object
    * represents an open connection, the connection is closed first before
    * the new connection is opened. This is equivalent to connect(host,
    * port) followed by bind(dn, passwd).
    *
    * Parameters are:
    *
    *  host           Contains a hostname or dotted string representing
    *                  the IP address of a host running an LDAP server
    *                  to connect to. Alternatively, it may contain a
    *                  list of host names, space-delimited.  Each host
    *                  name may include a trailing colon and port
    *                  number.  In the case where more than one host
    *                  name is specified, each host name in turn will be
    *                  contacted until a connection can be established.
    *                  Examples:
    *
    *     "directory.knowledge.com"
    *     "199.254.1.2"
    *     "directory.knowledge.com:1050 people.catalog.com 199.254.1.2"
    *
    *  port           Contains the TCP or UDP port number to connect to
    *                  or contact. The default LDAP port is 389. "port"
    *                  is ignored for any host name which includes a
    *                  colon and port number.
    *
    *  dn             If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
    */
   public void connect(String host,
                       int port,
                       String dn,
                       String passwd)
                       throws LDAPException;

   /**
    * Deletes the entry for the specified DN from the directory.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    */
   public void delete(String dn) throws LDAPException;

   /**
    * Deletes the entry for the specified DN from the directory.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to modify.
    *
    *  cons           Constraints specific to the operation.
    */
   public void delete(String dn,
                      LDAPConstraints cons)
                      throws LDAPException;

   /**
    * Disconnects from the LDAP server. Before the object can perform LDAP
    * operations again, it must reconnect to the server by calling connect.
    */
   public void disconnect() throws LDAPException;

   /**
    * Returns the value of the specified option for this object.
    *
    * Parameters are:
    *
    *  option         See LDAPConnection.setOption for a description of
    *                  valid options.
    */
   public Object getOption(int option) throws LDAPException;

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
    */
   public void modify(String dn,
                      LDAPModification mod)
                      throws LDAPException;

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
    *  cons           Constraints specific to the operation.
    */
   public void modify(String dn,
                      LDAPModification mod,
                      LDAPConstraints cons)
                      throws LDAPException;

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
    */
   public void modify(String dn,
                      LDAPModificationSet mods)
                      throws LDAPException;

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
                      throws LDAPException;

   /**
    * Reads the entry for the specified distiguished name (DN) and
    * retrieves all attributes for the entry.
    *
    * Parameters are:
    *
    *  dn             Distinguished name of the entry to retrieve.
    */
   public LDAPEntry read(String dn) throws LDAPException;

   /**
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
                         throws LDAPException;

   /**
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
                         throws LDAPException;

   /**
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
                         throws LDAPException;

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
    */
   public void rename(String dn,
                      String newRdn,
                      boolean deleteOldRdn)
                      throws LDAPException;

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
    *  cons           Constraints specific to the operation.
    */
   public void rename(String dn,
                      String newRdn,
                      boolean deleteOldRdn,
                      LDAPConstraints cons)
                      throws LDAPException;

   /**
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
      throws LDAPException;

   /**
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
    *
    *  cons           Constraints specific to the search.
    */
   public LDAPSearchResults search(String base,
                                   int scope,
                                   String filter,
                                   String attrs[],
                                   boolean typesOnly,
                                   LDAPSearchConstraints cons)
      throws LDAPException;

   /**
    * Performs the search specified by the LDAP URL.
    *
    *
    * Parameters are:
    *
    *  toGet          LDAP URL representing the search to run. It may
    *                  contain host, port, base DN, attributes to
    *                  return, and a search filter.
    */
   /*
   public static LDAPSearchResults search(LDAPUrl toGet)
                                          throws LDAPException;
   */

   /**
    * Performs the search specified by the LDAP URL.
    *
    *
    * Parameters are:
    *
    *  toGet          LDAP URL representing the search to run. It may
    *                  contain host, port, base DN, attributes to
    *                  return, and a search filter.
    *
    *  cons           Constraints specific to the search.
    */
   /*
   public static LDAPSearchResults search(LDAPUrl toGet,
                                          LDAPSearchConstraints cons)
                                          throws LDAPException;
   */

   /**
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
    *
    *     Option                       Type       Description
    *
    *     LDAPConnection.DEREF         Integer    Specifies under what
    *                                              circumstances the
    *                                              object dereferences
    *                                              aliases. By default,
    *                                              the value of this
    *                                              option is
    *                                              LDAPConnection.DEREF 
    *                                                                  _
    *                                              NEVER.
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
    *                  be the java.lang object wrapper for the
    *                  appropriate parameter (e.g. boolean->Boolean,
    *                  int->Integer).
    */
   public void setOption(int option,
                         Object value)
                         throws LDAPException;

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPv3.java,v 1.8 2000/09/14 22:43:26 judy Exp $
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

import java.util.*;
 
/*
 * 4.29 public interface LDAPv3
 *                     extends LDAPv2
 */
 
/**
 *
 *  Extends LDAPv2 by adding support for features of version 3 of
 *  the LDAP protocol. 
 *
 *  <p> The LDAPConnection class implements the LDAPv2 interface and the
 *  LDAPv3 interface. Applications can test for support of these
 *  protocol levels in a given package with the instanceof operator.</p>
 */
public interface LDAPv3 extends LDAPv2 {

   /*
    * Defines (are static and final)
    */

  /**
   * Specifies that the server controls are to be sent to the server with every 
   * LDAP operation.
   */
   public int SERVERCONTROLS  = 30;
   
  /**
   * Specifies that the client controls are to be applied to every LDAP operation.
   *
   * <p>These controls are applied before the operation is sent to the server.
   * Client controls are never sent to the server. </p>
   */
   public int CLIENTCONTROLS  = 31;

  /**
   * Used with search instead of an attribute list to indicate that no attributes
   * are to be returned.
   */
	public String NO_ATTRS = "1.1"; // from rfc 2251, sec 4.5.1


   /*
    * 4.40.1 bind
    */

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
    *                  in the bind, either 2 or 3. 
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
      throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and the specified set of
    * mechanisms. 
    *
    * <p>If none of the requested SASL mechanisms is available, an
    * exception is thrown.  If the object has been disconnected from an
    * LDAP server, this method attempts to reconnect to the server. If the
    * object had already authenticated, the old authentication is
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
                    throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and the specified set of
    * mechanisms. 
    *
    * <p>If none of the requested SASL mechanisms is available, an
    * exception is thrown.  If the object has been disconnected from an
    * LDAP server, this method attempts to reconnect to the server. If the
    * object had already authenticated, the old authentication is
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
                    throws LDAPException;

   /*
    * 4.40.2 connect
    */

   /**
    *
    *  Connects to the specified host and port, using the specified name,
    *  password, and LDAP version. 
    *
    *  <p>If this LDAPConnection object represents an open connection, the
    *  connection is colosed first before the new connection is opened. 
    *  This is equivalent to connect (host, port) followed by bind (dn, 
    *  passwd).</p>
    *
    *  <p>When more than one host name is specified, each host is contacted
    *  in turn until a connection can be established.</p>
    *
    *  <p>If the server does not support the requested protocol version, 
    *  an exception is thrown.</p> 
    *
    *  @param version  The LDAP protocol version, either 2 or 3.<br><br>
    *
    *  @param host A host name or a dotted string representing the IP address
    *              of a host running an LDAP server to connect to. It may also
    *              contain a list of host names, space-delimited. Each host 
    *              name can include a trailing colon and port number. Examples:
    *<ul>
    *              <li> directory.knowledge.com </li>
    *              <li> 199.254.1.2 </li>
    *              <li> directory.knowledge.com:1050 people.catalog.com 199.254.1.2
    *                    </li></ul>
    *<br>
    *
    *  @param port The TCP or UDP port number to connect to or contact. 
    *              The default LDAP port is 389. The port parameter is 
    *              ignored for any host hame which includes a colon and 
    *              port number.<br><br>
    *
    *  @param dn   If non-null and non-empty, specifies that the 
    *              connection and all operations through it should be 
    *              authenticated with the DN as the distinguished name.<br><br>
    *
    *  @param passwd   If non-null and non-empty, specifies that the
    *                  connection and all operations through it should 
    *                  be authenticated with the dn as the distinguished 
    *                  name and passwd as the password.
    *
    *  @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    *
    */
   public void connect(int version,
                       String host,
                       int port,
                       String dn,
                       String passwd)
                       throws LDAPException;

   /*
    * 4.40.3 extendedOperation
    */

   /**
    *
    * Provides a synchronous means to access extended, non-mandatory  
    * operations offered by a particular LDAPv3 compliant server.
    *
    * @param op  The object which contains (1) an identifier of an extended
    *            operation which should be recognized by the particular LDAP 
    *            server this client is connected to and (2)an operation-specific
    *            sequence of octet strings or BER-encoded values. 
    *
    * @return An operation-specific object, containing an ID and an octet string
    * or BER-encoded values.
    *
    * @exception LDAPException A general exception which includes an error 
    *  message and an LDAP error code.
    */
   public LDAPExtendedResponse extendedOperation(
                                   LDAPExtendedOperation op )
                                   throws LDAPException;

   /*
    * 4.40.4 getResponseControls
    */

    /**
    *
    *  Returns the latest server controls which an LDAP server returned with 
    *  its latest response to an LDAP request from the current thread or null 
    *  it the latest response contains no server controls.
    *
    *  @return The server controls from the latest response or null if the 
    *  response contains no server controls.
    */
   public LDAPControl[] getResponseControls();

   /*
    * 4.40.5 rename
    */

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
                      throws LDAPException;

   /**
    *
    * Synchronously renames an existing entry in the directory, using the 
    * specified constraints, and possibly repositioning the entry in the 
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
                      throws LDAPException;

}

/* **************************************************************************
 * $Id: LDAPv3.java,v 1.2 2000/03/14 18:17:31 smerrill Exp $
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
 
/**
 * 4.29 public interface LDAPv3
 *                     extends LDAPv2
 *
 *  LDAPv3 extends LDAPv2 by adding support for features of version 3 of
 *  the LDAP protocol. LDAPConnection implements at least LDAPv2, and may
 *  also implement LDAPv3. Applications can test for support of these
 *  protocol levels in a given package with the instanceof operator.
 */
public interface LDAPv3 extends LDAPv2 {

   /*
    * Defines (are static and final)
    */

   public int SERVERCONTROLS  = 0;
   public int CLIENTCONTROLS  = 1;

	public String NO_ATTRS = "1.1"; // from rfc 2251, sec 4.5.1


   /*
    * 4.29.1 bind
    */

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and password, with the
    * specified LDAP protocol version. If the server does not support the
    * requested protocol version, an exception is thrown.  If the object
    * has been disconnected from an LDAP server, this method attempts to
    * reconnect to the server. If the object had already authenticated, the
    * old authentication is discarded.
    *
    * Parameters are:
    *
    *  version        LDAP protocol version requested: currently 2 or
    *                  3.
    *
    *  dn              If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  passwd         If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name and passwd as password.
    */
   public void bind(int version,
                    String dn,
                    String passwd)
      throws LDAPException;

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and of a specified set of
    * mechanisms. If none of the requested SASL mechanisms is available, an
    * exception is thrown.  If the object has been disconnected from an
    * LDAP server, this method attempts to reconnect to the server. If the
    * object had already authenticated, the old authentication is
    * discarded. If mechanisms is null, or if the first version of the
    * method is called, the LDAP server will be interrogated for its
    * supportedSaslMechanisms attribute of its root DSE. See [5] for a
    * discussion of the SASL classes.
    *
    * Parameters are:
    *
    *  dn              If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  props          Optional qualifiers for the authentication
    *                  session, as defined in [5].
    *
    *  cbh            A class which may be called by the Mechanism
    *                  Driver to obtain additional information required,
    *                  such as additional credentials.
    */
   /*
   public void bind(String dn,
                    Properties props,
                    javax.security.auth.callback.CallbackHandler cbh)
                    throws LDAPException;
   */               

   /**
    * Authenticates to the LDAP server (that the object is currently
    * connected to) using the specified name and of a specified set of
    * mechanisms. If none of the requested SASL mechanisms is available, an
    * exception is thrown.  If the object has been disconnected from an
    * LDAP server, this method attempts to reconnect to the server. If the
    * object had already authenticated, the old authentication is
    * discarded. If mechanisms is null, or if the first version of the
    * method is called, the LDAP server will be interrogated for its
    * supportedSaslMechanisms attribute of its root DSE. See [5] for a
    * discussion of the SASL classes.
    *
    * Parameters are:
    *
    *  dn              If non-null and non-empty, specifies that the
    *                  connection and all operations through it should
    *                  be authenticated with dn as the distinguished
    *                  name.
    *
    *  mechanisms     An array of IANA-registered SASL mechanisms which
    *                  the client is willing to use for authentication.
    *
    *  props          Optional qualifiers for the authentication
    *                  session, as defined in [5].
    *
    *  cbh            A class which may be called by the Mechanism
    *                  Driver to obtain additional information required,
    *                  such as additional credentials.
    */
   /*
   public void bind(String dn,
                    String[] mechanisms,
                    Hashtable props,
                    javax.security.auth.callback.CallbackHandler cbh)
                    throws LDAPException;
   */               

   /*
    * 4.29.2 connect
    */

   /**
    * Connects to the specified host and port and uses the specified DN and
    * password to authenticate to the server, with the specified LDAP
    * protocol version. If the server does not support the requested
    * protocol version, an exception is thrown. If this LDAPConnection
    * object represents an open connection, the connection is closed first
    * before the new connection is opened. This is equivalent to
    * connect(host, port) followed by bind(version, dn, passwd).
    *
    * Parameters are:
    *
    *  version        LDAP protocol version requested: currently 2 or
    *                  3.
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
   public void connect(int version,
                       String host,
                       int port,
                       String dn,
                       String passwd)
                       throws LDAPException;

   /*
    * 4.29.3 extendedOperation
    */

   /**
    * Provides a means to access extended, non-mandatory operations offered
    * by a particular LDAP version 3 compliant server.
    *
    * Returns an operation-specific object, containing an ID and an Octet
    * String or BER-encoded value(s).
    *
    *  Note that the return value is different from that defined int the
    *  current draft.  The draft will be changed to match what we have
    *  here.
    *
    * Parameters are:
    *
    *  op             Object which contains an identifier of the
    *                  extended operation, which should be one
    *                  recognized by the particular server this client
    *                  is connected to, and  operation-specific sequence
    *                  of Octet String or BER-encoded value(s).
    */
   public LDAPExtendedResponse extendedOperation(
                                   LDAPExtendedOperation op )
                                   throws LDAPException;

   /*
    * 4.29.4 getResponseControls
    */

   /**
    * Returns the latest Server Controls returned by a Directory Server
    * with a response to an LDAP request from the current thread, or null
    * if the latest response contained no Server Controls.
    */
   public LDAPControl[] getResponseControls();

   /*
    * 4.29.5 rename
    */

   /**
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
    *                  to be the new parent of the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    */
   public void rename(String dn,
                      String newRdn,
                      String newParentdn,
                      boolean deleteOldRdn)
                      throws LDAPException;

   /**
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
    *                  to be the new parent of the entry.
    *
    *  deleteOldRdn   If true, the old name is not retained as an
    *                  attribute value.
    *
    *  cons           Constraints specific to the operation.
    */
   public void rename(String dn,
                      String newRdn,
                      String newParentdn,
                      boolean deleteOldRdn,
                      LDAPConstraints cons)
                      throws LDAPException;

}

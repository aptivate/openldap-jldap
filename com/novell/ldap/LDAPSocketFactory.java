/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPSocketFactory.java,v 1.3 2000/08/03 22:06:18 smerrill Exp $
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

import java.net.*;
import java.io.IOException;

/**
 * 4.25 public interface LDAPSocketFactory
 *
 *  Used to construct a socket connection for use in an LDAPConnection.
 *  An implementation of this interface may, for example, provide a
 *  TLSSocket connected to a secure server.
 */
public interface LDAPSocketFactory {

   /*
    * 4.25.1 makeSocket
    */

   /**
    * Returns a socket connected using the provided host name and port
    * number.
    *
    * There may be additional makeSocket methods defined when interfaces to
    * establish TLS and SASL authentication in the java environment have
    * been standardized.
    * Parameters are:
    *  host           Contains a hostname or dotted string representing
    *                  the IP address of a host running an LDAP server
    *                  to connect to.
    *
    *  port           Contains the TCP or UDP port number to connect to
    *                  or contact. The default LDAP port is 389.
    */
   public Socket makeSocket(String host, int port)
      throws IOException, UnknownHostException;

}

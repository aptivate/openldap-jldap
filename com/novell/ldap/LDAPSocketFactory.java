/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 *  Used to construct sockets used in LDAPConnection.
 *
 *  <p>This factory produces sockets that are to be used in an LDAPConnection.
 *  Programmers needing to provide or use specialized socket connections,
 *  including sockets based on Transport Layer Security (TLS) can implement
 *  this interface.
 *  An implementation of this interface may, for example, provide a
 *  TLSSocket connected to a secure server.</p>
 *  <p> An implementation of this
 *  factory can be set per connection or globally for all connections.</p>
 *
 *  @see LDAPConnection#LDAPConnection(LDAPSocketFactory)
 *  @see LDAPConnection#setSocketFactory
 */
public interface LDAPSocketFactory {

   /**
    * Returns a socket connected using the provided host name and port
    * number.
    * <p>This method is called in the constructor of LDAPConnection and 
    * the resulting socket will be used for the duration of the connection.</p>
    *
    *  @param host     The host name or a dotted string representing
    *                  the IP address of the LDAP server to which you want
    *                  to connect.
    *<br><br>
    *  @param port     The TCP or UDP port number to connect to
    *                  or contact. The default LDAP port is 389.
    *
    * @exception IOException The socket to the specified host and port
    *                        could not be created.
    *
    * @exception UnknownHostException The specified host could not be found.
    *
    * @return the new Socket
    */
   public Socket createSocket(String host, int port)
      throws IOException, UnknownHostException;
}

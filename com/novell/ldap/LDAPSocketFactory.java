/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSocketFactory.java,v 1.10 2001/06/13 17:51:06 jhammons Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 *
 *  Used to construct a socket connection for use in an LDAPConnection.
 *
 *  <p>Programmers needing to provide or use specialized socket connections,
 *  including Transport Layer Security (TLS) based ones, can provide an
 *  object constructor to implement them using this interface.
 *  An implementation of this interface may, for example, provide a
 *  TLSSocket connected to a secure server.</p>
 *
 */
public interface LDAPSocketFactory {

   /**
    * Returns a socket connected using the provided host name and port
    * number.
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
    */
   public Socket makeSocket(String host, int port)
      throws IOException, UnknownHostException;

}

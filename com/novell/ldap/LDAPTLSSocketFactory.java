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
 *  Used to construct a TLS socket and used by the startTLS method of
 *  LDAPConnection.
 *
 *  <p>Programmers needing to provide or use specialized socket connections for
 *  startTLS can provide an implementation of this interface.  Any socketFactory
 *  to be used by startTLS <i>must</i> implement this factory.  An
 *  implementation of this factory can be set per connection or globally for
 *  all connections. </p>
 *
 *  @see LDAPConnection#LDAPConnection(LDAPSocketFactory)
 *  @see LDAPConnection#setSocketFactory
 */
public interface LDAPTLSSocketFactory extends LDAPSocketFactory{

   /**
    * Called by startTLS and returns a TLS secured socket which is
    * layered over the specified socket.
    *
    * <p>Implementations of this interface MUST return a TLS secured socket
    * which, when closed, does not close the underlying socket.</p>
    * <p>
    * RFC2830 - the LDAP draft explaining how TLS should work in LDAP,
    * maindates that the connection identity must match the identify in the
    * certificate returned from the server.  For more information
    * see <a href="http://www.ietf.org/rfc/rfc2830.txt">rfc2830</a> section 5.2
    *
    * @param clearTextSocket The socket on which TLS is to be negotiated.
    *
    * @return The socket with TLS negotiated.
    *
    * @exception IOException The socket to the specified host and port
    *                        could not be created.
    *
    * @exception UnknownHostException The specified host could not be found.
    *
    * @see LDAPConnection#startTLS
    */
   public Socket createSocket(Socket clearTextSocket)
       throws IOException, UnknownHostException;
}

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

package org.ietf.ldap;

import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 *  Used to construct a socket connection for use in an LDAPConnection.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSocketFactory.html">
            com.novell.ldap.LDAPSocketFactory</a>
 */
public interface LDAPSocketFactory
{
    /**
     * Returns a socket connected using the provided host name and port
     * number.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSocketFactory.html#createSocket(java.lang.String, int)">
            com.novell.ldap.LDAPSocketFactory.createSocket(String, int)</a>
     */
    public Socket createSocket(String host, int port)
              throws IOException, UnknownHostException;
}

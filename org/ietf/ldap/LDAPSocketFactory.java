/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSocketFactory.java,v 1.9 2001/04/19 16:49:47 cmorris Exp $
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

package org.ietf.ldap;

import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 *  Used to construct a socket connection for use in an LDAPConnection.
 *
 * @see com.novell.ldap.LDAPSocketFactory
 */
public interface LDAPSocketFactory
{
    /**
     * Returns a socket connected using the provided host name and port
     * number.
     *
     * @see com.novell.ldap.LDAPSocketFactory#makeSocket(String,int)
     */
    public Socket makeSocket(String host, int port)
              throws IOException, UnknownHostException;
}

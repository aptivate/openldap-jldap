/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPJSSESecureSocketFactory.java,v 1.2 2001/05/02 20:36:34 cmorris Exp $
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

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Represents the socket factory that creates secure socket connections to
 * LDAP servers using JSSE technology.
 *
 * @see com.novell.ldap.LDAPJSSESecureSocketFactory
 */
public class LDAPJSSESecureSocketFactory implements LDAPSocketFactory
{
    private com.novell.ldap.LDAPJSSESecureSocketFactory factory;
    /**
     * Constructs an LDAPSecureSocketFactory object.
     *
     * @see com.novell.ldap.LDAPJSSESecureSocketFactory#LDAPJSSESecureSocketFactory()
     */
    public LDAPJSSESecureSocketFactory()
    {
        factory = new com.novell.ldap.LDAPJSSESecureSocketFactory();
        return;
    }

    /**
     * Returns the socket connected to the LDAP server with the specified
     * host name and port number.
     *
     * @see com.novell.ldap.LDAPJSSESecureSocketFactory#makeSocket(String,int)
     */
    public java.net.Socket makeSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        return factory.makeSocket(host, port);
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSecureSocketFactory.java,v 1.7 2001/03/01 00:29:57 cmorris Exp $
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
 * LDAP servers.
 *
 * @see com.novell.ldap.LDAPSecureSocketFactory
 */
public class LDAPSecureSocketFactory implements LDAPSocketFactory
{
    com.novell.ldap.LDAPSecureSocketFactory factory;

    /**
     * Constructs an LDAPSecureSocketFactory from a com.novell.ldap object
     */
    public LDAPSecureSocketFactory( com.novell.ldap.LDAPSecureSocketFactory f)
    {
        factory = f;
        return;
    }

    /**
     * Constructs an LDAPSecureSocketFactory object.
     *
     */
    public LDAPSecureSocketFactory()
    {
        factory = new com.novell.ldap.LDAPSecureSocketFactory();
        return;
    }

    /**
     * Returns the socket connected to the LDAP server with the specified
     * host name and port number.
     *
     * @see com.novell.ldap.LDAPSecureSocketFactory#makeSocket(String,int)
     */
    public java.net.Socket makeSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        return factory.makeSocket(host, port);
    }
}

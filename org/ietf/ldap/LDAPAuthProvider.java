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

/**
 *  This class encapsulates authentiation credentials for the specified
 *  host and port.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPAuthProvider.html">
            com.novell.ldap.LDAPAuthProvider</a>
 */
public class LDAPAuthProvider
{
    com.novell.ldap.LDAPAuthProvider auth; 
    /**
     * Constructs information that is used by the client for authentication
     * when following referrals automatically.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAuthProvider.html#LDAPAuthProvider(java.lang.String, java.lang.String)">
            com.novell.ldap.LDAPAuthProvider.LDAPAuthProvider( String, String)</a>
     */
    public LDAPAuthProvider ( String dn, byte[] password )
    {
        auth = new com.novell.ldap.LDAPAuthProvider( dn, password);
        return;
    }

    /**
     * Returns the com.novell.ldap.LDAPAuthProvider object
     */
    /* package */
    com.novell.ldap.LDAPAuthProvider getWrappedObject()
    {
        return auth;
    }
 
    /**
     * Returns the distinguished name to be used for authentication on
     * automatic referral following.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAuthProvider.html#getDN()">
            com.novell.ldap.LDAPAuthProvider.getDN()</a>
     */
    public String getDN()
    {
        return auth.getDN();
    }
 
    /**
     * Returns the password to be used for authentication on automatic
     * referral following.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAuthProvider.html#getPassword()">
            com.novell.ldap.LDAPAuthProvider.getPassword()</a>
     */
    public byte[] getPassword()
    {
        return auth.getPassword();
    }
}

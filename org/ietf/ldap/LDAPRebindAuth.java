/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPRebindAuth.java,v 1.9 2001/03/01 00:29:53 cmorris Exp $
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

/**
 *  This class encapsulates reauthentiation credentials for the specified
 *  host and port.
 *
 *  @see com.novell.ldap.LDAPRebindAuth
 */
public class LDAPRebindAuth
{
    com.novell.ldap.LDAPRebindAuth auth; 
    /**
     * Constructs information that is used by the client for authentication
     * when following referrals automatically.
     *
     *  @see com.novell.ldap.LDAPRebindAuth#LDAPRebindAuth(String,String)
     */
    public LDAPRebindAuth ( String dn, String password )
    {
        auth = new com.novell.ldap.LDAPRebindAuth( dn, password);
        return;
    }

    /**
     * Returns the com.novell.ldap.LDAPRebindAuth object
     */
    /* package */
    com.novell.ldap.LDAPRebindAuth getWrappedObject()
    {
        return auth;
    }
 
    /**
     * Returns the distinguished name to be used for reauthentication on
     * automatic referral following.
     *
     *  @see com.novell.ldap.LDAPRebindAuth#getDN()
     */
    public String getDN()
    {
        return auth.getDN();
    }
 
    /**
     * Returns the password to be used for reauthentication on automatic
     * referral following.
     *
     *  @see com.novell.ldap.LDAPRebindAuth#getPassword()
     */
    public String getPassword()
    {
        return auth.getPassword();
    }
}

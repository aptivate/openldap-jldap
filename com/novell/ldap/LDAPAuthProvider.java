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

/**
 *  An implementation of LDAPAuthHandler must be able to provide an
 *  LDAPAuthProvider object at the time of a referral.  The class
 *  encapsulates information that is used by the client for authentication
 *  when following referrals automatically.
 *
 *  @see LDAPAuthHandler
 *  @see LDAPBindHandler
 *  @see LDAPConstraints#setReferralFollowing(boolean)
 */
public class LDAPAuthProvider {
 
    private String dn;
    private byte[] password;
 
    /**
     * Constructs information that is used by the client for authentication
     * when following referrals automatically.
     *
     *  @param dn           The distinguished name to use when authenticating to
     *                      a server.
     *<br><br>
     *  @param password     The password to use when authenticating to a server.
     */
    public LDAPAuthProvider ( String dn, byte[] password ) {
        this.dn = dn;
        this.password = password;
        return;
    }
 
    /**
     * Returns the distinguished name to be used for authentication on
     * automatic referral following.
     *
     * @return The distinguished name from the object.
     */
    public String getDN() {
        return dn;
    }
 
    /**
     * Returns the password to be used for authentication on automatic
     * referral following.
     *
     * @return The byte[] value (UTF-8) of the password from the object.
     */
    public byte[] getPassword() {
        return password;
    }
}

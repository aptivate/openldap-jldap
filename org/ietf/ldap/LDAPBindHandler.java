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
 *
 *  Used to do explicit bind processing on a referral.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPBindHandler.html">
            com.novell.ldap.LDAPBindHandler</a>
 */
public interface LDAPBindHandler extends LDAPReferralHandler
{

    /**
     * Called by LDAPConnection when a referral is received.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPBindHandler.html#bind(java.lang.String[], com.novell.ldap.LDAPConnection)">
            com.novell.ldap.LDAPBindHandler.bind(String[], LDAPConnection)</a>
     */
    public LDAPConnection bind (String[] ldapurl, LDAPConnection conn)
            throws LDAPReferralException;
}

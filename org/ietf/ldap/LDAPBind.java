/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPBind.java,v 1.10 2001/03/01 00:29:46 cmorris Exp $
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
 *
 *  Used to do explicit bind processing on a referral.
 *
 * @see com.novell.ldap.LDAPBind
 */
public interface LDAPBind extends LDAPReferralHandler
{

    /**
     * Called by LDAPConnection when a referral is received.
     *
     * @see com.novell.ldap.LDAPBind#bind(String[], LDAPConnection)
     */
    public LDAPConnection bind (String[] ldapurl, LDAPConnection conn)
            throws LDAPReferralException;
}

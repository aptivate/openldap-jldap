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

package com.novell.ldap.client;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPUrl;

/**
 * This class encapsulates the combination of LDAPReferral URL and
 * the connection opened to service this URL
 */
public class ReferralInfo
{
    private LDAPConnection conn;
    private LDAPUrl referralUrl;
    private String[] referralList;

    /**
     * Construct the ReferralInfo class
     *
     * @param lc The LDAPConnection opened to process this referral
     *
     * @param refUrl The URL string associated with this connection
     */
    public ReferralInfo( LDAPConnection lc, String[] refList, LDAPUrl refUrl)
    {
        conn = lc;
        referralUrl = refUrl;
        referralList = refList;
        return;
    }

    /** Returns the referral URL
     *
     * @return the Referral URL
     */
    public final LDAPUrl getReferralUrl()
    {
        return referralUrl;
    }

    /** Returns the referral Connection
     *
     * @return the Referral Connection
     */
    public final LDAPConnection getReferralConnection()
    {
        return conn;
    }

    /** Returns the referral list
     *
     * @return the Referral list
     */
    public final String[] getReferralList()
    {
        return referralList;
    }
}

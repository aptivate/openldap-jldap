/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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
 *
 *  Used to provide credentials for authentication when processing a
 *  referral.
 *
 *  <p>A programmer desiring to supply authentication credentials
 *  to the API when automatically following referrals MUST
 *  implement this interface. If LDAPAuthHandler or LDAPBindHandler are not
 *  implemented, automatically followed referrals will use anonymous
 *  authentication. Referral URLs of any type other than LDAP (i.e. a
 *  referral URL other than ldap://something) are not chased automatically
 *  by the API on automatic following.</p>
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/SearchUtil.java.html">SearchUtil.java</p>
 *
 *  @see LDAPBindHandler
 *  @see LDAPConstraints#setReferralFollowing(boolean)
 */
public interface LDAPAuthHandler extends LDAPReferralHandler
{

    /**
     * Returns an object which can provide credentials for authenticating to
     * a server at the specified host and port.
     *
     *  @param host    Contains a host name or the IP address (in dotted string
     *                 format) of a host running an LDAP server.
     *<br><br>
     *  @param port    Contains the TCP or UDP port number of the host.
     *
     *  @return An object with authentication credentials to the specified
     *          host and port.
     */
    public LDAPAuthProvider getAuthProvider (String host, int port);
}

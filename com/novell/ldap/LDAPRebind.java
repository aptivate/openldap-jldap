/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPRebind.java,v 1.9 2001/01/25 16:34:06 vtag Exp $
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

package com.novell.ldap;

/**
 *
 *  Used to provide credentials for reauthentication when processing a
 *  referral.
 *
 *  <p>A programmer desiring to supply credentials to the default
 *  reauthentication behavior when automatically following referrals must
 *  implement this interface. If LDAPRebind or LDAPBind are not implemented,
 *  automatically followed referrals will use anonymous authentication.
 *  Referrals of any type other than to an LDAP server (i.e. a
 *  referral URL other than ldap://something) are ignored on automatic referral
 *  following.</p>
 *
 *  @see LDAPBind
 *  @see LDAPConstraints#setReferralFollowing(boolean)
 */
public interface LDAPRebind extends LDAPReferralHandler
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
   public LDAPRebindAuth getRebindAuthentication (String host, int port);
}



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
 *  Used to provide authentication credentials when processing a
 *  referral.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPAuthHandler.html">
            com.novell.ldap.LDAPAuthHandler</a>
 */
public interface LDAPAuthHandler extends LDAPReferralHandler
{
   /**
    * Returns an object which can provide authentication credentials to
    * a server at the specified host and port.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPAuthHandler.html#getRebindAuthentication(java.lang.String, int)">
            com.novell.ldap.LDAPAuthHandler.getRebindAuthentication(
            String, int)</a>
    */
   public LDAPAuthProvider getAuthProvider(String host, int port);
}

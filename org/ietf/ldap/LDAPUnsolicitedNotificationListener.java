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
 * An object that implements this interface can be notified when
 * unsolicited messages arrive from the server.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPUnsolicitedNotificationListener.html">
            com.novell.ldap.LDAPUnsolicitedNotificatonListener</a>
 */
public interface LDAPUnsolicitedNotificationListener
{

    /**
     * The method is called when an unsolicited message arrives from a server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUnsolicitedNotificationListener.html#messageReceived(com.novell.ldap.LDAPExtendedResponse)">
            com.novell.ldap.LDAPUnsolicitedNotificationListener.messageReceived(
            LDAPExtendedResponse)</a>
     */
    public void messageReceived(LDAPExtendedResponse msg);
}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.events;

import java.util.EventListener;


/**
 * This interface defines the generic listener for handling LDAP Events.
 * @see LDAPEvent
 */
public interface LDAPEventListener extends EventListener {
    /**
     * Called when LDAPEvent is received.
     * @param evt The non-null LDAPEvent.
     */
    void ldapEventNotification(LDAPEvent evt);

    /**
     * Called when an LDAPException while parsing a response or received from
     * server.
     * @param ldapevt The non-null LDAPExceptionEvent
     */
    void ldapExceptionNotification(LDAPExceptionEvent ldapevt);
}

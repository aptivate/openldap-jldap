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


/** This Listener is for receiving persistence search events. */
public interface PSearchEventListener extends LDAPEventListener {
    /**
     * Called when the Search Referal is returned from the server.
     * @param referalevent The non-null referal event.
     */
    void searchReferalEvent(SearchReferralEvent referalevent);

    /**
     * Called when the Search Result is returned from the server.
     * @param event The non-null SearchResultEvent
     */
    void searchResultEvent(SearchResultEvent event);
}

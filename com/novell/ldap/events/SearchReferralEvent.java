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

import com.novell.ldap.LDAPSearchResultReference;

/** This class represents an Persistence Search event fired in response to an
 * notification (LDAPSearchResult) sent by the LDAP Server.
 * @see LDAPEvent
 */
public class SearchReferralEvent extends LDAPEvent {
    /**
     * Default Constructor for SearchReferal Event.
     * @param source The LdapSearchResultReference Object which generated cause
     * this event.
     * @param eventsource
     * <b>Note:</b> For this Type of event Change type is undefined and hence
     * assign to any.
     */
    public SearchReferralEvent(
        final PsearchEventSource eventsource,
        final LDAPSearchResultReference source) {
        super(
            eventsource,
            source,
            EventConstant.CLASSIFICATION_LDAP_PSEARCH,
            EventConstant.LDAP_PSEARCH_ANY);
    }

    /**
     * Returns the Urls for Search Referal.
     * @return The non-null Array of Urls ( String).
     */
    public final String[] getUrls() {
        return (
            (LDAPSearchResultReference) getContainedEventInformation())
            .getReferrals();
    }
}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999-2002 Novell, Inc. All Rights Reserved.
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

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPSearchResultReference;
import com.novell.ldap.client.Debug;
import com.novell.ldap.controls.LDAPEntryChangeControl;
import com.novell.ldap.controls.LDAPPersistSearchControl;

/**
 * This Class is responsible for generation of Persistence Search Events.
 */
public class PsearchEventSource extends LDAPEventSource {
    /**
     * Removes a listener from receiving Persistence Search events fired
     * by this class.The listener may have registered more than once with
     * this PSearchEventSource, perhaps with different events request
     * arguments. After this method is invoked, the listener will no
     * longer receive events with this PSearchEventSource instance as the
     * event source (except for those events already in the process of
     * being dispatched). If the listener was not, or is no longer,
     * registered with this PSearchEventSource instance, this method does
     * not do anything.
     *
     * @param alistener Removes the listener from receiving the events
     *        fired by this PSearchEventSource.
     *
     * @throws LDAPException If problem is encountered while removing the
     *         listener.
     */
    public final void removeListener(final PSearchEventListener alistener)
        throws LDAPException {
        if (alistener == null) {
            throw new IllegalArgumentException("No parameter can be Null.");
        }

        super.stopeventpolling(alistener);
    }

    /**
     * This method is use to register for Persistence Search Events by the
     * given Listener. The events generate after registration would
     * include this PSearchEventSource as the EventSource. The valid
     * value for the scope parameter are the following:-
     *
     * <ul>
     * <li>
     * LdapConnection.SCOPE_BASE - searches only the base DN.
     * </li>
     * <li>
     * LdapConnection.SCOPE_ONE - searches only entries under the base DN.
     * </li>
     * <li>
     * LdapConnection.SCOPE_SUB - searches the base DN and all entries
     * within its subtree.
     * </li>
     * </ul>
     *
     * <br/><br/
     * > The valid value for eventchangetype parameter are the following:-
     *
     * <ul>
     * <li>
     * EventConstant.LDAP_PSEARCH_ADD: Track events for only additions to
     * the directory.
     * </li>
     * <li>
     * EventConstant.LDAP_PSEARCH_DELETE: Track events for only deletions
     * from the directory.
     * </li>
     * <li>
     * EventConstant.LDAP_PSEARCH_MODIFY: Track events for only
     * modification of  entries from the directory.
     * </li>
     * <li>
     * EventConstant.LDAP_PSEARCH_MODDN: Track events for only renaming of
     * entries from the directory.
     * </li>
     * <li>
     * EventConstant.LDAP_PSEARCH_ANY: Track events for all the changes in
     * the directory.
     * </li>
     * </ul>
     *
     * <br/>
     * <B>Note:</B>For the Persistence Search Events to be generate the
     * LDAP Server should support the Feature.
     *
     * @param conn LDAPConnection to be used for requesting the events
     *        from the server.
     * @param searchBase The base distinguished name to search from,i.e
     *        the Base for the Persistence search
     * @param scope The scope of the entries to search. The following are
     *        the valid options: <ul><li>LdapConnection.SCOPE_BASE -
     *        searches only the base DN   <li>LdapConnection.SCOPE_ONE -
     *        searches only entries under the base DN
     *        <li>LdapConnection.SCOPE_SUB - searches the base DN and all
     *        entries within its subtree   </ul><br><br>
     * @param filter Search filter specifying the search criteria.
     * @param attrs Names of attributes to retrieve.
     * @param typesOnly If true, returns the names but not the values of
     *        the attributes found. If false, returns the names and
     *        values for attributes found.
     * @param constraints The constraints specific to the search. The
     *        constraints Should not contain a
     *        LDAPPersistenceSearchControl.
     * @param eventchangetype Specify the type of Events to receive. The
     *        following are the valid options:-
     *        <ul><li>EventConstant.LDAP_PSEARCH_ADD: Track events for
     *        only additions to the directory.</li>
     *        <li>EventConstant.LDAP_PSEARCH_DELETE: Track events for
     *        only deletions from the directory.</li>
     *        <li>EventConstant.LDAP_PSEARCH_MODIFY: Track events for
     *        only modification of  entries from the directory.</li>
     *        <li>EventConstant.LDAP_PSEARCH_MODDN: Track events for only
     *        renaming of entries from the directory.</li>
     *        <li>EventConstant.LDAP_PSEARCH_ANY: Track events for all
     *        the changes in the directory.</li> </ul>
     * @param changeonly if true, Returns only changes to the directory as
     *        specified by eventchangetype. else false, it also returns
     *        all the current entries which satisfy the search criteria
     *        as Search Result.
     * @param alistener The non-null PSearchEventListener for receiving
     *        events notification.
     *
     * @throws LDAPException When the LDAP Server generates an exception.
     */
    public final void registerforEvent(
        final LDAPConnection conn,
        final String searchBase,
        final int scope,
        final String filter,
        final String[] attrs,
        final boolean typesOnly,
        final LDAPSearchConstraints constraints,
        final int eventchangetype,
        final boolean changeonly,
        final PSearchEventListener alistener)
        throws LDAPException {
        //check for Null.
        if ((conn == null)
            || (searchBase == null)
            || (filter == null)
            || (attrs == null)
            || (alistener == null)) {
            throw new IllegalArgumentException("Null argument specified");
        }

        LDAPSearchConstraints searchconstraints = null;

        if (constraints == null) {
            searchconstraints = new LDAPSearchConstraints();
        } else {
            searchconstraints = constraints;
        }

        //Create the persistent search control
        LDAPPersistSearchControl psCtrl =
            new LDAPPersistSearchControl(eventchangetype,
            // any change
        changeonly, //only get changes
        true, //return entry change controls
    true); //control is critcal

        // add the persistent search control to the search constraints
        searchconstraints.setControls(psCtrl);

        // perform the search with no attributes returned
            LDAPSearchQueue queue =
                conn.search(searchBase, // container to search
        scope, // search container's subtree
        filter, // search filter, all objects
        attrs, // don't return attributes
        typesOnly, // return attrs and values or attrs only.
        null, // use default search queue
    searchconstraints); // use default search constraints
        PSearchEventsGenerator eventgenerator = null;
        int[] ids = queue.getMessageIDs();

        if (ids.length == 1) {
            eventgenerator = new PSearchEventsGenerator(alistener, this);
            super.pollforevents(queue, conn, eventgenerator, ids[0], this);
        } else {
            throw new LDAPException(
                null,
                LDAPException.LOCAL_ERROR,
                "Unable to Obtain Message Id");
        }
    }

    /**
     * The inner class to transform the LDAPEvents received from
     * LDAPEventSource into PSearchListener's events.
     */
    private class PSearchEventsGenerator implements LDAPEventListener {
        /**
         * Local reference to the listener to which the event notification
         * is to be sent.
         */
        private PSearchEventListener listener;

        /**
         * Local Varible,which is used for storing the reference to the
         * source of the events generated from Generator. This reference
         * is send to all the LDAPEvents.
         */
        private PsearchEventSource eventsource;

        /**
         * Default Constructor for PSearchEventsGenerator.
         *
         * @param alistener PSearchEventListener which needs to be
         *        notified of Events.
         * @param aeventsource The Source Object which is required for
         *        LDAPEvents generation.
         */
        PSearchEventsGenerator(
            final PSearchEventListener alistener,
            final PsearchEventSource aeventsource) {
            listener = alistener;
            eventsource = aeventsource;
        }

        /**
         * Implements ldapEventNotification(LDAPEvent) in
         * LDAPEventListener
         *
         * @param evt LDAPEvent
         *
         * @see LDAPEventListener#ldapEventNotification(LDAPEvent)
         */
        public final void ldapEventNotification(final LDAPEvent evt) {
            LDAPMessage response = evt.getContainedEventInformation();

            if (response != null) {
                //Received a response object , use it to create a Event.
                switch (response.getType()) {
                    case LDAPResponse.SEARCH_RESULT_REFERENCE :

                        SearchReferralEvent event =
                            new SearchReferralEvent(
                                eventsource,
                                (LDAPSearchResultReference) response);
                        listener.searchReferalEvent(event);

                        break;

                    case LDAPResponse.SEARCH_RESPONSE :

                        int changeType = -1;
                        LDAPControl[] controls = response.getControls();

                        for (int i = 0; i < controls.length; i++) {
                            if (controls[i]
                                instanceof LDAPEntryChangeControl) {
                                LDAPEntryChangeControl ecCtrl =
                                    (LDAPEntryChangeControl) controls[i];

                                changeType = ecCtrl.getChangeType();

                                continue;
                            }
                        }

                        //if no changetype then value is -1.
                        SearchResultEvent resultevent =
                            new SearchResultEvent(
                                eventsource,
                                (LDAPSearchResult) response,
                                changeType);
                        listener.searchResultEvent(resultevent);

                        break;

                    case LDAPResponse.SEARCH_RESULT :

                        //Unexpected Search Response. Why
                        //Since Search result marks the end of
                        // search response, and in eventing
                        // search doesn't end from server
                        // side, only abandoned.
                        LDAPResponse responsemsg = (LDAPResponse) response;
                        int resultCode = responsemsg.getResultCode();

                        //It is expected that result code can
                        //be Success only, or else we would
                        //have got a ldapexception
                        if (Debug.LDAP_DEBUG) {
                            Debug.trace(
                                Debug.EventsCalls,
                                "\nUnexpected success response.");
                        }

                        //send an LDAPResponse event.
                        LDAPEvent ldapevent =
                            new LDAPEvent(
                                eventsource,
                                responsemsg,
                                EventConstant.CLASSIFICATION_LDAP_PSEARCH,
                                EventConstant.LDAP_PSEARCH_ANY);
                        listener.ldapEventNotification(ldapevent);

                        break;

                    default :

                        //well i don't know what to do.
                        //It is not a expected response.
                        //Just generated a generic ldapevent.
                        evt.setEventclassification(
                            EventConstant.CLASSIFICATION_LDAP_PSEARCH);

                        //Let the type be unknown
                        listener.ldapEventNotification(evt);
                }
            }
        }

        /**
         * Implements ldapExceptionNotification(LDAPExceptionEvent) for
         * LDAPEventListener.
         *
         * @param ldapevt LDAPExceptionEvent
         *
         * @see LDAPEventListener#ldapExceptionNotification(LDAPExceptionEvent)
         */
        public final void ldapExceptionNotification(final LDAPExceptionEvent ldapevt) {
            listener.ldapExceptionNotification(ldapevt);
        }

        /**
         * This method is required so that the Internal LDAPListener
         * is equal to external PSearchListener based on hashcode
         * equality.
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return listener.hashCode();
        }
    }

    /*End of Class PSearchEventsGenerator */
}

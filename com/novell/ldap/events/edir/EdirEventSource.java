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
package com.novell.ldap.events.edir;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPResponseQueue;
import com.novell.ldap.events.EventConstant;
import com.novell.ldap.events.LDAPEvent;
import com.novell.ldap.events.LDAPEventListener;
import com.novell.ldap.events.LDAPEventSource;
import com.novell.ldap.events.LDAPExceptionEvent;
import com.novell.ldap.resources.ExceptionMessages;


/**
 * This class act as a source for all the Edirectory Events. The class
 * contains method for eventregisteration (registerforEvent() and
 * registerforFilterevent()) and removal of events
 * listening(removeListener()). The events are generated for a generic
 * LDAPListener interface. .
 * 
 * <p>
 * Each of the method specified throws an IllegalArgumentException if the
 * arguments are null.
 * </p>
 */
public class EdirEventSource extends LDAPEventSource {
    /**
     * Default Constructor for this class.
     *
     * @throws LDAPException When intialization fails.
     */
    public EdirEventSource() throws LDAPException {
        super();
        registerIntermediateResponses();
    }

    /**
     * Removes a listener from receiving Edirectoryevents fired by this
     * class.The listener may have registered more than once with this
     * EdirEventSource, perhaps with different events request arguments.
     * After this method is invoked, the listener will no longer receive
     * events with this EdirEvent instance as the event source (except
     * for those events already in the process of being dispatched).
     *
     * @param alistener Removes the listener from receiving the events
     *        fired by this EdirEventSource.
     *
     * @throws LDAPException If problem is encountered while removing the
     *         listener or the listener is not registered for any event.
     */
    public void removeListener(final LDAPEventListener alistener)
        throws LDAPException {
        if (alistener == null) {
            throw new IllegalArgumentException(
                "No parameter can be Null."
            );
        }

        super.stopeventpolling(alistener);
    }

    /**
     * This method is use to register for Edirectory Events by the given
     * Listener. The events generated after registration would include
     * this EdirEventSource as the EventSource.
     *
     * @param specifier EdirEventSpecifier is used to specify the event
     *        type to generate.
     * @param conn LDAPConnection Object used to connect to server.
     * @param alistener The LDAPEventListener for receiving the events.
     *
     * @throws LDAPException When the LDAP operation fails.
     */
    public final void registerforEvent(
        final EdirEventSpecifier[] specifier, final LDAPConnection conn,
        final LDAPEventListener alistener
    ) throws LDAPException {
        if ((specifier == null) || (conn == null) || (alistener == null)) {
            throw new IllegalArgumentException("Null argument specified");
        }

        MonitorEventRequest requestoperation =
            new MonitorEventRequest(specifier);
        sendExtendedRequest(conn, requestoperation, alistener);
    }

    /**
     * Sends the LDAPExtendedRequest to the ldap server using the
     * specified, connection and registers the listener with the parent
     * polling thread.
     *
     * @param conn LDAPConnection for sending LDAPOperation.
     * @param requestoperation LDAPExtendedOperation to be send as
     *        LDAPExtendedRequest.
     * @param alistener The LDAPListener to be registered.
     *
     * @throws LDAPException When the underlying operations on connection
     *         fails.
     */
    private void sendExtendedRequest(
        final LDAPConnection conn,
        final LDAPExtendedOperation requestoperation,
        final LDAPEventListener alistener
    ) throws LDAPException {
        LDAPResponseQueue queue =
            conn.extendedOperation(requestoperation, null, null);
        EdirEventsGenerator eventgenerator = null;
        int[] ids = queue.getMessageIDs();

        if (ids.length == 1) {
            eventgenerator = new EdirEventsGenerator(alistener);

            super.pollforevents(queue, conn, eventgenerator, ids[0], this);
        } else {
            ///CLOVER:OFF
            throw new LDAPException(
                null, LDAPException.LOCAL_ERROR,
                "Unable to Obtain Message Id"
            );

            ///CLOVER:ON
        }
    }

    /**
     * This method is use to register for Edirectory Events by the given
     * Listener.It also specifies a Filter (Search Filter) which are used
     * to filter the events on the server side.The events generated after
     * registration would include this EdirEventSource as the
     * EventSource.
     *
     * @param specifier EdirEventSpecifier is used to specify the event
     *        type to generate.
     * @param conn LDAPConnection Object used to connect to server.
     * @param alistener The LDAPEventListener for receiving the events.
     *
     * @throws LDAPException When the LDAP operation fails.
     */
    public final void registerforFilterEvent(
        final EdirEventSpecifier[] specifier, final LDAPConnection conn,
        final LDAPEventListener alistener
    ) throws LDAPException {
        if ((specifier == null) || (conn == null) || (alistener == null)) {
            throw new IllegalArgumentException("Null argument specified");
        }

        MonitorFilterEventRequest requestoperation =
            new MonitorFilterEventRequest(specifier);

        sendExtendedRequest(conn, requestoperation, alistener);
    }

    /**
     * This method helps in registering the EdirEventIntermediateResponse
     * class with the LDAPIntermediateResponse. Actual registration code
     * is contained in EdirEventIntermediateResponse, which is excuted on
     * loading the same.
     *
     * @throws LDAPException When Class in Not Found.
     */
    private void registerIntermediateResponses() throws LDAPException {
        try {
            //Load EdirEventIntermediateResponse Class
            //so that it can register its Oid.
            getClass().getClassLoader().loadClass(
                EdirEventIntermediateResponse.class.getName()
            );
        } catch (ClassNotFoundException e) {
            ///CLOVER:OFF
            //Call it a Parameter Error with the message of Exception.
            throw new LDAPException(
                ExceptionMessages.PARAM_ERROR, LDAPException.LOCAL_ERROR,
                e.getMessage()
            );

            ///CLOVER:ON                
        }
    }

    /**
     * This class acts as a Decorator for the LDAPEventListener registered
     * by the user. It simply delegates the events to contained listener
     * after setting the Event Types as required.
     */
    class EdirEventsGenerator implements LDAPEventListener {
        private final LDAPEventListener listener;

        /**
         * Default Constructor
         *
         * @param delegatelistener The LDAPlistener to which the events
         *        are delegated.
         */
        EdirEventsGenerator(final LDAPEventListener delegatelistener) {
            listener = delegatelistener;
        }

        /**
         * @see com.novell.ldap.events.LDAPEventListener
         *      #ldapEventNotification(com.novell.ldap.events.LDAPEvent)
         */
        public void ldapEventNotification(final LDAPEvent evt) {
            evt.setEventclassification(
                EventConstant.CLASSIFICATION_EDIR_EVENT
            );

            if (
                evt.getContainedEventInformation().getType() == LDAPMessage.INTERMEDIATE_RESPONSE
            ) {
                //handling for intermediate response.
                //assuming only EdirEventIntermediateResponse expected.
                LDAPMessage message = evt.getContainedEventInformation();

                //Although it is not expected to obtain the other type
                //of Intermediate response
                if (message instanceof EdirEventIntermediateResponse) {
                    EdirEventIntermediateResponse responseintermediate =
                        (EdirEventIntermediateResponse) message;
                    evt.setEventtype(responseintermediate.getEventtype());
                }
            }

            listener.ldapEventNotification(evt);
        }

        /**
         * @see com.novell.ldap.events.LDAPEventListener
         *      #ldapExceptionNotification(com.novell.ldap.events.LDAPExceptionEvent)
         */
        public void ldapExceptionNotification(
            final LDAPExceptionEvent ldapevt
        ) {
            // send notification to client.
            listener.ldapExceptionNotification(ldapevt);
        }

        /**
         * This method is required so that the Internal LDAPListener is
         * equal to external PSearchListener based on hashcode equality.
         *
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return listener.hashCode();
        }
    }
}

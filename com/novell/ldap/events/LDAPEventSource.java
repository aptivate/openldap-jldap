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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPMessageQueue;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.client.Debug;

/**
 * The base Class which is used by all Ldap Event Generator. This class is
 * defined abstract so that it cannot be used directly. But it needs to
 * be subclassed to define a EventSource which can also classify
 * events.Since Event Classification is not handled completely in this
 * class.
 */
public abstract class LDAPEventSource {
    /**
     * Local Constant for time to sleep between two calls to checking the
     * queue.
     */
    private long sleepTime = 1000L;

    /**
     * Local Map to Store LDAPEventListener and associated generation
     * thread.
     */
    private Map listenermap = new TreeMap(new HashComparator());

    /**
     * Stops and removes the polling Thread for this listener from
     * receiving LDAP events fired by this class. The listener can only
     * register once once with this LDAPEventSource. After this method is
     * invoked, the listener will no longer receive events with this
     * LDAPEventSource instance as the event source (except for those
     * events already in the process of being dispatched). If the
     * listener was not, or is no longer, registered with this instance,
     * this method does not do anything.
     *
     * @param alistener Removes the listener from receiving the events
     *        fired by this LDAPEventSource.
     *
     * @throws LDAPException If problem is encountered while removing the
     *         listener.
     */
    protected void stopeventpolling(final LDAPEventListener alistener)
        throws LDAPException {
        if (alistener == null) {
            throw new IllegalArgumentException("No parameter can be Null.");
        }

        if (!listenermap.containsKey(alistener)) {
            if (Debug.LDAP_DEBUG) {
                Debug.trace(
                    Debug.EventsCalls,
                    "Unknown Listener send for removal");
            }
            throw new LDAPException(
                null,
                LDAPException.OTHER,
                "This in not a registered Listener");

        }

        Iterator eventthreaditerator =
            ((List) listenermap.get(alistener)).iterator();

        listenermap.remove(alistener);

        while (eventthreaditerator.hasNext()) {
            EventsGenerator generator =
                ((EventsGenerator) eventthreaditerator.next());

            generator.stopEventGeneration();

        }

    }

    /**
     * This method is to register for LDAPEvents generated from a specific
     * LDAPMessageQueue.
     *
     * @param queue LDAPMessageQueue for checking the events.
     * @param conn LDAPConnection used to create this MessageQueue ,
     *        required to abandon the message.
     * @param alistener The listener which would get the event.
     * @param msgid The message id for the Message send.
     * @param source The object which would be used as for LDAPEvents
     *        Generated.
     */
    protected void pollforevents(
        final LDAPMessageQueue queue,
        final LDAPConnection conn,
        final LDAPEventListener alistener,
        final int msgid,
        final LDAPEventSource source) {
        //check for not null, for each event.
        if ((queue == null)
            || (conn == null)
            || (alistener == null)
            || (source == null)) {
            throw new IllegalArgumentException("No parameter can be Null.");
        }

        EventsGenerator eventsthread =
            createListeningThread(queue, conn, alistener, msgid, source);

        eventsthread.start();

        List eventdecoratorlist = null;

        if (listenermap.containsKey(alistener)) {
            eventdecoratorlist = (List) listenermap.get(alistener);
        } else {
            eventdecoratorlist = new ArrayList();
            listenermap.put(alistener, eventdecoratorlist);
        }

        //Add Thread to List of Thread generating events for this Listener.
        eventdecoratorlist.add(eventsthread);

    }

    /**
     * This method creates the instance of EventsGenerator Thread, which
     * transforms the ldapresponse received into events.
     *
     * @param queue LDAPMessageQueue for checking the events.
     * @param conn LDAPConnection used to create this MessageQueue ,
     *        required to abandon the message.
     * @param alistener The listener which would get the event.
     * @param msgid The message id for the Message send.
     * @param source The object which would be used as for LDAPEvents
     *        Generated.
     *
     * @return instance of EventsGenerator
     */
    protected EventsGenerator createListeningThread(
        final LDAPMessageQueue queue,
        final LDAPConnection conn,
        final LDAPEventListener alistener,
        final int msgid,
        final LDAPEventSource source) {
        return new EventsGenerator(source, queue, alistener, conn, msgid);
    }

    /**
     * This method returns the time to sleep (or wait) between two calls
     * to check the ldap message queue for response.
     *
     * @return time in milliseconds
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * This method is used to set the time(in milliseconds) to sleep
     * between two calls to check the ldap messsage queue for response.
     * The value of sleep time cannot be zero or negative.
     * <b><i>Note:</i> If the time is set to very high value, the events
     * would not be received. Recommended value is 1 second
     * (Default).</b>
     *
     * @param l Sleep Time in MilliSeconds.
     */
    public void setSleepTime(final long l) {
        if (l <= 0) {
            throw new IllegalArgumentException("Sleep Time must be a non-zero positive number");
        }
        sleepTime = l;
    }

    /**
     * This inner Thread is used to notify the Registered Listener when an
     * LDAPResponse occurs.
     */
    protected class EventsGenerator extends Thread {
        /**
         * Local Variable, which is a reference to  SearchQueue containing
         * the response.
         */
        private final LDAPMessageQueue searchqueue;

        /**
         * Local Varible,which is used for storing the reference to the
         * source of the events generated from this Thread. This
         * reference is send to all the LDAPEvents.
         */
        private final LDAPEventSource eventsource;

        /** Message id for the Search Request Message. */
        private final int messageid;

        /** LDAPConnection for used to abandon the Message Queue.. */
        private final LDAPConnection ldapconnection;

        /**
         * Local reference to the listener to which the event notification
         * is to be sent.
         */
        private final LDAPEventListener eventlistener;

        /** Local variable to control the state of the system. */
        private volatile boolean isrunning = true;

        /**
         * Default Constructor for EventGenerator.
         *
         * @param aeventsource The Source Object which is required for
         *        LDAPEvents generation.
         * @param queue LDAPSearchqueue which contains the LDAPResponse
         *        objects.
         * @param listener LDAPEventListener which needs to be notified of
         *        the events.
         * @param aconnection LDAPConnection used to create this
         *        searchqueue.
         * @param amessageid LDAPMessage Id for the message send. events.
         */
        public EventsGenerator(
            final LDAPEventSource aeventsource,
            final LDAPMessageQueue queue,
            final LDAPEventListener listener,
            final LDAPConnection aconnection,
            final int amessageid) {
            super();
            eventsource = aeventsource;
            searchqueue = queue;
            eventlistener = listener;
            ldapconnection = aconnection;
            messageid = amessageid;
        }

        /**
         * Waits for the Messages on the Queue and notifies the Listener
         * of the same.
         *
         * @see java.lang.Runnable#run()
         */
        public final void run() {
            while (isrunning) {
                try {
                    LDAPMessage response = null;

                    while ((isrunning)
                        && (!searchqueue.isResponseReceived(messageid))) {
                        try {
                            sleep(sleepTime);
                        } catch (InterruptedException e) {
                            ///CLOVER:OFF
                            // ignore exception, just log it
                            if (Debug.LDAP_DEBUG) {
                                Debug.trace(
                                    Debug.EventsCalls,
                                    "Interrupt Exception"
                                        + e.getMessage());
                            }
                            ///CLOVER:ON
                        }
                    }

                    if (isrunning) {
                        response = searchqueue.getResponse(messageid);
                    }

                    if (response != null) {
                        processmessage(response);
                    }
                } catch (LDAPException e) {
                    ///CLOVER:OFF
                    LDAPExceptionEvent exceptionevent =
                        new LDAPExceptionEvent(eventsource, e, null);
                    eventlistener.ldapExceptionNotification(
                        exceptionevent);
                    ///CLOVER:ON
                }
            }
        }

        /**Processes the Message Receive on the queue.
         * This method actually sends the notification to the 
         * LDAPEventListener.
         * @param response
         */
        protected void processmessage(final LDAPMessage response) {
            //Received a response object , use it to create a Event.
            if (response instanceof LDAPResponse) {
                //Throws a LDAPException , which is sent to client.
                try {
                    ((LDAPResponse) response).chkResultCode();

                    //or simple send a ldapevent
                    eventlistener.ldapEventNotification(
                        new LDAPEvent(
                            eventsource,
                            response,
                            EventConstant.CLASSIFICATION_UNKNOWN,
                            EventConstant.TYPE_UNKNOWN));
                } catch (LDAPException e) {
                    LDAPExceptionEvent exceptionevent =
                        new LDAPExceptionEvent(eventsource, e, response);
                    eventlistener.ldapExceptionNotification(
                        exceptionevent);
                }
            } else {
                eventlistener.ldapEventNotification(
                    new LDAPEvent(
                        eventsource,
                        response,
                        EventConstant.CLASSIFICATION_UNKNOWN,
                        EventConstant.TYPE_UNKNOWN));
            }
        }

        /**
         * Stop the Persistence Search Events Generation by abandoning the
         * Search Request.
         *
         * @throws LDAPException When Cancelling of LDAPConnection Fails.
         */
        public final void stopEventGeneration() throws LDAPException {
            if (Debug.LDAP_DEBUG) {
                Debug.trace(Debug.EventsCalls, "Closing EventGeneration");
            }

            isrunning = false;
            ldapconnection.abandon(searchqueue);
        }
    }

    /* end of class EventsGenerator */

    /**
     * This  Comparator defines Equality based on hashcode value and not
     * actual equality of instance. Used primarly to make User defined
     * LDAPListener and internal contained LDAPListener (as in
     * PSearchSource) equal for a TreeMap.
     */
    private class HashComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final Object o1, final Object o2) {
            int hash1 = o1.hashCode();
            int hash2 = o2.hashCode();

            if (hash1 > hash2) {
                return 1;
            }

            if (hash1 < hash2) {
                return -1;
            }

            return 0;
        }
    }
}

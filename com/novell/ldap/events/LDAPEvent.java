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

import com.novell.ldap.LDAPMessage;

import java.util.EventObject;


/**
 * This class represents an generic event fired in response to an notification
 * (LDAPResponse) sent by the LDAP Server.
 * The LDAPEvent contains the following value:
 * <ul>
 * <li>The Event classification: Any event can be classified into a no of group
 * defined in EventConstant Interface.Currently it defines Two classification.
 * <ul>
 *  <li>CLASSIFICATION_LDAP_PSEARCH -The Standard Events System as defined by
 *  LDAP Persistence Search Draft.</li>
 *  <li>CLASSIFICATION_EDIR_EVENT - The Novell Edirectory specific extensions
 *  for event monitoring.
 * </li>
 * </ul>
 * </li>
 * <li>The Event Type: Apart from the classification grouping, each event is
 * also has a Type associate with it.</li>
 * <li>The LDAPResponse Object which triggered this event.</li>
 * </ul>
 * The LDAPEvent is created by LDAPEventSource whenever a new LDAPResponse
 * is obtained.
 * The Notification for this event can be received from LDAPEventListener
 * and PSearchEventListener.
 * @see LDAPEventSource
 * @see LDAPEventListener
 */
public class LDAPEvent extends EventObject {
    /**
     * This variable is used to store the EventClassification.
     */
    private int eventclassification;

    /**
     * LDAPMessage which generatted this message.
     */
    private LDAPMessage eventmessage;

    /**
     *This variable is used to store the Event Type.
     */
    private int eventtype;

    /**
     * The default constructor used to construct the LDAPEvent.
     * @param eventsource The Source Object which generated this event.
     * @param sourcemessage The LDAPResponse which generates this event.
     * @param aclassification The Event Classification for this Event.
     * @param atype The Event Type for this Event.
     */
    public LDAPEvent(
        final LDAPEventSource eventsource, final LDAPMessage sourcemessage,
        final int aclassification, final int atype
    ) {
        super(eventsource);
        eventmessage = sourcemessage;
        eventclassification = aclassification;
        eventtype = atype;
    }

    /**
     * Returns the Classification for this Event.
     * @return The event classification of this Event.
     */
    public final int getClassification() {
        return eventclassification;
    }

    /**
     * Returns the Type for this Event.
     * @return The event type of this Event.
     */
    public final int getType() {
        return eventtype;
    }

    /**
     * Returns the LDAPResponse which generated this event.
     * @return The LDAPResponse which generated this event.
     */
    public final LDAPMessage getContainedEventInformation() {
        return eventmessage;
    }

    /**
     * Returns an String Representaion of SearchResultEvent.
     * @return String Representation.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getClass().getName() + ":");
        buf.append("(Classification=" + getClassification() + ")");
        buf.append("(Type=" + getType() + ")");
        buf.append(
            "(EventInformation:" + getContainedEventInformation() + ")"
        );
        buf.append("]");

        return buf.toString();
    }

    /**
     * Set the classification of this Event.
     * @param i The classification value.
     */
    public final void setEventclassification(final int i) {
        eventclassification = i;
    }

    /**
     * Set the Type of this event.
     * @param i The Type value.
     */
    public final void setEventtype(final int i) {
        eventtype = i;
    }
}

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

import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.controls.LDAPPersistSearchControl;


/**
 * This class represents an Persistence Search event fired in response to
 * an notification (LDAPSearchResult) sent by the LDAP Server.
 * @see LDAPEvent
 */
public class SearchResultEvent extends LDAPEvent {
    /**
     * Default Constructor for SearchResultEvent.
     * @param eventsource The PSearchEventSource which generated this event.
     * @param source The source of the Event.
     * @param atype The type of Event.
     */
    public SearchResultEvent(
        final PsearchEventSource eventsource, final LDAPSearchResult source,
        final int atype
    ) {
        super(
            eventsource, source, EventConstant.CLASSIFICATION_LDAP_PSEARCH,
            atype
        );
    }

    /**
     * Returns the Entry returned as part of this Event.
     * @return The non-null stored in this class.
     */
    public final LDAPEntry getEntry() {
        return ((LDAPSearchResult) getContainedEventInformation())
        .getEntry();
    }

    /**
     * Returns an String Representaion of SearchResultEvent.
     * @return String Representation.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + getClass().getName() + ":");
        buf.append("(Classification=LDAP Persistence Search Event)");
        buf.append("(Type=" + getChangeTypeString(getType()) + ")");
        buf.append(
            "(EventInformation:"
            + getStringRepresentaionOfEventInformation() + ")"
        );
        buf.append("]");

        return buf.toString();
    }

    /**
     * Retruns an String Representation of Event Information
     * @return String representaion of Event.
     */
    private String getStringRepresentaionOfEventInformation() {
        StringBuffer buf = new StringBuffer();
        LDAPSearchResult result =
            (LDAPSearchResult) getContainedEventInformation();

        buf.append("(Entry=" + result.getEntry() + ")");

        LDAPControl[] controls = result.getControls();

        if (controls != null) {
            buf.append("(Controls=");

            for (int i = 0; i < controls.length; i++) {
                buf.append("(Control" + (i + 1) + "=" + controls[i] + ")");
            }

            buf.append(")");
        }

        return buf.toString();
    }

    /**
     * Return a string indicating the type of change represented by the
     * changeType parameter.
     * @param changeType The changeType to be converted into string
     * representation.
     * @return The string representation.
     */
    private String getChangeTypeString(final int changeType) {
        String changeTypeString;

        switch (changeType) {
        case LDAPPersistSearchControl.ADD:
            changeTypeString = "ADD";

            break;

        case LDAPPersistSearchControl.MODIFY:
            changeTypeString = "MODIFY";

            break;

        case LDAPPersistSearchControl.MODDN:
            changeTypeString = "MODDN";

            break;

        case LDAPPersistSearchControl.DELETE:
            changeTypeString = "DELETE";

            break;

        default:
            changeTypeString =
                "No change type: " + String.valueOf(changeType);

            break;
        }

        return changeTypeString;
    }
}

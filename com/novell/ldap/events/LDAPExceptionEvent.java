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

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;

import java.util.EventObject;


/**
 * This class represents an event fired when an Exception occurs during
 * the process of listening to the LDAP Events.
 */
public class LDAPExceptionEvent extends EventObject {
    /** Local variable to store the LDAPException, which resulted in
     * the Event. */
    private LDAPException expection;

    /**
    * LDAPMessage which generatted this message.
    */
    private final LDAPMessage eventmessage;

    /** The Default Constructor which contains an LDAPException causing
     * the event.
     * @param ldapsource LDAPEventSource instance which created this event.
     * @param amessage LDAPMessage which resulted in this exception, can
     * be null.
     * @param aexpection The LDAPException which would result in this event.
     */
    public LDAPExceptionEvent(
        final LDAPEventSource ldapsource, final LDAPException aexpection,
        final LDAPMessage amessage
    ) {
        super(ldapsource);
        expection = aexpection;
        eventmessage = amessage;
    }

    /**
     * Returns the LDAPException  which caused this Event.
     * @return The non-null LDAPException.
     */
    public final LDAPException getLDAPException() {
        return (LDAPException) expection;
    }

    /**
    * Returns the LDAPResponse which generated this event.
    * @return The LDAPResponse which generated this event.
    */
    public final LDAPMessage getContainedEventInformation() {
        return eventmessage;
    }
}

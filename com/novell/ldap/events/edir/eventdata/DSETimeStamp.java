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
package com.novell.ldap.events.edir.eventdata;

import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1Sequence;


/**
 * The class represents the Timestamp datastructure for Edir Events
 * Notification. It contains a time (in seconds), replicaNumber and Event
 * Type.
 * 
 * <p>
 * DSETimeStamp ::= SEQUENCE {<br>
 * &nbsp;&nbsp;seconds          INTEGER,<br>
 * &nbsp;&nbsp;replicaNumber    INTEGER,<br>
 * &nbsp;&nbsp;event            INTEGER<br>
 * &nbsp;&nbsp;}
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class DSETimeStamp {
    /** Local variable to store the seconds from the Timestamp. */
    private int seconds;

    /** Local variable to store the replicaNumber of this Timestamp. */
    private int replicaNumber;

    /** Local variable to store the event type of this Timestamp. */
    private int event;

    /**
     * The Default Constructor which gets the encoded TimeStamp and
     * decodes it into the various component.
     *
     * @param dseobject ASN1 encoded form of TimeStamp.
     */
    public DSETimeStamp(final ASN1Sequence dseobject) {
        super();
        seconds = ((ASN1Integer) dseobject.get(0)).intValue();
        replicaNumber = ((ASN1Integer) dseobject.get(1)).intValue();
        event = ((ASN1Integer) dseobject.get(2)).intValue();
    }

    /**
     * Specify the Event Type.
     *
     * @return Event type as int.
     */
    public int getEvent() {
        return event;
    }

    /**
     * Specify the replica Number of Edor server , for which this event is
     * generated.
     *
     * @return ReplicaNumber as integer.
     */
    public int getReplicaNumber() {
        return replicaNumber;
    }

    /**
     * Time in seconds.
     *
     * @return Time in seconds as integer.
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * String Representation of the Class
     *
     * @return string.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[TimeStamp ");
        buf.append("(seconds=" + getSeconds() + ")");
        buf.append("(replicaNumber=" + getReplicaNumber() + ")");
        buf.append("(event=" + getEvent() + ")");
        buf.append("]");

        return buf.toString();
    }
}

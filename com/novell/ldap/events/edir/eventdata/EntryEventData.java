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
import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.events.edir.EventResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * This class represents the data for Entry Events. The event data has
 * the following encoding:-
 * 
 * <p>
 * EntryEventData ::= SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;perpetratorDN   LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;entry           LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;class           LDAPOID,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;creationTime    DSETimestamp,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;verb            INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;flags           INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;newDN            LDAPOID OPTIONAL <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 * 
 * @see DSETimeStamp
 */
public class EntryEventData implements EventResponseData {
    private final String perpetratorDN;
    private final String entry;
    private final String newdn;
    private final String classid;
    private final int verb;
    private final int flags;
    private final DSETimeStamp timeStamp;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public EntryEventData(final ASN1Object message) throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        perpetratorDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        entry =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        classid =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();

        timeStamp =
            new DSETimeStamp((ASN1Sequence) decode.decode(in, length));
        verb = ((ASN1Integer) decode.decode(in, length)).intValue();
        flags = ((ASN1Integer) decode.decode(in, length)).intValue();
        newdn =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Returns the Entry which generated this event.
     *
     * @return Entry as String.
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Returns Prepetrator DN as String.
     *
     * @return Prepetrator DN as String.
     */
    public String getPerpetratorDN() {
        return perpetratorDN;
    }

    /**
     * Returns a the Flag associated with Event.
     *
     * @return Flags as integer.
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns the New Dn (if any).
     *
     * @return New DN as String.
     */
    public String getNewdn() {
        return newdn;
    }

    /**
     * Returns the Object Class oid .
     *
     * @return Class oid as String.
     */
    public String getClassid() {
        return classid;
    }

    /**
     * Returns Verb as integer.
     *
     * @return Verb as integer.
     */
    public int getVerb() {
        return verb;
    }

    /**
     * Returns the Time for this event.
     *
     * @return Time as a DSETimeStamp.
     */
    public DSETimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Used by toString , for creating a String representation.
     *
     * @param buf StringBuffer which is the destination of data.
     * @param dataname The String data name.
     * @param value The String value for the above data.
     */
    private void addObject(
        final StringBuffer buf, final String dataname, final String value
    ) {
        buf.append("(" + dataname + "=" + value + ")");
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("EntryEventData[");
        addObject(buf, "Entry", getEntry());
        addObject(buf, "Prepetrator", getPerpetratorDN());
        addObject(buf, "ClassId", getClassid());
        buf.append("(Verb=" + getVerb() + ")");
        buf.append("(Flags=" + getFlags() + ")");
        buf.append("(NewDN=" + getNewdn() + ")");
        buf.append("(TimeStamp=" + getTimeStamp() + ")");
        buf.append("]");

        return buf.toString();
    }
}

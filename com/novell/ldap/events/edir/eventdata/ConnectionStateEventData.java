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
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.events.edir.EventResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * This class represents the data for Connection Change Event. The event
 * data has the following encoding:-
 * 
 * <p>
 * ConnectionState ::= [APPLICATION 9]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;connectionDN     LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;oldFlags         INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;newFlags         INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;sourceModule     LDAPSTRING<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class ConnectionStateEventData implements EventResponseData {
    private final String connectiondn;
    private final int oldFlags;
    private final int newFlags;
    private final String sourceModule;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public ConnectionStateEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        connectiondn =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        oldFlags = ((ASN1Integer) decode.decode(in, length)).intValue();
        newFlags = ((ASN1Integer) decode.decode(in, length)).intValue();
        sourceModule =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Returns the Connection DN, for which Connection State changed.
     *
     * @return Connection DN as String.
     */
    public String getConnectiondn() {
        return connectiondn;
    }

    /**
     * Returns the new Flag associated with the Connections.
     *
     * @return New Flags as integer.
     */
    public int getNewFlags() {
        return newFlags;
    }

    /**
     * Returns the old Flag associated with the Connections.
     *
     * @return Old Flags as integer.
     */
    public int getOldFlags() {
        return oldFlags;
    }

    /**
     * Returns the Source Module which generated this event.
     *
     * @return Source Module as String.
     */
    public String getSourceModule() {
        return sourceModule;
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
        buf.append(
            "[ConnectionStateEvent[connectiondn=" + getConnectiondn()
            + "]"
        );
        buf.append("[oldFlags=" + getOldFlags() + "]");
        buf.append("[newFlags=" + getNewFlags() + "]");
        buf.append("[SourceModule=" + getSourceModule() + "]]");

        return buf.toString();
    }
}

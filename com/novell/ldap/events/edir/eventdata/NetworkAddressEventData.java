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
 * This class represents the data for Network Address Events. The event
 * data has the following encoding:-
 * 
 * <p>
 * NetworkAddress ::=[APPLICATION 8]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;type    INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;data    OCTET  STRING <br>
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
public class NetworkAddressEventData implements EventResponseData {
    private int type;
    private String data;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public NetworkAddressEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] bytedata = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(bytedata);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];
        type = ((ASN1Integer) decode.decode(in, length)).intValue();
        data = ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Returns the Address as a Data.
     *
     * @return Address as String.
     */
    public String getData() {
        return data;
    }

    /**
     * Returns the Address Type.
     *
     * @return Type as int.
     */
    public int getType() {
        return type;
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
        buf.append("[NetworkAddress[type=" + getType() + "]");
        buf.append("[Data=" + getData() + "]]");

        return buf.toString();
    }
}

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
 * This class represents the data for Change Address Events. The event
 * data has the following encoding:-
 * 
 * <p>
 * ChangeServerAddress ::= [APPLICATION 12]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;flags        INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;proto        INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;addrFamily   INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;address      OCTET STRING,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;pstkname     LDAPSTRING,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;source       LDAPSTRING<br>
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
public class ChangeAddressEventData implements EventResponseData {
    private final int flags;
    private final int proto;
    private final int addrFamily;
    private final String address;
    private final String pstkName;
    private final String source;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public ChangeAddressEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        flags = ((ASN1Integer) decode.decode(in, length)).intValue();
        proto = ((ASN1Integer) decode.decode(in, length)).intValue();
        addrFamily = ((ASN1Integer) decode.decode(in, length)).intValue();
        address =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();

        pstkName =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        source =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Returns the Address as a String.
     *
     * @return Address as String.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the Address Family.
     *
     * @return Address Family as integer.
     */
    public int getAddrFamily() {
        return addrFamily;
    }

    /**
     * Returns the Flags.
     *
     * @return Flags as integer.
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns the Protocols.
     *
     * @return Protocols as integer.
     */
    public int getProto() {
        return proto;
    }

    /**
     * Returns the Pstk Name.
     *
     * @return PStk Name as String.
     */
    public String getPstkName() {
        return pstkName;
    }

    /**
     * Returns the source Module.
     *
     * @return Source Module as String.
     */
    public String getSource() {
        return source;
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
        buf.append("[ChangeAddresssEvent[flags=" + getFlags() + "]");
        buf.append("[proto=" + getProto() + "]");
        buf.append("[addrFamily=" + getAddrFamily() + "]");
        buf.append("[address=" + getAddress() + "]");
        buf.append("[pstkName=" + getPstkName() + "]");
        buf.append("[source=" + getSource() + "]]");

        return buf.toString();
    }
}

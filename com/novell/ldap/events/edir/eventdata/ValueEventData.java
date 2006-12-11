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
 * This class represents the data for Value Events. The event data has the
 * following encoding:-
 * 
 * <p>
 * ValueEventData ::= [APPLICATION 2] <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;perpetratorDN   LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;entry           LDAPDN, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;attribute       OCTET STRING, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;syntax            LDAPOID, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;class           OCTET STRING, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;timeStamp       DSETimestamp, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;data               OCTET STRING <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;verb            INTEGER <br>
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
public class ValueEventData implements EventResponseData {
    private final String attribute;
    private final String classid;
    private final ASN1OctetString octData;
    private final String data;
    private final byte[] binData;
    private final String entry;
    private final String prepetratorDN;
    private final String syntax;
    private final DSETimeStamp timeStamp;
    private final int verb;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public ValueEventData(final ASN1Object message) throws IOException {
        super();

        byte[] bytedata = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(bytedata);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        prepetratorDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        entry =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        attribute =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        syntax =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();

        classid =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();

        timeStamp =
            new DSETimeStamp((ASN1Sequence) decode.decode(in, length));

        octData = ((ASN1OctetString) decode.decode(in, length));
        data = octData.stringValue();
        binData = octData.byteValue();

        verb = ((ASN1Integer) decode.decode(in, length)).intValue();
    }

    /**
     * Returns the Attribute.
     *
     * @return Attribute as String.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Returns the objectclass id.
     *
     * @return classid as String.
     */
    public String getClassid() {
        return classid;
    }

    /**
     * Returns the Data.
     *
     * @return Data as String.
     */
    public String getData() {
        return data;
    }

    /**
     * Returns the binaryData.
     *
     * @return Data as Byte Array.
     */
    public byte[] getBinaryData() {
        return binData;
    }
    /**
     * Return the Entry.
     *
     * @return Entry as String.
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Returns PrepetratorDN.
     *
     * @return PrepetratorDN as String.
     */
    public String getPrepetratorDN() {
        return prepetratorDN;
    }

    /**
     * Returns Syntax.
     *
     * @return syntax as String.
     */
    public String getSyntax() {
        return syntax;
    }

    /**
     * Returns the TimeStamp.
     *
     * @return TimeStamp as DSETimeStamp.
     */
    public DSETimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns the Verb.
     *
     * @return Verb as integer.
     */
    public int getVerb() {
        return verb;
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
        buf.append("[ValueEventData");
        buf.append("(Attribute =" + getAttribute() + ")");
        buf.append("(Classid =" + getClassid() + ")");
        buf.append("(Data=" + getData() + ")");
        buf.append("(Entry =" + getEntry() + ")");
        buf.append("(Perpetrator =" + getPrepetratorDN() + ")");
        buf.append("(Syntax =" + getSyntax() + ")");
        buf.append("(TimeStamp =" + getTimeStamp() + ")");
        buf.append("(Verb =" + getVerb() + ")");
        buf.append("]");

        return buf.toString();
    }
}

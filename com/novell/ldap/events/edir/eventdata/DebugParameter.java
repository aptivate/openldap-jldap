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
import com.novell.ldap.asn1.ASN1Tagged;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.events.edir.EdirEventConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the data for Debug Event. The parameter has the
 * following encoding:-
 * 
 * <p>
 * DebugParameter ::= CHOICE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;entryDN         [1] LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;stringValue     [2] OCTET STRING,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;rawData         [3] OCTET STRING,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;integerValue    [4] INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;address         [5] ReferralAddress,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;timeStamp       [6] DSETimeStamp,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;timeVector      [7] SEQUENCE{<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;count INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE OF DSETimeStamp<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
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
public class DebugParameter {
    private int type;
    private Object data;

    /**
     * Default Constructor
     *
     * @param dseobject ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public DebugParameter(final ASN1Tagged dseobject) throws IOException {
        super();

        switch (dseobject.getIdentifier().getTag()) {
            case EdirEventConstant.DB_PARAM_TYPE_ENTRYID :

                int integervalue = getTaggedintValue(dseobject);
                data = new Integer(integervalue);

                break;

            case EdirEventConstant.DB_PARAM_TYPE_INTEGER :
                integervalue = getTaggedintValue(dseobject);
                data = new Integer(integervalue);

                break;

            case EdirEventConstant.DB_PARAM_TYPE_BINARY :

                byte[] binarydata =
                    ((ASN1OctetString) dseobject.taggedValue())
                        .byteValue();
                data = binarydata;

                break;

            case EdirEventConstant.DB_PARAM_TYPE_STRING :

                String utf8String =
                    ((ASN1OctetString) dseobject.taggedValue())
                        .stringValue();
                data = utf8String;

                break;

            case EdirEventConstant.DB_PARAM_TYPE_TIMESTAMP :

                DSETimeStamp timeStamp =
                    new DSETimeStamp(getTaggedSequence(dseobject));
                data = timeStamp;

                break;

            case EdirEventConstant.DB_PARAM_TYPE_TIMEVECTOR :

                List timeVector = Collections.EMPTY_LIST;
                ASN1Sequence seq = getTaggedSequence(dseobject);
                int count = ((ASN1Integer) seq.get(0)).intValue();

                if (count > 0) {
                    ASN1Sequence timeseq = (ASN1Sequence) seq.get(1);
                    timeVector = new ArrayList();

                    for (int i = 0; i < count; i++) {
                        timeVector.add(
                            new DSETimeStamp(
                                (ASN1Sequence) timeseq.get(i)));
                    }
                }

                data = timeVector;

                break;

            case EdirEventConstant.DB_PARAM_TYPE_ADDRESS :

                ReferralAddress address =
                    new ReferralAddress(getTaggedSequence(dseobject));
                data = address;

                break;

            default :
                throw new IOException("Unknown Tag in DebugParameter..");
        }

        type = dseobject.getIdentifier().getTag();
    }

    /**
     * Returns the Type of Data Contained in this class. The Type is
     * defined by the constants defined in EdirEventConstant.
     *
     * @return Type as integer.
     *
     * @see EdirEventConstant#DB_PARAM_TYPE_ENTRYID
     * @see EdirEventConstant#DB_PARAM_TYPE_STRING
     * @see EdirEventConstant#DB_PARAM_TYPE_BINARY
     * @see EdirEventConstant#DB_PARAM_TYPE_INTEGER
     * @see EdirEventConstant#DB_PARAM_TYPE_ADDRESS
     * @see EdirEventConstant#DB_PARAM_TYPE_TIMESTAMP
     * @see EdirEventConstant#DB_PARAM_TYPE_TIMEVECTOR
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
        buf.append("[DebugParameter");

        switch (getType()) {
            case EdirEventConstant.DB_PARAM_TYPE_ENTRYID :
                buf.append("type=EVENT_TAG_DEBUG_INFO_ENTRYID");
                buf.append(" value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_INTEGER :
                buf.append("type=EVENT_TAG_DEBUG_INFO_INTEGER");
                buf.append("value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_BINARY :
                buf.append("type=DB_PARAM_TYPE_BINARY");
                buf.append("value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_STRING :
                buf.append("type=DB_PARAM_TYPE_STRING");
                buf.append("value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_TIMESTAMP :
                buf.append("type=DB_PARAM_TYPE_TIMESTAMP");
                buf.append("value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_TIMEVECTOR :
                buf.append("type=DB_PARAM_TYPE_TIMEVECTOR");
                buf.append("value=" + getData());

                break;

            case EdirEventConstant.DB_PARAM_TYPE_ADDRESS :
                buf.append("type=DB_PARAM_TYPE_ADDRESS");
                buf.append("value=" + getData());

                break;

            default :
                buf.append("type=Unknown");

                break;
        }

        buf.append("]");

        return buf.toString();
    }

    /**
     * Extracts an integer from an ASN1 Tagged Object.
     *
     * @param tagvalue ASN1Tagged Object containing an integer.
     *
     * @return value as integer.
     *
     * @throws IOException when the decoding fails.
     */
    private int getTaggedintValue(final ASN1Tagged tagvalue)
        throws IOException {
        ASN1Object obj = tagvalue.taggedValue();

        byte[] databytes = ((ASN1OctetString) obj).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(databytes);

        LBERDecoder decode = new LBERDecoder();

        int length = databytes.length;

        return ((Long) decode.decodeNumeric(in, length)).intValue();
    }

    /**
     * Extracts an ASN1Sequence from an ASN1 Tagged Object.
     *
     * @param tagvalue ASN1Tagged Object containing an ASN1Sequence.
     *
     * @return value as ASN1Sequence.
     *
     * @throws IOException when the decoding fails.
     */
    private ASN1Sequence getTaggedSequence(final ASN1Tagged tagvalue)
        throws IOException {
        ASN1Object obj = tagvalue.taggedValue();

        byte[] databytes = ((ASN1OctetString) obj).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(databytes);

        LBERDecoder decode = new LBERDecoder();

        return new ASN1Sequence(decode, in, databytes.length);
    }

    /**
     * Returns the Contained Data as Object. The Type of Data is
     * controlled by getType().
     *
     * @return Data as Object.
     */
    public Object getData() {
        return data;
    }
}

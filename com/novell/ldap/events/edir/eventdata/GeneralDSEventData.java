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
import com.novell.ldap.events.edir.EventResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This class represents the data for General DS Events. The event data
 * has the following encoding:-
 * 
 * <p>
 * GeneralEventData ::=<br>
 * &nbsp;&nbsp;[APPLICATION 3]SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;dsTime            [1] INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;milliseconds      [2] INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;verb              [3] INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;currentProcess    [4] INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;perpetratorDN     [5] LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;integerValues        [6] SEQUENCE OF INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;stringValues      [7]SEQUENCE OF LDAPSTRING<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<b>Note:</b> The Tags for Integer and
 * stringValues are complex.
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class GeneralDSEventData implements EventResponseData {
    private static final int EVT_TAG_GEN_DSTIME = 1;
    private static final int EVT_TAG_GEN_MILLISEC = 2;
    private static final int EVT_TAG_GEN_VERB = 3;
    private static final int EVT_TAG_GEN_CURRPROC = 4;
    private static final int EVT_TAG_GEN_PERP = 5;
    private static final int EVT_TAG_GEN_INTEGERS = 6;
    private static final int EVT_TAG_GEN_STRINGS = 7;
    private final int dsTime;
    private final int milliseconds;
    private final int verb;
    private final int currentProcess;
    private final String perpetratorDN;
    private final int[] integerValues;
    private final String[] stringValues;

    /**
     * Default Constructor
     *
     * @param dsobject ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public GeneralDSEventData(final ASN1Object dsobject)
        throws IOException {
        super();

        byte[] bytedata = ((ASN1OctetString) dsobject).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(bytedata);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        dsTime =
            getTaggedintValue(
                (ASN1Tagged) decode.decode(in, length),
                EVT_TAG_GEN_DSTIME);
        milliseconds =
            getTaggedintValue(
                (ASN1Tagged) decode.decode(in, length),
                EVT_TAG_GEN_MILLISEC);

        verb =
            getTaggedintValue(
                (ASN1Tagged) decode.decode(in, length),
                EVT_TAG_GEN_VERB);
        currentProcess =
            getTaggedintValue(
                (ASN1Tagged) decode.decode(in, length),
                EVT_TAG_GEN_CURRPROC);

        perpetratorDN =
            getTaggedStringValue(
                (ASN1Tagged) decode.decode(in, length),
                EVT_TAG_GEN_PERP);

        ASN1Tagged temptaggedvalue =
            ((ASN1Tagged) decode.decode(in, length));

        if (temptaggedvalue.getIdentifier().getTag()
            == EVT_TAG_GEN_INTEGERS) {
            //Integer List.
            ASN1Sequence inteseq =
                getTaggedSequence(temptaggedvalue, EVT_TAG_GEN_INTEGERS);
            ASN1Object[] intobject = inteseq.toArray();
            integerValues = new int[intobject.length];

            for (int i = 0; i < intobject.length; i++) {
                integerValues[i] = ((ASN1Integer) intobject[i]).intValue();
            }

            //second decoding for Strings.
            temptaggedvalue = ((ASN1Tagged) decode.decode(in, length));
        } else {
            integerValues = null;
        }

        if ((temptaggedvalue.getIdentifier().getTag()
            == EVT_TAG_GEN_STRINGS)
            && (temptaggedvalue.getIdentifier().getConstructed())) {
            //String values.
            ASN1Sequence inteseq =
                getTaggedSequence(temptaggedvalue, EVT_TAG_GEN_STRINGS);
            ASN1Object[] stringobject = inteseq.toArray();
            stringValues = new String[stringobject.length];

            for (int i = 0; i < stringobject.length; i++) {
                stringValues[i] =
                    ((ASN1OctetString) stringobject[i]).stringValue();
            }
        } else {
            stringValues = null;
        }
    }

    /**
     * Returns the Current Processor id , which generated this event.
     *
     * @return Current Processor id as integer.
     */
    public int getCurrentProcess() {
        return currentProcess;
    }

    /**
     * Returns the DS Time for this event.
     *
     * @return DSTime for this event.
     */
    public int getDsTime() {
        return dsTime;
    }

    /**
     * Returns the array of Integer values, returned as data. The function
     * returns null, if no values are returned.
     *
     * @return array of int.
     */
    public int[] getIntegerValues() {
        return integerValues;
    }

    /**
     * Returns the Time in Milliseconds.
     *
     * @return Time as integer.
     */
    public int getMilliSeconds() {
        return milliseconds;
    }

    /**
     * Returns the Perpetrator DN for this Event.
     *
     * @return PerpetratorDN as String.
     */
    public String getPerpetratorDN() {
        return perpetratorDN;
    }

    /**
     * Returns the array of String values, returned as data. The function
     * returns null, if no values are returned.
     *
     * @return array of String.
     */
    public String[] getStringValues() {
        return stringValues;
    }

    /**
     * Returns the Verb associated with this event.
     *
     * @return Verb as integer.
     */
    public int getVerb() {
        return verb;
    }

    /**
     * Extracts an integer from an ASN1 Tagged Object.
     *
     * @param tagvalue ASN1Tagged Object containing an integer.
     * @param tagid The tagid  used for tagging this object.
     *
     * @return value as integer.
     *
     * @throws IOException when the decoding fails.
     */
    private int getTaggedintValue(
        final ASN1Tagged tagvalue,
        final int tagid)
        throws IOException {
        ASN1Object obj = tagvalue.taggedValue();

        if (tagid != tagvalue.getIdentifier().getTag()) {
            throw new IOException("Unknown Tagged Data");
        }

        byte[] databytes = ((ASN1OctetString) obj).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(databytes);

        LBERDecoder decode = new LBERDecoder();

        int length = databytes.length;

        return ((Long) decode.decodeNumeric(in, length)).intValue();
    }

    /**
     * Extracts an String from an ASN1 Tagged Object.
     *
     * @param tagvalue ASN1Tagged Object containing an String.
     * @param tagid The tagid  used for tagging this object.
     *
     * @return value as integer.
     *
     * @throws IOException when the decoding fails.
     */
    private String getTaggedStringValue(
        final ASN1Tagged tagvalue,
        final int tagid)
        throws IOException {
        ASN1Object obj = tagvalue.taggedValue();

        if (tagid != tagvalue.getIdentifier().getTag()) {
            throw new IOException("Unknown Tagged Data");
        }

        byte[] databytes = ((ASN1OctetString) obj).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(databytes);

        LBERDecoder decode = new LBERDecoder();

        int length = databytes.length;

        return (String) decode.decodeCharacterString(in, length);
    }

    /**
     * Extracts an ASN1Sequence from an ASN1 Tagged Object.
     *
     * @param tagvalue ASN1Tagged Object containing an ASN1Sequence.
     * @param tagid The tagid  used for tagging this object.
     *
     * @return value as ASN1Sequence.
     *
     * @throws IOException when the decoding fails.
     */
    private ASN1Sequence getTaggedSequence(
        final ASN1Tagged tagvalue,
        final int tagid)
        throws IOException {
        ASN1Object obj = tagvalue.taggedValue();

        if (tagid != tagvalue.getIdentifier().getTag()) {
            throw new IOException("Unknown Tagged Data");
        }

        byte[] databytes = ((ASN1OctetString) obj).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(databytes);

        LBERDecoder decode = new LBERDecoder();
        int length = databytes.length;

        return new ASN1Sequence(decode, in, length);
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
        buf.append("[GeneralDSEventData");
        buf.append("(DSTime =" + getDsTime() + ")");
        buf.append("(MilliSeconds=" + getMilliSeconds() + ")");
        buf.append("(verb =" + getVerb() + ")");
        buf.append("(currentProcess " + getCurrentProcess() + ")");
        buf.append("(PerpetartorDN =" + getPerpetratorDN() + ")");
        buf.append("(Integer Values =" + getIntegerValues() + ")");
        buf.append("(String Values =" + getStringValues() + ")");
        buf.append("]");

        return buf.toString();
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class represents the data for Debug Event. The event data has the
 * following encoding:-
 * 
 * <p>
 * DebugEventData ::= [APPLICATION 13]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;DsTime          INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Milliseconds    INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Perpetrator     LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;FormatString    LDAPSTRING,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Verb            INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;ParameterCount  INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Parameters      SEQUENCE OF Parameter OPTIONAL<br>
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
public class DebugEventData implements EventResponseData {
    private int dsTime;
    private int milliSeconds;
    private String perpetratorDN;
    private String formatString;
    private int verb;
    private int parametercount;
    private List parameters = Collections.EMPTY_LIST;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public DebugEventData(final ASN1Object message) throws IOException {
        super();

        byte[] bytedata = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(bytedata);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        dsTime = ((ASN1Integer) decode.decode(in, length)).intValue();
        milliSeconds =
            ((ASN1Integer) decode.decode(in, length)).intValue();

        perpetratorDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        formatString =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        verb = ((ASN1Integer) decode.decode(in, length)).intValue();
        parametercount =
            ((ASN1Integer) decode.decode(in, length)).intValue();

        if (parametercount > 0) {
            parameters = new ArrayList();

            ASN1Sequence seq = (ASN1Sequence) decode.decode(in, length);

            for (int i = 0; i < parametercount; i++) {
                parameters.add(
                    new DebugParameter((ASN1Tagged) seq.get(i))
                );
            }
        }
    }

    /**
     * Returns the Time in Milliseconds.
     *
     * @return Time in milliseconds as a integer.
     */
    public int getMilliSeconds() {
        return milliSeconds;
    }

    /**
     * Returns a List of Parameters.
     *
     * @return List of Parameters.
     */
    public List getParameters() {
        return parameters;
    }

    /**
     * Returns Perpetrator DN as String.
     *
     * @return Perpetrator DN as String.
     */
    public String getPerpetratorDN() {
        return perpetratorDN;
    }

    /**
     * Returns Verbs.
     *
     * @return Verbs as integer.
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
        buf.append("[DebugEventData");
        buf.append("(Millseconds=" + getMilliSeconds() + ")");
        buf.append("(DSTime =" + getDsTime() + ")");
        buf.append("(PerpetratorDN=" + getPerpetratorDN() + ")");
        buf.append("(Verb=" + getVerb() + ")");
        buf.append("(Parameters=" + getParameters() + ")");
        buf.append("]");

        return buf.toString();
    }

    /**
     * Returns the DsTime.
     *
     * @return DsTime as integer.
     */
    public int getDsTime() {
        return dsTime;
    }

	/**
	 * Returns formatString
	 *
	 * @return formatString as String.
	 */
	public String getFormatString() {
		return formatString;
	}
}

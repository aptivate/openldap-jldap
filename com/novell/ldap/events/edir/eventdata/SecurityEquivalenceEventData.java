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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class represents the data for Security Equivalence Events. The event data has
 * the following encoding:-
 * 
 * <p> 
 * DSESEVInfo ::= [APPLICATION 6]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;entry        LDAPDN, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;retryCount   INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;valueDN        LDAPDN, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;referral    ReferralList<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * referralList := SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;count        INTEGER, <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;referrals    SEQUENCE of ReferralAddress <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * <b>Note:</b>Above referrals are a ASN1Sequence not ASN1SequenceOF.
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class SecurityEquivalenceEventData implements EventResponseData {
    private String entryDN;
    private int retryCount;
    private String valueDN;
    private int referralCount;
    private List referralList = Collections.EMPTY_LIST;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public SecurityEquivalenceEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        entryDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        retryCount = ((ASN1Integer) decode.decode(in, length)).intValue();
        valueDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();

        ASN1Sequence referalseq =
            ((ASN1Sequence) decode.decode(in, length));

        referralCount = ((ASN1Integer) referalseq.get(0)).intValue();

        if (referralCount > 0) {
            referralList = new ArrayList();

            ASN1Sequence referalseqof = ((ASN1Sequence) referalseq.get(1));

            for (int i = 0; i < referralCount; i++) {
                referralList.add(
                    new ReferralAddress(
                        (ASN1Sequence) referalseqof.get(i)
                    )
                );
            }
        }
    }

    /**
     * Returns the Entry DN.
     *
     * @return EntryDn as String.
     */
    public String getEntryDN() {
        return entryDN;
    }

    /**
     * Returns Referral Count as integer.
     *
     * @return Referral Count as int.
     */
    public int getReferralCount() {
        return referralCount;
    }

    /**
     * Returns an List of Referral Address.
     *
     * @return List of ReferralAddress.
     *
     * @see ReferralAddress
     */
    public List getReferralList() {
        return referralList;
    }

    /**
     * Returns the retry count.
     *
     * @return RetryCount as integer.
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Returns ValueDN as String.
     *
     * @return ValueDN as String.
     */
    public String getValueDN() {
        return valueDN;
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
        buf.append("[SecurityEquivalenceEventData");
        buf.append("(EntryDN =" + getEntryDN() + ")");
        buf.append("(RetryCount =" + getRetryCount() + ")");
        buf.append("(valueDN =" + getValueDN() + ")");
        buf.append("(referralCount" + getReferralCount() + ")");
        buf.append("(Referral Lists =" + getReferralList() + ")");
        buf.append("]");

        return buf.toString();
    }
}

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
package com.novell.ldap.events.edir;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.asn1.ASN1Enumerated;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Set;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.rfc2251.RfcLDAPMessage;


/**
 * This object represents the ExtendedResponse returned when Event
 * Registeration fails. This Extended Response structure is generated for
 * requests send as MonitorEventRequest or MonitorFilterEventRequest.
 * 
 * <p>
 * The MonitorEventResponse uses the following OID:<br>
 * &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.80
 * </p>
 * 
 * <p>
 * The responseValue has the following format:<br>
 * responseValue ::= <br>
 * &nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;eventCount  INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;events      SET OF {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;eventSpecifier<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;eventSpecifier ::= SEQUENCE {<br>
 * &nbsp;&nbsp;eventType    INTEGER,<br>
 * &nbsp;&nbsp;eventStatus  ENUMERATED {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AllEvents          (0),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SuccessfulEvents   (1),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FailedEvents       (2)}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * </p>
 * 
 * <p>
 * The ExtendedResponse receives a result code. Possible values are :- <br>
 * &nbsp;&nbsp;LDAP_OPERATIONS_ERROR - indicates that an error occurred
 * on the server while trying to perform the operation. The errorMessage
 * may give an indication of the error.<br>
 * &nbsp;&nbsp;LDAP_PROTOCOL_ERROR - an invalid event type or status was
 * specified in the request. The invalid invents will be contained in the
 * badEvents array.<br>
 * &nbsp;&nbsp;LDAP_ADMINLIMIT_EXCEEDED - the maximum number of active
 * Monitor Events are already active on this server.<br>
 * &nbsp;&nbsp;LDAP_INSUFFICIENT_ACCESS - the bound object associated
 * with the connection does not have rights to perform a Monitor Events
 * request. &nbsp;&nbsp;LDAP_UNAVAILABLE - although the extension is
 * generally supported by the server, the event service is not currently
 * available.<br>
 * </p>
 */
public class MonitorEventResponse extends LDAPExtendedResponse {
    private EdirEventSpecifier[] specifierlist = new EdirEventSpecifier[0];

    /**
     * Default Constructor for MonitorEventResponse, called by
     * ExtResponseFactory Object.
     *
     * @param message The RFCLDAPMessage to parse.
     *
     * @throws LDAPException When decoding of the message fails.
     */
    public MonitorEventResponse(final RfcLDAPMessage message)
        throws LDAPException {
        super(message);

        byte[] returnedValue = this.getValue();

        ///CLOVER:OFF
        if (returnedValue == null) {
            throw new LDAPException(
                LDAPException.resultCodeToString(getResultCode()),
                getResultCode(), (String) null
            );
        }

        ///CLOVER:ON
        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();

        ASN1Sequence sequence =
            (ASN1Sequence) decoder.decode(returnedValue);

        int length = ((ASN1Integer) sequence.get(0)).intValue();
        ASN1Set sequenceset = (ASN1Set) sequence.get(1);
        specifierlist = new EdirEventSpecifier[length];

        for (int i = 0; i < length; i++) {
            ASN1Sequence eventspecifiersequence =
                (ASN1Sequence) sequenceset.get(i);
            int classfication =
                ((ASN1Integer) eventspecifiersequence.get(0)).intValue();
            int enumtype =
                ((ASN1Enumerated) eventspecifiersequence.get(1)).intValue();
            specifierlist[i] =
                new EdirEventSpecifier(classfication, enumtype);
        }
    }

    /**
     * Gets the List of EdirEventSpecifiers which generated this error.
     *
     * @return Array of EdirEventSpecifier.
     */
    public EdirEventSpecifier[] getSpecifierList() {
        return specifierlist;
    }
}

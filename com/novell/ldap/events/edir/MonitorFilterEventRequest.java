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
import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPIntermediateResponse;
import com.novell.ldap.asn1.ASN1Enumerated;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Set;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class is used for registering for Edirectory events while also
 * specifying an <b>filterString for event Filtering</b>. This request
 * encodes an eventType ,eventStatus and filterString which are send to
 * the Edirectory Server. The class extracts the above values from
 * EdirEventSpecifier class. The filterString is used to filter the
 * events, the filter syntax is same as that of ldap search filter. The
 * operation defined by this class is just an extension of the simple
 * event registration as provided by MonitorEventRequest. This class
 * extendes it by adding an filterString.
 *
 * <p>
 * The MonitorFilterEventRequest uses the following OID:<br>
 * &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.84
 * </p>
 *
 * <p>
 * The responseValue has the following format:<br>
 * requestValue ::= <br>
 * &nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;eventCount  INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;events      SET OF {FilteredEventSpecifier },<br>
 * &nbsp;&nbsp;}<br>
 * FilteredEventSpecifier ::= <br>
 * &nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;eventType    INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;eventStatus  ENUMERATED<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AllEvents (0),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SuccessfulEvents (1),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; FailedEvents (2) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;eventFilter  OPTIONAL FilterString<br>
 * &nbsp;&nbsp;}<br>
 * </p>
 *
 * @see EdirEventSpecifier
 * @see MonitorEventRequest
 */
public class MonitorFilterEventRequest extends LDAPExtendedOperation {
    static {
        /*
        * Register the extendedresponse class which is returned by the
        * server in response to a MonitorEventRequest
        */
        try {
            LDAPExtendedResponse.register(
                EdirEventConstant.NLDAP_MONITOR_EVENTS_RESPONSE,
                Class.forName(
                    "com.novell.ldap.events.edir.MonitorEventResponse"
                )
            );

            ///CLOVER:OFF
        } catch (ClassNotFoundException e) {
            System.err.println(
                "Could not register Extended Response -"
                + " Class not found"
            );
        } catch (Exception e) {
            e.printStackTrace();

            ///CLOVER:ON
        }

        //Also try to register EdirEventIntermediateResponse
        try {
            LDAPIntermediateResponse.register(
                EdirEventConstant.NLDAP_EVENT_NOTIFICATION,
                Class.forName(
                    EdirEventIntermediateResponse.class.getName()
                )
            );

            ///CLOVER:OFF
        } catch (ClassNotFoundException e) {
            System.err.println(
                "Could not register LDAP Intermediate Response -"
                + " Class not found"
            );
        } catch (Exception e) {
            e.printStackTrace();

            ///CLOVER:ON
        }
    }

    /**
     * Default Constructor for the Monitor Filter Event Request Used to
     * Send a Monitor Filter Event Request to LDAPServer.
     *
     * @param specifiers The list of EdirEventSpecifiers to send to
     *        server.
     *
     * @throws LDAPException When the data encoding fails.
     */
    public MonitorFilterEventRequest(
        final EdirEventSpecifier[] specifiers
    ) throws LDAPException {
        super(
            EdirEventConstant.NLDAP_FILTERED_MONITOR_EVENTS_REQUEST, null
        );

        if ((specifiers == null)) {
            throw new IllegalArgumentException(
                ExceptionMessages.PARAM_ERROR
            );
        }

        ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
        LBEREncoder encoder = new LBEREncoder();

        ASN1Sequence asnsequence = new ASN1Sequence();

        try {
            asnsequence.add(new ASN1Integer(specifiers.length));

            ASN1Set asnset = new ASN1Set();

            for (int i = 0; i < specifiers.length; i++) {
                ASN1Sequence specifiersequence = new ASN1Sequence();
                specifiersequence.add(
                    new ASN1Integer(specifiers[i].getEventclassification())
                );
                specifiersequence.add(
                    new ASN1Enumerated(specifiers[i].getEventtype())
                );

                //If Not Null and Specified Value.
                if (specifiers[i].getFilter() != null) {
                    specifiersequence.add(
                        new ASN1OctetString(specifiers[i].getFilter())
                    );
                } else {
                    throw new IllegalArgumentException(
                        "Filter cannot be null,for Filter events"
                    );
                }

                asnset.add(specifiersequence);
            }

            asnsequence.add(asnset);

            asnsequence.encode(encoder, encodedData);
        } catch (IOException e) {
            throw new LDAPException(
                ExceptionMessages.ENCODING_ERROR,
                LDAPException.ENCODING_ERROR, (String) null
            );
        }

        setValue(encodedData.toByteArray());
    }
}

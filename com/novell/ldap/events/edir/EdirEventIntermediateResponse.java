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
import com.novell.ldap.LDAPIntermediateResponse;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Tagged;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.client.Debug;
import com.novell.ldap.events.edir.eventdata.BinderyObjectEventData;
import com.novell.ldap.events.edir.eventdata.ChangeAddressEventData;
import com.novell.ldap.events.edir.eventdata.ConnectionStateEventData;
import com.novell.ldap.events.edir.eventdata.DebugEventData;
import com.novell.ldap.events.edir.eventdata.EntryEventData;
import com.novell.ldap.events.edir.eventdata.GeneralDSEventData;
import com.novell.ldap.events.edir.eventdata.ModuleStateEventData;
import com.novell.ldap.events.edir.eventdata.NetworkAddressEventData;
import com.novell.ldap.events.edir.eventdata.SecurityEquivalenceEventData;
import com.novell.ldap.events.edir.eventdata.ValueEventData;
import com.novell.ldap.resources.ExceptionMessages;
import com.novell.ldap.rfc2251.RfcLDAPMessage;

import java.io.IOException;


/**
 * This class represents the LdapIntermediateResponse Message returned by
 * Edirectory during Event Notification.
 * 
 * <p>
 * The EdirEventIntermediateResponse uses the following OID:<br>
 * &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.81
 * </p>
 * 
 * <p>
 * The responseValue has the following format:<br>
 * responseValue ::= <br>
 * &nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EventType  INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ResultCode INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;EventData  OCTET STRING OPTIONAL<br>
 * &nbsp;&nbsp;}<br>
 * </p>
 */
public class EdirEventIntermediateResponse extends LDAPIntermediateResponse {
    /* Tags for the various event data sequences
    application and constructed  */
    private static final int EDIR_TAG_ENTRY_EVENT_DATA = 1;
    private static final int EDIR_TAG_VALUE_EVENT_DATA = 2;
    private static final int EDIR_TAG_GENERAL_EVENT_DATA = 3;
    private static final int EDIR_TAG_SKULK_DATA = 4;
    private static final int EDIR_TAG_BINDERY_EVENT_DATA = 5;
    private static final int EDIR_TAG_DSESEV_INFO = 6;
    private static final int EDIR_TAG_MODULE_STATE_DATA = 7;
    private static final int EDIR_TAG_NETWORK_ADDRESS = 8;
    private static final int EDIR_TAG_CONNECTION_STATE = 9;
    private static final int EDIR_TAG_CHANGE_SERVER_ADDRESS = 12;
    private static final int EDIR_TAG_CHANGE_CONFIG_PARAM = 13;
    private static final int EDIR_TAG_NO_DATA = 14;
    private static final int EDIR_TAG_STATUS_LOG = 15;
    private static final int EDIR_TAG_DEBUG_EVENT_DATA = 16;

    /**
     * Local variable to store the event type of the event contained in
     * this response.
     */
    private int eventtype;

    /**
     * Local Variable to store result code for the event contained in the
     * response.
     */
    private int eventResult;
    private EventResponseData responsedata;

    /**
     * Default Constructor using a RFCLDAPMessage.
     *
     * @param message RFCLDAPMessage Object for constructing this message.
     *
     * @throws LDAPException When the decoding of the message fails, it
     *         results in an LDAPException.
     */
    public EdirEventIntermediateResponse(final RfcLDAPMessage message)
        throws LDAPException {
        super(message);

        try {
            //Process the BerCoded Response Value
            byte[] returnedValue = getValue();

            processmessage(returnedValue);

            ///CLOVER:OFF
        } catch (IOException e) {
            if (Debug.LDAP_DEBUG) {
                Debug.trace(
                    Debug.EventsCalls,
                    "Exception in processing message:" + e.getMessage()
                );
            }

            throw new LDAPException(
                ExceptionMessages.DECODING_ERROR,
                LDAPException.DECODING_ERROR, (String) e.getMessage()
            );
        }

        ///CLOVER:ON
    }

    /**
     * The constructor for this object which accepts bytes and convert
     * into a message. <b>Note:</b> This constructor is for testing
     * purpose.
     *
     * @param message bytes of data.
     *
     * @throws Exception When the message creation fails.
     */
    public EdirEventIntermediateResponse(final byte[] message)
        throws Exception {
        //Just a Empty Message.
        super(new RfcLDAPMessage(new ASN1Sequence()));

        //Process the BerCoded Response Value
        processmessage(message);
    }

    /**
     * This method actually decodes the message into its various units.
     *
     * @param returnedValue array of bytes.
     *
     * @throws IOException When the processing of data fails.
     */
    private void processmessage(final byte[] returnedValue)
        throws IOException {
        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();

        ASN1Sequence sequence =
            (ASN1Sequence) decoder.decode(returnedValue);

        eventtype = ((ASN1Integer) sequence.get(0)).intValue();
        eventResult = ((ASN1Integer) sequence.get(1)).intValue();

        if (sequence.size() > 2) {
            ASN1Tagged taggedobject = (ASN1Tagged) sequence.get(2);

            switch (taggedobject.getIdentifier().getTag()) {
            case EDIR_TAG_ENTRY_EVENT_DATA:

                //Entry is returned by directory.
                responsedata =
                    new EntryEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_VALUE_EVENT_DATA:
                responsedata =
                    new ValueEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_DEBUG_EVENT_DATA:
                responsedata =
                    new DebugEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_GENERAL_EVENT_DATA:
                responsedata =
                    new GeneralDSEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_SKULK_DATA:
                responsedata = null;

                break;

            case EDIR_TAG_BINDERY_EVENT_DATA:
                responsedata =
                    new BinderyObjectEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_DSESEV_INFO:
                responsedata =
                    new SecurityEquivalenceEventData(
                        taggedobject.taggedValue()
                    );

                break;

            case EDIR_TAG_MODULE_STATE_DATA:
                responsedata =
                    new ModuleStateEventData(taggedobject.taggedValue());

                break;

            case EDIR_TAG_NETWORK_ADDRESS:
                responsedata =
                    new NetworkAddressEventData(
                        taggedobject.taggedValue()
                    );

                break;

            case EDIR_TAG_CONNECTION_STATE:
                responsedata =
                    new ConnectionStateEventData(
                        taggedobject.taggedValue()
                    );

                break;

            case EDIR_TAG_CHANGE_SERVER_ADDRESS:
                responsedata =
                    new ChangeAddressEventData(taggedobject.taggedValue());

                break;

            /*
            case EDIR_TAG_CHANGE_CONFIG_PARAM :
                responsedata =
                    new ChangeConfigEventData(
                        taggedobject.taggedValue());

                break;

            case EDIR_TAG_STATUS_LOG :
                responsedata =
                    new StatusLogEventData(taggedobject.taggedValue());

                break;
            */
            case EDIR_TAG_NO_DATA:
                responsedata = null;

                break;

            default:

                //unhandled data.
                throw new IOException();
            }
        } else {
            //NO DATA
            responsedata = null;
        }
    }

    /**
     * Returns the event Result Code.
     *
     * @return event result as int.
     */
    public int getEventResult() {
        return eventResult;
    }

    /**
     * Returns the event type for this response.
     *
     * @return event type as integer.
     */
    public int getEventtype() {
        return eventtype;
    }

    /**
     * Returns the response data associated with this event.
     *
     * @return EventResponseData datastructure.
     */
    public EventResponseData getResponsedata() {
        return responsedata;
    }

    /**
     * Returns the String Representation of this Object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "[EdirEventIntermediateResponse" + "(" + getMessageID()
        + "): " + "EventType=" + getEventtype() + " ResultCode="
        + getEventResult() + " ResponseData=" + getResponsedata() + "]";
    }
}

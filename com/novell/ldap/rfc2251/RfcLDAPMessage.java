/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap.rfc2251;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 * Represents an LDAP Message.
 *
 *<pre>
 *       LDAPMessage ::= SEQUENCE {
 *               messageID       MessageID,
 *               protocolOp      CHOICE {
 *                   bindRequest     BindRequest,
 *                   bindResponse    BindResponse,
 *                   unbindRequest   UnbindRequest,
 *                   searchRequest   SearchRequest,
 *                   searchResEntry  SearchResultEntry,
 *                   searchResDone   SearchResultDone,
 *                   searchResRef    SearchResultReference,
 *                   modifyRequest   ModifyRequest,
 *                   modifyResponse  ModifyResponse,
 *                   addRequest      AddRequest,
 *                   addResponse     AddResponse,
 *                   delRequest      DelRequest,
 *                   delResponse     DelResponse,
 *                   modDNRequest    ModifyDNRequest,
 *                   modDNResponse   ModifyDNResponse,
 *                   compareRequest  CompareRequest,
 *                   compareResponse CompareResponse,
 *                   abandonRequest  AbandonRequest,
 *                   extendedReq     ExtendedRequest,
 *                   extendedResp    ExtendedResponse },
 *                controls       [0] Controls OPTIONAL }
 *</pre>
 *
 *<br><br>
 * Note: The creation of a MessageID should be hidden within the creation of
 *       an RfcLDAPMessage. The MessageID needs to be in sequence, and has an
 *       upper and lower limit. There is never a case when a user should be
 *       able to specify the MessageID for an RfcLDAPMessage. The MessageID()
 *       constructor should be package protected. (So the MessageID value
 *       isn't arbitrarily run up.)
 */
public class RfcLDAPMessage extends ASN1Sequence
{

    private RfcRequest op;
    private RfcControls controls;
    private LDAPMessage requestMessage = null;

    /**
     * Create an RfcLDAPMessage by copying the content array
     *
     * @param origContent the array list to copy
     */
    /* package */
    RfcLDAPMessage( ASN1Object[] origContent,
                    RfcRequest origRequest,
                    String dn,
                    String filter,
                    boolean reference)
            throws LDAPException
    {
        super( origContent, origContent.length);

        set(0, new RfcMessageID()); // MessageID has static counter

        RfcRequest req = (RfcRequest)origContent[1];
        RfcRequest newreq = req.dupRequest(dn, filter, reference);
        op = newreq;
        set(1, (ASN1Object)newreq);

        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.referrals,
                "RfcLDAPMessage created copy of msg with new id " +
                    getMessageID() + ",dn=" + dn + ", filter=" + filter +
                    ", reference=" + reference);
        }
        return;
    }

    /**
     * Create an RfcLDAPMessage using the specified LDAP Request Protocol Op.
     */
    public RfcLDAPMessage(RfcRequest op)
    {
        this(op, null);
        return;
    }

    /**
     * Create an RfcLDAPMessage from input parameters.
      */
    public RfcLDAPMessage(RfcRequest op, RfcControls controls)
    {
        super(3);

        this.op = op;
        this.controls = controls;

        add(new RfcMessageID()); // MessageID has static counter
        add((ASN1Object)op);
        if(controls != null) {
            add(controls);
        }        
        return;
    }

    /**
     * Will decode an RfcLDAPMessage directly from an InputStream.
     */
    public RfcLDAPMessage(ASN1Decoder dec, InputStream in, int len)
            throws IOException
    {
        super(dec, in, len);

        byte[] content;
        ByteArrayInputStream bais;

        // Decode implicitly tagged protocol operation from an ASN1Tagged type
        // to its appropriate application type.
        ASN1Tagged protocolOp = (ASN1Tagged)get(1);
        ASN1Identifier protocolOpId = protocolOp.getIdentifier();
        content = ((ASN1OctetString)protocolOp.taggedValue()).byteValue();
        bais = new ByteArrayInputStream(content);

        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, "RfcLDAPMessage: input message w/tag " +
            protocolOpId.getTag());
        }
        switch(protocolOpId.getTag()) {
            case RfcProtocolOp.SEARCH_RESULT_ENTRY:
                set(1, new RfcSearchResultEntry(dec, bais, content.length));
                break;
            case RfcProtocolOp.SEARCH_RESULT_DONE:
                set(1, new RfcSearchResultDone(dec, bais, content.length));
                break;
            case RfcProtocolOp.SEARCH_RESULT_REFERENCE:
                set(1, new RfcSearchResultReference(dec, bais, content.length));
                break;
            case RfcProtocolOp.ADD_RESPONSE:
                set(1, new RfcAddResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.BIND_RESPONSE:
                set(1, new RfcBindResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.COMPARE_RESPONSE:
                set(1, new RfcCompareResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.DEL_RESPONSE:
                set(1, new RfcDelResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.EXTENDED_RESPONSE:
                set(1, new RfcExtendedResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.MODIFY_RESPONSE:
                set(1, new RfcModifyResponse(dec, bais, content.length));
                break;
            case RfcProtocolOp.MODIFY_DN_RESPONSE:
                set(1, new RfcModifyDNResponse(dec, bais, content.length));
                break;
            default:
                throw new RuntimeException("RfcLDAPMessage: Invalid tag: " +
                    protocolOpId.getTag());
        }

        // decode optional implicitly tagged controls from ASN1Tagged type to
        // to RFC 2251 types.
        if(size() > 2) {
            ASN1Tagged controls = (ASN1Tagged)get(2);
            //   ASN1Identifier controlsId = protocolOp.getIdentifier();
            // we could check to make sure we have controls here....

            content = ((ASN1OctetString)controls.taggedValue()).byteValue();
            bais = new ByteArrayInputStream(content);
            set(2, new RfcControls(dec, bais, content.length));
        }
        return;
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Returns this RfcLDAPMessage's messageID as an int.
     */
    public final int getMessageID()
    {
        return ((ASN1Integer)get(0)).intValue();
    }

    /**
     * Returns the Protocol Operation for this RfcLDAPMessage.
     */
    public final ASN1Object getProtocolOp()
    {
        return (ASN1Object)get(1);
    }

    /**
     * Returns the optional Controls for this RfcLDAPMessage.
     */
    public final RfcControls getControls()
    {
        if(size() > 2)
            return (RfcControls)get(2);
        return null;
    }

    /**
     * Duplicate this message, replacing base dn, filter, and scope if supplied
     *
     * @param dn the base dn
     * <br><br>
     * @param filter the filter
     * <br><br>
     * @param scope the scope
     *
     * @return the object representing the new message
     */
    public final Object dupMessage( String dn, String filter, boolean reference)
        throws LDAPException
    {
        if( (op == null)) {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.referrals,
                    "RfcLDAPMessage op == null, cannot dupMessage");
            }
            throw new LDAPException("DUP_ERROR", LDAPException.LOCAL_ERROR);
        }

        RfcLDAPMessage newMsg = new RfcLDAPMessage( toArray(),
                                                    (RfcRequest)get(1),
                                                    dn,
                                                    filter,
                                                    reference);
        return newMsg;
    }

    /**
     * Returns the dn of the request, may be null
     */
    public final String getRequestDN()
    {
        return op.getRequestDN();
    }
    
    /**
     * sets the original request in this message
     *
     * @param msg the original request for this response
     */
    public final void setRequestingMessage( LDAPMessage msg)
    {
        requestMessage = msg;
        return;
    }

    /**
     * returns the original request in this message
     *
     * @return the original msg request for this response
     */
    public final LDAPMessage getRequestingMessage( )
    {
        return requestMessage;
    }
}

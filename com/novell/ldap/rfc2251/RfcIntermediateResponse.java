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
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;

/** 
 * Represents an LDAP Intermediate Response.
 *
 *<pre>
 *       IntermediateResponse ::= [APPLICATION 25] SEQUENCE {
 *               COMPONENTS OF LDAPResult, note: only present on incorrectly 
                                                 encoded response from 
 *                                               pre Falcon-sp1 server 
 *               responseName     [10] LDAPOID OPTIONAL,
 *               responseValue    [11] OCTET STRING OPTIONAL }
 *</pre>
 */
public class RfcIntermediateResponse extends ASN1Sequence implements RfcResponse {

    /**
     * Context-specific TAG for optional responseName.
     */
    public final static int TAG_RESPONSE_NAME = 0;
    /**
     * Context-specific TAG for optional response.
     */
    public final static int TAG_RESPONSE      = 1;

    private int m_referralIndex;
    private int m_responseNameIndex;
	    private int m_responseValueIndex;

    //*************************************************************************
    // Constructors for ExtendedResponse
    //*************************************************************************

    /**
     * The only time a client will create a IntermediateResponse is when it is
     * decoding it from an InputStream. The stream contains the intermediate
     * response sequence that follows the msgID in the PDU. The intermediate
     * response draft defines this as:
     *      IntermediateResponse ::= [APPLICATION 25] SEQUENCE {
     *             responseName     [0] LDAPOID OPTIONAL,
     *             responseValue    [1] OCTET STRING OPTIONAL }
     *
     * Until post Falcon sp1, the LDAP server was incorrectly encoding
     * intermediate response as:
     *      IntermediateResponse ::= [APPLICATION 25] SEQUENCE {
     *             Components of LDAPResult,
     *             responseName     [0] LDAPOID OPTIONAL,
     *             responseValue    [1] OCTET STRING OPTIONAL }
     *
     * where the Components of LDAPResult are
     *               resultCode      ENUMERATED {...}
     *               matchedDN       LDAPDN,
     *               errorMessage    LDAPString,
     *               referral        [3] Referral OPTIONAL }
     *
     *
     * (The components of LDAPResult never have the optional referral.)
     * This constructor is written to handle both cases.
     *
     * The sequence of this intermediate response will have the element
     * at index m_responseNameIndex set to an RfcLDAPOID containing the
     * oid of the response. The element at m_responseValueIndex will be set
     * to an ASN1OctetString containing the value bytes.
     */
    public RfcIntermediateResponse(ASN1Decoder dec, InputStream in, int len)
        throws IOException
    {
        super(dec, in, len);
        
    	int i=0;		
        m_responseNameIndex = m_responseValueIndex = 0;

        // decode optional tagged elements. The parent class constructor will
        // have decoded these elements as ASN1Tagged objects with the value
        // stored as an ASN1OctectString object.

        if(size() >= 3) //the incorrectly encoded case, LDAPResult contains 
          i = 3;        //at least 3 components
        else
          i = 0; //correctly encoded case, can have zero components

        for(; i<size(); i++) {
            ASN1Tagged obj = (ASN1Tagged)get(i);
            ASN1Identifier id = obj.getIdentifier();
            switch(id.getTag()) {
                case TAG_RESPONSE_NAME:
                    set(i, new RfcLDAPOID(
                        ((ASN1OctetString)obj.taggedValue()).byteValue()));
                    m_responseNameIndex = i;
                    break;
                case TAG_RESPONSE:
                    set(i, obj.taggedValue());
                    m_responseValueIndex = i;
                    break;
            }
        }
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     *
     */
    public final RfcLDAPOID getResponseName()
    {
        return (m_responseNameIndex >= 0) ? (RfcLDAPOID)get(m_responseNameIndex)
                                        : null;
    }

    /**
     *
     */
    public final ASN1OctetString getResponse()
    {
        return (m_responseValueIndex != 0) ? (ASN1OctetString)get(m_responseValueIndex)
                                    : null;
    }

    /**
     * Override getIdentifier to return an application-wide id.
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.INTERMEDIATE_RESPONSE);
    }
    
    /**
     *
     */
    public final ASN1Enumerated getResultCode()
    {
        if ( size() > 3)    
            return (ASN1Enumerated)get(0);
        else
            return null;
    }
    
    /**
     *
     */
    public final RfcLDAPDN getMatchedDN()
    {
        if ( size() > 3)
            return new RfcLDAPDN(((ASN1OctetString)get(1)).byteValue());
        else
            return null;
    }
    
    /**
     * Returns the referral(s) from the server
     *
     * @return the referral(s)
     */
    public final RfcReferral getReferral()
    {
        return (size() > 3) ? (RfcReferral)get(3) : null;
    }
    
    /**
     * Returns the error message from the server
     *
     * @return the server error message
     */
    public final RfcLDAPString getErrorMessage()
    {
        if ( size() > 3)
            return new RfcLDAPString(((ASN1OctetString)get(2)).byteValue());
        else
            return null;
    }
        
}

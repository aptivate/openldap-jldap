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
 * Represents an LDAP Extended Response.
 *
 *<pre>
 *       ExtendedResponse ::= [APPLICATION 24] SEQUENCE {
 *               COMPONENTS OF LDAPResult,
 *               responseName     [10] LDAPOID OPTIONAL,
 *               response         [11] OCTET STRING OPTIONAL }
 *</pre>
 */
public class RfcExtendedResponse extends ASN1Sequence implements RfcResponse {

    /**
     * Context-specific TAG for optional responseName.
     */
    public final static int RESPONSE_NAME = 10;
    /**
     * Context-specific TAG for optional response.
     */
    public final static int RESPONSE      = 11;

    private int referralIndex;
    private int responseNameIndex;
    private int responseIndex;

   
    //*************************************************************************
    // Constructors for ExtendedResponse
    //*************************************************************************

	/**
   * Creates the RFC Extended Response Object passing the individual parameters.
   *  
   * @param resultCode  The result code as defined in LDAPException.
   *
   * @param matchedDN   The name of the lowest entry that was matched
   *                    for some error result codes, an empty string
   *                    or <code>null</code> if none.
   *
   * @param errorMessage  A diagnostic message returned by the server,
   *                       an empty string or <code>null</code> if none.
   *
   * @param referral   The referral URLs returned for a REFERRAL result
   *                    code or <code>null</code> if none.
   *
   * @param responseName   The LDAPOID for this extended operation
   *
   * @param response  Any Response returned by the server 
   */
	public RfcExtendedResponse(ASN1Enumerated resultCode, RfcLDAPDN matchedDN,
	RfcLDAPString errorMessage, RfcReferral referral,RfcLDAPOID responseName ,ASN1OctetString response)
	{
		super(6);
	   add(resultCode);
	   add(matchedDN);
	   add(errorMessage);
	   int counter = 3;
	   if(referral != null)
	   {	   
		   add(referral);
		referralIndex = counter++;
	   }
		if (responseName != null)
	   {		
		responseNameIndex = counter++;
			add(responseName);
	   }
		if (response != null)
	   {
			add(response);
		responseIndex = counter++;
	   } 
	   return;
	}
    /**
     * The only time a client will create a ExtendedResponse is when it is
     * decoding it from an InputStream
     */
    public RfcExtendedResponse(ASN1Decoder dec, InputStream in, int len)
        throws IOException
    {
        super(dec, in, len);


        // decode optional tagged elements
        if(size() > 3) {
            for(int i=3; i<size(); i++) {
                ASN1Tagged obj = (ASN1Tagged)get(i);
                ASN1Identifier id = obj.getIdentifier();
                switch(id.getTag()) {
                    case RfcLDAPResult.REFERRAL:
                        byte[] content =
                            ((ASN1OctetString)obj.taggedValue()).byteValue();
                        ByteArrayInputStream bais =
                            new ByteArrayInputStream(content);
                        set(i, new RfcReferral(dec, bais, content.length));
                        referralIndex = i;
                        break;
                    case RESPONSE_NAME:
                        set(i, new RfcLDAPOID(
                            ((ASN1OctetString)obj.taggedValue()).byteValue()));
                        responseNameIndex = i;
                        break;
                    case RESPONSE:
                        set(i, obj.taggedValue());
                        responseIndex = i;
                        break;
                }
            }
        }
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     *
     */
    public final ASN1Enumerated getResultCode()
    {
        return (ASN1Enumerated)get(0);
    }

    /**
     *
     */
    public final RfcLDAPDN getMatchedDN()
    {
        return new RfcLDAPDN(((ASN1OctetString)get(1)).byteValue());
    }

    /**
     *
     */
    public final RfcLDAPString getErrorMessage()
    {
        return new RfcLDAPString(((ASN1OctetString)get(2)).byteValue());
    }

    /**
     *
     */
    public final RfcReferral getReferral()
    {
        return (referralIndex != 0) ? (RfcReferral)get(referralIndex) : null;
    }

    /**
     *
     */
    public final RfcLDAPOID getResponseName()
    {
        return (responseNameIndex != 0) ? (RfcLDAPOID)get(responseNameIndex)
                                        : null;
    }

    /**
     *
     */
    public final ASN1OctetString getResponse()
    {
        return (responseIndex != 0) ? (ASN1OctetString)get(responseIndex)
                                    : null;
    }

    /**
     * Override getIdentifier to return an application-wide id.
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.EXTENDED_RESPONSE);
    }
}

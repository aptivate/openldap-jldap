
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       ExtendedResponse ::= [APPLICATION 24] SEQUENCE {
 *               COMPONENTS OF LDAPResult,
 *               responseName     [10] LDAPOID OPTIONAL,
 *               response         [11] OCTET STRING OPTIONAL }
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
							((ASN1OctetString)obj.getContent()).getContent();
						ByteArrayInputStream bais = 
							new ByteArrayInputStream(content);
						set(i, new RfcReferral(dec, bais, content.length));
						referralIndex = i;
						break;
					case RESPONSE_NAME:
						set(i, new RfcLDAPOID(
							((ASN1OctetString)obj.getContent()).getContent()));
						responseNameIndex = i;
						break;
					case RESPONSE:
						set(i, (ASN1OctetString)obj.getContent());
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
	public ASN1Enumerated getResultCode()
	{
		return (ASN1Enumerated)get(0);
	}

	/**
	 *
	 */
	public RfcLDAPDN getMatchedDN()
	{
		return new RfcLDAPDN(((ASN1OctetString)get(1)).getContent());
	}

	/**
	 *
	 */
	public RfcLDAPString getErrorMessage()
	{
		return new RfcLDAPString(((ASN1OctetString)get(2)).getContent());
	}

	/**
	 *
	 */
	public RfcReferral getReferral()
	{
		return (referralIndex != 0) ? (RfcReferral)get(referralIndex) : null;
	}

	/**
	 *
	 */
	public RfcLDAPOID getResponseName()
	{
		return (responseNameIndex != 0) ? (RfcLDAPOID)get(responseNameIndex)
		                                : null;
	}

	/**
	 *
	 */
	public ASN1OctetString getResponse()
	{
		return (responseIndex != 0) ? (ASN1OctetString)get(responseIndex)
		                            : null;
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.EXTENDED_RESPONSE);
	}

}



package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       BindResponse ::= [APPLICATION 1] SEQUENCE {
 *
 *            COMPONENTS OF LDAPResult,
 *            serverSaslCreds    [7] OCTET STRING OPTIONAL }
 */
public class RfcBindResponse extends ASN1Sequence implements RfcResponse {

	//*************************************************************************
	// Constructors for BindResponse
	//*************************************************************************

	/**
	 * The only time a client will create a BindResponse is when it is
	 * decoding it from an InputStream
	 *
	 * Note: If serverSaslCreds is included in the BindResponse, it does not
	 *       need to be decoded since it is already an OCTET STRING.
	 */
	public RfcBindResponse(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);

		// Decode optional referral from ASN1OctetString to Referral.
		if(size() > 3) {
			ASN1Tagged obj = (ASN1Tagged)get(3);
			ASN1Identifier id = obj.getIdentifier();
			if(id.getTag() == RfcLDAPResult.REFERRAL) {
				byte[] content =
					((ASN1OctetString)obj.getContent()).getContent();
				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				set(3, new RfcReferral(dec, bais, content.length));
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
		ASN1Object obj = get(3);
		if(obj instanceof RfcReferral)
			return (RfcReferral)obj;
		return null;
	}

	/**
	 * Returns the OPTIONAL serverSaslCreds of a BindResponse if it exists
	 * otherwise null.
	 */
	public ASN1OctetString getServerSaslCreds()
	{
		if(size() == 5)
			return (ASN1OctetString)((ASN1Tagged)get(4)).getContent();

		if(size() == 4) { // could be referral or serverSaslCreds
			ASN1Object obj = get(3);
			if(obj instanceof ASN1Tagged)
				return (ASN1OctetString)((ASN1Tagged)obj).getContent();
		}

		return null;
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.BIND_RESPONSE);
	}

}


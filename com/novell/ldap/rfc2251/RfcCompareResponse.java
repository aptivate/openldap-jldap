
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *      CompareResponse ::= [APPLICATION 15] LDAPResult
 */
public class RfcCompareResponse extends RfcLDAPResult {

	//*************************************************************************
	// Constructor for CompareResponse
	//*************************************************************************

	/**
	 * The only time a client will create a CompareResponse is when it is
	 * decoding it from an InputStream
	 */
	public RfcCompareResponse(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.COMPARE_RESPONSE);
	}

}


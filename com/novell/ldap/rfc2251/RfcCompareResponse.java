
package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *      CompareResponse ::= [APPLICATION 15] LDAPResult
 */
public class CompareResponse extends LDAPResult {

	//*************************************************************************
	// Constructor for CompareResponse
	//*************************************************************************

	/**
	 * The only time a client will create a CompareResponse is when it is
	 * decoding it from an InputStream
	 */
	public CompareResponse(ASN1Decoder dec, InputStream in, int len)
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
			                       ProtocolOp.COMPARE_RESPONSE);
	}

}


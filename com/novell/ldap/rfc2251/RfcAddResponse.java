
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       AddResponse ::= [APPLICATION 9] LDAPResult
 */
public class RfcAddResponse extends RfcLDAPResult {

	//*************************************************************************
	// Constructors for AddResponse
	//*************************************************************************

	/**
	 * The only time a client will create a AddResponse is when it is
	 * decoding it from an InputStream
	 */
	public RfcAddResponse(ASN1Decoder dec, InputStream in, int len)
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
			                       RfcProtocolOp.ADD_RESPONSE);
	}

}



package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *       DelResponse ::= [APPLICATION 11] LDAPResult
 */
public class DelResponse extends LDAPResult {

	//*************************************************************************
	// Constructors for DelResponse
	//*************************************************************************

	/**
	 * The only time a client will create a DelResponse is when it is
	 * decoding it from an InputStream
	 */
	public DelResponse(ASN1Decoder dec, InputStream in, int len)
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
			                       ProtocolOp.DEL_RESPONSE);
	}

}


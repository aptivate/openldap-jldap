
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *      ModifyResponse ::= [APPLICATION 7] LDAPResult
 */
public class ModifyResponse extends LDAPResult {

	//*************************************************************************
	// Constructor for ModifyResponse
	//*************************************************************************

	/**
	 * The only time a client will create a ModifyResponse is when it is
	 * decoding it from an InputStream
	 */
	public ModifyResponse(ASN1Decoder dec, InputStream in, int len)
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
			                       ProtocolOp.MODIFY_RESPONSE);
	}

}


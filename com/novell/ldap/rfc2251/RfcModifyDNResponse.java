
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       ModifyDNResponse ::= [APPLICATION 13] LDAPResult
 */
public class RfcModifyDNResponse extends RfcLDAPResult {

	//*************************************************************************
	// Constructor for ModifyDNResponse
	//*************************************************************************

	/**
	 * The only time a client will create a ModifyDNResponse is when it is
	 * decoding it from an InputStream
	 */
	public RfcModifyDNResponse(ASN1Decoder dec, InputStream in, int len)
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
			                       RfcProtocolOp.MODIFY_DN_RESPONSE);
	}

}


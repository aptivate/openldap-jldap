
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       DelRequest ::= [APPLICATION 10] LDAPDN
 */
public class DelRequest extends LDAPDN implements Request {

	//*************************************************************************
	// Constructors for DelRequest
	//*************************************************************************

	/**
	 *
	 */
	public DelRequest(String s)
	{
		super(s);
	}

	/**
	 *
	 */
	public DelRequest(byte[] s)
	{
		super(s);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 10.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
			                       ProtocolOp.DEL_REQUEST);
	}

}


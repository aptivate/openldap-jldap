
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       UnbindRequest ::= [APPLICATION 2] NULL
 */
public class UnbindRequest extends ASN1Null implements Request {

	//*************************************************************************
	// Constructor for UnbindRequest
	//*************************************************************************

	/**
	 *
	 */
	public UnbindRequest()
	{
		super();
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: PRIMITIVE, TAG: 2. (0x42)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
			                       ProtocolOp.UNBIND_REQUEST);
	}

}



package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       UnbindRequest ::= [APPLICATION 2] NULL
 */
public class RfcUnbindRequest extends ASN1Null implements RfcRequest {

	//*************************************************************************
	// Constructor for UnbindRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcUnbindRequest()
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
			                       RfcProtocolOp.UNBIND_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, Integer scope)
    {
        throw new RuntimeException("Cannot create new RfcUnbindRequest, not allowed");
    }
}

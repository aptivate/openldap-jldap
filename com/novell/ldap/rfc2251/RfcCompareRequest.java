
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *               entry           LDAPDN,
 *               ava             AttributeValueAssertion }
 */
public class CompareRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructor for CompareRequest
	//*************************************************************************

	/**
	 *
	 */
	public CompareRequest(LDAPDN entry, AttributeValueAssertion ava)
	{
		super(2);
		add(entry);
		add(ava);
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
			                       ProtocolOp.COMPARE_REQUEST);
	}

}


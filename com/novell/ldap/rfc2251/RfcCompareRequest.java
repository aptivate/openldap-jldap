
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *               entry           LDAPDN,
 *               ava             AttributeValueAssertion }
 */
public class RfcCompareRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructor for CompareRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcCompareRequest(RfcLDAPDN entry, RfcAttributeValueAssertion ava)
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
			                       RfcProtocolOp.COMPARE_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, Integer scope)
    {
        throw new RuntimeException("Cannot create new RfcCompareRequest, not allowed");
    }
}

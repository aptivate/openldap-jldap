
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *               requestName      [0] LDAPOID,
 *               requestValue     [1] OCTET STRING OPTIONAL }
 */
public class ExtendedRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructors for ExtendedRequest
	//*************************************************************************

	/**
	 * Constructs an extended request.
	 *
	 * @param requestName The OID for this extended operation.
	 */
	public ExtendedRequest(LDAPOID requestName)
	{
		this(requestName, null);
	}

	/**
	 * Constructs an extended request.
	 *
	 * @param requestName The OID for this extended operation.
	 * @param requestValue An optional request value.
	 */
	public ExtendedRequest(LDAPOID requestName, ASN1OctetString requestValue)
	{
		super(2);
		add(new ASN1Tagged(
			new ASN1Identifier(ASN1Identifier.CONTEXT, false, 0),
			                   requestName, false));
		if(requestValue != null)
			add(new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, false, 1),
										 requestValue, false));
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 23.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       ProtocolOp.EXTENDED_REQUEST);
	}

}


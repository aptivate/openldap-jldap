
package com.novell.asn1.ldap;

import com.novell.asn1.*;

/**
 *       ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *               object          LDAPDN,
 *               modification    SEQUENCE OF SEQUENCE {
 *                       operation       ENUMERATED {
 *                                               add     (0),
 *                                               delete  (1),
 *                                               replace (2) },
 *                       modification    AttributeTypeAndValues } }
 */
public class ModifyRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructor for ModifyRequest
	//*************************************************************************

	/**
	 *
	 */
	public ModifyRequest(LDAPDN object, ASN1SequenceOf modification)
	{
		super(2);
		add(object);
		add(modification);
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
			                       ProtocolOp.MODIFY_REQUEST);
	}

}


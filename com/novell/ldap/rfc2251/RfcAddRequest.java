
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       AddRequest ::= [APPLICATION 8] SEQUENCE {
 *               entry           LDAPDN,
 *               attributes      AttributeList }
 */
public class AddRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructors for AddRequest
	//*************************************************************************

	/**
	 *
	 */
	public AddRequest(RfcLDAPDN entry, AttributeList attributes)
	{
		super(2);
		add(entry);
		add(attributes);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public RfcLDAPDN getEntry()
	{
		return (RfcLDAPDN)get(0);
	}

	/**
	 *
	 */
	public AttributeList getAttributes()
	{
		return (AttributeList)get(1);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 8. (0x68)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       ProtocolOp.ADD_REQUEST);
	}

}


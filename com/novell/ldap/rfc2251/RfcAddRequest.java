
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       AddRequest ::= [APPLICATION 8] SEQUENCE {
 *               entry           LDAPDN,
 *               attributes      AttributeList }
 */
public class RfcAddRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructors for AddRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcAddRequest(RfcLDAPDN entry, RfcAttributeList attributes)
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
	public RfcAttributeList getAttributes()
	{
		return (RfcAttributeList)get(1);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 8. (0x68)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.ADD_REQUEST);
	}

}


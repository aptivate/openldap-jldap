package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       ModifyDNRequest ::= [APPLICATION 12] SEQUENCE {
 *               entry           LDAPDN,
 *               newrdn          RelativeLDAPDN,
 *               deleteoldrdn    BOOLEAN,
 *               newSuperior     [0] LDAPDN OPTIONAL }
 */
public class RfcModifyDNRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructors for ModifyDNRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcModifyDNRequest(RfcLDAPDN entry, RfcRelativeLDAPDN newrdn,
		                    ASN1Boolean deleteoldrdn)
	{
		this(entry, newrdn, deleteoldrdn, null);
	}

	/**
	 *
	 */
	public RfcModifyDNRequest(RfcLDAPDN entry, RfcRelativeLDAPDN newrdn,
		                    ASN1Boolean deleteoldrdn, RfcLDAPDN newSuperior)
	{
		super(4);
		add(entry);
		add(newrdn);
		add(deleteoldrdn);
		if(newSuperior != null)
			add(newSuperior);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 12.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.MODIFY_DN_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, Integer scope)
    {
        throw new RuntimeException("Cannot create new RfcModifyDNRequest, not allowed");
    }
}

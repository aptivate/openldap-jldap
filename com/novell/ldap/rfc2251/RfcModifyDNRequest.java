
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       ModifyDNRequest ::= [APPLICATION 12] SEQUENCE {
 *               entry           LDAPDN,
 *               newrdn          RelativeLDAPDN,
 *               deleteoldrdn    BOOLEAN,
 *               newSuperior     [0] LDAPDN OPTIONAL }
 */
public class ModifyDNRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructors for ModifyDNRequest
	//*************************************************************************

	/**
	 *
	 */
	public ModifyDNRequest(RfcLDAPDN entry, RelativeLDAPDN newrdn,
		                    ASN1Boolean deleteoldrdn)
	{
		this(entry, newrdn, deleteoldrdn, null);
	}

	/**
	 *
	 */
	public ModifyDNRequest(RfcLDAPDN entry, RelativeLDAPDN newrdn,
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
			                       ProtocolOp.MODIFY_DN_REQUEST);
	}

}


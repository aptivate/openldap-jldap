
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       BindRequest ::= [APPLICATION 0] SEQUENCE {
 *               version                 INTEGER (1 .. 127),
 *               name                    LDAPDN,
 *               authentication          AuthenticationChoice }
 */
public class RfcBindRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructors for BindRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcBindRequest(ASN1Integer version, RfcLDAPDN name,
		                RfcAuthenticationChoice auth)
	{
		super(3);
		add(version);
		add(name);
		add(auth);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	/**
	 *
	 */
	public void setVersion(ASN1Integer version)
	{
		set(0, version);
	}

	/**
	 *
	 */
	public void setName(RfcLDAPDN name)
	{
		set(1, name);
	}

	/**
	 *
	 */
	public void setAuthenticationChoice(RfcAuthenticationChoice auth)
	{
		set(2, auth);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public ASN1Integer getVersion()
	{
		return (ASN1Integer)get(0);
	}

	/**
	 *
	 */
	public RfcLDAPDN getName()
	{
		return (RfcLDAPDN)get(1);
	}

	/**
	 *
	 */
	public RfcAuthenticationChoice getAuthenticationChoice()
	{
		return (RfcAuthenticationChoice)get(2);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 0. (0x60)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.BIND_REQUEST);
	}

}


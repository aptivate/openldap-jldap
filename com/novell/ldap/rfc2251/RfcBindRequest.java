
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       BindRequest ::= [APPLICATION 0] SEQUENCE {
 *               version                 INTEGER (1 .. 127),
 *               name                    LDAPDN,
 *               authentication          AuthenticationChoice }
 */
public class BindRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructors for BindRequest
	//*************************************************************************

	/**
	 *
	 */
	public BindRequest(ASN1Integer version, LDAPDN name,
		                AuthenticationChoice auth)
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
	public void setName(LDAPDN name)
	{
		set(1, name);
	}

	/**
	 *
	 */
	public void setAuthenticationChoice(AuthenticationChoice auth)
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
	public LDAPDN getName()
	{
		return (LDAPDN)get(1);
	}

	/**
	 *
	 */
	public AuthenticationChoice getAuthenticationChoice()
	{
		return (AuthenticationChoice)get(2);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 0. (0x60)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       ProtocolOp.BIND_REQUEST);
	}

}


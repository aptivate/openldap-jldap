
package com.novell.asn1.ldap;

import com.novell.asn1.*;

/**
 *       SaslCredentials ::= SEQUENCE {
 *               mechanism               LDAPString,
 *               credentials             OCTET STRING OPTIONAL }
 */
public class SaslCredentials extends ASN1Sequence {

	//*************************************************************************
	// Constructors for SaslCredentials
	//*************************************************************************

	/**
	 *
	 */
	public SaslCredentials(LDAPString mechanism)
	{
		this(mechanism, null);
	}

	/**
	 *
	 */
	public SaslCredentials(LDAPString mechanism, ASN1OctetString credentials)
	{
		super(2);
		add(mechanism);
		if(credentials != null)
			add(credentials);
	}

}


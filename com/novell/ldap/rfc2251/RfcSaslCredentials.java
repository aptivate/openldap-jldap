
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

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
	public SaslCredentials(RfcLDAPString mechanism)
	{
		this(mechanism, null);
	}

	/**
	 *
	 */
	public SaslCredentials(RfcLDAPString mechanism, ASN1OctetString credentials)
	{
		super(2);
		add(mechanism);
		if(credentials != null)
			add(credentials);
	}

}


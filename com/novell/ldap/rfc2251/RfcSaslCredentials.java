
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *       SaslCredentials ::= SEQUENCE {
 *               mechanism               LDAPString,
 *               credentials             OCTET STRING OPTIONAL }
 */
public class RfcSaslCredentials extends ASN1Sequence {

	//*************************************************************************
	// Constructors for SaslCredentials
	//*************************************************************************

	/**
	 *
	 */
	public RfcSaslCredentials(RfcLDAPString mechanism)
	{
		this(mechanism, null);
	}

	/**
	 *
	 */
	public RfcSaslCredentials(RfcLDAPString mechanism, ASN1OctetString credentials)
	{
		super(2);
		add(mechanism);
		if(credentials != null)
			add(credentials);
	}

}



package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

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

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public LDAPString getMechanism()
	{
		return (LDAPString)get(0);
	}

	/**
	 *
	 */
	public ASN1OctetString getCredentials()
	{
		return (ASN1OctetString)get(1);
	}

	/**
	 * Since the credentials component is OPTIONAL, this method will
	 * return a boolean value indicating its presence.
	 */
	public boolean hasCredentials()
	{
		return size() == 2;
	}

}


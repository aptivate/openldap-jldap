
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *        Control ::= SEQUENCE {
 *               controlType             LDAPOID,
 *               criticality             BOOLEAN DEFAULT FALSE,
 *               controlValue            OCTET STRING OPTIONAL }
 */
public class Control extends ASN1Sequence {

	//*************************************************************************
	// Constructors for Control
	//*************************************************************************

	/**
	 *
	 */
	public Control(LDAPOID controlType)
	{
		this(controlType, new ASN1Boolean(false), null);
	}

	/**
	 *
	 */
	public Control(LDAPOID controlType, ASN1Boolean criticality)
	{
		this(controlType, criticality, null);
	}

	/**
	 *
	 */
	public Control(LDAPOID controlType, ASN1Boolean criticality,
		            ASN1OctetString controlValue)
	{
		super(3);
		add(controlType);
		add(criticality);
		if(controlValue != null)
			add(controlValue);
	}

	/**
	 * Constructs a Control object by decoding it from an InputStream.
	 */
	public Control(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
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
	public LDAPOID getControlType()
	{
		return (LDAPOID)get(0);
	}

	/**
	 *
	 */
	public ASN1Boolean getCriticality()
	{
		return (ASN1Boolean)get(1);
	}

	/**
	 * Since controlValue is an OPTIONAL component, we need to check
	 * to see if one is available.
	 */
	public ASN1OctetString getControlValue()
	{
		return (size()==3) ? (ASN1OctetString)get(2) : null;
	}

}


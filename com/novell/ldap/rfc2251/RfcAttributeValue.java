
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *
 */
public class AttributeValue extends ASN1OctetString {

	/**
	 *
	 */
	public AttributeValue(String value)
	{
		super(value);
	}

	/**
	 *
	 */
	public AttributeValue(byte[] value)
	{
		super(value);
	}

}



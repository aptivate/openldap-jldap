
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *
 */
public class RfcAttributeValue extends ASN1OctetString {

	/**
	 *
	 */
	public RfcAttributeValue(String value)
	{
		super(value);
	}

	/**
	 *
	 */
	public RfcAttributeValue(byte[] value)
	{
		super(value);
	}

}



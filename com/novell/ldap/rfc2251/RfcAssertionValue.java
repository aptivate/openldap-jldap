
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        AssertionValue ::= OCTET STRING
 */
public class RfcAssertionValue extends ASN1OctetString {

	/**
	 *
	 */
	public RfcAssertionValue(byte[] value) {
		super(value);
	}

	/**
	 *
	 */
	public RfcAssertionValue(String value) {
		super(value);
	}

}


package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        AssertionValue ::= OCTET STRING
 */
public class AssertionValue extends ASN1OctetString {

	/**
	 *
	 */
	public AssertionValue(byte[] value) {
		super(value);
	}

	/**
	 *
	 */
	public AssertionValue(String value) {
		super(value);
	}

}

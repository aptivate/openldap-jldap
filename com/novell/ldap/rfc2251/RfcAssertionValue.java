
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

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

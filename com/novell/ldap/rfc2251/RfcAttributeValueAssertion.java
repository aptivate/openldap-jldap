
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       AttributeValueAssertion ::= SEQUENCE {
 *               attributeDesc   AttributeDescription,
 *               assertionValue  AssertionValue }
 *
 */
public class RfcAttributeValueAssertion extends ASN1Sequence {

	/**
	 *
	 */
	public RfcAttributeValueAssertion(RfcAttributeDescription ad, RfcAssertionValue av)
	{
		super(2);
		add(ad);
		add(av);
	}

}


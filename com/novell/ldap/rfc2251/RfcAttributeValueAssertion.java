
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       AttributeValueAssertion ::= SEQUENCE {
 *               attributeDesc   AttributeDescription,
 *               assertionValue  AssertionValue }
 *
 */
public class AttributeValueAssertion extends ASN1Sequence {

	/**
	 *
	 */
	public AttributeValueAssertion(AttributeDescription ad, AssertionValue av)
	{
		super(2);
		setAttributeDescription(ad);
		setAssertionValue(av);
	}

	/**
	 *
	 */
	public void setAttributeDescription(AttributeDescription ad)
	{
		set(0, ad);
	}

	/**
	 *
	 */
	public void setAssertionValue(AssertionValue av)
	{
		set(1, av);
	}

	/**
	 *
	 */
	public AttributeDescription getAttributeDescription()
	{
		return (AttributeDescription)get(0);
	}

	/**
	 *
	 */
	public AssertionValue getAssertionValue()
	{
		return (AssertionValue)get(1);
	}

}


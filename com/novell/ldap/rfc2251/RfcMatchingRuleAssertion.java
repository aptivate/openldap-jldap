
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       MatchingRuleAssertion ::= SEQUENCE {
 *               matchingRule    [1] MatchingRuleId OPTIONAL,
 *               type            [2] AttributeDescription OPTIONAL,
 *               matchValue      [3] AssertionValue,
 *               dnAttributes    [4] BOOLEAN DEFAULT FALSE }
 */
public class MatchingRuleAssertion extends ASN1Sequence {

	//*************************************************************************
	// Constructors for MatchingRuleAssertion
	//*************************************************************************

	/**
	 * Creates a MatchingRuleAssertion with the only required parameter.
	 *
	 * @param matchValue The assertion value.
	 */
	public MatchingRuleAssertion(AssertionValue matchValue)
	{
		this(null, null, matchValue, null);
	}

	/**
	 * Creates a MatchingRuleAssertion.
	 *
	 * The value null may be passed for an optional value that is not used.
	 *
	 * @param matchValue The assertion value.
	 * @param matchingRule Optional matching rule.
	 * @param type Optional attribute description.
	 * @param dnAttributes ASN1Boolean value. (default false)
	 */
	public MatchingRuleAssertion(MatchingRuleId matchingRule,
		                          AttributeDescription type,
		                          AssertionValue matchValue, 
		                          ASN1Boolean dnAttributes)
	{
		super(4);
		if(matchingRule != null)
			add(new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, false, 1),
										 matchingRule, false));

		if(type != null)
			add(new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, false, 2),
										 type, false));

		add(new ASN1Tagged( // must be present
			new ASN1Identifier(ASN1Identifier.CONTEXT, false, 3),
			                   matchValue, false));

		// if dnAttributes if false, that is the default value and we must not
		// encode it. (See RFC 2251 5.1 number 4)
		if(dnAttributes != null && dnAttributes.getContent())
			add(new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, false, 4),
										 dnAttributes, false));
	}

}


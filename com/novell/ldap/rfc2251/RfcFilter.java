
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       Filter ::= CHOICE {
 *               and             [0] SET OF Filter,
 *               or              [1] SET OF Filter,
 *               not             [2] Filter,
 *               equalityMatch   [3] AttributeValueAssertion,
 *               substrings      [4] SubstringFilter,
 *               greaterOrEqual  [5] AttributeValueAssertion,
 *               lessOrEqual     [6] AttributeValueAssertion,
 *               present         [7] AttributeDescription,
 *               approxMatch     [8] AttributeValueAssertion,
 *               extensibleMatch [9] MatchingRuleAssertion }
 */
public class Filter extends ASN1Choice {

	//*************************************************************************
	// Constructors for Filter
	//*************************************************************************

	/**
	 *
	 */
	public Filter(ASN1Tagged choice)
	{
		super(choice);
	}

}



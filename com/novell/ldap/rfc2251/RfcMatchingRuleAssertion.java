/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 * Represents an LDAP Matching Rule Assertion.
 *
 *<pre>
 *       MatchingRuleAssertion ::= SEQUENCE {
 *               matchingRule    [1] MatchingRuleId OPTIONAL,
 *               type            [2] AttributeDescription OPTIONAL,
 *               matchValue      [3] AssertionValue,
 *               dnAttributes    [4] BOOLEAN DEFAULT FALSE }
 *</pre>
 */
public class RfcMatchingRuleAssertion extends ASN1Sequence {

    //*************************************************************************
    // Constructors for MatchingRuleAssertion
    //*************************************************************************

    /**
     * Creates a MatchingRuleAssertion with the only required parameter.
     *
     * @param matchValue The assertion value.
     */
    public RfcMatchingRuleAssertion(RfcAssertionValue matchValue)
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
    public RfcMatchingRuleAssertion(RfcMatchingRuleId matchingRule,
                                  RfcAttributeDescription type,
                                  RfcAssertionValue matchValue,
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
        if(dnAttributes != null && dnAttributes.booleanValue())
            add(new ASN1Tagged(
                new ASN1Identifier(ASN1Identifier.CONTEXT, false, 4),
                                         dnAttributes, false));
        return;                                     
    }
}

/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1.ldap;

/**
 *        MatchingRuleId ::= LDAPString
 */
public class MatchingRuleId extends LDAPString {

	/**
	 * Constructs a MatchingRuleId from a String.
	 */
	public MatchingRuleId(String s)
	{
		super(s);
	}

}


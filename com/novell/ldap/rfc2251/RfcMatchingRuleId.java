/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/protocol/MatchingRuleId.java,v 1.3 2000/08/30 23:46:05 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.protocol;

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


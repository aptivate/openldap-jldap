/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/protocol/MatchingRuleId.java,v 1.4 2000/09/11 21:06:02 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.protocol;

/**
 *        MatchingRuleId ::= LDAPString
 */
public class MatchingRuleId extends RfcLDAPString {

	/**
	 * Constructs a MatchingRuleId from a String.
	 */
	public MatchingRuleId(String s)
	{
		super(s);
	}

}


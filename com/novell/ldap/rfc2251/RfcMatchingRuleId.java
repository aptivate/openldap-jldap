/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/protocol/RfcMatchingRuleId.java,v 1.5 2000/11/09 18:27:22 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.protocol;

/**
 *        MatchingRuleId ::= LDAPString
 */
public class RfcMatchingRuleId extends RfcLDAPString {

	/**
	 * Constructs a MatchingRuleId from a String.
	 */
	public RfcMatchingRuleId(String s)
	{
		super(s);
	}

}


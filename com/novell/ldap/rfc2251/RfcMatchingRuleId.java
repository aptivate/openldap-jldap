/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcMatchingRuleId.java,v 1.6 2000/11/09 23:50:56 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.rfc2251;

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


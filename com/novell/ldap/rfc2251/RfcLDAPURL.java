
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *        LDAPURL ::= LDAPString -- limited to characters permitted in URLs
 */
public class RfcLDAPURL extends RfcLDAPString {

	//*************************************************************************
	// Constructor for RfcLDAPURL
	//*************************************************************************

	/**
	 *
	 */
	public RfcLDAPURL(String s)
	{
		super(s);
	}

}


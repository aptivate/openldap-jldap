
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        LDAPURL ::= LDAPString -- limited to characters permitted in URLs
 */
public class LDAPURL extends LDAPString {

	//*************************************************************************
	// Constructor for LDAPURL
	//*************************************************************************

	/**
	 *
	 */
	public LDAPURL(String s)
	{
		super(s);
	}

}


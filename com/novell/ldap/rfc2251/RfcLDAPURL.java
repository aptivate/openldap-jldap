
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

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


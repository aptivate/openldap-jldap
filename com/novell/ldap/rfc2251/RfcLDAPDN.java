
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *        LDAPDN ::= LDAPString
 */
public class LDAPDN extends LDAPString {

	//*************************************************************************
	// Constructors for LDAPDN
	//*************************************************************************

	/**
	 *
	 */
	public LDAPDN(String s)
	{
		super(s);
	}

	/**
	 *
	 */
	public LDAPDN(byte[] s)
	{
		super(s);
	}

}


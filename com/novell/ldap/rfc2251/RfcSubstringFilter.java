
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       SubstringFilter ::= SEQUENCE {
 *               type            AttributeDescription,
 *               -- at least one must be present
 *               substrings      SEQUENCE OF CHOICE {
 *                       initial [0] LDAPString,
 *                       any     [1] LDAPString,
 *                       final   [2] LDAPString } }
 */
public class SubstringFilter extends ASN1Sequence {

	//*************************************************************************
	// Constructors for SubstringFilter
	//*************************************************************************

	/**
	 *
	 */
	public SubstringFilter(AttributeDescription type,
		                    ASN1SequenceOf substrings)
	{
		super(2);
		add(type);
		add(substrings);
	}

}


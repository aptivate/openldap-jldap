
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        AuthenticationChoice ::= CHOICE {
 *               simple                  [0] OCTET STRING,
 *                                        -- 1 and 2 reserved
 *               sasl                    [3] SaslCredentials }
 */
public class AuthenticationChoice extends ASN1Choice {

	//*************************************************************************
	// Constructors for AuthenticationChoice
	//*************************************************************************

	/**
	 *
	 */
	public AuthenticationChoice(ASN1Tagged choice)
	{
		super(choice);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************
}


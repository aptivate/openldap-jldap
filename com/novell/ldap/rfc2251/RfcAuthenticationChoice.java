
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

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


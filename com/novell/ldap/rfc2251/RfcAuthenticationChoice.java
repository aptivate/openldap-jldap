/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 * Represents an LDAP Authentication Choice.
 *
 *<pre>
 *        AuthenticationChoice ::= CHOICE {
 *               simple                  [0] OCTET STRING,
 *                                        -- 1 and 2 reserved
 *               sasl                    [3] SaslCredentials }
 *</pre>
 */
public class RfcAuthenticationChoice extends ASN1Choice {

    //*************************************************************************
    // Constructors for AuthenticationChoice
    //*************************************************************************

    /**
     *
     */
    public RfcAuthenticationChoice(ASN1Tagged choice)
    {
        super(choice);
    }

	public RfcAuthenticationChoice(
        String mechanism,
        byte[] credentials)
    {
        super( new ASN1Tagged(
                         new ASN1Identifier(ASN1Identifier.CONTEXT, true, 3),
                         new RfcSaslCredentials(
                                new RfcLDAPString(mechanism),            
                                credentials != null ? 
                                   new ASN1OctetString(credentials) : null),
                         false)); // implicit tagging
    }

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcAddRequest.java,v 1.9 2001/03/01 00:30:14 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

/**
 *       AddRequest ::= [APPLICATION 8] SEQUENCE {
 *               entry           LDAPDN,
 *               attributes      AttributeList }
 */
public class RfcAddRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructors for AddRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcAddRequest(RfcLDAPDN entry, RfcAttributeList attributes)
	{
		super(2);
		add(entry);
		add(attributes);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public RfcLDAPDN getEntry()
	{
		return (RfcLDAPDN)get(0);
	}

	/**
	 *
	 */
	public RfcAttributeList getAttributes()
	{
		return (RfcAttributeList)get(1);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 8. (0x68)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.ADD_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        throw new LDAPException(
                    LDAPExceptionMessageResource.NO_DUP_REQUEST,
                    new Object[] { "add" },
                    LDAPException.LDAP_NOT_SUPPORTED);
    }
}

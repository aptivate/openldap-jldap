/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcBindRequest.java,v 1.12 2001/06/11 21:04:52 cmorris Exp $
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
import com.novell.ldap.resources.*;

/**
 *       BindRequest ::= [APPLICATION 0] SEQUENCE {
 *               version                 INTEGER (1 .. 127),
 *               name                    LDAPDN,
 *               authentication          AuthenticationChoice }
 */
public class RfcBindRequest extends ASN1Sequence implements RfcRequest {

   /**
    * ID is added for Optimization. ID needs only be one Value for every instance
    * Thus we create it only once.
    */
    private static final ASN1Identifier ID =
        new ASN1Identifier(ASN1Identifier.APPLICATION, true, RfcProtocolOp.BIND_REQUEST);


	//*************************************************************************
	// Constructors for BindRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcBindRequest(ASN1Integer version, RfcLDAPDN name,
		                RfcAuthenticationChoice auth)
	{
		super(3);
		add(version);
		add(name);
		add(auth);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	/**
	 *
	 */
	public void setVersion(ASN1Integer version)
	{
		set(0, version);
	}

	/**
	 *
	 */
	public void setName(RfcLDAPDN name)
	{
		set(1, name);
	}

	/**
	 *
	 */
	public void setAuthenticationChoice(RfcAuthenticationChoice auth)
	{
		set(2, auth);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public ASN1Integer getVersion()
	{
		return (ASN1Integer)get(0);
	}

	/**
	 *
	 */
	public RfcLDAPDN getName()
	{
		return (RfcLDAPDN)get(1);
	}

	/**
	 *
	 */
	public RfcAuthenticationChoice getAuthenticationChoice()
	{
		return (RfcAuthenticationChoice)get(2);
	}

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 0. (0x60)
	 */
	public ASN1Identifier getIdentifier()
	{
		return ID; // new ASN1Identifier(ASN1Identifier.APPLICATION, true, RfcProtocolOp.BIND_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        throw new LDAPException(
                    ExceptionMessages.NO_DUP_REQUEST,
                    new Object[] { "bind" },
                    LDAPException.LDAP_NOT_SUPPORTED);
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcAbandonRequest.java,v 1.9 2001/03/01 18:11:25 vtag Exp $
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
 *       AbandonRequest ::= [APPLICATION 16] MessageID
 */
public class RfcAbandonRequest extends RfcMessageID implements RfcRequest {

	//*************************************************************************
	// Constructor for AbandonRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcAbandonRequest(int msgId)
	{
		super(msgId);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 16. (0x50)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
			                       RfcProtocolOp.ABANDON_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, boolean reference)
            throws LDAPException
    {
        throw new LDAPException(
                    ExceptionMessages.NO_DUP_REQUEST,
                    new Object[] { "abandon" },
                    LDAPException.LDAP_NOT_SUPPORTED);
    }
}

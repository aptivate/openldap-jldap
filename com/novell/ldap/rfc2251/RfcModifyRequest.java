/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;
import com.novell.ldap.resources.*;

/**
 *       ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *               object          LDAPDN,
 *               modification    SEQUENCE OF SEQUENCE {
 *                       operation       ENUMERATED {
 *                                               add     (0),
 *                                               delete  (1),
 *                                               replace (2) },
 *                       modification    AttributeTypeAndValues } }
 */
public class RfcModifyRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructor for ModifyRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcModifyRequest(RfcLDAPDN object, ASN1SequenceOf modification)
	{
		super(2);
		add(object);
		add(modification);
	}

    /**
    * Constructs a new Modify Request copying from the ArrayList of
    * an existing request.
    */
    /* package */
    RfcModifyRequest(   ArrayList origRequest,
                        String base)
            throws LDAPException
    {
        for(int i=0; i < origRequest.size(); i++) {
            content.add(origRequest.get(i));
        }
        // Replace the base if specified, otherwise keep original base
        if( base != null) {
            content.set(0, new RfcLDAPDN(base));
        }
        return;
    }
	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.MODIFY_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcModifyRequest( content, base);
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcSearchRequest.java,v 1.8 2001/03/01 00:30:20 cmorris Exp $
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

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.ArrayList;

/**
 *       SearchRequest ::= [APPLICATION 3] SEQUENCE {
 *               baseObject      LDAPDN,
 *               scope           ENUMERATED {
 *                       baseObject              (0),
 *                       singleLevel             (1),
 *                       wholeSubtree            (2) },
 *               derefAliases    ENUMERATED {
 *                       neverDerefAliases       (0),
 *                       derefInSearching        (1),
 *                       derefFindingBaseObj     (2),
 *                       derefAlways             (3) },
 *               sizeLimit       INTEGER (0 .. maxInt),
 *               timeLimit       INTEGER (0 .. maxInt),
 *               typesOnly       BOOLEAN,
 *               filter          Filter,
 *               attributes      AttributeDescriptionList }
 */
public class RfcSearchRequest extends ASN1Sequence implements RfcRequest {

	//*************************************************************************
	// Constructors for SearchRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcSearchRequest(RfcLDAPDN baseObject, ASN1Enumerated scope,
		                  ASN1Enumerated derefAliases, ASN1Integer sizeLimit,
		                  ASN1Integer timeLimit, ASN1Boolean typesOnly,
		                  RfcFilter filter, RfcAttributeDescriptionList attributes)
	{
		super(8);
		add(baseObject);
		add(scope);
		add(derefAliases);
		add(sizeLimit);
		add(timeLimit);
		add(typesOnly);
		add(filter);
		add(attributes);
	}

    /**
    * Constructs a new Search Request copying from the ArrayList of
    * an existing request.
    */
    /* package */
    RfcSearchRequest(   ArrayList origRequest,
                        String base,
                        String filter,
                        boolean request)
            throws LDAPException
    {
        super(origRequest.size());
        for(int i=0; i < origRequest.size(); i++) {
            content.add(origRequest.get(i));
        }
        // Replace the base if specified, otherwise keep original base
        if( base != null) {
            content.set(0, new RfcLDAPDN(base));
        }
        // If this is a reencode as a result of a search continuation reference
        // and if original scope was one-level, we need to change the scope to
        // base so we don't return objects a level deeper than requested
        if( request ) {
            int scope = ((Long)origRequest.get(1)).intValue();
            if( scope == LDAPConnection.SCOPE_ONE) {
                content.set(1, new ASN1Enumerated( LDAPConnection.SCOPE_BASE));
            }
        }
        // Replace the filter if specified, otherwise keep original filter
        if( filter != null) {
            content.set(6, new RfcFilter(filter));
        }
        return;
    }

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 3. (0x63)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.SEARCH_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcSearchRequest( content, base, filter, request);
    }
}

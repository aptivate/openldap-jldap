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

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;

/**
 * Represents an LDAP Search Request.
 *
 *<pre>
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
 *</pre>
 */
public class RfcSearchRequest extends ASN1Sequence implements RfcRequest {

    //*************************************************************************
    // Constructors for SearchRequest
    //*************************************************************************

    /*
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
        return;
    }

    /**
    * Constructs a new Search Request copying from an existing request.
    */
    /* package */
    RfcSearchRequest(   ASN1Object[] origRequest,
                        String base,
                        String filter,
                        boolean request)
            throws LDAPException
    {
        super(origRequest, origRequest.length);
        
        // Replace the base if specified, otherwise keep original base
        if( base != null) {
            set( 0, new RfcLDAPDN(base));
        }
        
        // If this is a reencode of a search continuation reference
        // and if original scope was one-level, we need to change the scope to
        // base so we don't return objects a level deeper than requested
        if( request ) {
            int scope = ((ASN1Enumerated)origRequest[1]).intValue();
            if( scope == LDAPConnection.SCOPE_ONE) {
                set( 1, new ASN1Enumerated( LDAPConnection.SCOPE_BASE));
            }
        }
        // Replace the filter if specified, otherwise keep original filter
        if( filter != null) {
            set( 6, new RfcFilter(filter));
        }
        return;
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return an application-wide id.
     *
     *<pre>
     * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 3. (0x63)
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.SEARCH_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcSearchRequest( toArray(), base, filter, request);
    }
    
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(0)).stringValue();
    }
}

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

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

/**
 * Represents and LDAP Compare Request.
 *
 *<pre>
 *       CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *               entry           LDAPDN,
 *               ava             AttributeValueAssertion }
 *</pre>
 */
public class RfcCompareRequest extends ASN1Sequence implements RfcRequest {

    //*************************************************************************
    // Constructor for CompareRequest
    //*************************************************************************

    /**
     *
     */
    public RfcCompareRequest(RfcLDAPDN entry, RfcAttributeValueAssertion ava)
    {
        super(2);
        add(entry);
        add(ava);
        if( ava.getAssertionValue() == null) {
            throw new IllegalArgumentException(
                            "compare: Attribute must have an assertion value");
        }
        return;
    }

    /**
    * Constructs a new Compare Request copying from the data of
    * an existing request.
    */
    /* package */
    RfcCompareRequest( ASN1Object[] origRequest,
                       String base)
            throws LDAPException
    {
        super(origRequest, origRequest.length);
        // Replace the base if specified, otherwise keep original base
        if( base != null) {
            set( 0, new RfcLDAPDN(base));
        }
        return;
    }
    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return an application-wide id.
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.COMPARE_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcCompareRequest( toArray(), base);
    }
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(0)).stringValue();
    }
    public final RfcAttributeValueAssertion getAttributeValueAssertion()
    {
        return ((RfcAttributeValueAssertion)get(1));
    }
}

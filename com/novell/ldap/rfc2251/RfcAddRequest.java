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
 * Represents the LDAP Add Request.
 *
 *<pre>
 *       AddRequest ::= [APPLICATION 8] SEQUENCE {
 *               entry           LDAPDN,
 *               attributes      AttributeList }
 *</pre>
 */
public class RfcAddRequest extends ASN1Sequence implements RfcRequest {

    //*************************************************************************
    // Constructors for AddRequest
    //*************************************************************************

    /**
     * Constructs an RFCAddRequest
     *
     * @param entry the entry
     *
     * @param attributes the Attributes making up the Entry
     */
    public RfcAddRequest(RfcLDAPDN entry, RfcAttributeList attributes)
    {
        super(2);
        add(entry);
        add(attributes);
        return;
    }

    /**
    * Constructs a new Add Request using data from an existing request.
    *
    * @param origRequest the original request data
    *
    * @param base if not null, replaces the dn of the original request
    */
    /* package */
    RfcAddRequest( ASN1Object[] origRequest,
                   String base)
            throws LDAPException
    {
        super(origRequest, origRequest.length);
        // Replace the base if specified, otherwise keep original base
        if( base != null) {
            set(0, new RfcLDAPDN(base));
        }
        return;
    }
    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Gets the attributes of the entry
     */
    public final RfcAttributeList getAttributes()
    {
        return (RfcAttributeList)get(1);
    }

    /**
     * Override getIdentifier to return an application-wide id.
     *<pre>
     * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 8. (0x68)
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.ADD_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcAddRequest( toArray(), base);
    }
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(0)).stringValue();
    }
}

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
 * Represents an LDAP Modify Request.
 *
 *<pre>
 *       ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *               object          LDAPDN,
 *               modification    SEQUENCE OF SEQUENCE {
 *                       operation       ENUMERATED {
 *                                               add     (0),
 *                                               delete  (1),
 *                                               replace (2) },
 *                       modification    AttributeTypeAndValues } }
 *</pre>
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
        return;
    }

    /**
    * Constructs a new Modify Request copying from the ArrayList of
    * an existing request.
    */
    /* package */
    RfcModifyRequest(   ASN1Object[] origRequest,
                        String base)
            throws LDAPException
    {
        super( origRequest, origRequest.length);
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
                                   LDAPMessage.MODIFY_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcModifyRequest( toArray(), base);
    }
    
    /**
     * Return the String value of the DN associated with this request
     *
     * @return the DN for this request.
     */
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(0)).stringValue();
    }
    
    /**
     * Return the Modifications for this request
     *
     * @return the modifications for this request.
     */
    public final ASN1SequenceOf getModifications()
    {
        return (ASN1SequenceOf)get(1);
    }
}

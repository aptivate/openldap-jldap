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
 * Represents an LDAM MOdify DN Request.
 *
 *<pre>
 *       ModifyDNRequest ::= [APPLICATION 12] SEQUENCE {
 *               entry           LDAPDN,
 *               newrdn          RelativeLDAPDN,
 *               deleteoldrdn    BOOLEAN,
 *               newSuperior     [0] LDAPDN OPTIONAL }
 *</pre>
 */
public class RfcModifyDNRequest extends ASN1Sequence implements RfcRequest {

    //*************************************************************************
    // Constructors for ModifyDNRequest
    //*************************************************************************

    /**
     *
     */
    public RfcModifyDNRequest(RfcLDAPDN entry, RfcRelativeLDAPDN newrdn,
                            ASN1Boolean deleteoldrdn)
    {
        this(entry, newrdn, deleteoldrdn, null);
    }

    /**
     *
     */
    public RfcModifyDNRequest(RfcLDAPDN entry, RfcRelativeLDAPDN newrdn,
                            ASN1Boolean deleteoldrdn, RfcLDAPSuperDN newSuperior)
    {
        super(4);
        add(entry);
        add(newrdn);
        add(deleteoldrdn);
        if(newSuperior != null)
            add(newSuperior);
    }

    /**
    * Constructs a new Delete Request copying from the ArrayList of
    * an existing request.
    */
    /* package */
    RfcModifyDNRequest(   ASN1Object[] origRequest,
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
     *
     *<pre>
     * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 12.
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.MODIFY_RDN_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcModifyDNRequest( toArray(), base);
    }
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(0)).stringValue();
    }
}

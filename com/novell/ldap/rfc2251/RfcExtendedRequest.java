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

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

/** 
 * Represents an LDAP Extended Request.
 *
 *<pre>
 *       ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *               requestName      [0] LDAPOID,
 *               requestValue     [1] OCTET STRING OPTIONAL }
 *</pre>
 */
public class RfcExtendedRequest extends ASN1Sequence implements RfcRequest {

    /**
     * Context-specific TAG for optional requestName.
     */
    public final static int REQUEST_NAME = 0;
    /**
     * Context-specific TAG for optional requestValue.
     */
    public final static int REQUEST_VALUE = 1;

    //*************************************************************************
    // Constructors for ExtendedRequest
    //*************************************************************************

    /**
     * Constructs an extended request.
     *
     * @param requestName The OID for this extended operation.
     */
    public RfcExtendedRequest(RfcLDAPOID requestName)
    {
          this(requestName, null);
        return;                                     
    }

    /**
     * Constructs an extended request.
     *
     * @param requestName The OID for this extended operation.
     * @param requestValue An optional request value.
     */
    public RfcExtendedRequest(RfcLDAPOID requestName, ASN1OctetString requestValue)
    {
        super(2);
        add(new ASN1Tagged(
            new ASN1Identifier(ASN1Identifier.CONTEXT, false, REQUEST_NAME),
               requestName, false));
        if(requestValue != null)
            add(new ASN1Tagged(
                new ASN1Identifier(ASN1Identifier.CONTEXT, false, REQUEST_VALUE),
                                         requestValue, false));
        return;                                     
    }


    /**
     * Constructs an extended request from an existing request.
     *
     * @param origRequest ASN1Object of existing request
     */
    /* package */
    public RfcExtendedRequest( ASN1Object[] origRequest)
            throws LDAPException
    {
        super(origRequest, origRequest.length);
        return;
    }
    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return an application-wide id.
     *
     *<pre>
     * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 23.
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.EXTENDED_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        // Just dup the original request
        return new RfcExtendedRequest( toArray());
    }
    public final String getRequestDN()
    {
        return null;
    }
}

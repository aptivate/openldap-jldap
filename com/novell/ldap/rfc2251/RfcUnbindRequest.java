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
import com.novell.ldap.resources.*;

/**
 * Represents the LDAP Unbind request.
 *
 *<pre>
 *       UnbindRequest ::= [APPLICATION 2] NULL
 *</pre>
 */
public class RfcUnbindRequest extends ASN1Null implements RfcRequest {

    //*************************************************************************
    // Constructor for UnbindRequest
    //*************************************************************************

    /**
     * Construct an RfCUnbind Request
     */
    public RfcUnbindRequest()
    {
        super();
        return;
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return an application-wide id.
     *<pre>
     * ID = CLASS: APPLICATION, FORM: PRIMITIVE, TAG: 2. (0x42)
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
                                   LDAPMessage.UNBIND_REQUEST);
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        throw new LDAPException(
                    ExceptionMessages.NO_DUP_REQUEST,
                    new Object[] { "unbind" },
                    LDAPException.LDAP_NOT_SUPPORTED,(String)null);
    }
    
    public final String getRequestDN()
    {
        return null;
    }
}

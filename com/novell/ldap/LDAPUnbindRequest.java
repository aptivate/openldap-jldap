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

package com.novell.ldap;

import com.novell.ldap.rfc2251.*;

/**
 * Represents an LDAP Unbind Request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       UnbindRequest ::= [APPLICATION 2] NULL
 */
public class LDAPUnbindRequest extends LDAPMessage
{
    /**
     * Constructs an LDAP Unbind Request.
     *
     * @param cont Any controls that apply to the unbind request
     */
    public LDAPUnbindRequest( LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.UNBIND_REQUEST, new RfcUnbindRequest(), cont);
        return;
    }
}

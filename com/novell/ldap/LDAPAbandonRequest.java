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
 * Represents an LDAP Abandon Request
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       AbandonRequest ::= [APPLICATION 16] MessageID
 */
public class LDAPAbandonRequest extends LDAPMessage
{
    /**
     * Construct an LDAP Abandon Request.
     *<br><br>
     * @param id The ID of the operation to abandon.
     *<br><br>
     * @param cont Any controls that apply to the abandon request
     * or null if none.
     */
    public LDAPAbandonRequest( int id,
                               LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.ABANDON_REQUEST, new RfcAbandonRequest( id), cont);
        return;
    }
}

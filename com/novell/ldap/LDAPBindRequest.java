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

import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 * Represents a simple bind request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       BindRequest ::= [APPLICATION 0] SEQUENCE {
 *               version                 INTEGER (1 .. 127),
 *               name                    LDAPDN,
 *               authentication          AuthenticationChoice }
 */
public class LDAPBindRequest extends LDAPMessage
{
    /**
     * Constructs a simple bind request.
     *
     *  @param version  The LDAP protocol version, use LDAP_V3.
     *                  LDAP_V2 is not supported.
     *<br><br>
     *  @param dn      If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name.
     *<br><br>
     *  @param passwd  If non-null and non-empty, specifies that the
     *                 connection and all operations through it should
     *                 be authenticated with dn as the distinguished
     *                 name and passwd as password.
     *
     * @param cont Any controls that apply to the simple bind request,
     * or null if none.
     */
    public LDAPBindRequest( int version,
                            String dn,
                            byte[] passwd,
                            LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.BIND_REQUEST,
               new RfcBindRequest(
                    new ASN1Integer(version),
                    new RfcLDAPDN(dn),
                    new RfcAuthenticationChoice(
                         new ASN1Tagged(
                             new ASN1Identifier(ASN1Identifier.CONTEXT,false,0),
                             new ASN1OctetString(passwd),
                             false))), // implicit tagging
                             cont);
        return;
    }

    /**
     * Retrieves the Authentication DN for a bind request.
     *
     * @return the Authentication DN for a bind request
     */
    public String getAuthenticationDN()
    {
        return getASN1Object().getRequestDN();
    }

    /**
     * Return an ASN1 representation of this add request.
     *
     * #return an ASN1 representation of this object.
     */
    public String toString()
    {
        return getASN1Object().toString();
    }
}

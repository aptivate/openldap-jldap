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

package com.novell.ldap.message;

import com.novell.ldap.*;
import com.novell.ldap.rfc2251.*;

/**
 * Represents an LDAP Compare Request.
 *//*
 *       CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *               entry           LDAPDN,
 *               ava             AttributeValueAssertion }
 */
public class LDAPCompareRequest extends LDAPMessage
{
    /**
     * Constructs an LDAPCompareRequest Object.
     *<br><br>
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.
     *<br><br>
     *  @param name    The name of the attribute to compare.
     *<br><br>
     *  @param name    The value of the attribute to compare.
     *
     *<br><br>
     * @param cont Any controls that apply to the compare request,
     * or null if none.
     */
    public LDAPCompareRequest( String dn,
                               String name,
                               byte[] value,
                               LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.COMPARE_REQUEST,
               new RfcCompareRequest(
                   new RfcLDAPDN(dn),
                   new RfcAttributeValueAssertion(
                       new RfcAttributeDescription(name),
                       new RfcAssertionValue(value))),
               cont);
        return;
    }
}

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

/**
 * Represents an LDAP Substring Filter.
 *
 *<pre>
 *       SubstringFilter ::= SEQUENCE {
 *               type            AttributeDescription,
 *               -- at least one must be present
 *               substrings      SEQUENCE OF CHOICE {
 *                       initial [0] LDAPString,
 *                       any     [1] LDAPString,
 *                       final   [2] LDAPString } }
 *</pre>
 */
public class RfcSubstringFilter extends ASN1Sequence {

    //*************************************************************************
    // Constructors for SubstringFilter
    //*************************************************************************

    /**
     *
     */
    public RfcSubstringFilter(RfcAttributeDescription type,
                            ASN1SequenceOf substrings)
    {
        super(2);
        add(type);
        add(substrings);
    }
}

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
 *<pre>
 * The AttributeDescriptionList is used to list attributes to be returned in
 * a search request.
 *
 *<pre>
 *       AttributeDescriptionList ::= SEQUENCE OF
 *               AttributeDescription
 *</pre>
 *
 * @see RfcAttributeDescription
 * @see ASN1SequenceOf
 * @see RfcSearchRequest
 */
public class RfcAttributeDescriptionList extends ASN1SequenceOf
{
    /**
     *
     */
    public RfcAttributeDescriptionList(int size)
    {
        super(size);
        return;
    }

    /**
     * Convenience constructor. This constructor will construct an
     * AttributeDescriptionList using the supplied array of Strings.
     */
    public RfcAttributeDescriptionList(String[] attrs)
    {
        super(attrs == null ? 0 : attrs.length);

        if(attrs != null) {
            for(int i=0; i<attrs.length; i++) {
                add(new RfcAttributeDescription(attrs[i]));
            }
        }
        return;
    }

    /*
     * Override add() to only accept types of AttributeDescription
     *
     * @exception ASN1InvalidTypeException
     */

}

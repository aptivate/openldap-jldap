/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.protocol.*;

/**
 * 3.3 public class LDAPSortControl
 *                extends LDAPControl
 *
 *  LDAPSortControl is a Server Control to specify how search results are
 *  to be sorted by the server (see [5]). If a server does not support
 *  sorting in general or for a particular query, the results will be
 *  returned unsorted, along with a control indicating why they were not
 *  sorted (or that sort controls are not supported). If the control was
 *  marked "critical", the whole search operation will fail if the sort
 *  control is not supported.
 */
public class LDAPSortControl extends LDAPControl {

    public static final int ORDERING_RULE = 0;
    public static final int REVERSE_ORDER = 1;
    public static final String OID = "1.2.840.113556.1.4.473";

    // 3.3.1 Constructors

    /**
     * Constructs a sort control with a single key.
     *
     *  key            A sort key object, which specifies attribute,
     *                 order, and optional matching rule.
     *
     *  critical       True if the search operation is to fail if the
     *                 server does not support this control.
     */
    public LDAPSortControl(LDAPSortKey key, boolean critical)
    {
        this(new LDAPSortKey[]{key}, critical);
    }

    /**
     * Constructs a sort control with multiple sort keys.
     *
     *  keys           An array of sort key objects, to be processed in
     *                 order.
     *
     *  critical       True if the search operation is to fail if the
     *                 server does not support this control.
     */
    public LDAPSortControl(LDAPSortKey[] keys, boolean critical)
    {
        super(OID, critical, null);

        ASN1SequenceOf sortKeyList = new ASN1SequenceOf();
        for(int i=0; i<keys.length; i++) {
            ASN1Sequence key = new ASN1Sequence();
            key.add(new AttributeDescription(keys[i].getKey()));
            if(keys[i].getMatchRule() != null) {
                key.add(
                    new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, false,
                                           ORDERING_RULE),
                        new MatchingRuleId(keys[i].getMatchRule()),
                        false));
            }
            if(keys[i].getReverse() == true) { // only add if true
                key.add(
                    new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, false,
                                           REVERSE_ORDER),
                        new ASN1Boolean(true),
                        false));
            }
            sortKeyList.add(key);
        }
    }

    // 3.3.2 getFailedAttribute

    /**
     *  If not null, this returns the attribute that caused the sort
     *  operation to fail.
     */
    public String getFailedAttribute()
    {
        return null;
    }


    // 3.3.3 getResultCode

    /**
     * Returns the result code from the sort, as defined in [1], section
     * 4.1.10.
     */
    public int getResultCode()
    {
        return -1;
    }

}


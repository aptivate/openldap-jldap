/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPSortControl.java,v 1.6 2001/03/01 00:30:07 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

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
     *  @param key     A sort key object, which specifies attribute,
     *                 order, and optional matching rule.
     *
     *  @param critical	True if the search operation is to fail if the
     *					server does not support this control.
     */
    public LDAPSortControl(LDAPSortKey key, boolean critical)
    {
        this(new LDAPSortKey[]{key}, critical);
    }

    /**
     * Constructs a sort control with multiple sort keys.
     *
     *  @param keys		An array of sort key objects, to be processed in
     *					order.
     *
     *  @param critical	True if the search operation is to fail if the
     *					server does not support this control.
     */
    public LDAPSortControl(LDAPSortKey[] keys, boolean critical)
    {
        super(OID, critical, null);

        ASN1SequenceOf sortKeyList = new ASN1SequenceOf();

        for(int i=0; i<keys.length; i++) {

			ASN1Sequence key = new ASN1Sequence();

            key.add(new RfcAttributeDescription(keys[i].getKey()));

			if(keys[i].getMatchRule() != null) {
                key.add(
                    new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, false,
                                           ORDERING_RULE),
                        new RfcMatchingRuleId(keys[i].getMatchRule()),
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

		setValue (sortKeyList.getEncoding(new LBEREncoder()));

    }

}


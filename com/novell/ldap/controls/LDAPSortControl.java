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

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *  LDAPSortControl is a Server Control to specify how search results are
 *  to be sorted by the server. If a server does not support
 *  sorting in general or for a particular query, the results will be
 *  returned unsorted, along with a control indicating why they were not
 *  sorted (or that sort controls are not supported). If the control was
 *  marked "critical", the whole search operation will fail if the sort
 *  control is not supported.
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/AsyncSortControl.java.html">AsyncSortControl.java</p>
 */
public class LDAPSortControl extends LDAPControl {

    private static int ORDERING_RULE = 0;
    private static int REVERSE_ORDER = 1;
    /**
     * The requestOID of the sort control
     */
    private static String requestOID = "1.2.840.113556.1.4.473";

    /**
     * The responseOID of the sort control
     */
    private static String responseOID = "1.2.840.113556.1.4.474";

    /*
     * This is where we register the control responses
     */
    static
    {
		/*
         * Register the Server Sort Control class which is returned by the
		 * server in response to a Sort Request
		 */
        try {
            LDAPControl.register(responseOID,
                    Class.forName("com.novell.ldap.controls.LDAPSortResponse"));
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                  "Registered Sort Control Response Class");
            }
        } catch (ClassNotFoundException e) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                  "Could not register Sort Control Response - Class not found");
            }
        }
    }
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
        return;
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
        super(requestOID, critical, null);

        ASN1SequenceOf sortKeyList = new ASN1SequenceOf();

        for(int i=0; i<keys.length; i++) {

			ASN1Sequence key = new ASN1Sequence();

            key.add(new ASN1OctetString(keys[i].getKey()));

			if(keys[i].getMatchRule() != null) {
                key.add(
                    new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, false,
                                           ORDERING_RULE),
                        new ASN1OctetString(keys[i].getMatchRule()),
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
        return;
    }
}

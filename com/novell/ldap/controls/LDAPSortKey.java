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
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.controls;

/**
 * Encapsulates parameters for sorting search results.
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/controls/AsyncSortControl.java.html">AsyncSortControl.java</p>
 */
public class LDAPSortKey {

    private String key;
    private boolean reverse;
    private String matchRule;

    // Constructors

    /**
     * Constructs a new LDAPSortKey object using an attribute as the sort key.
     *
     * @param keyDescription The single attribute to use for sorting. If the
     *                       name is preceded by a minus sign (-), the sorting
     *                       is done in reverse order. The Novell LDAP server
     *                       does not support reverse order sorting. Examples:
     *<ul>
     *  <li> "cn" (sorts by the cn attribute)</li>
     *  <li> "-cn" (sorts, in reverse order, by the cn attribute) </li>
     *</ul>
     */
    public LDAPSortKey(String keyDescription)
    {
        this(keyDescription, false, null);
    }

    /**
     * Constructs a new LDAPSortKey object with the specified attribute name
     * and sort order.
     *
     * @param key     The single attribute to use for sorting.
     *<br><br>
     * @param reverse If true, sorting is done in descending order. If false,
     *                sorting is done in ascending order. The Novell LDAP server
     *                does not support ascending order sorting.
     */
    public LDAPSortKey(String key, boolean reverse)
    {
        this(key, reverse, null);
    }

    /**
     * Constructs a new LDAPSortKey object with the specified attribute name,
     * sort order, and a matching rule.
     *
     *  @param key     The attribute name (for example, "cn") to use for sorting.
     *<br><br>
     *  @param reverse   If true, sorting is done in descending order. If false,
     *                sorting is done in ascending order. The Novell LDAP server
     *                does not support ascending order sorting.
     *<br><br>
     *  @param matchRule   The object ID (OID) of a matching rule used for
     *                     collation. If the object will be used to request
     *                     server-side sorting of search results, it should
     *                     be the OID of a matching rule known to be
     *                     supported by that server.
     */
    public LDAPSortKey(String key, boolean reverse, String matchRule)
    {
        this.key = key;
        this.reverse = reverse;
        this.matchRule = matchRule;
    }

    /**
     * Returns the attribute to used for sorting.
     *
     * @return The name of the attribute used for sorting.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the sorting order, ascending or descending.
     *
     * @return True if the sorting is done is descending order; false, if the
     *         sorting is done is ascending order.
     */
    public boolean getReverse()
    {
        return reverse;
    }

    /**
     * Returns the OID to be used as a matching rule.
     *
     * @return The OID to be used as matching rule, or null if none is to be
     * used.
     */
    public String getMatchRule()
    {
        return matchRule;
    }

}


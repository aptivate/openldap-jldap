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

/**
 * Encapsulates parameters for sorting search results.
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/AsyncSortControl.java.html">AsyncSortControl.java</p>
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
     *                       is done in reverse (descending) order. 
     *                       An OID for a matching rule may be appended
     *                       following a ":".
     *<br>
     *                       Examples:
     *<ul>
     *  <li> "cn" (sorts in ascending order by the cn attribute)</li>
     *  <li> "-cn" (sorts in descending order by the cn attribute) </li>
     *  <li> "cn:1.2.3.4.5" (sorts in ascending order by the cn attribute
     *                        using the matching rule 1.2.3.4.5) </li>
     *</ul>
     */
    public LDAPSortKey(String keyDescription)
    {
        matchRule = null;
        reverse = false;
        String myKey = keyDescription;
        if( myKey.charAt(0) == '-') {
            myKey = myKey.substring(1);
            this.reverse = true;
        }
        int pos = myKey.indexOf(":");
        if( pos != -1) {
            this.key = myKey.substring(0,pos);    
            this.matchRule = myKey.substring(pos+1);
        } else {
            this.key = myKey;
        }
        return;
    }

    /**
     * Constructs a new LDAPSortKey object with the specified attribute name
     * and sort order.
     *
     * @param key     The single attribute to use for sorting.
     *<br><br>
     * @param reverse If true, sorting is done in descending order. If false,
     *                sorting is done in ascending order.
     */
    public LDAPSortKey(String key, boolean reverse)
    {
        this(key, reverse, null);
        return;
    }

    /**
     * Constructs a new LDAPSortKey object with the specified attribute name,
     * sort order, and a matching rule.
     *
     *  @param key     The attribute name (for example, "cn") to use for sorting.
     *<br><br>
     *  @param reverse   If true, sorting is done in descending order. If false,
     *                sorting is done in ascending order.
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
        return;
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


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

package org.ietf.ldap;

/**
 *
 *  Defines the options controlling search operations.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html">
            com.novell.ldap.LDAPSearchConstraints</a>
 */
public class LDAPSearchConstraints extends LDAPConstraints
{
    private com.novell.ldap.LDAPSearchConstraints cons =
            new com.novell.ldap.LDAPSearchConstraints();

    /**
     * Used to indicate that aliases are never dereferenced.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#DEREF_NEVER">
            com.novell.ldap.LDAPSearchConstraints.DEREF_NEVER</a>
     */
    public static final int DEREF_NEVER  = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_NEVER;

    /**
     * Used to indicate that aliases are are derefrenced when
     * searching the entries beneath the starting point but not when
     * searching for the starting entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#DEREF_SEARCHING">
            com.novell.ldap.LDAPSearchConstraints.DEREF_SEARCHING</a>
     */
    public static final int DEREF_SEARCHING = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_SEARCHING;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry but are not dereferenced when
     * searching the entries beneath the starting point.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#DEREF_FINDING">
            com.novell.ldap.LDAPSearchConstraints.DEREF_FINDING</a>
     */
    public static final int DEREF_FINDING = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_FINDING;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry and when
     * searching the entries beneath the starting point.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#DEREF_ALWAYS">
            com.novell.ldap.LDAPSearchConstraints.DEREF_ALWAYS</a>
     */
    public static final int DEREF_ALWAYS = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_ALWAYS;

    /**
     * Constructs a search constraints object from a
     * com.novell.ldap.LDAPSearchConstraints object
     */
    /* package */
    LDAPSearchConstraints( com.novell.ldap.LDAPSearchConstraints cons)
    {
        super( cons);
        this.cons = cons;
        return;
    }

    /**
     * Constructs an LDAPSearchConstraints object using the default values for
     * the search constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#LDAPSearchConstraints()">
            com.novell.ldap.LDAPSearchConstraints.LDAPSearchConstraints()</a>
     */
    public LDAPSearchConstraints()
    {
        super( new com.novell.ldap.LDAPSearchConstraints());
        cons = (com.novell.ldap.LDAPSearchConstraints)super.getWrappedObject();
        return;
    }
    
    /**
     * Constructs an LDAPSearchConstraints object using the values from
     * an existing Constraints object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#LDAPSearchConstraints(com.novell.ldap.LDAPConstraints)">
            com.novell.ldap.LDAPSearchConstraints.LDAPSearchConstraints(LDAPConstraints)</a>
     */
    public LDAPSearchConstraints( LDAPConstraints cons)
    {
        super( new com.novell.ldap.LDAPSearchConstraints( cons.getWrappedObject()));
        this.cons = (com.novell.ldap.LDAPSearchConstraints)super.getWrappedObject();
        return;
    }

    /**
     * Constructs a new LDAPSearchConstraints object and allows specifying
     * the operational constraints in that object, including the LDAPBind
     * object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#LDAPSearchConstraints(int, int, int, int, boolean, int, 
            com.novell.ldap.LDAPReferralHandler, int)">
            com.novell.ldap.LDAPSearchConstraints.LDAPSearchConstraints(
            int, int, int, int, boolean, int, LDAPReferralHandler, int)</a>
     */
    public LDAPSearchConstraints(int msLimit,
                                 int serverTimeLimit,
                                 int dereference,
                                 int maxResults,
                                 boolean doReferrals,
                                 int batchSize,
                                 LDAPReferralHandler binder,
                                 int hop_limit)
    {
        super( msLimit, doReferrals, binder, hop_limit);
        cons = (com.novell.ldap.LDAPSearchConstraints)super.getWrappedObject();
        cons.setServerTimeLimit(serverTimeLimit);
        cons.setDereference(dereference);
        cons.setMaxResults(maxResults);
        cons.setBatchSize(batchSize);
        return;
    }

    /**
     * Returns a com.novell.ldap.LDAPSearchConstraints object
     */
    com.novell.ldap.LDAPSearchConstraints getWrappedSearchObject()
    {
        return cons;
    }

    /**
     * Returns how results are returned during a search.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#getBatchSize()">
            com.novell.ldap.LDAPSearchConstraints.getBatchSize()</a>
     */
    public int getBatchSize()
    {
        return cons.getBatchSize();
    }

    /**
     * Specifies when aliases should be dereferenced.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#getDereference()">
            com.novell.ldap.LDAPSearchConstraints.getDereference()</a>
     */
    public int getDereference()
    {
        return cons.getDereference();
    }

    /**
     * Returns the maximum number of search results to be returned; 0 means
     * no limit.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#getMaxResults()">
            com.novell.ldap.LDAPSearchConstraints.getMaxResults()</a>
     */
    public int getMaxResults()
    {
        return cons.getMaxResults();
    }

    /**
     * Reports the maximum number of seconds that the server is to wait when
     * returning search results while using this constraint object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#getServerTimeLimit()">
            com.novell.ldap.LDAPSearchConstraints.getServerTimeLimit()</a>
     */
    public int getServerTimeLimit()
    {
        return cons.getServerTimeLimit();
    }

    /**
     *  Specifies how results are returned during a search operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#setBatchSize(int)">
            com.novell.ldap.LDAPSearchConstraints.setBatchSize(int)</a>
     */
    public void setBatchSize(int batchSize)
    {
        cons.setBatchSize(batchSize);
        return;
    }

    /**
     * Sets a preference indicating whether or not aliases should be
     * dereferenced, and if so, when.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#setDereference(int)">
            com.novell.ldap.LDAPSearchConstraints.setDereference(int)</a>
     */
    public void setDereference(int dereference)
    {
        cons.setDereference(dereference);
        return;
    }

    /**
     * Sets the maximum number of search results to be returned; 0 means no
     * limit.  The default is 1000.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#setMaxResults(int)">
            com.novell.ldap.LDAPSearchConstraints.setMaxResults(int)</a>
     */
    public void setMaxResults(int maxResults)
    {
        cons.setMaxResults(maxResults);
        return;
    }

    /**
     * Sets the maximum number of seconds that the server is to wait when
     * returning search results.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchConstraints.html#setServerTimeLimit(int)">
            com.novell.ldap.LDAPSearchConstraints.setServerTimeLimit(int)</a>
     */
    public void setServerTimeLimit(int seconds)
    {
        cons.setServerTimeLimit(seconds);
        return;
    }
}

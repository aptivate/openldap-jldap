/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchConstraints.java,v 1.16 2001/04/27 21:47:17 dsteck Exp $
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

package org.ietf.ldap;

/**
 *
 *  Defines the options controlling search operations.
 *
 * @see com.novell.ldap.LDAPSearchConstraints
 */
public class LDAPSearchConstraints extends LDAPConstraints
{
    private com.novell.ldap.LDAPSearchConstraints cons =
            new com.novell.ldap.LDAPSearchConstraints();

    /**
     * Used to indicate that aliases are never dereferenced.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_NEVER
     */
    public static final int DEREF_NEVER  = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_NEVER;

    /**
     * Used to indicate that aliases are are derefrenced when
     * searching the entries beneath the starting point but not when
     * searching for the starting entry.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_SEARCHING
     */
    public static final int DEREF_SEARCHING = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_SEARCHING;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry but are not dereferenced when
     * searching the entries beneath the starting point.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_FINDING
     */
    public static final int DEREF_FINDING = 
                com.novell.ldap.LDAPSearchConstraints.DEREF_FINDING;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry and when
     * searching the entries beneath the starting point.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_ALWAYS
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
     * @see com.novell.ldap.LDAPSearchConstraints#LDAPSearchConstraints()
     */
    public LDAPSearchConstraints()
    {
        super( (LDAPSearchConstraints)null);
        cons = (com.novell.ldap.LDAPSearchConstraints)super.getWrappedObject();
        return;
    }

    /**
     * Constructs a new LDAPSearchConstraints object and allows specifying
     * the operational constraints in that object, including the LDAPBind
     * object.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#LDAPSearchConstraints(
                int,int,int,int,boolean,int,LDAPReferralHandler,int)
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
     * @see com.novell.ldap.LDAPSearchConstraints#getBatchSize()
     */
    public int getBatchSize()
    {
        return cons.getBatchSize();
    }

    /**
     * Specifies when aliases should be dereferenced.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#getDereference()
     */
    public int getDereference()
    {
        return cons.getDereference();
    }

    /**
     * Returns the maximum number of search results to be returned; 0 means
     * no limit.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#getMaxResults()
     */
    public int getMaxResults()
    {
        return cons.getMaxResults();
    }

    /**
     * Reports the maximum number of seconds that the server is to wait when
     * returning search results while using this constraint object.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#getServerTimeLimit()
     */
    public int getServerTimeLimit()
    {
        return cons.getServerTimeLimit();
    }

    /**
     *  Specifies how results are returned during a search operation.
     *
     * @see com.novell.ldap.LDAPSearchConstraints#setBatchSize(int)
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
     * @see com.novell.ldap.LDAPSearchConstraints#setDereference(int)
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
     * @see com.novell.ldap.LDAPSearchConstraints#setMaxResults(int)
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
     * @see com.novell.ldap.LDAPSearchConstraints#setServerTimeLimit(int)
     */
    public void setServerTimeLimit(int seconds)
    {
        cons.setServerTimeLimit(seconds);
        return;
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchConstraints.java,v 1.14 2001/02/26 19:58:25 vtag Exp $
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

package com.novell.ldap;

import com.novell.ldap.client.Debug;

/**
 *
 *  Defines the options controlling search operations.
 *
 *  <p>An LDAPSearchConstraints object is always associated with an LDAPConnection
 *  object; its values can be changed with the LDAPConnection.setOption method,
 *  or overridden by passing an LDAPConstraints object to the search operation. </p>
 *
 *  @see LDAPConnection#setSearchConstraints(LDAPSearchConstraints)
 *  @see LDAPConstraints
 *  @see LDAPConnection#setConstraints(LDAPConstraints)
 */
public class LDAPSearchConstraints extends LDAPConstraints {

    private int dereference = DEREF_NEVER;
    private int serverTimeLimit = 0;
    private int maxResults = 1000;
    private int batchSize = 1;
    private static Object nameLock = new Object(); // protect agentNum
    private static int lSConsNum = 0;  // Debug, LDAPConnection number
    private String name;             // String name for debug

    /**
     * Used to indicate that aliases are are never derefrenced.
     *
     * <p> DEREF_NEVER = 0 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_NEVER  = 0;

    /**
     * Used to indicate that aliases are are derefrenced when
     * searching the entries beneath the starting point but not when
     * searching for the starting entry.
     *
     * <p> DEREF_SEARCHING = 1 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_SEARCHING = 1;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry but are not dereferenced when
     * searching the entries beneath the starting point.
     *
     * <p> DEREF_FINDING = 2 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_FINDING = 2;

    /**
     * Used to indicate that aliases are dereferenced when
     * searching for the starting entry and when
     * searching the entries beneath the starting point.
     *
     * <p> DEREF_ALWAYS = 3 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_ALWAYS = 3;

    /**
     * Constructs an LDAPSearchConstraints object using the default values for
     * the search constraints.
     */
    public LDAPSearchConstraints()
    {
        super();
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPSearchConstraints(" + ++lSConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created");
        }
    }

    /**
     * Constructs a new LDAPSearchConstraints object and allows specifying
     * the operational constraints in that object, including the LDAPBind
     * object.
     *
     *  @param msLimit  The maximum time in milliseconds to wait for results.
     *                  The default value is 0, which means that there is no
     *                  maximum time limit. This is an API imposed limit.
     *<br><br>
     *  @param serverTimeLimit The maximum time in seconds that the server should
     *                         spend returning results. This is a server-imposed
     *                         limit.
     *<br><br>
     *  @param dereference     Specifies when aliases should be dereferenced.
     *                         Must be either DEREF_NEVER,
     *                         DEREF_FINDING, DEREF_SEARCHING, or
     *                         DEREF_ALWAYS from this class.
     *                         Default: LDAPConnection.DEREF_NEVER
     *<br><br>
     *  @param maxResults      The maximum number of search results to return.
     *                         Default: 1000
     *<br><br>
     *  @param doReferrals     Specifies whether referrals are followed
     *                         automatically. Set to true to follow referrals
     *                         automatically, or false to throw an
     *                         LDAPReferralException error it the server sends
     *                         back a referral. Default: false
     *<br><br>
     *  @param batchSize       The number of results to return in a batch.
     *                         Specifying 0 means to block until all results are in.
     *                         Specifying 1 means to return results one at a time.
     *                         Default: 1
     *
     *<br><br>
     *  @param binder   The custom authentication processor, called when the
     *                  LDAPConnection needs to authenticate, typically
     *                  on following a referral. Null may be specified to
     *                  indicate default authentication processing.
	 *                  The object implements either an LDAPBind or
	 *                  an LDAPRebind interface.
     *                  On asynchronous operations, this constraint is ignored.
     *<br><br>
     *  @param hop_limit  The maximum number of referrals to follow in a
     *                    sequence when attempting to resolve a request and
     *                    when doing automatic referral following.
     *                    The default value is 10.
     *                    On asynchronous operations, this constraint is ignored.
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
        super(msLimit, doReferrals, binder, hop_limit);
        this.serverTimeLimit = serverTimeLimit;
        this.dereference = dereference;
        this.maxResults = maxResults;
        this.batchSize = batchSize;
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPSearchConstraints(" + ++lSConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created");
        }
    }

    /**
     * Returns how results are returned during a search.
     *
     * <p>This should be 0 if intermediate results are not
     * needed, and 1 if results are to be processed as they come in. </p>
     *
     * @return How results are to be returned.
     */
    public int getBatchSize()
    {
        return batchSize;
    }

    /**
     * Specifies when aliases should be dereferenced.
     *
     * <p>Returns one of the following:
     * <ul>
     *   <li>DEREF_NEVER</li>
     *   <li>DEREF_FINDING</li>
     *   <li>DEREF_SEARCHING</li>
     *   <li>DEREF_ALWAYS</li>
     * </ul>
     *
     * @return When aliases are dereferenced.
     */
    public int getDereference()
    {
        return dereference;
    }

    /**
     * Returns the maximum number of search results to be returned; 0 means
     * no limit.
     *
     * @return The limit for the maximum number of results.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Reports the maximum number of seconds that the server is to wait when
     * returning search results while using this constraint object.
     *
     * @return The maximum time the server can wait for search results.
     */
    public int getServerTimeLimit()
    {
        return serverTimeLimit;
    }

    /**
     *  Specifies how results are returned during a search operation.
     *
     * <p>This should be 0 if intermediate results are not
     * needed, and 1 if results are to be processed as they come in.  The
     * default is 1.
     *
     *
     *  @param batchSize      The number of results to wait for.
     */
    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * Sets a preference indicating whether or not aliases should be
     * dereferenced, and if so, when.
     *
     *
     *  @param dereference  Specifies how aliases are dereference and can be set
     *                      to one of the following:
     * <ul>
     *                  <li>DEREF_NEVER - do not dereference aliases</li>
     *                  <li>DEREF_FINDING - dereference aliases when finding
     *                            the base object to start the search</li>
     *                  <li>DEREF_SEARCHING - dereference aliases when
     *                                 searching but not when finding the base
     *                                 object to start the search</li>
     *                  <li>DEREF_ALWAYS - dereference aliases when finding
     *                         the base object and when searching</li>
     * </ul>
     */
    public void setDereference(int dereference)
    {
        this.dereference = dereference;
    }

    /**
     * Sets the maximum number of search results to be returned; 0 means no
     * limit.  The default is 1000.
     *
     *  @param maxResults     Maxumum number of search results to return.
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    /**
     * Sets the maximum number of seconds that the server is to wait when
     * returning search results.
     *
     * <p>The parameter is only recognized on search operations. </p>
     *
     * @param seconds The number of seconds to wait for search results.
     */
    public void setServerTimeLimit(int seconds)
    {
        this.serverTimeLimit = seconds;
    }
}

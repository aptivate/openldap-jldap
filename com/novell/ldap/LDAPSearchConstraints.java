/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.util.Hashtable;
import com.novell.ldap.client.Debug;

/**
 *
 *  Defines the options controlling search operations.
 *
 *  <p>An LDAPSearchConstraints object is always associated with an
 *  LDAPConnection object; its values can be changed with the
 *  LDAPConnection.setConstraints method, or overridden by passing
 *  an LDAPSearchConstraints object to the search operation.</p>
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/Search.java.html">Search.java</p>
 *
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
     * Indicates that aliases are never dereferenced.
     *
     * <p> DEREF_NEVER = 0 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_NEVER  = 0;

    /**
     * Indicates that aliases are are derefrenced when
     * searching the entries beneath the starting point of the search,
     * but not when finding the starting entry.
     *
     * <p> DEREF_SEARCHING = 1 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_SEARCHING = 1;

    /**
     * Indicates that aliases are dereferenced when
     * finding the starting point for the search,
     * but not when searching under that starting entry.
     *
     * <p> DEREF_FINDING = 2 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_FINDING = 2;

    /**
     * Indicates that aliases are always dereferenced, both when
     * finding the starting point for the search, and also when
     * searching the entries beneath the starting entry.
     *
     * <p> DEREF_ALWAYS = 3 </p>
     *
     * @see #getDereference()
     * @see #setDereference(int)
     */
    public static final int DEREF_ALWAYS = 3;

    /**
     * Constructs an LDAPSearchConstraints object with a default set
     * of search constraints.
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
     * Constructs an LDAPSearchConstraints object initialized with values
     * from an existing constraints object (LDAPConstraints
     * or LDAPSearchConstraints).
     */
    public LDAPSearchConstraints( LDAPConstraints cons)
    {
        super( cons.getTimeLimit(), cons.getReferralFollowing(),
                    cons.getReferralHandler(), cons.getHopLimit());
        LDAPControl[] lsc = cons.getControls();
        if( lsc != null) {
            super.setControls((LDAPControl[])lsc.clone());
        }
        Hashtable lp = cons.getProperties();
        if( lp != null) {
            super.setProperties( (Hashtable)lp.clone());
        }

        if( cons instanceof LDAPSearchConstraints) {
            LDAPSearchConstraints scons = (LDAPSearchConstraints)cons;
            this.serverTimeLimit = scons.getServerTimeLimit();
            this.dereference = scons.getDereference();
            this.maxResults = scons.getMaxResults();
            this.batchSize = scons.getBatchSize();
        }
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPSearchConstraints(" + ++lSConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created");
        }
        return;
    }

    /**
     * Constructs a new LDAPSearchConstraints object and allows the
     * specification operational constraints in that object.
     *
     *  @param msLimit  The maximum time in milliseconds to wait for results.
     *                  The default is 0, which means that there is no
     *                  maximum time limit. This limit is enforced for an
     *                  operation by the API, not by the server.
     *                  The operation will be abandoned and terminated by the
     *                  API with an LDAPException.LDAP_TIMEOUT if the
     *                  operation exceeds the time limit.
     *<br><br>
     *  @param serverTimeLimit The maximum time in seconds that the server
     *                  should spend returning search results. This is a
     *                  server-enforced limit.  The default of 0 means
     *                  no time limit.
     *                  The operation will be terminated by the server with an
     *                  LDAPException.TIME_LIMIT_EXCEEDED if the search
     *                  operation exceeds the time limit.
     *<br><br>
     *  @param dereference Specifies when aliases should be dereferenced.
     *                  Must be either DEREF_NEVER, DEREF_FINDING,
     *                  DEREF_SEARCHING, or DEREF_ALWAYS from this class.
     *                  Default: DEREF_NEVER
     *<br><br>
     *  @param maxResults The maximum number of search results to return
     *                  for a search request.
     *                  The search operation will be terminated by the server
     *                  with an LDAPException.SIZE_LIMIT_EXCEEDED if the
     *                  number of results exceed the maximum.
     *                  Default: 1000
     *<br><br>
     * @param doReferrals Determines whether to automatically follow
     *                  referrals or not. Specify true to follow
     *                  referrals automatically, and false to throw
     *                  an LDAPException.REFERRAL if the server responds
     *                  with a referral.
     *                  It is ignored for asynchronous operations.
     *                  Default: false
     *<br><br>
     *  @param batchSize The number of results to return in a batch. Specifying
     *                  0 means to block until all results are received.
     *                  Specifying 1 means to return results one result at a
     *                  time.  Default: 1
     *
     *<br><br>
     * @param handler   The custom authentication handler called when
     *                  LDAPConnection needs to authenticate, typically on
     *                  following a referral.  A null may be specified to
     *                  indicate default authentication processing, i.e.
     *                  referrals are followed with anonymous authentication.
     *                  ThE object may be an implemention of either the
     *                  the LDAPBindHandler or LDAPAuthHandler interface.
     *                  It is ignored for asynchronous operations.
     *<br><br>
     * @param hop_limit The maximum number of referrals to follow in a
     *                  sequence during automatic referral following.
     *                  The default value is 10. A value of 0 means no limit.
     *                  It is ignored for asynchronous operations.
     *                  The operation will be abandoned and terminated by the
     *                  API with an LDAPException.REFERRAL_LIMIT_EXCEEDED if the
     *                  number of referrals in a sequence exceeds the limit.
     *
     * @see LDAPException#LDAP_TIMEOUT
     * @see LDAPException#REFERRAL
     * @see LDAPException#SIZE_LIMIT_EXCEEDED
     * @see LDAPException#TIME_LIMIT_EXCEEDED
     */
    public LDAPSearchConstraints(int msLimit,
                                 int serverTimeLimit,
                                 int dereference,
                                 int maxResults,
                                 boolean doReferrals,
                                 int batchSize,
                                 LDAPReferralHandler handler,
                                 int hop_limit)
    {
        super(msLimit, doReferrals, handler, hop_limit);
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
        return;
    }

    /**
     * Returns the number of results to block on during receipt of search
     * results.
     *
     * </p>This should be 0 if intermediate reults are not needed,
     * and 1 if results are to be processed as they come in. A value of
     * indicates block until all results are received.  Default: </p>
     *
     * @return The the number of results to block on.
     *
     * @see #setBatchSize(int)
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
     * @return The setting for dereferencing aliases.
     *
     * @see #setDereference(int)
     */
    public int getDereference()
    {
        return dereference;
    }

    /**
     * Returns the maximum number of search results to be returned for
     * a search operation. A value of 0 means no limit.  Default: 1000
     * The search operation will be terminated with an
     * LDAPException.SIZE_LIMIT_EXCEEDED if the number of results
     * exceed the maximum.
     *
     * @return The value for the maximum number of results to return.
     *
     * @see #setMaxResults(int)
     * @see LDAPException#SIZE_LIMIT_EXCEEDED
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Returns the maximum number of seconds that the server waits when
     * returning search results.
     * The search operation will be terminated with an
     * LDAPException.TIME_LIMIT_EXCEEDED if the operation exceeds the time
     * limit.
     *
     * @return The maximum number of seconds the server waits for search'
     * results.
     *
     * @see #setServerTimeLimit(int)
     * @see LDAPException#TIME_LIMIT_EXCEEDED
     */
    public int getServerTimeLimit()
    {
        return serverTimeLimit;
    }

    /**
     *  Specifies the number of results to return in a batch.

     *  <p>Specifying 0 means to block until all results are received.
     *  Specifying 1 means to return results one result at a time.  Default: 1
     *  </p>
     *
     * <p>This should be 0 if intermediate results are not needed,
     * and 1 if results are to be processed as they come in.  The
     * default is 1.
     *
     * @param batchSize      The number of results to block on.
     *
     * @see #getBatchSize()
     */
    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
        return;
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
     *
     * @see #getDereference()
     */
    public void setDereference(int dereference)
    {
        this.dereference = dereference;
        return;
    }

    /**
     * Sets the maximum number of search results to be returned from a
     * search operation. The value 0 means no limit.  The default is 1000.
     * The search operation will be terminated with an
     * LDAPException.SIZE_LIMIT_EXCEEDED if the number of results
     * exceed the maximum.
     *
     * @param maxResults     Maximum number of search results to return.
     *
     * @see #getMaxResults()
     * @see LDAPException#SIZE_LIMIT_EXCEEDED
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
        return;
    }

    /**
     * Sets the maximum number of seconds that the server is to wait when
     * returning search results.
     * The search operation will be terminated with an
     * LDAPException.TIME_LIMIT_EXCEEDED if the operation exceeds the time
     * limit.
     *
     * <p>The parameter is only recognized on search operations. </p>
     *
     * @param seconds The number of seconds to wait for search results.
     *
     * @see #getServerTimeLimit()
     * @see LDAPException#TIME_LIMIT_EXCEEDED
     */
    public void setServerTimeLimit(int seconds)
    {
        this.serverTimeLimit = seconds;
        return;
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConstraints.java,v 1.17 2001/02/27 22:53:14 vtag Exp $
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
 *  Represents a set of options to control an LDAP operation.
 *
 * <p>An LDAPConstraints object is always associated with an LDAPConnection
 * object; its values can be changed with LDAPConnection.setOption, or
 * overridden by passing an LDAPConstraints object to an operation.</p>
 */
public class LDAPConstraints implements Cloneable {

    private int msLimit = 0;
    private int hopLimit = 10;
    private boolean doReferrals = false;
    private LDAPReferralHandler refHandler = null;
    private LDAPControl[] clientCtls = null;
    private LDAPControl[] serverCtls = null;
    private static Object nameLock = new Object(); // protect agentNum
    private static int lConsNum = 0;  // Debug, LDAPConstraints number
    private String name;             // String name for debug

    /**
     * Constructs an LDAPConstraints object, using the default
     * option values.
     */
    public LDAPConstraints() {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConstraints(" + ++lConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created, follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Constructs a new LDAPConstraints object, using the specified
     * operational constraints for waiting, referrals, LDAPBind
     * object, and hop limit.
     *
     *
     * @param msLimit   The maximum time in milliseconds to wait for results.
     *                  The default is 0, which means that there is no
     *                  maximum time limit. This an API imposed limit.
     *<br><br>
     * @param doReferrals    The constraint for following referrals. True
     *                       indicates to follow referrals automatically and
     *                       false to throw an LDAPReferralException error if
     *                       the server sends back a referral. False is the
     *                       default value. It is ignored for asynchronous
     *                       operations.
     *<br><br>
     * @param binder The custom authentication processor, called when the
     *               LDAPConnection needs to authenticate, typically on
     *               following a referral.  A null may be specified to
     *               indicate default authentication processing.
     *               tHE object may implement either the LDAPBind or
     *               the LDAPRebind interface.
     *               It is ignored for asynchronous operations.
     *<br><br>
     * @param hop_limit   The maximum number of referrals to follow in a
     *                    sequence when attempting to resolve a request,
     *                    when doing automatic referral following. The default
     *                    is 10. It is ignored for asynchronous operations.
     */
    public LDAPConstraints(int msLimit,
                      boolean doReferrals,
                      LDAPReferralHandler binder,
                      int hop_limit) {
        this.msLimit = msLimit;
        this.doReferrals = doReferrals;
        this.refHandler = binder;
        this.hopLimit = hop_limit;
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConstraints(" + ++lConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created, follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Returns the maximum number of hops to follow during automatic
     * referral following.
     *
     * @return The maximum number of hops to follow during automatic
     * referral following.
     */
    public int getHopLimit() {
        return hopLimit;
    }

    /**
     * Returns an object that can process authentication for automatic
     * referral handling.
     *
     * <p>It may be null.</p>
     *
     * @return An LDAPReferralHandler object that can process authentication.
     */
    public LDAPReferralHandler getReferralHandler() {
        return refHandler;
    }

    /**
     * Returns true if referrals are to be followed automatically.
     *
     * @return  True if referrals are to be followed automatically, or
     * false if referrals are to throw an LDAPReferralException.</p>
     */
    public boolean getReferralFollowing() {
        return doReferrals;
    }

    /**
     * Returns the maximum number of milliseconds to wait for any operation
     * under these constraints.
     *
     * <p>If 0, there is no maximum time limit on waiting for the operation
     *  results. The actual granularity of the timeout depends on the
     * implementation.</p>
     *
     * @return The maximum number of milliseconds to wait for the operation.
     */
    public int getTimeLimit() {
        return msLimit;
    }

    /**
     * Sets the maximum number of hops to follow in sequence during
     * automatic referral following.
     *
     * <p>The default is 10.</p>
     *
     *  @param hop_limit   The maximum number of chained referrals to follow
     *                     automatically.
     */
    public void setHopLimit(int hop_limit) {
        this.hopLimit = hop_limit;
        return;
    }

    /**
     * Specifies the object that will process authentication requests.
     *
     * <p>The default is null.</p>
     *
     *  @param binder    An object that implements LDAPReferralHandler.
     */
    public void setReferralHandler(LDAPReferralHandler binder) {
        refHandler = binder;
        return;
    }

    /**
     * Specifies whether referrals are followed automatically or whether
     * referrals throw an LDAPReferralException.
     *
     * <p>Referrals of any type other than to an LDAP server (for example, a
     *  referral URL other than ldap://something) are ignored on automatic
     *  referral following. </p>
     *
     * <p> The default is false.</p>
     *
     *  @param doReferrals    True to follow referrals automatically.
     *                        False to throw an LDAPReferralException if
     *                        the server returns a referral.
     */
    public void setReferralFollowing(boolean doReferrals) {
        this.doReferrals = doReferrals;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                    "Follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Sets the maximum number of milliseconds the client waits for
     * any operation under these search constraints to complete.
     *
     * <p>If 0, there is no maximum time limit on waiting for the operation
     * results. The actual granularity of the timeout depends on the
     * implementation.</p>
     *
     *  @param msLimit      The maximum milliseconds to wait.
     */
    public void setTimeLimit(int msLimit) {
        this.msLimit = msLimit;
        return;
    }

    /**
     * Returns the client controls to be used by the interface.
     *
     * @return The client controls.
     */
    public LDAPControl[] getClientControls() {
        return clientCtls;
    }

    /**
     * Returns the server controls to be sent to the server.
     *
     * @return The server controls.
     */
    public LDAPControl[] getServerControls() {
        return serverCtls;
    }

    /**
     * Sets a client control for use by the interface.
     *
     *  @param control     A single client control.
     */
    public void setClientControls(LDAPControl control) {
        clientCtls = new LDAPControl[1];
        clientCtls[0] = control;
        return;
    }

    /**
     * Sets an array of client controls for use by the interface.
     *
     *  @param controls       An array of client controls.
     */
    public void setClientControls(LDAPControl[] controls) {
        clientCtls = controls;
        return;
    }

    /**
     * Sets a server control to be sent to the server.
     *
     * @param control     A single control to be sent to the server.
     */
    public void setServerControls(LDAPControl control) {
        serverCtls = new LDAPControl[1];
        serverCtls[0] = control;
        return;
    }

    /**
     * Sets an array of server controls to be sent to the server.
     *
     * @param controls       An array of controls to be sent to the server.
     */
    public void setServerControls(LDAPControl[] controls) {
        serverCtls = controls;
        return;
    }

    /**
     * Clones an LDAPConstraints object.
     *
     * @return An LDAPConstraints object.
     */
    public Object clone() {
        try {
            LDAPConstraints lc = (LDAPConstraints)super.clone();
            if(clientCtls != null) {
                lc.clientCtls = (LDAPControl[])clientCtls.clone();
            }
            if( serverCtls != null) {
                lc.serverCtls = (LDAPControl[])serverCtls.clone();
            }
            return lc;
        }
        catch(CloneNotSupportedException e) {
            /*
             * This cannot happen; we support clone as does arrays.
             * Since this method throws no exceptions, we must
             * throw a RuntimeException
             */
            throw new RuntimeException(e.toString());
        }
    }

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPConstraints.java,v 1.5 2000/08/28 22:18:55 vtag Exp $
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

package com.novell.ldap;

/**
 * 4.7 public class LDAPConstraints
 *
 *  A set of options to control any operation. There is always an
 *  LDAPConstraints associated with an LDAPConnection object; its
 *  values can be changed with LDAPConnection.setOption, or overridden by
 *  passing an LDAPConstraints object to an operation.
 */
public class LDAPConstraints implements Cloneable {

   private int msLimit = 0;
   private int hopLimit = 5;
   private boolean doReferrals = false;
   private LDAPBind binder = null;
   private LDAPRebind reauth = null;
   private LDAPControl[] clientCtls = null;
   private LDAPControl[] serverCtls = null;

   /*
    * 4.7.1 Constructors
    */

   /**
    * Constructs an LDAPConstraints object that specifies the default
    * set of constraints.
    */
   public LDAPConstraints() {
   }

   /**
    * Constructs a new LDAPConstraints object and allows specifying
    * the operational constraints in that object.
    *
    * Parameters are:
    *
    *  msLimit         Maximum time in milliseconds to wait for results
    *                  (0 by default, which means that there is no
    *                  maximum time limit). This is an interface-imposed
    *                  limit.
    *
    *  doReferrals     Specify true to follow referrals automatically,
    *                  or false to throw an LDAPReferralException error
    *                  if the server sends back a referral (false by
    *                  default).  It is ignored for asynchronous operations.
    *
    *  binder          Custom authentication processor, called when the
    *                  LDAPConnection needs to authenticate, typically
    *                  on following a referral. null may be specified to
    *                  indicate default authentication processing. It
    *                  is ignored for asynchronous operations.
    *
    *  hop_limit       Maximum number of referrals to follow in a
    *                  sequence when attempting to resolve a request,
    *                  when doing automatic referral following.
    */
   public LDAPConstraints(int msLimit,
                          boolean doReferrals,
                          LDAPBind binder,
                          int hop_limit) {
      this.msLimit = msLimit;
      this.doReferrals = doReferrals;
      this.binder = binder;
      this.hopLimit = hop_limit;

      // use default values
      reauth = null;
      clientCtls = null;
      serverCtls = null;
   }

   /**
    * Constructs a new LDAPConstraints object and allows specifying
    * the operational constraints in that object.
    *
    * Parameters are:
    *
    *  msLimit         Maximum time in milliseconds to wait for results
    *                  (0 by default, which means that there is no
    *                  maximum time limit). This is an interface-imposed
    *                  limit.
    *
    *  doReferrals     Specify true to follow referrals automatically,
    *                  or false to throw an LDAPReferralException error
    *                  if the server sends back a referral (false by
    *                  default).  It is ignored for asynchronous operations.
    *
    *  reauth          Specifies an object of the class that implements
    *                  the LDAPRebind interface. The object will be used
    *                  when the client follows referrals automatically.
    *                  The object provides a method for getting the
    *                  distinguished name and password used to
    *                  authenticate to another LDAP server during a
    *                  referral. Specifying null indicates the default
    *                  LDAPRebind will be used if one has been assigned
    *                  with LDAPConnection.setOption(), or anonymous
    *                  authentication otherwise.  It is ignored for
	*                  asynchronous operations.
    *
    *  hop_limit       Maximum number of referrals to follow in a
    *                  sequence when attempting to resolve a request,
    *                  when doing automatic referral following.  It is
	*                  ignored for asynchronous operations.
    */
   public LDAPConstraints(int msLimit,
                          boolean doReferrals,
                          LDAPRebind reauth,
                          int hop_limit) {
      this.msLimit = msLimit;
      this.doReferrals = doReferrals;
      this.reauth = reauth;
      this.hopLimit = hop_limit;

      // use default values
      this.binder = binder;
      clientCtls = null;
      serverCtls = null;
   }

   /*
    * 4.7.2 getHopLimit
    */

   /**
    * Returns the maximum number of hops to follow during automatic
    * referral following.
    */
   public int getHopLimit() {
      return hopLimit;
   }

   /*
    * 4.7.3 getBindProc
    */

   /**
    * Returns an object that can process authentication. It may be null.
    */
   public LDAPBind getBindProc() {
      return binder;
   }

   /*
    * 4.7.4 getRebindProc
    */

   /**
    * Returns the object that provides the method for getting
    * authentication information. It may be null.
    */
   public LDAPRebind getRebindProc() {
      return reauth;
   }

   /*
    * 4.7.5 getReferrals
    */

   /**
    * Specifies whether nor not referrals are followed automatically.
    * Returns true if referrals are to be followed automatically, or false
    * if referrals throw an LDAPReferralException.
    */
   public boolean getReferrals() {
      return doReferrals;
   }

   /*
    * 4.7.6 getTimeLimit
    */

   /**
    * Returns the maximum number of milliseconds to wait for any operation
    * under these constraints. If 0, there is no maximum time limit
    * on waiting for the operation results. The actual granularity of the
    * timeout depends on the implementation.
    */
   public int getTimeLimit() {
      return msLimit;
   }

   /*
    * 4.7.7 setHopLimit
    */

   /**
    * Sets the maximum number of hops to follow in sequence during
    * automatic referral following. The default is 10.
    *
    * Parameters are:
    *
    *  hop_limit      Maximum number of chained referrals to follow
    *                  automatically.
    */
   public void setHopLimit(int hop_limit) {
      this.hopLimit = hop_limit;
   }

   /*
    * 4.7.8 setBindProc
    */

   /**
    * Specifies the object that will process authentication requests. The
    * default is null.
    *
    * Parameters are:
    *
    *  binder         An object that implements LDAPBind.
    */
   public void setBindProc(LDAPBind binder) {
      this.binder = binder;
   }

   /*
    * 4.7.9 setRebindProc
    */

   /**
    * Specifies the object that provides the method for getting
    * authentication information. The default is null. If referrals is set
    * to true, and the reauth is null, referrals will be followed with
    * anonymous (= no) authentication.
    *
    * Parameters are:
    *
    *  reauth         An object that implements LDAPRebind.
    */
   public void setRebindProc(LDAPRebind reauth) {
      this.reauth = reauth;
   }

   /*
    * 4.7.10 setReferrals
    */

   /**
    * Specifies whether nor not referrals are followed automatically, or if
    * referrals throw an LDAPReferralException.  Referrals of any type
    * other than to an LDAP server (i.e. a referral URL other than
    * ldap://something) are ignored on automatic referral following. The
    * default is false.
    *
    * Parameters are:
    *
    *  doReferrals    True to follow referrals automatically.
    */
   public void setReferrals(boolean doReferrals) {
      this.doReferrals = doReferrals;
   }

   /*
    * 4.7.11 setTimeLimit
    */

   /**
    * Sets the maximum number of milliseconds to wait for any operation
    * under these search constraints. If 0, there is no maximum time limit
    * on waiting for the operation results. The actual granularity of the
    * timeout depends on the implementation.
    *
    * Parameters are:
    *
    *  msLimit        Maximum milliseconds to wait.
    */
   public void setTimeLimit(int msLimit) {
      this.msLimit = msLimit;
   }

   /*
    * 4.7.12 getClientControls
    */

   /**
    * Returns controls to be used by the interface.
    */
   public LDAPControl[] getClientControls() {
      return clientCtls;
   }

   /*
    * 4.7.13 getServerControls
    */

   /**
    * Returns controls to be sent to the server.
    */
   public LDAPControl[] getServerControls() {
      return serverCtls;
   }

   /*
    * 4.7.14 setClientControls
    */

   /**
    * Sets a control for use by the interface.
    *
    *  control        A single client control.
    */
   public void setClientControls(LDAPControl control) {
      clientCtls = new LDAPControl[1];
      clientCtls[0] = control;
   }

   /**
    * Sets controls for use by the interface.
    *
    *  controls       An array of client controls.
    */
   public void setClientControls(LDAPControl[] controls) {
      clientCtls = controls;
   }

   /*
    * 4.7.15 setServerControls
    */

   /**
    * Sets a control to be sent to the server.
    *
    * control        A single control to be sent to the server.
    */
   public void setServerControls(LDAPControl control) {
      serverCtls = new LDAPControl[1];
      serverCtls[0] = control;
   }

   /**
    * Sets controls to be sent to the server.
    *
    * controls       An array of controls to be sent to the server.
    */
   public void setServerControls(LDAPControl[] controls) {
      serverCtls = controls;
   }

   /**
    * Clone the LDAPConstraints object.
    */
   public Object clone() {
      try {
         LDAPConstraints lc = (LDAPConstraints)super.clone();
         if(clientCtls != null){
           lc.clientCtls = (LDAPControl[])clientCtls.clone();
          }
          if( serverCtls != null){
           lc.serverCtls = (LDAPControl[])serverCtls.clone();
          }
         return lc;
      }
      catch(CloneNotSupportedException e) {
         // cannot happen; we support clone as does arrays.
         throw new InternalError(e.toString());
      }
   }

}

/**
 * 4.23 public class LDAPSearchConstraints
 *                extends LDAPConstraints
 *
 *  A set of options to control a search operation. There is always an
 *
 *  LDAPSearchConstraints associated with an LDAPConnection object; its
 *  values can be changed with LDAPConnection.setOption, or overridden by
 *  passing an LDAPConstraints object to the search operation.
 */
package com.novell.ldap; 
 
public class LDAPSearchConstraints extends LDAPConstraints {

	private int dereference = LDAPv2.LDAP_DEREF_NEVER;
	private int serverTimeLimit = 0;
	private int maxResults = 1000;
	private int batchSize = 1;

   /*
    * 4.23.1 Constructors
    */

   /**
    * Constructs an LDAPSearchConstraints object that specifies the default
    * set of search constraints.
    */
   public LDAPSearchConstraints() {
		super();
   }

   /**
    * Constructs a new LDAPSearchConstraints object and allows specifying
    * the operational constraints in that object.
    *
    * Parameters are:
    *
    *  msLimit         Maximum time in milliseconds to wait for results
    *                  (0 by default, which means that there is no
    *                  maximum time limit). This is an interface-imposed
    *                  limit.
    *
    *  serverTimeLimit Maximum time in seconds that the server should
    *                  spend returning results. This is a server-imposed
    *                  limit.
    *
    *  dereference     Specifies when aliases should be dereferenced.
    *                  Must be either LDAP_DEREF_NEVER,
    *                  LDAP_DEREF_FINDING, LDAP_DEREF_SEARCHING, or
    *                  LDAP_DEREF_ALWAYS from LDAPv2
    *                  (LDAPv2.LDAP_DEREF_NEVER by default).
    *
    *  maxResults      Maximum number of search results to return (1000
    *                  by default).
    *
    *  doReferrals     Specify true to follow referrals automatically,
    *                  or false to throw an LDAPReferralException error
    *                  if the server sends back a referral (false by
    *                  default).
    *
    *  batchSize       Specify the number of results to block on during
    *                  enumeration. 0 means to block until all results
    *                  are in (1 by default).
    *
    *
    *  binder          Custom authentication processor, called when the
    *                  LDAPConnection needs to authenticate, typically
    *                  on following a referral. null may be specified to
    *                  indicate default authentication processing.
    *
    *  hop_limit       Maximum number of referrals to follow in a
    *                  sequence when attempting to resolve a request,
    *                  when doing automatic referral following.
    */
   public LDAPSearchConstraints(int msLimit,
                                int serverTimeLimit,
                                int dereference,
                                int maxResults,
                                boolean doReferrals,
                                int batchSize,
                                LDAPBind binder,
                                int hop_limit) {
		super(msLimit, doReferrals, binder, hop_limit);
		this.serverTimeLimit = serverTimeLimit;
		this.dereference = dereference;
		this.maxResults = maxResults;
		this.batchSize = batchSize;
   }

   /**
    * Constructs a new LDAPSearchConstraints object and allows specifying
    * the operational constraints in that object.
    *
    * Parameters are:
    *
    *  msLimit         Maximum time in milliseconds to wait for results
    *                  (0 by default, which means that there is no
    *                  maximum time limit). This is an interface-imposed
    *                  limit.
    *
    *  serverTimeLimit Maximum time in seconds that the server should
    *                  spend returning results. This is a server-imposed
    *                  limit.
    *
    *  dereference     Specifies when aliases should be dereferenced.
    *                  Must be either LDAP_DEREF_NEVER,
    *                  LDAP_DEREF_FINDING, LDAP_DEREF_SEARCHING, or
    *                  LDAP_DEREF_ALWAYS from LDAPv2
    *                  (LDAPv2.LDAP_DEREF_NEVER by default).
    *
    *  maxResults      Maximum number of search results to return (1000
    *                  by default).
    *
    *  doReferrals     Specify true to follow referrals automatically,
    *                  or false to throw an LDAPReferralException error
    *                  if the server sends back a referral (false by
    *                  default).
    *
    *  batchSize       Specify the number of results to block on during
    *                  enumeration. 0 means to block until all results
    *                  are in (1 by default).
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
    *                  authentication otherwise.
    *
    *  hop_limit       Maximum number of referrals to follow in a
    *                  sequence when attempting to resolve a request,
    *                  when doing automatic referral following.
    */
   public LDAPSearchConstraints(int msLimit,
                                int serverTimeLimit,
                                int dereference,
                                int maxResults,
                                boolean doReferrals,
                                int batchSize,
                                LDAPRebind reauth,
                                int hop_limit) {
		super(msLimit, doReferrals, reauth, hop_limit);
		this.serverTimeLimit = serverTimeLimit;
		this.dereference = dereference;
		this.maxResults = maxResults;
		this.batchSize = batchSize;
   }

   /*
    * 4.23.2 getBatchSize
    */

   /**
    * Returns the number of results to block on during enumeration of
    * search results. This should be 0 if intermediate results are not
    * needed, and 1 if results are to be processed as they come in.
    */
   public int getBatchSize() {
      return batchSize;
   }

   /*
    * 4.23.3 getDereference
    */

   /**
    * Specifies when aliases should be dereferenced. Returns either
    * LDAP_DEREF_NEVER, LDAP_DEREF_FINDING, LDAP_DEREF_SEARCHING, or
    * LDAP_DEREF_ALWAYS from LDAPv2.
    */
   public int getDereference() {
      return dereference;
   }

   /*
    * 4.23.4 getMaxResults
    */

   /**
    * Returns the maximum number of search results to be returned; 0 means
    * no limit.
    */
   public int getMaxResults() {
      return maxResults;
   }

   /*
    * 4.23.5 getServerTimeLimit
    */

   /**
    * Reports the maximum number of seconds that the server is to wait when
    * returning search results while using this constraint object
    */
   public int getServerTimeLimit() {
      return serverTimeLimit;
   }

   /*
    * 4.23.6 setBatchSize
    */

   /**
    * Sets the suggested number of results to block on during enumeration
    * of search results. This should be 0 if intermediate results are not
    * needed, and 1 if results are to be processed as they come in.  The
    * default is 1.
    *
    * Parameters are:
    *
    *  batchSize      Blocking size on search enumerations.
    */
   public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
   }

   /*
    * 4.23.7 setDereference
    */

   /**
    * Sets a preference indicating whether or not aliases should be
    * dereferenced, and if so, when.
    *
    * Parameters are:
    *
    *  dereference    Either LDAP_DEREF_NEVER, LDAP_DEREF_FINDING,
    *                  LDAP_DEREF_SEARCHING, or LDAP_DEREF_ALWAYS from
    *                  LDAPv2.
    */
   public void setDereference(int dereference) {
		this.dereference = dereference;
   }

   /*
    * 4.23.8 setMaxResults
    */

   /**
    * Sets the maximum number of search results to be returned; 0 means no
    * limit.  The default is 1000.
    *
    * Parameters are:
    *
    *  maxResults     Maxumum number of search results to return.
    */
   public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
   }

   /*
    * 4.23.9 setServerTimeLimit
    */

   /**
    * Sets the maximum number of seconds that the server is to wait when
    * returning search results. The parameter is only recognized on search
    * operations.
    */
   public void setServerTimeLimit(int seconds) {
		this.serverTimeLimit = seconds;
   }

}

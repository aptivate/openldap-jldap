/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPReferralException.java,v 1.7 2000/10/02 21:49:43 judy Exp $
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
 *  Thrown when a server returns a referral and when a referral has not
 *  been followed.  It contains a list of URL strings corresponding
 *  to the referrals or search continuation references received on an LDAP
 *  operation.
 */
public class LDAPReferralException extends LDAPException {

	private String serverMessage = null;
	private String[] referrals = null;
   /**
    * Constructs a default exception with no specific error information.
    */
   public LDAPReferralException() {
      super();
   }

   /**
    * Constructs a default exception with a specified string as additional
    * information. 
    *
    * <p>This form is used for lower-level errors.</p>
    *
    *@param message The additional error information.
    */
   public LDAPReferralException(String message) {
      super( message, LDAPException.REFERRAL);
   }

   /**
    *
    * Constructs an exception with a specified error string, result code, and 
    * an error message from the server.
    *
    *  @param message        The additional error information.
    *<br><br>
    *  @param resultCode     The result code returned.
    *<br><br>
    *  @param serverMessage  Error message specifying additional information
    *                        from the server.
    */
   public LDAPReferralException(String message,
                                int resultCode,
                                String serverMessage) {
      super( message, resultCode);
	  return;
   }

   /**
    *
    * Constructs an exception with a specified error string, result code, and 
    * referral strings. (EXPEREMENTIAL - Novell specific)
    *
    *  @param message        The additional error information.
    *<br><br>
    *  @param resultCode     The result code returned.
    *<br><br>
    *  @param serverMessage  Referral strings.
    */
   public LDAPReferralException(String message,
                                int resultCode,
                                String[] referrals) {
      super( message, resultCode);
	  this.referrals = referrals;
	  return;
   }

   /**
    * Gets the list of referrals (LDAP URLs to other servers) returned by
    * the LDAP server. 
    *
    * <p>This exception is only thrown, and therefore the URL
    * list is only available, if automatic referral following is not enabled.
    * The referrals may include URLs of a type other than ones for an LDAP
    * server (for example, a referral URL other than ldap://something).</p>
    */
   public LDAPUrl[] getURLs() {
      throw new RuntimeException( "LDAPReferralException: getURLs() not supported");
   }

   /**
    * Gets the list of referrals (LDAP URLs to other servers) returned by
    * the LDAP server. 
    *
    * <p>This exception is only thrown, and therefore the URL
    * list is only available, if automatic referral following is not enabled.
    * The referrals may include URLs of a type other than ones for an LDAP
    * server (for example, a referral URL other than ldap://something).</p>
    */
   public String[] getReferrals() {
	  return this.referrals;
   }
}

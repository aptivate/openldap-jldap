/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPReferralException.java,v 1.9 2000/11/03 23:06:15 vtag Exp $
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
    * Constructs a default exception with a specified string as additional
    * information, and an exception that indicates a failure to follow a
    * referral.  This exception applies only to synchronous operations
    * and is thrown only on receipt of a referral when the referral was
    * not followed.
    *
    *@param message The additional error information.
    *<br><br>
    *@param rootException An exception which caused referral following to fail.
    */
   public LDAPReferralException(String message, LDAPException rootException) {
      super( message, LDAPException.REFERRAL);
      throw new RuntimeException("LDAPReferralException(msg, exception) not implemented");
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
                                String serverMessage)
   {
      super( message, resultCode);
	  return;
   }

   /**
    *
    * Constructs an exception with a specified error string, result code, 
    * an error message from the server, and an exception that indicates
    * a failure to follow a referral.  This exception applies only to
    * synchronous operations and is thrown only on receipt of a referral
    * when the referral was not followed.
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
                                String serverMessage,
                                LDAPException rootException)
    {
      super( message, resultCode);
      throw new RuntimeException("LDAPReferralException(msg, exception) not implemented");
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
    *  @param referrals      Referral strings.
    */
   /*package*/ LDAPReferralException(String message,
                                int resultCode,
                                String[] referrals) {
      super( message, resultCode);
	  this.referrals = referrals;
	  return;
   }

   /**
    * Constructs a default exception with a specified string as additional
    * information, and an exception that indicates a failure to follow a
    * referral.  This exception applies only to synchronous operations
    * and is thrown only on receipt of a referral when the referral was
    * not followed.
    *
    *@param message         The additional error information.
    *<br><br>
    *@param rootException   An exception which caused referral following to fail.
    *<br><br>
    *@param referrals       Referral strings.
    */
   /*package*/ LDAPReferralException(String message,
                                LDAPException rootException,
                                String[] referrals) {
      super( message, LDAPException.REFERRAL);
      throw new RuntimeException("LDAPReferralException(msg, exception) not implemented");
   }

   /**
    * Returns the LDAPException which caused the referral to fail, if any.
    *
    * @return an LDAPException
    */
   public LDAPException getReferralFailureException()
   {
      throw new RuntimeException("LDAPReferralException.getReferralFailureException not implemented");
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

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

package com.novell.ldap;

/**
 *  Thrown when a server returns a referral and when a referral has not
 *  been followed.  It contains a list of URL strings corresponding
 *  to the referrals or search continuation references received on an LDAP
 *  operation.
 */
public class LDAPReferralException extends LDAPException
{

    private String failedReferral = null;
	private String serverMessage = null;
	private String[] referrals = null;

   /**
    * Constructs a default exception with no specific error information.
    */
   public LDAPReferralException() {
      super();
      return;
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
      return;
   }

   /**
    * Constructs a default exception with a specified string as additional
    * information.
    *
    * <p>This form is used for lower-level errors.</p>
    *
    *<br><br>
    *  @param arguments     The modifying arguments to be included in the
    *                       message string.
    *
    *@param message The additional error information.
    */
   public LDAPReferralException(String message, Object[] arguments) {
      super( message, arguments, LDAPException.REFERRAL);
      return;
   }
   /**
    * Constructs a default exception with a specified string as additional
    * information and an exception that indicates a failure to follow a
    * referral. This excepiton applies only to synchronous operations and
    * is thrown only on receipt of a referral when the referral was not
    * followed.
    *
    *@param message The additional error information.
    *
    *<br><br>
    *@param rootException An exception which caused referral following to fail.
    */
   public LDAPReferralException(String message,
                                Throwable rootException) {
      super( message, LDAPException.REFERRAL, rootException);
      return;
   }

   /**
    * Constructs a default exception with a specified string as additional
    * information and an exception that indicates a failure to follow a
    * referral. This excepiton applies only to synchronous operations and
    * is thrown only on receipt of a referral when the referral was not
    * followed.
    *
    *@param message The additional error information.
    *
    *<br><br>
    *  @param arguments     The modifying arguments to be included in the
    *                       message string.
    *<br><br>
    *@param rootException An exception which caused referral following to fail.
    */
   public LDAPReferralException(String message,
                                Object[] arguments,
                                Throwable rootException) {
      super( message, arguments, LDAPException.REFERRAL, rootException);
      return;
   }

   /**
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
      super( message + ": " + serverMessage, resultCode);
	  return;
   }

   /**
    * Constructs an exception with a specified error string, result code, and
    * an error message from the server.
    *
    *  @param message        The additional error information.
    *<br><br>
    *  @param arguments      The modifying arguments to be included in the
    *                        message string.
    *<br><br>
    *  @param resultCode     The result code returned.
    *<br><br>
    *  @param serverMessage  Error message specifying additional information
    *                        from the server.
    */
   public LDAPReferralException(String message,
                                Object[] arguments,
                                int resultCode,
                                String serverMessage)
   {
      super( message + ": " + serverMessage, arguments, resultCode);
	  return;
   }

   /**
    * Constructs an exception with a specified error string, result code,
    * an error message from the server, and an exception that indicates
    * a failure to follow a referral.
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
                                Throwable rootException)
    {
      super( message + ": " + serverMessage, resultCode, rootException);
      return;
    }
   /**
    * Constructs an exception with a specified error string, result code,
    * an error message from the server, and an exception that indicates
    * a failure to follow a referral.
    *
    *  @param message        The additional error information.
    *<br><br>
    *  @param arguments      The modifying arguments to be included in the
    *                        message string.
    *<br><br>
    *  @param resultCode     The result code returned.
    *<br><br>
    *  @param serverMessage  Error message specifying additional information
    *                        from the server.
    */
   public LDAPReferralException(String message,
                                Object[] arguments,
                                int resultCode,
                                String serverMessage,
                                Throwable rootException)
    {
      super( message + ": " + serverMessage, arguments, resultCode, rootException);
      return;
    }

    /* Gets the referral that could not be processed.  If multiple referrals
     * could not be processed, the method returns one of them.
     *
     * @return the referral that could not be followed.
     */
    public String getFailedReferral()
    {
        return failedReferral;
    }

   /**
    * Gets the list of referral URLs (LDAP URLs to other servers) returned by
    * the LDAP server.
    *
    * The referral list may include URLs of a type other than ones for an LDAP
    * server (for example, a referral URL other than ldap://something).</p>
    *
    * @return The list of URLs that comprise this referral
    */
   public String[] getReferrals() {
	  return referrals;
   }

   /**
    * Sets a referral that could not be processed
    *
    * @param url The referral URL that could not be processed.
    */
   public void setFailedReferral( String url)
   {
       failedReferral = url;
       return;
   }

   /**
    * Sets the list of referrals
    *
    * @param urls the list of referrals returned by the LDAP server in a
    * single response.
    */
    /* package */
    void setReferrals( String[] urls)
    {
        referrals = urls;
        return;
    }
}

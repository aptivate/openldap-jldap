/**
 * 4.20 public class LDAPReferralException
 *                 extends LDAPException
 *
 *  This exception, derived from LDAPException, is thrown when a server
 *  returns a referral and automatic referral following has not been
 *  enabled.
 */
package com.novell.ldap; 
 
public class LDAPReferralException extends LDAPException {

   /*
    * 4.20.1 Constructors
    */

   /**
    * Constructs a default exception with no specific error information.
    */
   public LDAPReferralException() {
   }

   /**
    * Constructs a default exception with a specified string as additional
    * information. This form is used for lower-level errors.
    */
   public LDAPReferralException(String message) {
   }

   /**
    * Parameters are:
    *
    *  message        The additional error information.
    *
    *  resultCode     The result code returned
    *
    *  serverMessage  Error message specifying additional information
    *                  from the server.
    */
   public LDAPReferralException(String message,
                                int resultCode,
                                String serverMessage) {
   }

   /*
    * 4.20.2 getURLs
    */

   /**
    * Gets the list of referrals (LDAP URLs to other servers) returned by
    * the LDAP server. This exception is only thrown, and therefor the URL
    * list only available, if automatic referral following is not enabled.
    * The referrals may include URLs of a type other than ones for an LDAP
    * server (i.e. a referral URL other than ldap://something).
    */
   public LDAPUrl[] getURLs() {
      return null;
   }

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPReferralException.java,v 1.4 2000/08/28 22:18:57 vtag Exp $
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
 
/*
 * 4.26 public class LDAPReferralException
 *                 extends LDAPException
 */
 
/**
 *  Thrown when a server returns a referral and automatic referral following
 *  has not been enabled.
 */
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
    * information. 
    *
    * <p>This form is used for lower-level errors.</p>
    *
    *@param message The additional error information.
    */
   public LDAPReferralException(String message) {
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
   }

   /*
    * 4.26.2 getURLs
    */

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
      return null;
   }

}

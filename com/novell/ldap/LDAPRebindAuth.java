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
 *  @deprecated replaced by {@link LDAPAuthProvider}.  This interface
 *  has been renamed to LDAPAuthProvider in IETF draft 17 of the Java LDAP API
 *  (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
 *  in fall of 2003.
 *
 *  An implementation of LDAPRebind must be able to provide an LDAPRebindAuth
 *  object at the time of a referral.  The class encapsulates reauthentiation
 *  credentials for the specified host and port.
 *
 *  @see LDAPRebind
 *  @see LDAPBind
 *  @see LDAPConstraints#setReferralFollowing(boolean)
 *  @deprecated replaced by {@link LDAPAuthProvider}
 */
public class LDAPRebindAuth {

   private String _dn;
   private String _password;

   /**
    * Constructs information that is used by the client for authentication
    * when following referrals automatically.
    *
    *  @param dn           The distinguished name to use when authenticating to
    *                      a server.
    *<br><br>
    *  @param password     The password to use when authenticating to a server.
    */
   public LDAPRebindAuth ( String dn, String password ) {
      _dn = dn;
      _password = password;
   }

   /**
    * Returns the distinguished name to be used for reauthentication on
    * automatic referral following.
    *
    * @return The distinguished name from the object.
    */
   public String getDN() {
      return _dn;
   }

   /**
    * Returns the password to be used for reauthentication on automatic
    * referral following.
    *
    * @return The password from the object.
    */
   public String getPassword() {
      return _password;
   }

}

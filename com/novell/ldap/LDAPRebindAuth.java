/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPRebindAuth.java,v 1.3 2000/08/03 22:06:16 smerrill Exp $
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
 *4.18 public class LDAPRebindAuth
 *
 *  Represents information used to authenticate the client in cases where
 *  the client follows referrals automatically.
 */
public class LDAPRebindAuth {

   private String _dn;
   private String _password;

   /*
    * 4.18.1 Constructors
    */

   /**
    * Constructs information that is used by the client for authentication
    * when following referrals automatically.
    *
    * Parameters are:
    *
    *  dn             Distinguished name to use in authenticating to
    *                  the server.
    *
    *  password       Password to use in authenticating to the server.
    */
   public LDAPRebindAuth ( String dn, String password ) {
      _dn = dn;
      _password = password;
   }

   /*
    * 4.18.2 getDN
    */

   /**
    * Returns the distinguished name to be used for reauthentication on
    * automatic referral following.
    */
   public String getDN() {
      return _dn;
   }

   /*
    * 4.18.3 getPassword
    */

   /**
    * Returns the password to be used for reauthentication on automatic
    * referral following.
    */
   public String getPassword() {
      return _password;
   }

}

/**
 *4.18 public class LDAPRebindAuth
 *
 *  Represents information used to authenticate the client in cases where
 *  the client follows referrals automatically.
 */
package com.novell.ldap; 

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

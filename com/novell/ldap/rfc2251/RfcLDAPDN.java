
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        LDAPDN ::= LDAPString
 */
public class LDAPDN extends LDAPString {

   //*************************************************************************
   // Constructors for LDAPDN
   //*************************************************************************

   /**
    *
    */
   public LDAPDN(String s)
   {
      super(s);
   }

   /**
    *
    */
   public LDAPDN(byte[] s)
   {
      super(s);
   }

}


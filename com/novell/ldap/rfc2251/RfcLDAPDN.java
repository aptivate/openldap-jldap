
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *        LDAPDN ::= LDAPString
 */
public class RfcLDAPDN extends RfcLDAPString {

   //*************************************************************************
   // Constructors for RfcLDAPDN
   //*************************************************************************

   /**
    *
    */
   public RfcLDAPDN(String s)
   {
      super(s);
   }

   /**
    *
    */
   public RfcLDAPDN(byte[] s)
   {
      super(s);
   }

}


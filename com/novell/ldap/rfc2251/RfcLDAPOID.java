
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *
 */
public class LDAPOID extends ASN1OctetString {

   /**
    *
    */
   public LDAPOID(String s)
   {
      super(s);
   }

   /**
    *
    */
   public LDAPOID(byte[] s)
   {
      super(s);
   }

   /**
    * Convert octet string to String.
    */
//   public String getString()
//   {
//      return new String(getContent()); // UTF8 ???
//   }

}



package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *
 */
public class LDAPString extends ASN1OctetString {

   /**
    *
    */
   public LDAPString(String s)
   {
      super(s);
   }

   /**
    *
    */
   public LDAPString(byte[] s)
   {
      super(s);
   }

   /**
    *
    */
   public LDAPString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);
   }

   /**
    * Convert octet string to String.
    */
//   public String getString()
//   {
//      return new String(getContent()); // UTF8 ???
//   }

}


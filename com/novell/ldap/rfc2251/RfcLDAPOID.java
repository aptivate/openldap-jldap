
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *
 */
public class RfcLDAPOID extends ASN1OctetString
{

   /**
    *
    */
   public RfcLDAPOID(String s)
   {
      super(s);
   }

   /**
    *
    */
   public RfcLDAPOID(byte[] s)
   {
      super(s);
   }

   /* 
    * Convert octet string to String.
    */
/*
 * public String getString()
 * {
 *    return new String(getContent()); // UTF8 ???
 * }
 */

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcLDAPString.java,v 1.6 2000/11/09 18:27:24 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *
 */
public class RfcLDAPString extends ASN1OctetString {

   /**
    *
    */
   public RfcLDAPString(String s)
   {
      super(s);
   }

   /**
    *
    */
   public RfcLDAPString(byte[] ba)
   {
      super(ba);
   }

   /**
    *
    */
   public RfcLDAPString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);
   }

}


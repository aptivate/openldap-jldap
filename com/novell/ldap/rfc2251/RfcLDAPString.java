/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

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
   public LDAPString(byte[] ba)
   {
      super(ba);
   }

   /**
    *
    */
   public LDAPString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);
   }

}


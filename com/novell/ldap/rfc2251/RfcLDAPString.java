/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/protocol/LDAPString.java,v 1.4 2000/09/03 06:43:09 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

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


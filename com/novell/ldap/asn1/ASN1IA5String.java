/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1IA5String.java,v 1.3 2000/09/03 06:43:07 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 IA5String type.
 */
public class ASN1IA5String extends ASN1CharacterString {

   /**
    * ASN.1 IA5String tag definition.
    */
   public static final int TAG = 0x16;

   //*************************************************************************
   // Constructors for ASN1IA5String
   //*************************************************************************

   /**
    * Constructs an ASN1IA5String object using a String value.
    */
   public ASN1IA5String(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1IA5String object by decoding data from an input
    * stream.
    */
   public ASN1IA5String(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1IA5String specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1IA5String.
    */
   public String toString()
   {
      return super.toString() + "IA5String: " + content;
   }

}


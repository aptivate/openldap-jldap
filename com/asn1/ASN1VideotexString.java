/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 VideotexString type.
 */
public class ASN1VideotexString extends ASN1CharacterString {

   /**
    * ASN.1 VideotexString tag definition.
    */
   public static final int TAG = 0x15;

   //*************************************************************************
   // Constructors for ASN1VideotexString
   //*************************************************************************

   /**
    * Constructs an ASN1VideotexString object using a String value.
    */
   public ASN1VideotexString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1VideotexString object by decoding data from an input
    * stream.
    */
   public ASN1VideotexString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1VideotexString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1VideotexString.
    */
   public String toString()
   {
      return super.toString() + "VideotexString: " + content;
   }

}


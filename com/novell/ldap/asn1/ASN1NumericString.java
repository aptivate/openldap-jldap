/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 NumericString type.
 */
public class ASN1NumericString extends ASN1CharacterString {

   /**
    * ASN.1 NumericString tag definition.
    */
   public static final int TAG = 0x12;

   //*************************************************************************
   // Constructors for ASN1NumericString
   //*************************************************************************

   /**
    * Constructs an ASN1NumericString object using a String value.
    */
   public ASN1NumericString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1NumericString object by decoding data from an input
    * stream.
    */
   public ASN1NumericString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1NumericString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "NumericString: " + content;
   }

}


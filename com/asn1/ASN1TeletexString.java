/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 TeletexString type.
 */
public class ASN1TeletexString extends ASN1CharacterString {

   /**
    * ASN.1 TeletexString tag definition.
    */
   public static final int TAG = 0x14;

   //*************************************************************************
   // Constructors for ASN1TeletexString
   //*************************************************************************

   /**
    * Constructs an ASN1TeletexString object using a String value.
    */
   public ASN1TeletexString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1TeletexString object by decoding data from an input
    * stream.
    */
   public ASN1TeletexString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1TeletexString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1TeletexString.
    */
   public String toString()
   {
      return super.toString() + "TeletexString: " + content;
   }

}


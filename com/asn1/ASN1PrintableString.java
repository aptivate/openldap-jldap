/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 PrintableString type.
 */
public class ASN1PrintableString extends ASN1CharacterString {

   /**
    * ASN.1 PrintableString tag definition.
    */
   public static final int TAG = 0x13;

   //*************************************************************************
   // Constructors for ASN1PrintableString
   //*************************************************************************

   /**
    * Constructs an ASN1PrintableString object using a String value.
    */
   public ASN1PrintableString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1PrintableString object by decoding data from an input
    * stream.
    */
   public ASN1PrintableString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1PrintableString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1PrintableString.
    */
   public String toString()
   {
      return super.toString() + "PrintableString: " + content;
   }

}


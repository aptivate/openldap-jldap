/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 GraphicString type.
 */
public class ASN1GraphicString extends ASN1CharacterString {

   /**
    * ASN.1 GraphicString tag definition.
    */
   public static final int TAG = 0x19;

   //*************************************************************************
   // Constructors for ASN1GraphicString
   //*************************************************************************

   /**
    * Constructs an ASN1GraphicString object using a String value.
    */
   public ASN1GraphicString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1GraphicString object by decoding data from an input
    * stream.
    */
   public ASN1GraphicString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1GraphicString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1GraphicString.
    */
   public String toString()
   {
      return super.toString() + "GraphicString: " + content;
   }

}


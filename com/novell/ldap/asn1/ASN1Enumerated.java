/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 ENUMERATE type.
 */
public class ASN1Enumerated extends ASN1Numeric {

   /**
    * ASN.1 tag definition for ENUMERATED
    */
   public static final int TAG = 0x0a;

   //*************************************************************************
   // Constructors for ASN1Enumerated
   //*************************************************************************

   /**
    * Constructs an ASN1Enumerated object using an int value.
    */
   public ASN1Enumerated(int content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = new Long(content);
   }

   /**
    * Constructs an ASN1Enumerated object using a long value.
    */
   public ASN1Enumerated(long content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = new Long(content);
   }

   /**
    * Constructs an ASN1Enumerated object by decoding data from an input
    * stream.
    */
   public ASN1Enumerated(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, ENUMERATED);
      content = (Long)dec.decodeNumeric(in, len);
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1Enumerated directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "ENUMERATED: " + content;
   }

}


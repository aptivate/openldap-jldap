/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 INTEGER type.
 */
public class ASN1Integer extends ASN1Numeric {

   /**
    * ASN.1 INTEGER tag definition.
    */
   public static final int TAG = 0x02;

   //*************************************************************************
   // Constructors for ASN1Integer
   //*************************************************************************

   /**
    * Constructs an ASN1Integer object using an int value.
    */
   public ASN1Integer(int content)
   {
      this((long)content);
   }

   /**
    * Constructs an ASN1Integer object using a long value.
    */
   public ASN1Integer(long content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = new Long(content);
   }

   /**
    * Constructs an ASN1Integer object by decoding data from an input stream.
    */
   public ASN1Integer(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (Long)dec.decodeNumeric(in, len);
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1Integer directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1Integer specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1Integer.
    */
   public String toString()
   {
      return super.toString() + "INTEGER: " + content;
   }

}


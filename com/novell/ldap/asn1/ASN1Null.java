/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents the ASN.1 NULL type.
 */
public class ASN1Null extends ASN1Simple {

   /**
    * ASN.1 NULL tag definition.
    */
   public static final int TAG = 0x05;

   //*************************************************************************
   // Constructor for ASN1Null
   //*************************************************************************

   /**
    * Constructs an ASN1Null object.
    */
   public ASN1Null()
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes this Null directly to an output stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1Null specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "NULL: \"\"";
   }

}


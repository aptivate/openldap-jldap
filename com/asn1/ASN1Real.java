/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1Real.java,v 1.3 2000/09/03 06:43:08 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 REAL type.
 */
public class ASN1Real extends ASN1Simple {

   private double content;

   /**
    * ASN.1 REAL tag definition.
    */
   public static final int TAG = 0x09;

   //*************************************************************************
   // Constructors for ASN1Real
   //*************************************************************************

   /**
    * Constructs an ASN1Real object using a REAL value.
    */
   public ASN1Real(double content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1Real object by decoding data from an input stream.
    */
   public ASN1Real(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = ((Double)dec.decodeReal(in, len)).doubleValue();
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this Real directly to an output stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1Real specific methods
   //*************************************************************************

   /**
    * Returns the content of this ASN1Real as a double.
    */
   public double getContent()
   {
      return content;
   }

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "REAL: "; // finish this
   }

}


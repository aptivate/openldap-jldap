/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1OctetString.java,v 1.7 2000/09/11 21:05:53 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 OCTET STRING type.
 */
public class ASN1OctetString extends ASN1Simple {

   private byte[] content;

   /**
    * ASN.1 OCTET STRING tag definition.
    */
   public static final int TAG = 0x04;

   //*************************************************************************
   // Constructors for ASN1OctetString
   //*************************************************************************

   /**
    * Constructs an ASN1OctetString object using a byte array value.
    *
    * @param content Non-null byte array value.
    */
   public ASN1OctetString(byte[] content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1OctetString object using a String value.
    *
    * @param content Non-null String value.
    */
   public ASN1OctetString(String content)
   {
      //this(content.getBytes());
      //content must be converted to utf8 data
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      try {
        this.content = content.getBytes("UTF8");
      }
      catch(UnsupportedEncodingException uee) {
      }
   }

   /**
    * Constructs an ASN1OctetString object by decoding data from an input
    * stream.
    */
   public ASN1OctetString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);

      content = (len>0) ? (byte[])dec.decodeOctetString(in, len)
                        : new byte[0];
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1OctetString directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1OctetString specific methods
   //*************************************************************************

   /**
    * Returns the content of this ASN1OctetString as a byte array.
    */
   public byte[] getContent()
   {
      return content;
   }

   /**
    * Returns a String representation of this ASN1OctetString.
    */
   public String getString()
   {
      String s = null;
      try {
         s = new String(content, "UTF8");
      }
      catch(UnsupportedEncodingException uee) {
      }
      return s;
   }

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "OCTET STRING: " + getString();
   }

}



package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1OctetString extends ASN1Simple {

   private byte[] content;

   //*************************************************************************
   // Constructors for ASN1OctetString
   //*************************************************************************

   /**
    * Constructs an ASN1OctetString object using a byte array value.
    */
   public ASN1OctetString(byte[] content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
                              OCTET_STRING);
      this.content = content;
   }

   /**
    * Constructs an ASN1OctetString object using a String value.
    */
   public ASN1OctetString(String content)
   {
      this(content.getBytes()); // UTF8 ???
   }

   /**
    * Constructs an ASN1OctetString object by decoding data from an input
    * stream.
    */
   public ASN1OctetString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
                              OCTET_STRING);

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
    * Returns a byte array representation of this ASN1OctetString.
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
      return new String(content); // UTF8 ???
   }

   /**
    * Return a String representation of this ASN1Object.
    */
   public String toString()
   {
      return super.toString() + "OCTET STRING: " + new String(content);
   }

}


/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

/**
 * This class provides LBER encoding routines for ASN.1 Types. LBER is a
 * subset of BER as described in the following taken from 5.1 of RFC 2251:
 *
 * <p>5.1. Mapping Onto BER-based Transport Services
 *
 * The protocol elements of LDAP are encoded for exchange using the
 * Basic Encoding Rules (BER) [11] of ASN.1 [3]. However, due to the
 * high overhead involved in using certain elements of the BER, the
 * following additional restrictions are placed on BER-encodings of LDAP
 * protocol elements:
 *
 * <li>(1) Only the definite form of length encoding will be used.
 *
 * <li>(2) OCTET STRING values will be encoded in the primitive form only.
 *
 * <li>(3) If the value of a BOOLEAN type is true, the encoding MUST have
 * its contents octets set to hex "FF".
 *
 * <li>(4) If a value of a type is its default value, it MUST be absent.
 * Only some BOOLEAN and INTEGER types have default values in this
 * protocol definition.
 *
 * <p>These restrictions do not apply to ASN.1 types encapsulated inside of
 * OCTET STRING values, such as attribute values, unless otherwise
 * noted.
 *
 * <p>[3] ITU-T Rec. X.680, "Abstract Syntax Notation One (ASN.1) -
 * Specification of Basic Notation", 1994.
 *
 * <p>[11] ITU-T Rec. X.690, "Specification of ASN.1 encoding rules: Basic,
 * Canonical, and Distinguished Encoding Rules", 1994.
 *
 */
public class LBEREncoder implements ASN1Encoder {

   /* Encoders for ASN.1 simple type Contents
    */

   /**
    * BER Encode an ASN1Boolean directly into the specified output stream.
    */
   public void encode(ASN1Boolean b, OutputStream out)
      throws IOException
   {
      /* Encode the id */
      encode(b.getIdentifier(), out);

      /* Encode the length */
      out.write(0x01);

      /* Encode the boolean content*/
      out.write(b.booleanValue() ? (byte) 0xff : (byte) 0x00);

      return;
   }

   /**
    * Encode an ASN1Numeric directly into the specified outputstream.
    *
    * <p>Use a two's complement representation in the fewest number of octets
    * possible.
    *
    * <p>Can be used to encode INTEGER and ENUMERATED values.
    */
   public final void encode(ASN1Numeric n, OutputStream out)
      throws IOException
   {
      byte[] octets = new byte[8];
      byte len;
      long value = n.longValue();
      long endValue = (value < 0) ? -1 : 0;
      long endSign = endValue & 0x80;

      for(len=0; len==0 || value != endValue || (octets[len-1] & 0x80) != endSign; len++) {
         octets[len] = (byte)(value & 0xFF);
         value >>= 8;
      }

      encode(n.getIdentifier(), out);
      out.write(len);                  // Length
      for(int i=len-1; i>=0; i--)      // Content
         out.write(octets[i]);
      return;
   }

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1Real directly to a stream.
   public void encode(ASN1Real r, OutputStream out)
      throws IOException
   {
      throw new IOException("LBEREncoder: Encode to a stream not implemented");
   }
   */

   /**
    * Encode an ASN1Null directly into the specified outputstream.
    */
   public final void encode(ASN1Null n, OutputStream out)
      throws IOException
   {
      encode(n.getIdentifier(), out);
      out.write(0x00);                 // Length (with no Content)
      return;
   }

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1BitString directly to a stream.
   public void encode(ASN1BitString bs, OutputStream out)
      throws IOException
   {
      throw new IOException("LBEREncoder: Encode to a stream not implemented");
   }
   */

   /**
    * Encode an ASN1OctetString directly into the specified outputstream.
    */
   public final void encode(ASN1OctetString os, OutputStream out)
      throws IOException
   {
      encode(os.getIdentifier(), out);
      encodeLength(os.byteValue().length, out);
      out.write(os.byteValue());
      return;
   }

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1ObjectIdentifier directly to a stream.
    * public void encode(ASN1ObjectIdentifier oi, OutputStream out)
    * throws IOException
    * {
    * throw new IOException("LBEREncoder: Encode to a stream not implemented");
    * }
    */

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1CharacterString directly to a stream.
    * public void encode(ASN1CharacterString cs, OutputStream out)
    * throws IOException
    * {
    * throw new IOException("LBEREncoder: Encode to a stream not implemented");
    * }
    */

   /* Encoders for ASN.1 structured types
    */

   /**
    * Encode an ASN1Structured into the specified outputstream.  This method
    * can be used to encode SET, SET_OF, SEQUENCE, SEQUENCE_OF
    */
   public final void encode(ASN1Structured c, OutputStream out)
      throws IOException
   {
      encode(c.getIdentifier(), out);

      ASN1Object[] value = c.toArray();

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      /* Cycle through each element encoding each element */
      for( int i=0; i < value.length; i++) {
         (value[i]).encode(this, output);
      }

      /* Encode the length */
      encodeLength(output.size(), out);

      /* Add each encoded element into the output stream */
      out.write(output.toByteArray());
      return;
   }

   /**
    * Encode an ASN1Tagged directly into the specified outputstream.
    */
   public final void encode(ASN1Tagged t, OutputStream out)
      throws IOException
   {
      if(t.isExplicit()) {
         encode(t.getIdentifier(), out);

         /* determine the encoded length of the base type. */
         ByteArrayOutputStream encodedContent = new ByteArrayOutputStream();
         t.taggedValue().encode(this, encodedContent);

         encodeLength(encodedContent.size(), out);
         out.write(encodedContent.toByteArray());
      }
      else {
         t.taggedValue().encode(this, out);
      }
      return;
   }

   /* Encoders for ASN.1 useful types
    */
   /* Encoder for ASN.1 Identifier
    */

   /**
    * Encode an ASN1Identifier directly into the specified outputstream.
    */
   public final void encode(ASN1Identifier id, OutputStream out)
      throws IOException
   {
      int c = id.getASN1Class();
      int t = id.getTag();
      byte ccf = (byte) ((c << 6) | (id.getConstructed() ? 0x20 : 0));

      if(t < 30) {
        /* single octet */
         out.write(ccf | t);
      }
      else {
        /* multiple octet */
         out.write(ccf | 0x1F);
         encodeTagInteger(t, out);
      }
      return;
   }

   /* Private helper methods
    */

   /*
    *  Encodes the specified length into the the outputstream
    */
   private final void encodeLength(int length, OutputStream out)
      throws IOException
   {
      if(length < 0x80) {
         out.write(length);
      }

      else {
         byte[] octets = new byte[4]; // 4 bytes sufficient for 32 bit int.
         byte n;
         for(n=0; length != 0; n++) {
            octets[n] = (byte)(length & 0xFF);
            length >>= 8;
         }

         out.write(0x80 | n);

         for(int i=n-1; i>=0; i--)
            out.write(octets[i]);
      }
      return;
   }

   /**
    * Encodes the provided tag into the outputstream.
    */
   private final void encodeTagInteger(int value, OutputStream out)
      throws IOException
   {
      byte[] octets = new byte[5];
      int n;
      for(n=0; value != 0; n++) {
         octets[n] = (byte)(value & 0x7F);
         value = value >> 7;
      }
      for(int i=n-1; i>0; i--) {
         out.write(octets[i] | 0x80);
      }
      out.write(octets[0]);
      return;
   }
}

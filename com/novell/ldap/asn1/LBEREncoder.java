/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/LBEREncoder.java,v 1.6 2001/03/01 00:30:04 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;
import com.novell.ldap.client.ArrayList;

/**
 * This class provides LBER encoding routines for ASN.1 Types. LBER is a
 * subset of BER as described in the following taken from 5.1 of RFC 2251:
 *
 * 5.1. Mapping Onto BER-based Transport Services
 *
 *  The protocol elements of LDAP are encoded for exchange using the
 *  Basic Encoding Rules (BER) [11] of ASN.1 [3]. However, due to the
 *  high overhead involved in using certain elements of the BER, the
 *  following additional restrictions are placed on BER-encodings of LDAP
 *  protocol elements:
 *
 *  (1) Only the definite form of length encoding will be used.
 *
 *  (2) OCTET STRING values will be encoded in the primitive form only.
 *
 *  (3) If the value of a BOOLEAN type is true, the encoding MUST have
 *      its contents octets set to hex "FF".
 *
 *  (4) If a value of a type is its default value, it MUST be absent.
 *      Only some BOOLEAN and INTEGER types have default values in this
 *      protocol definition.
 *
 *  These restrictions do not apply to ASN.1 types encapsulated inside of
 *  OCTET STRING values, such as attribute values, unless otherwise
 *  noted.
 *
 *  [3] ITU-T Rec. X.680, "Abstract Syntax Notation One (ASN.1) -
 *      Specification of Basic Notation", 1994.
 *
 *  [11] ITU-T Rec. X.690, "Specification of ASN.1 encoding rules: Basic,
 *      Canonical, and Distinguished Encoding Rules", 1994.
 *
 */
public class LBEREncoder implements ASN1Encoder {

   //*************************************************************************
   // Encoders for ASN.1 simple type Contents
   //*************************************************************************

   /**
    * Encode an ASN1Boolean directly to a stream.
    */
   public void encode(ASN1Boolean b, OutputStream out)
      throws IOException
   {
      encode(b.getIdentifier(), out);
      out.write(0x01); // length
      out.write(b.getContent() ? (byte) 0xff : (byte) 0x00);
      return;
   }

   /**
    * Encode an ASN1Numeric directly to a stream.
    *
    * Use a two's complement representation in the fewest number of octets
    * possible.
    *
    * Will encode INTEGER and ENUMERATED values.
    */
   public void encode(ASN1Numeric n, OutputStream out)
      throws IOException
   {
      byte[] octets = new byte[8];
      byte len;
      long value = n.getLong();
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
    * Encode an ASN1Null directly to a stream.
    */
   public void encode(ASN1Null n, OutputStream out)
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
    * Encode an ASN1OctetString directly to a stream.
    */
   public void encode(ASN1OctetString os, OutputStream out)
      throws IOException
   {
      encode(os.getIdentifier(), out);
      encodeLength(os.getContent().length, out);
      out.write(os.getContent());
      return;
   }

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1ObjectIdentifier directly to a stream.
   public void encode(ASN1ObjectIdentifier oi, OutputStream out)
      throws IOException
   {
      throw new IOException("LBEREncoder: Encode to a stream not implemented");
   }
   */
   

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1CharacterString directly to a stream.
   public void encode(ASN1CharacterString cs, OutputStream out)
      throws IOException
   {
      throw new IOException("LBEREncoder: Encode to a stream not implemented");
   }
   */
    
   //*************************************************************************
   // Encoders for ASN.1 structured types
   //*************************************************************************

   /**
    * Encode an ASN1Structured directly to a stream.
    *
    * Will encode SET, SET_OF, SEQUENCE, SEQUENCE_OF
    */
   public void encode(ASN1Structured c, OutputStream out)
      throws IOException
   {
      encode(c.getIdentifier(), out);

      ArrayList value = c.getContent();
      ArrayList codes = new ArrayList(value.size());
      int length = 0;

      for( int i=0; i < value.size(); i++) {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         ((ASN1Object)value.get(i)).encode(this, output);
         codes.add(output);
         length += output.size();
      }

      encodeLength(length, out);     // Length

      for( int i=0; i< codes.size(); i++) {
          ByteArrayOutputStream output = (ByteArrayOutputStream)codes.get(i);
          out.write(output.toByteArray());
      }
      return;
   }

   /**
    * Encode an ASN1Tagged directly to a stream.
    */
   public void encode(ASN1Tagged t, OutputStream out)
      throws IOException
   {
      if(t.isExplicit()) {
         encode(t.getIdentifier(), out);

         // determine the encoded length of the base type.
         ByteArrayOutputStream encodedContent = new ByteArrayOutputStream();
         t.getContent().encode(this, encodedContent);

         encodeLength(encodedContent.size(), out);
         out.write(encodedContent.toByteArray());
      }
      else {
         t.getContent().encode(this, out);
      }
      return;
   }

   //*************************************************************************
   // Encoders for ASN.1 useful types
   //*************************************************************************

   //*************************************************************************
   // Encoder for ASN.1 Identifier
   //*************************************************************************

   /**
    * Encode an ASN1Identifier directly to a stream.
    */
   public void encode(ASN1Identifier id, OutputStream out)
      throws IOException
   {
      int c = id.getASN1Class();
      int t = id.getTag();
      byte ccf = (byte) ((c << 6) | (id.getConstructed() ? 0x20 : 0));
      if(t < 30) { // single octet
         out.write(ccf | t);
      }
      else {  // multiple octet
         out.write(ccf | 0x1F);
         encodeTagInteger(t, out);
      }
      return;
   }

   //*************************************************************************
   // Helper methods
   //*************************************************************************

   /**
    *
    */
   private void encodeLength(int length, OutputStream out)
      throws IOException
   {
      if(length < 0x80) {
         out.write((byte)length);
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
    *
    */
   private void encodeTagInteger(int value, OutputStream out)
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

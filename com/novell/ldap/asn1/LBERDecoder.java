/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/LBERDecoder.java,v 1.6 2000/09/11 21:05:54 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * This class provides LBER decoding routines for ASN.1 Types. LBER is a
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
public class LBERDecoder implements ASN1Decoder
{

   //*************************************************************************
   // Generic decode routines
   //*************************************************************************

   /**
    * Decode an LBER encoded value into an ASN1Type from a byte array.
    */
   public ASN1Object decode(byte[] value)
   {
      ASN1Object asn1 = null;

      ByteArrayInputStream in = new ByteArrayInputStream(value);
      try {
         asn1 = decode(in);
      }
      catch(IOException ioe) {
      }
      return asn1;
   }

   /**
    * Decode an LBER encoded value into an ASN1Type from an InputStream.
    */
   public ASN1Object decode(InputStream in)
      throws IOException
   {
      int[] len = new int[1];
      return decode(in, len);
   }

   /**
    * Decode an LBER encoded value into an ASN1Object from an InputStream.
    *
    * Will return the total length of this encoded ASN1Object (length of type
    * + length of length + length of content) in the parameter len.
    * This information is helpful when decoding structured types.
    */
   public ASN1Object decode(InputStream in, int len[])
      throws IOException
   {
      ASN1Identifier asn1ID = new ASN1Identifier(in);
      ASN1Length asn1Len = new ASN1Length(in);
      int length = asn1Len.getLength();
      len[0] = asn1ID.getEncodedLength() +
               asn1Len.getEncodedLength() +
               length;

      if(asn1ID.isUniversal()) {
         switch(asn1ID.getTag()) {
            case ASN1Boolean.TAG:
               return new ASN1Boolean(this, in, length);
            case ASN1Integer.TAG:
               return new ASN1Integer(this, in, length);
            case ASN1OctetString.TAG:
               return new ASN1OctetString(this, in, length);
            case ASN1Enumerated.TAG:
               return new ASN1Enumerated(this, in, length);
            case ASN1Null.TAG:
               return new ASN1Null(); // has no content to decode.
            case ASN1BitString.TAG:
               return new ASN1BitString(this, in, length);
            case ASN1ObjectIdentifier.TAG:
               return new ASN1ObjectIdentifier(this, in, length);
            case ASN1Real.TAG:
               return new ASN1Real(this, in, length);
            case ASN1NumericString.TAG:
               return new ASN1NumericString(this, in, length);
            case ASN1PrintableString.TAG:
               return new ASN1PrintableString(this, in, length);
            case ASN1TeletexString.TAG:
               return new ASN1TeletexString(this, in, length);
            case ASN1VideotexString.TAG:
               return new ASN1VideotexString(this, in, length);
            case ASN1IA5String.TAG:
               return new ASN1IA5String(this, in, length);
            case ASN1GraphicString.TAG:
               return new ASN1GraphicString(this, in, length);
            case ASN1VisibleString.TAG:
               return new ASN1VisibleString(this, in, length);
            case ASN1GeneralString.TAG:
               return new ASN1GeneralString(this, in, length);
            case ASN1Sequence.TAG:
               return new ASN1Sequence(this, in, length);
            case ASN1Set.TAG:
               return new ASN1Set(this, in, length);
            default:
               throw new EOFException("Unknown tag"); // !!! need a better exception
         }
      }
      else { // APPLICATION or CONTEXT-SPECIFIC tag
         return new ASN1Tagged(this, in, length, asn1ID);
      }
   }

   //*************************************************************************
   // Decoders for ASN.1 simple type Contents
   //*************************************************************************

   /**
    * Decode a boolean directly from a stream.
    */
   public Object decodeBoolean(InputStream in, int len)
      throws IOException
   {
      byte[] lber = new byte[len];

      int i = in.read(lber);

      if(i != len)
         throw new EOFException("LBER: BOOLEAN: decode error: EOF");

      return (lber[0] == 0x00) ? new Boolean(false) : new Boolean(true);
   }

   /**
    * Decode a Numeric type directly from a stream.
    *
    * Decodes INTEGER and ENUMERATED types.
    */
   public Object decodeNumeric(InputStream in, int len)
      throws IOException
   {
      long l = 0;
      int r = in.read();

      if(r < 0)
         throw new EOFException("LBER: NUMERIC: decode error: EOF");

      if((r & 0x80) != 0) { // check for negative number
         l = -1;
      }

      l = (l << 8) | r;

      for(int i=1; i<len; i++) {
         r = in.read();
         if(r < 0)
            throw new EOFException("LBER: NUMERIC: decode error: EOF");
         l = (l << 8) | r;
      }
      return new Long(l);
   }

   /**
    * Decode a Real directly from a stream.
    */
   public Object decodeReal(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode a BitString directly from a stream.
    */
   public Object decodeBitString(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode an OctetString directly from a stream.
    */
   public Object decodeOctetString(InputStream in, int len)
      throws IOException
   {
      byte[] octets = new byte[len];

      for(int i=0; i<len; i++) {
         int ret = in.read(); // blocks
         if(ret == -1)
            throw new EOFException("LBER: OCTET STRING: decode error: EOF");
         octets[i] = (byte)ret;
      }

      return octets;
   }

   /**
    * Decode an ObjectIdentifier directly from a stream.
    */
   public Object decodeObjectIdentifier(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode a CharacterString directly from a stream.
    */
   public Object decodeCharacterString(InputStream in, int len)
      throws IOException
   {
      byte[] octets = new byte[len];

      for(int i=0; i<len; i++) {
			int ret = in.read(); // blocks
         if(ret == -1)
            throw new EOFException(
               "LBER: CHARACTER STRING: decode error: EOF");
         octets[i] = (byte)ret;
      }

      return new String(octets, "UTF8");
   }

   //*************************************************************************
   // Decoders for ASN.1 useful type Contents
   //*************************************************************************

   /**
    * Decode a GeneralizedTime directly from a stream.
    */
   public Object decodeGeneralizedTime(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode a UniversalTime directly from a stream.
    */
   public Object decodeUniversalTime(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode an External directly from a stream.
    */
   public Object decodeExternal(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   /**
    * Decode an ObjectDescriptor directly from a stream.
    */
   public Object decodeObjectDescriptor(InputStream in, int len)
      throws IOException
   {
      return null;
   }

   //*************************************************************************
   // Helper methods for the BERDecoder class
   //*************************************************************************

   /* 
    * Decodes an ASN1Identifier.
    *
    * @param len An int array in which the length of this Identifier is
    *            returned.
    */
/*
   private int decodeIdentifier(InputStream in, int[] len)
      throws IOException
   {
      int r = in.read();
      len[0]++; // increment return length
      if(r < 0)
         throw new EOFException("BERDecoder: decode: EOF in Identifier");
      int ccf = r >> 5;    // save CLASS & FORM bits.
      int tag = r & 0x1F;  // single or multiple octet variant?
      if(tag == 0x1F)      // if true, its a multiple octet identifier.
         tag = decodeTagNumber(in, len);
      return (ccf << asn1ID.FORM_SHIFT) | tag;
   }
*/ 

   /* 
    * In the case that we have a tag number that is greater than 30, we need
    * to decode a multiple octet tag number.
    */
/*
   private int decodeTagNumber(InputStream in, int[] len)
      throws IOException
   {
      int n = 0;
      while(true) {
         int r = in.read();
         len[0]++;  // increment return length
         if(r < 0)
            throw new EOFException("BERDecoder: decode: EOF in tag number");
         n = (n<<7) + (r & 0x7F);
         if((r & 0x80) == 0)
            break;
      }
      return n;
   }
*/ 

   /* 
    *
    */
/*
   private int decodeLength(InputStream in, int[] len)
      throws IOException
   {
      int r = in.read();
      len[0]++; // increment return length
      if(r == 128)
         return -1;
      if(r < 128)
         return r;

      int l=0;
      for(r = r & 0x7F; r > 0; r--) {
         int part = in.read();
         len[0]++; // increment return length
         if(part < 0)
            throw new EOFException("BERDecoder: decode: EOF in length");
         l = (l << 8) + part;
      }
      return l;
   }
*/

}


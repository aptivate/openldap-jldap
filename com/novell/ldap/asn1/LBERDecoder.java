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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * This class provides LBER decoding routines for ASN.1 Types. LBER is a
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
public class LBERDecoder implements ASN1Decoder
{
    //used to speed up decode, so it doesn't need to recreate an identifier every time
    //instead just reset is called CANNOT be static for multiple connections
    private ASN1Identifier   asn1ID  = new ASN1Identifier();
    private ASN1Length       asn1Len = new ASN1Length();


   /* Generic decode routines
    */

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
    * <p> This method also returns the total length of this encoded
    * ASN1Object (length of type + length of length + length of content)
    * in the parameter len. This information is helpful when decoding
    * structured types.
    */
   public ASN1Object decode(InputStream in, int len[])
      throws IOException
   {
      asn1ID.reset(in);
      asn1Len.reset(in);

      int length = asn1Len.getLength();
      len[0] = asn1ID.getEncodedLength() +
               asn1Len.getEncodedLength() +
               length;

      if(asn1ID.isUniversal()) {
         switch(asn1ID.getTag()) {
            case ASN1Sequence.TAG:
               return new ASN1Sequence(this, in, length);
            case ASN1Set.TAG:
               return new ASN1Set(this, in, length);
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
            /* ASN1 TYPE NOT YET SUPPORTED
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
            */

            default:
               throw new EOFException("Unknown tag"); // !!! need a better exception
         }
      }
      else { // APPLICATION or CONTEXT-SPECIFIC tag
         return 
          new ASN1Tagged(this, in, length, (ASN1Identifier)asn1ID.clone());
      }
   }

   /* Decoders for ASN.1 simple type Contents
    */

   /**
    * Decode a boolean directly from a stream.
    */
   public final Object decodeBoolean(InputStream in, int len)
      throws IOException
   {
      byte[] lber = new byte[len];

      int i = in.read(lber);

      if(i != len)
         throw new EOFException("LBER: BOOLEAN: decode error: EOF");

      return (lber[0] == 0x00) ? new Boolean(false) : new Boolean(true);
   }

   /**
    * Decode a Numeric type directly from a stream. Decodes INTEGER
    * and ENUMERATED types.
    */
   public final Object decodeNumeric(InputStream in, int len)
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
    * Decode an OctetString directly from a stream.
    */
   public final Object decodeOctetString(InputStream in, int len)
      throws IOException
   {
      byte[] octets = new byte[len];
      int totalLen = 0;
    
      while( totalLen < len) {  // Make sure we have read all the data
         int inLen = in.read(octets, totalLen, len - totalLen);
         totalLen += inLen;
      }

      return octets;
   }

   /**
    * Decode a CharacterString directly from a stream.
    */
   public final Object decodeCharacterString(InputStream in, int len)
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
}

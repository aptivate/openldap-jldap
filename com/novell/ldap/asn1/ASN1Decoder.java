/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Decoder.java,v 1.5 2001/03/01 00:29:59 cmorris Exp $
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

/**
 * This interface defines the methods for decoding each of the ASN.1 types.
 *
 * Decoders which implement this interface may be used to decode any of the
 * ASN1Object data types.
 */
public interface ASN1Decoder extends Serializable {

   /**
    * Decode an encoded value into an ASN1Object from a byte array.
    */
   public ASN1Object decode(byte[] value);

   /**
    * Decode an encoded value into an ASN1Object from an InputStream.
    */
   public ASN1Object decode(InputStream in)
      throws IOException;

   /**
    * Decode an encoded value into an ASN1Object from an InputStream.
    *
    * @param length The decoded components encoded length. This value is
    *               handy when decoding structured types. It allows you
    *               to accumulate the number of bytes decoded, so you know
    *               when the structured type has decoded all of its
    *               components.
    */
   public ASN1Object decode(InputStream in, int[] length)
      throws IOException;

   //*************************************************************************
   // Decoders for ASN.1 simple types
   //*************************************************************************

   /**
    * Decode a BOOLEAN directly from a stream.
    */
   public Object decodeBoolean(InputStream in, int len)
      throws IOException;

   /**
    * Decode a Numeric value directly from a stream.
    *
    * Decodes INTEGER and ENUMERATED types.
    */
   public Object decodeNumeric(InputStream in, int len)
      throws IOException;

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a REAL directly from a stream.
   public Object decodeReal(InputStream in, int len)
      throws IOException;
    */
    
   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a BIT_STRING directly from a stream.
   public Object decodeBitString(InputStream in, int len)
      throws IOException;
    */

   /**
    * Decode an OCTET_STRING directly from a stream.
    */
   public Object decodeOctetString(InputStream in, int len)
      throws IOException;

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an OBJECT_IDENTIFIER directly from a stream.
   public Object decodeObjectIdentifier(InputStream in, int len)
      throws IOException;
    */

   /**
    * Decode a CharacterString directly from a stream.
    *
    * Decodes any of the specialized character strings.
    */
   public Object decodeCharacterString(InputStream in, int len)
      throws IOException;

   //*************************************************************************
   // No Decoders for ASN.1 structured types. A structured type's value is a
   // collection of other types.
   //*************************************************************************

   //*************************************************************************
   // Decoders for ASN.1 useful types
   //*************************************************************************

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a GENERALIZED_TIME directly from a stream.
   public Object decodeGeneralizedTime(InputStream in, int len)
      throws IOException;
     */

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a UNIVERSAL_TIME directly from a stream.
   public Object decodeUniversalTime(InputStream in, int len)
      throws IOException;
      */

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an EXTERNAL directly from a stream.
   public Object decodeExternal(InputStream in, int len)
      throws IOException;
    */
      

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an OBJECT_DESCRIPTOR directly from a stream.
   public Object decodeObjectDescriptor(InputStream in, int len)
      throws IOException;
    */
      
}


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

import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;

/**
 * This interface defines the methods for decoding each of the ASN.1 types.
 *
 * Decoders which implement this interface may be used to decode any of the
 * ASN1Object data types.
 *
 * <p>This package also provides the BERDecoder class that can be used to 
 * BER decode ASN.1 classes.  However an application might chose to use 
 * its own decoder class.
 *
 * <p>This interface thus allows an application to use this package to
 * decode ASN.1 objects using other decoding rules if needed.  
 * 
 *<p>Note that LDAP packets are required to be BER encoded. Since this package
 * includes a BER decoder no application provided decoder is needed for 
 * building LDAP packets.
 */
public interface ASN1Decoder extends Serializable {

   /**
    * Decode an encoded value into an ASN1Object from a byte array.
    *
    * @param value A byte array that points to the encoded ASN1 data
    */
   public ASN1Object decode(byte[] value);

   
   /**
    * Decode an encoded value into an ASN1Object from an InputStream.
    *
    * @param in An input stream containig the encoded ASN.1 data.
    */
   public ASN1Object decode(InputStream in)
      throws IOException;

   
   /**
    * Decode an encoded value into an ASN1Object from an InputStream.
    *
    * @param length The decoded components encoded length. This value is
    * handy when decoding structured types. It allows you to accumulate 
    * the number of bytes decoded, so you know when the structured 
    * type has decoded all of its components.<br>
    *
    * @param in An input stream containig the encoded ASN.1 data.
    */
   public ASN1Object decode(InputStream in, int[] length)
      throws IOException;

   /* Decoders for ASN.1 simple types
    */

   /**
    * Decode a BOOLEAN directly from a stream. Call this method when you
    * know that the next ASN.1 encoded element is a BOOLEAN
    *
    * @param in An input stream containig the encoded ASN.1 data.<br>
    *
    * @param len Length in bytes
    */
   public Object decodeBoolean(InputStream in, int len)
      throws IOException;

   /**
    * Decode a Numeric value directly from a stream.  Call this method when you
    * know that the next ASN.1 encoded element is a Numeric
    *
    * <p>Can be used to decodes INTEGER and ENUMERATED types.
    *
    * @param in An input stream containig the encoded ASN.1 data.<br>
    *
    * @param len Length in bytes    
    */
   public Object decodeNumeric(InputStream in, int len)
      throws IOException;

   
   
   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a REAL directly from a stream.
    * public Object decodeReal(InputStream in, int len)
    * throws IOException;
    */
   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a BIT_STRING directly from a stream.
    * public Object decodeBitString(InputStream in, int len)
    * throws IOException;
    */



   /**
    * Decode an OCTET_STRING directly from a stream. Call this method when you
    * know that the next ASN.1 encoded element is a OCTET_STRING.
    *
    * @param in An input stream containig the encoded ASN.1 data.<br>
    *
    * @param len Length in bytes    
    */
   public Object decodeOctetString(InputStream in, int len)
      throws IOException;



   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an OBJECT_IDENTIFIER directly from a stream.
    * public Object decodeObjectIdentifier(InputStream in, int len)
    * throws IOException;
    */
    
    
    
   /**
    * Decode a CharacterString directly from a stream.
    *
    * Decodes any of the specialized character strings.
    *
    * @param in An input stream containig the encoded ASN.1 data.
    *
    * @param len Length in bytes    
    */
   public Object decodeCharacterString(InputStream in, int len)
      throws IOException;

   /* No Decoders for ASN.1 structured types. A structured type's value is a
    * collection of other types.
    */
    

   /* Decoders for ASN.1 useful types
    */

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a GENERALIZED_TIME directly from a stream.
    * public Object decodeGeneralizedTime(InputStream in, int len)
    * throws IOException;
    */

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode a UNIVERSAL_TIME directly from a stream.
    * public Object decodeUniversalTime(InputStream in, int len)
    * throws IOException;
    */

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an EXTERNAL directly from a stream.
    * public Object decodeExternal(InputStream in, int len)
    * throws IOException;
    */
      

   /* ASN1 TYPE NOT YET SUPPORTED  
    * Decode an OBJECT_DESCRIPTOR directly from a stream.
    * public Object decodeObjectDescriptor(InputStream in, int len)
    * throws IOException;
    */
      
}


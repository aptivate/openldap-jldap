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
import java.io.IOException;
import java.io.OutputStream;

/**
 * This interface defines the methods for encoding each of the ASN.1 types.
 *
 * Encoders which implement this interface may be used to encode any of the
 * ASN1Object data types.
 *
 * <p>This package also provides the BEREncoder class that can be used to 
 * BER encode ASN.1 classes.  However an application might chose to use 
 * its own encoder class.
 *
 * <p>This interface thus allows an application to use this package to
 * encode ASN.1 objects using other encoding rules if needed.  
 * 
 *<p>Note that LDAP packets are required to be BER encoded. Since this package
 * includes a BER encoder no application provided encoder is needed for 
 * building LDAP packets.
 */
public interface ASN1Encoder extends Serializable {

   /* Encoders for ASN.1 simple types */

   /**
    * Encode an ASN1Boolean directly into the provided output stream.
    *
    * @param b The ASN1Boolean object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>    
    */
   public void encode(ASN1Boolean b, OutputStream out)
      throws IOException;

   /**
    * Encode an ASN1Numeric directly to a stream.
    *
    * <p>Use a two's complement representation in the fewest number of octets
    * possible.
    *
    * Can be used to encode both INTEGER and ENUMERATED values.<br>
    *
    * @param n The ASN1Numeric object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>
    */
   public void encode(ASN1Numeric n, OutputStream out)
      throws IOException;

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1Real directly to a stream.
    * public void encode(ASN1Real r, OutputStream out)
    * throws IOException;
    */
    
   /**
    * Encode an ASN1Null directly to a stream.
    *
    * @param n The ASN1Null object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>    
    */
   public void encode(ASN1Null n, OutputStream out)
      throws IOException;

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1BitString directly to a stream.
    * public void encode(ASN1BitString bs, OutputStream out)
    * throws IOException;
    */      

   /**
    * Encode an ASN1OctetString directly to a stream.
    *
    * @param os The ASN1OctetString object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>     
    */
   public void encode(ASN1OctetString os, OutputStream out)
      throws IOException;

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1ObjectIdentifier directly to a stream.
    * public void encode(ASN1ObjectIdentifier oi, OutputStream out)
    * throws IOException;
    */

   /* ASN1 TYPE NOT YET SUPPORTED
    * Encode an ASN1CharacterString directly to a stream.
    * public void encode(ASN1CharacterString cs, OutputStream out)
    * throws IOException;
    */
    
   /* Encoder for ASN.1 structured types
    */

   /**
    * Encode an ASN1Structured directly to a stream.
    *
    * @param c The ASN1Structured object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>  
    */
   public void encode(ASN1Structured c, OutputStream out)
      throws IOException;

   /**
    * Encode an ASN1Tagged directly to a stream.
    *
    * @param t The ASN1Tagged object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>      
    */
   public void encode(ASN1Tagged t, OutputStream out)
      throws IOException;

   /* Encoders for ASN.1 useful types
    */

   /* Encoder for ASN.1 Identifier
    */

   /**
    * Encode an ASN1Identifier directly to a stream.
    *
    * @param id The ASN1Identifier object to encode<br>
    *
    * @param out The output stream onto which the ASN.1 object is 
    * to be encoded<br>      
    */    
   public void encode(ASN1Identifier id, OutputStream out)
      throws IOException;
}


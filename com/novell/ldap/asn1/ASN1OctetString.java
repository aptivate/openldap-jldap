/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1OctetString.java,v 1.11 2001/04/16 18:24:08 javed Exp $
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
 * This class encapsulates the OCTET STRING type.
 */
public class ASN1OctetString extends ASN1Simple {

   private byte[] content;

   /**
    * ASN.1 OCTET STRING tag definition.
    */
   public static final int TAG = 0x04;

   /**
    * ID is added for Optimization. id needs only be one Value for every instance
    * Thus we create it only once.
    */
   protected static final ASN1Identifier ID =
         new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
   /* Constructors for ASN1OctetString
    */

   /**
    * Call this constructor to construct an ASN1OctetString
    * object from a byte array.
    *
    * @param content A byte array representing the string that
    * will be contained in the this ASN1OctetString object
    */
   public ASN1OctetString(byte[] content)
   {
      id = ID;
      this.content = content;
   }


   /**
    * Call this constructor to construct an ASN1OctetString
    * object from a String object.
    *
    * @param content A string value that will be contained
    * in the this ASN1OctetString object
    */
   public ASN1OctetString(String content)
   {
      //this(content.getBytes());
      //content must be converted to utf8 data
      id = ID;
      try {
        this.content = content.getBytes("UTF8");
      }
      catch(UnsupportedEncodingException uee) {
      }
   }


   /**
    * Constructs an ASN1OctetString object by decoding data from an
    * input stream.
    *
    * @param dec The decoder object to use when decoding the
    * input stream.  Sometimes a developer might want to pass
    * in his/her own decoder object<br>
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public ASN1OctetString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = ID;
      content = (len>0) ? (byte[])dec.decodeOctetString(in, len)
                        : new byte[0];
   }


   /* ASN1Object implementation
    */

   /**
    * Call this method to encode the current instance into the
    * specified output stream using the specified encoder object.
    *
    * @param enc Encoder object to use when encoding self.<br>
    *
    * @param out The output stream onto which the encoded byte
    * stream is written.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }


   /*ASN1OctetString specific methods
    */

   /**
    * Returns the content of this ASN1OctetString as a byte array.
    */
   public byte[] getContent()
   {
      return content;
   }


   /**
    * Returns the content of this ASN1OctetString as a String.
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


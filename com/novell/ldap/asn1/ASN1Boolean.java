/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Boolean.java,v 1.6 2001/03/01 00:29:58 cmorris Exp $
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
 * This class encapsulates the ASN.1 BOOLEAN type.
 */
public class ASN1Boolean extends ASN1Simple {

   private boolean content;

   /**
    * ASN.1 BOOLEAN tag definition.
    */
   public static final int TAG = 0x01;

   /* Constructors for ASN1Boolean
    */

   /**
    * Call this constructor to construct an ASN1Boolean 
    * object from a boolean value.
    *
    * @param content The boolean value to be contained in the
    * this ASN1Boolean object
    */
   public ASN1Boolean(boolean content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1Boolean object by decoding data from an 
    * input stream.
    *
    * @param dec The decoder object to use when decoding the
    * input stream.  Sometimes a developer might want to pass
    * in his/her own decoder object<br>
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public ASN1Boolean(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = ((Boolean)dec.decodeBoolean(in, len)).booleanValue();
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

   /* ASN1Boolean specific methods
    */
   
   /**
    * Returns the content of this ASN1Boolean as a boolean.
    */
   public boolean getContent()
   {
      return content;
   }

   /**
    * Returns a String representation of this ASN1Boolean object.
    */
   public String toString()
   {
      return super.toString() + "BOOLEAN: " + content;
   }

}


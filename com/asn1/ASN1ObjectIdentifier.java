/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1ObjectIdentifier.java,v 1.4 2000/09/11 21:05:53 vtag Exp $
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
 * Represents the ASN.1 OBJECT IDENTIFIER type.
 */
public class ASN1ObjectIdentifier extends ASN1Simple {

   private byte[] content;

   /**
    * ASN.1 OBJECT IDENTIFIER tag definition.
    */
   public static final int TAG = 0x06;

   //*************************************************************************
   // Constructors for ASN1ObjectIdentifier
   //*************************************************************************

   /**
    * Constructs an ASN1ObjectIdentifier object using a byte array.
    */
   public ASN1ObjectIdentifier(byte[] content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1ObjectIdentifier object by decoding data from an input
    * stream.
    */
   public ASN1ObjectIdentifier(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (byte[])dec.decodeObjectIdentifier(in, len);
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1ObjectIdentifier directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1ObjectIdentifier specific methods
   //*************************************************************************

   /**
    * Returns the content of this ASN1ObjectIdentifier as a byte array.
    */
   public byte[] getContent()
   {
      return content;
   }

   /**
    * Return a String representation of this ASN1ObjectIdentifier.
    */
   public String toString()
   {
      return super.toString() + "OBJECT IDENTIFIER: "; // finish this
   }

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Enumerated.java,v 1.7 2001/03/01 00:29:59 cmorris Exp $
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
 * Represents the ASN.1 ENUMERATE type.
 */
public class ASN1Enumerated extends ASN1Numeric {

   /**
    * ASN.1 tag definition for ENUMERATED
    */
   public static final int TAG = 0x0a;

   //*************************************************************************
   // Constructors for ASN1Enumerated
   //*************************************************************************

   /**
    * Constructs an ASN1Enumerated object using an int value.
    */
   public ASN1Enumerated(int content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = new Long(content);
   }

   /**
    * Constructs an ASN1Enumerated object using a long value.
    */
   public ASN1Enumerated(long content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = new Long(content);
   }

   /**
    * Constructs an ASN1Enumerated object by decoding data from an input
    * stream.
    */
   public ASN1Enumerated(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (Long)dec.decodeNumeric(in, len);
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1Enumerated directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   /**
    * Return a String representation of this ASN1Enumerated.
    */
   public String toString()
   {
      return super.toString() + "ENUMERATED: " + content;
   }
}

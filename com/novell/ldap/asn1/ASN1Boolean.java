/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1Boolean.java,v 1.4 2000/09/03 19:55:54 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 BOOLEAN type.
 */
public class ASN1Boolean extends ASN1Simple {

   private boolean content;

   /**
    * ASN.1 BOOLEAN tag definition.
    */
   public static final int TAG = 0x01;

   //*************************************************************************
   // Constructors for ASN1Boolean
   //*************************************************************************

   /**
    * Constructs an ASN1Boolean object from a boolean value.
    */
   public ASN1Boolean(boolean content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1Boolean object by decoding data from an input stream.
    */
   public ASN1Boolean(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = ((Boolean)dec.decodeBoolean(in, len)).booleanValue();
   }

   //*************************************************************************
   // ASN1Object implementation
   //*************************************************************************

   /**
    * Encodes the contents of this ASN1Boolean directly to an output stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   //*************************************************************************
   // ASN1Boolean specific methods
   //*************************************************************************

   /**
    * Returns the content of this ASN1Boolean as a boolean.
    */
   public boolean getContent()
   {
      return content;
   }

   /**
    * Returns a String representation of this ASN1Boolean.
    */
   public String toString()
   {
      return super.toString() + "BOOLEAN: " + content;
   }

}


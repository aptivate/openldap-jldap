/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1VisibleString.java,v 1.3 2000/09/03 06:43:08 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 VisibleString type.
 */
public class ASN1VisibleString extends ASN1CharacterString {

   /**
    * ASN.1 VisibleString tag definition.
    */
   public static final int TAG = 0x1a;

   //*************************************************************************
   // Constructors for ASN1VisibleString
   //*************************************************************************

   /**
    * Constructs an ASN1VisibleString object using a String value.
    */
   public ASN1VisibleString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1VisibleString object by decoding data from an input
    * stream.
    */
   public ASN1VisibleString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1VisibleString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1VisibleString.
    */
   public String toString()
   {
      return super.toString() + "VisibleString: " + content;
   }

}


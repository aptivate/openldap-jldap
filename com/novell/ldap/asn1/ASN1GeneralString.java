/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1GeneralString.java,v 1.3 2000/09/03 06:43:07 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;

/**
 * Represents the ASN.1 GeneralString type.
 */
public class ASN1GeneralString extends ASN1CharacterString {

   /**
    * ASN.1 GeneralString tag definition.
    */
   public static final int TAG = 0x1b;

   //*************************************************************************
   // Constructors for ASN1GeneralString
   //*************************************************************************

   /**
    * Constructs an ASN1GeneralString object using a String value.
    */
   public ASN1GeneralString(String content)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      this.content = content;
   }

   /**
    * Constructs an ASN1GeneralString object by decoding data from an input
    * stream.
    */
   public ASN1GeneralString(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
      content = (String)dec.decodeCharacterString(in, len);
   }

   //*************************************************************************
   // ASN1GeneralString specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1GeneralString.
    */
   public String toString()
   {
      return super.toString() + "GeneralString: " + content;
   }

}


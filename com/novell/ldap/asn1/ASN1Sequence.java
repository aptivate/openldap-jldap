/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Sequence.java,v 1.4 2000/09/11 21:05:53 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;
import com.novell.ldap.client.ArrayList;

/**
 * The ASN1Sequence class can hold an ordered collection of components with
 * distinct type.
 */
public class ASN1Sequence extends ASN1Structured
{

   /**
    * ASN.1 SEQUENCE tag definition.
    */
   public static final int TAG = 0x10;

   //*************************************************************************
   // Constructors for ASN1Sequence
   //*************************************************************************

   /**
    * Constructs an ASN1Sequence.
    */
   public ASN1Sequence()
   {
      this(5);
      return;
   }

   /**
    * Constructs an ASN1Sequence.
    *
    * @param size Specifies the initial size of the collection.
    */
   public ASN1Sequence(int size)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
      content = new ArrayList(size);
      return;
   }

   /**
    * Constructs an ASN1Sequence object by decoding data from an input
    * stream.
    */
   public ASN1Sequence(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
      decodeStructured(dec, in, len);
      return;
   }

   //*************************************************************************
   // ASN1Sequence specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1Sequence.
    */
   public String toString()
   {
      return super.toString("SEQUENCE: { ");
   }
}

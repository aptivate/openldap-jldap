/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1SetOf.java,v 1.4 2000/09/11 21:05:53 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;
import com.novell.ldap.client.ArrayList;
import java.util.Enumeration;

/**
 * The ASN1SetOf class can hold an unordered collection of components with
 * identical type.
 */
public class ASN1SetOf extends ASN1Structured {

   /**
    * ASN.1 SET OF tag definition.
    */
   public static final int TAG = 0x11;

   //*************************************************************************
   // Constructors for ASN1SetOf
   //*************************************************************************

   /**
    * Constructs an ASN1SetOf object.
    */
   public ASN1SetOf()
   {
      this(5);
      return;
   }

   /**
    * Constructs an ASN1SetOf object.
    *
    * @param size Specifies the initial size of the collection.
    */
   public ASN1SetOf(int size)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
      content = new ArrayList(size);
      return;
   }

   /**
    * A copy constructor which creates an ASN1SetOf from an
    * instance of ASN1Set.
    *
    * Since SET and SET_OF have the same identifier, the decoder
    * will always return a SET object when it detects that identifier.
    * In order to take advantage of the ASN1SetOf type, we need to be
    * able to construct this object when knowingly receiving an
    * ASN1Set.
    */
   public ASN1SetOf(ASN1Set set)
   {
      id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
      content = new ArrayList(set.size());
      Enumeration e = set.elements();
      while(e.hasMoreElements()) {
         add((ASN1Object)e.nextElement());
      }
      return;
   }

   //*************************************************************************
   // ASN1SetOf specific methods
   //*************************************************************************

   /**
    * Return a String representation of this ASN1SetOf.
    */
   public String toString()
   {
      return super.toString("SET OF: { ");
   }
}

/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1;

import java.io.*;

/**
 * Represents an ASN.1 Identifier.
 *
 * An ASN1Identifier is composed of three parts: 1) a class type,
 * 2) a form, and 3) a tag.
 *
 * The class type is defined as:
 * bit 8 7 TAG CLASS
 * ------- -----------
 *     0 0 UNIVERSAL
 *     0 1 APPLICATION
 *     1 0 CONTEXT
 *     1 1 PRIVATE
 *
 * The form is defined as:
 * bit 6 FORM
 * ----- --------
 *     0 PRIMITIVE
 *     1 CONSTRUCTED
 *
 * Note: CONSTRUCTED types are made up of other CONSTRUCTED or PRIMITIVE
 *       types.
 *
 * The tag is defined as:
 * bit 5 4 3 2 1 TAG
 * ------------- ---------------------------------------------
 *     0 0 0 0 0
 *     . . . . .
 *     1 1 1 1 0 (0-30) single octet tag
 *
 *     1 1 1 1 1 (> 30) multiple octet tag, more octets follow
 * 
 */
public class ASN1Identifier { 

   /**
    * Universal tag class
    */
   public static final int UNIVERSAL = 0;
   /**
    * Application-wide tag class
    */
   public static final int APPLICATION = 1;
   /**
    * Context-specific tag class
    */
   public static final int CONTEXT = 2;
   /**
    * Private-use tag class
    */
   public static final int PRIVATE = 3;

   
   //*************************************************************************
   // Private variables
   //*************************************************************************

   private int tagClass;
   private boolean constructed;
   private int tag;
   private int encodedLength;

   //*************************************************************************
   // Constructors for ASN1Identifier
   //*************************************************************************

   /**
    * Constructs an ASN1Identifier.
    */
   public ASN1Identifier(int tagClass, boolean constructed, int tag)
   {
      this.tagClass = tagClass;
      this.constructed = constructed;
      this.tag = tag;
   }

   /**
    * Decode an ASN1Identifier directly from an InputStream. Save the
    * encoded length of the ASN1Identifier.
    */
   public ASN1Identifier(InputStream in)
      throws IOException
   {
      int r = in.read();
      encodedLength++;
      if(r < 0)
         throw new EOFException("BERDecoder: decode: EOF in Identifier");
      tagClass = r >> 6;
      constructed = (r & 0x20) != 0;
      tag = r & 0x1F;      // if tag < 30 then its a single octet identifier.
      if(tag == 0x1F)      // if true, its a multiple octet identifier.
         tag = decodeTagNumber(in);
   }

   /**
    * In the case that we have a tag number that is greater than 30, we need
    * to decode a multiple octet tag number.
    */
   private int decodeTagNumber(InputStream in)
      throws IOException
   {
      int n = 0;
      while(true) {
         int r = in.read();
         encodedLength++;
         if(r < 0)
            throw new EOFException("BERDecoder: decode: EOF in tag number");
         n = (n<<7) + (r & 0x7F);
         if((r & 0x80) == 0)
            break;
      }
      return n;
   }

   /**
    * Returns the CLASS of this ASN1Identifier as an int value.
    *
    * @see UNIVERSAL
    * @see APPLICATION
    * @see CONTEXT
    * @see PRIVATE
    */
   public int getASN1Class()
   {
      return tagClass;
   }

   /**
    * Return a boolean indicating if the constructed bit is set.
    *
    * FORM bit: 0 = primitive, 1 = constructed
    */
   public boolean getConstructed()
   {
      return constructed;
   }

   /**
    * Returns the TAG of this ASN1Identifier.
    */
   public int getTag()
   {
      return tag;
   }

   /**
    * Returns the encoded length of this ASN1Identifier.
    */
   public int getEncodedLength()
   {
      return encodedLength;
   }

   //*************************************************************************
   // Convenience methods
   //*************************************************************************

   /**
    * Returns a boolean value indicating whether or not this ASN1Identifier
    * has a TAG CLASS of UNIVERSAL.
    */
   public boolean isUniversal()
   {
      return tagClass == UNIVERSAL;
   }

   /**
    * Returns a boolean value indicating whether or not this ASN1Identifier
    * has a TAG CLASS of APPLICATION.
    */
   public boolean isApplication()
   {
      return tagClass == APPLICATION;
   }

   /**
    * Returns a boolean value indicating whether or not this ASN1Identifier
    * has a TAG CLASS of CONTEXT-SPECIFIC.
    */
   public boolean isContext()
   {
      return tagClass == CONTEXT;
   }

   /**
    * Returns a boolean value indicating whether or not this ASN1Identifier
    * has a TAG CLASS of PRIVATE.
    */
   public boolean isPrivate()
   {
      return tagClass == PRIVATE;
   }

}


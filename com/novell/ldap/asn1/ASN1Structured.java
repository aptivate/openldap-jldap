/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/asn1/ASN1Structured.java,v 1.4 2000/09/03 19:55:55 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;
import java.util.*;

/**
 * Base type for all ASN.1 structured types.
 */
public abstract class ASN1Structured extends ASN1Object {

   protected Vector content;

   /**
    * Encodes the contents of this ASN1Structured directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   /**
    * Decode an ASN1Structured type from an InputStream.
    */
   protected void decodeStructured(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      content = new Vector();
      int[] componentLen = new int[1]; // collects length of component

      while(len > 0) {
         add(dec.decode(in, componentLen));
         len -= componentLen[0];
      }
   }

   /**
    *
    */
   public Vector getContent()
   {
      return content;
   }

   /**
    *
    */
   public void add(ASN1Object value)
   {
      content.addElement(value);
   }

   /**
    *
    */
   public void set(int index, ASN1Object value)
   {
      content.setElementAt(value, index);
   }

   /**
    *
    */
   public Enumeration elements()
   {
      return content.elements();
   }

   /**
    *
    */
   public ASN1Object get(int index)
   {
      return (ASN1Object)content.elementAt(index);
   }

   /**
    *
    */
   public void remove(int index)
   {
      content.removeElementAt(index);
   }

   /**
    *
    */
   public int size()
   {
      return content.size();
   }

   /**
    * Return a String representation of this ASN1Structured.
    */
   public String toString(String type)
   {
      StringBuffer sb = new StringBuffer();

      sb.append(type);

      int len = content.size();
      for(int i=0; i < len; i++)
      {
         sb.append(content.elementAt(i));
         if(i != len-1)
            sb.append(", ");
      }
      sb.append(" }");

      return super.toString() + sb.toString();
   }

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Structured.java,v 1.10 2001/02/26 19:58:26 vtag Exp $
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
import com.novell.ldap.client.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Base type for all ASN.1 structured types.
 */
public abstract class ASN1Structured extends ASN1Object
{

   class EnumerationImpl implements Enumeration
   {
       private int enumerationIndex = 0;

       public boolean hasMoreElements()
       {
          if( (enumerationIndex >= content.size()) || (enumerationIndex < 0)) {
             return false;
          }
          return true;
       }

       public Object nextElement()
           throws NoSuchElementException
       {
           Object obj;
           try {
               obj = content.get( enumerationIndex++);
           } catch ( IndexOutOfBoundsException ex) {
               throw new NoSuchElementException("ASN1Structured: no such element " +
                   enumerationIndex);
           }
           return obj;
       }
   }

   protected ArrayList content;

   /**
    * Encodes the contents of this ASN1Structured directly to an output
    * stream.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
      return;
   }

   /**
    * Decode an ASN1Structured type from an InputStream.
    */
   protected void decodeStructured(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      content = new ArrayList();
      int[] componentLen = new int[1]; // collects length of component

      while(len > 0) {
         add(dec.decode(in, componentLen));
         len -= componentLen[0];
      }
   }

   /**
    *
    */
   public ArrayList getContent()
   {
      return content;
   }

   /**
    *
    */
   public void add(ASN1Object value)
   {
      content.add(value);
      return;
   }

   /**
    *
    */
   public void set(int index, ASN1Object value)
   {
      content.set(index, value);
      return;
   }

   /**
    *
    */
   public Enumeration elements()
   {
      return new EnumerationImpl();
   }

   /**
    *
    */
   public ASN1Object get(int index)
   {
      return (ASN1Object)content.get(index);
   }

   /**
    *
    */
   public void remove(int index)
   {
      content.remove(index);
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
         sb.append(content.get(i));
         if(i != len-1)
            sb.append(", ");
      }
      sb.append(/*{*/ " }");

      return super.toString() + sb.toString();
   }
}

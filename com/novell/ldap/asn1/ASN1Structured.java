/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.asn1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.novell.ldap.client.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This class serves as the base type for all ASN.1 
 * structured types.
 */
public abstract class ASN1Structured extends ASN1Object
{

   /* An inner class is used to maintain a list of subtypes
    * that this structured type holds. Note the use of our
    * own enumeration rather than directly using the 
    * Vector class.
    *
    * We could have used the Vector class but that would
    * have been inefficient due to synchronization that
    * Vector class provides but is not really needed here.
    *
    * We have our own implementation of Enumeration because
    * we wanted to be backward compatible with older JDK
    * revisions that did not have some methods that we
    * needed. So we implement those here in this inner
    * class
    * (javed)
    */
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
    * Returns the individual ASN.1 elements in this ASN1Structed
    * object as an array of ASN1 Objects.
    */
   public ArrayList getContent()
   {
      return content;
   }

   /**
    * Adds a new ASN1Object to the end of this ASN1Structured
    * object.  
    *
    * @param value The ASN1Object to add to this ASN1Structured 
    * object.  Note the use of the ASN1Object type as the base
    * class for this object.  This allows the programmer to specify
    * an ASN1 Obejct of any sub type.
    */
   public void add(ASN1Object value)
   {
      content.add(value);
      return;
   }

   /**
    * Adds a new ASN1Object in the specified index position of 
    * this ASN1Structured object. 
    *
    * @param index The index into the ASN1Structured object where
    * this new ANS1Object will be added.
    *
    * @param value The ASN1Object to add to this ASN1Structured 
    * object.  Note the use of the ASN1Object type as the base
    * class for this object.  This allows the programmer to specify
    * an ASN1 Obejct of any sub type.
    */
   public void set(int index, ASN1Object value)
   {
      content.set(index, value);
      return;
   }

   /**
    * Returns the ASN1Objects in this ASN1Structured object as an
    * Enumeration.
    */
   public Enumeration elements()
   {
      return new EnumerationImpl();
   }

   /**
    * Gets a specific ASN1Object in this structred object.
    *
    * param index The index of the ASN1Object to get from
    * this ASN1Structured object.
    */
   public ASN1Object get(int index)
   {
      return (ASN1Object)content.get(index);
   }

   /**
    * Removes an ASN1Object at a specific index location
    *
    * param index The index of the ASN1Object to remove from
    * this ASN1Structured object.
    */
   public void remove(int index)
   {
      content.remove(index);
   }

   /**
    * Returns the number of ASN1Obejcts that have been encoded
    * into this ASN1Structured class.
    */
   public int size()
   {
      return content.size();
   }

   /**
    * Return a String representation of this ASN1Structured.
    * object.
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

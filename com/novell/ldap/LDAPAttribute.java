/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPAttribute.java,v 1.3 2000/08/03 22:06:13 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
package org.ietf.ldap;

import java.util.*;
/*
 * 4.1 public class LDAPAttribute
 */

/** 
 * Represents the name and values of an attribute. 
 *
 * <p>The LDAPAttribute class is used to specify an attribute to be added 
 * to, deleted from, or modified in a directory entry. It is also returned 
 * on a search of a directory.</p>
 */
public class LDAPAttribute {
   private String name;
   private Vector values = new Vector();

   /*
    * 4.1.1 Constructors
    */

   /**
    * Constructs an attribute with copies of all values of the input
    * attribute.
    *
    * @param attr  An attribute to use as a template.
    */
   public LDAPAttribute(LDAPAttribute attr) {
      name = attr.getName();
      values = (Vector)attr.getValues().clone();
   }

   /**
    * Constructs an attribute with no values.
    *
    * @param attrName Name of the attribute.
    */
   public LDAPAttribute(String attrName) {
      name = attrName;
   }

   /**
    * Constructs an attribute with a byte-formatted value.
    *
    * @param attrName Name of the attribute.
    * @param attrBytes Value of the attribute as raw bytes.
    */
   public LDAPAttribute(String attrName, byte attrBytes[]) {
      name = attrName;
      values.add(attrBytes);
   }

   /**
    * Constructs an attribute that has a single string value.
    *
    * @param attrName Name of the attribute.
    * @param attrString Value of the attribute as a string.
    */
   public LDAPAttribute(String attrName, String attrString) {
      name = attrName;
      values.add(attrString);
   }

   /**
    * Constructs an attribute that has an array of string values.
    *
    * @param attrName Name of the attribute.
    * @param attrStrings Array of values as strings.
    */
   public LDAPAttribute(String attrName, String attrStrings[]) {
      name = attrName;
      for(int i=0; i<attrStrings.length; i++) {
         values.add(attrStrings[i]);
      }
   }

   /*
    * 4.1.2 addValue
    */

   /**
    * Adds a string value to the attribute.
    *
    * @param attrString Value of the attribute as a string.
    */
   public synchronized void addValue(String attrString) {
      values.add(attrString);
   }

   /**
    * Adds a byte[]-formatted value to the attribute.
    *
    * @param attrBytes Value of the attribute as raw bytes.
    */
   public synchronized void addValue(byte attrBytes[]) {
      values.add(attrBytes);
   }

   /*
    * 4.1.3 getByteValues
    */

   /**
    * Returns an enumerator for the values of the attribute in byte[]
    * format.
    */
   public Enumeration getByteValues() {
      Vector bv = new Vector(values.size());
      Enumeration e = values.elements();
      while(e.hasMoreElements()) {
         Object o = e.nextElement();
         if(o instanceof String) {
            bv.add(((String)o).getBytes());
         }
         else {
            bv.add(o);
         }
      }
      return bv.elements();
   }

   /*
    * 4.1.4 getStringValues
    */

   /**
    * Returns an enumerator for the string values of an attribute.
    */
   public Enumeration getStringValues() {
      Vector sv = new Vector(values.size());
      Enumeration e = values.elements();
      while(e.hasMoreElements()) {
         Object o = e.nextElement();
         if(o instanceof String) {
            sv.add(o);
         }
         else {
            sv.add(new String((byte[])o));
         }
      }
      return sv.elements();
   }

   /*
    * 4.1.5 getByteValueArray
    */

   /**
    * Returns the values of the attribute as an array of byte[].
    */
   public byte[][] getByteValueArray() {
      byte[][] bva = new byte[values.size()][];
      int i=0;
      Enumeration e = values.elements();
      while(e.hasMoreElements()) {
         Object o = e.nextElement();
         bva[i++] = (o instanceof String) ? ((String)o).getBytes() : (byte[])o;
      }
      return bva;
   }

   /*
    * 4.1.6 getStringValueArray
    */

   /**
    * Returns the values of the attribute as an array of strings.
    */
   public String[] getStringValueArray() {
      String[] sva = new String[values.size()];
      int i=0;
      Enumeration e = values.elements();
      while(e.hasMoreElements()) {
         Object o = e.nextElement();
         sva[i++] = (o instanceof String) ? (String)o : new String((byte[])o);
      }
      return sva;
   }

   /*
    * 4.1.7 getLangSubtype
    */

   /**
    * Returns the language subtype of the attribute, if any. 
    *
    * <p>For example, if the attribute name is cn;lang-ja;phonetic, 
    * this method returns the string, lang-ja.</p>
    */
   public String getLangSubtype() {
      return null;
   }

   /*
    * 4.1.8 getBaseName
    */

   /**
    * Returns the base name of the attribute. 
    *
    *<p>For example, if the attribute name is cn;lang-ja;phonetic, 
    * this method returns cn.</p>
    */
   public String getBaseName() {
      return null;
   }

   /**
    * Returns the base name of the attribute. 
    *
    * <p>For example, if the attribute name is cn;lang-ja;phonetic, 
    * this method returns cn.</p>
    *
    * @param attrName Name of the attribute from which to extract the 
    * base name.
    */
   public static String getBaseName(String attrName) {
      return null;
   }

   /*
    * 4.1.9 getName
    */

   /**
    * Returns the name of the attribute.
    */
   public String getName() {
      return name;
   }

   /*
    * 4.1.10 getSubtypes
    */

   /**
    * Extracts the subtypes from the specified attribute name. 
    *
    *<p>For example, if the attribute name is cn;lang-ja;phonetic, 
    * this method returns an array containing lang-ja and phonetic.
    */
   public String[] getSubtypes() {
      return null;
   }

   /**
    * Extracts the subtypes from the specified attribute name. 
    *
    * <p>For example, if the attribute name is cn;lang-ja;phonetic, 
    * this method returns an array containing lang-ja and phonetic.</p>
    *
    * @param attrName   Name of the attribute from which to extract 
    * the subtypes.
    */
   public static String[] getSubtypes(String attrName) {
      return null;
   }

   /*
    * 4.1.11 hasSubtype
    */

   /**
    * Reports if the attribute name contains the specified subtype. 
    *
    * <p>For example, if you check for the subtype lang-en and the 
    * attribute name is cn;lang-en, this method returns true.</p>
    *
    * @param subtype  The single subtype to check for.
    */
   public boolean hasSubtype(String subtype) {
      return false;
   }

   /*
    * 4.1.12 hasSubtypes
    */

   /**
    * Reports if the attribute name contains all the specified subtypes. 
    *
    * <p> For example, if you check for the subtypes lang-en and phonetic 
    * and if the attribute name is cn;lang-en;phonetic, this method 
    * returns true. If the attribute name is cn;phonetic or cn;lang-en, 
    * this method returns false.</p>
    *
    * @param subtypes   An array of subtypes to check for.
    */
   public boolean hasSubtypes(String[] subtypes) {
      return false;
   }

   /*
    * 4.1.13 removeValue
    */

   /**
    * Removes a string value from the attribute.
    *
    * @param attrString   Value of the attribute as a string.
    */
   public synchronized void removeValue(String attrString) {
      values.remove(attrString);
   }

   /**
    * Removes a byte[]-formatted value from the attribute.
    *
    * @param attrBytes    Value of the attribute as raw bytes.
    */
   public synchronized void removeValue(byte attrBytes[]) {
      values.remove(attrBytes);
   }

   /*
    * 4.1.14 size
    */

   /**
    * Returns the number of values of the attribute.
    */
   public int size() {
      return values.size();
   }

   /* *
    * Returns the internal Vector which stores values.
    */
   private Vector getValues() {
      return values;
   }

}

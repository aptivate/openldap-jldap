/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPAttributeSet.java,v 1.4 2000/08/03 22:06:13 smerrill Exp $
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
 * 4.3 public class LDAPAttributeSet
 *                implements Cloneable
 */

/**
 *  Represents a collection of LDAPAttributes, as returned in an entry
 *  on a search or read operation, or used to construct an entry
 *  to be added to a directory.
 */
public class LDAPAttributeSet implements Cloneable {
   private Vector attrs;

   /*
    * 4.3.1 Constructors
    */

   /**
    * Constructs a new set of attributes. This set is initially empty.
    */
   public LDAPAttributeSet() {
      attrs = new Vector();
   }

   /*
    * 4.3.2 add
    */

   /**
    * Adds the specified attribute to this attribute set.
    *
    * @param attr Attribute to add to this set.
    */
   public synchronized void add(LDAPAttribute attr) {
      attrs.add(attr);
   }

   /*
    * 4.3.3 clone
    */

   /**
    * Returns a deep copy of this attribute set.
    *
    * @return A deep copy of this attribute set.
    */
   public Object clone() {
      return null;
   }

   /*
    * 4.3.4 elementAt
    */

   /**
    * Returns the attribute at the position specified by the index. The
    * index is 0-based.
    *
    * @param index    Index of the attribute to get.
    *
    * @return    The attribute at the position specified.
    *
    * @exception ArrayIndexOutOfBoundsException The value specified by the 
    * index is outside of the array.
    *
    */
   public LDAPAttribute elementAt(int index)
    throws ArrayIndexOutOfBoundsException {
      return (LDAPAttribute)attrs.elementAt(index);
   }

   /*
    * 4.3.5 getAttribute
    */

   /**
    * Returns the attribute matching the specified attrName. 
    *
    * <p>For example:</p>
    * <ul>
    * <li>getAttribute("cn")          returns only the "cn" attribute</li>
    * <li>getAttribute("cn;lang-en")  returns only the "cn;lang-en"
    *                                 attribute.</li>
    * </ul>
    * <p>In both cases, null is returned if there is no exact match to the
    * specified attrName.</p>
    *
    * @param attrName   The name of an attribute to retrieve, with or without
    * subtype specification(s). For example, "cn", "cn;phonetic", and 
    * cn;binary" are valid attribute names.
    *
    * @return The attribute matching the specified attrName, or null if there
    * is no exact match.
    */
   public LDAPAttribute getAttribute(String attrName) {
      LDAPAttribute attrib;
      Enumeration enumAttr = attrs.elements();
      while( enumAttr.hasMoreElements()){
        attrib = (LDAPAttribute) enumAttr.nextElement();
        if(attrib.getName().equals(attrName)){
          return attrib;
        }
      }
      return null;
   }

   /**
    * Returns a single best-match attribute, or null if no match is
    * available in the entry.
    *
    * <p>LDAP version 3 allows adding a subtype specification to an attribute
    * name. For example, "cn;lang-ja" indicates a Japanese language
    * subtype of the "cn" attribute and "cn;lang-ja-JP-kanji" may be a subtype
    * of "cn;lang-ja". This feature may be used to provide multiple
    * localizations in the same directory. For attributes which do not vary
    * among localizations, only the base attribute may be stored, whereas
    * for others there may be varying degrees of specialization.</p>
    *
    * <p>For example, getAttribute(attrName,lang) returns the subtype that 
    * matches attrName and that best matches lang.</p> 
    *
    * <p>If there are subtypes other than "lang" subtypes included
    * in attrName, for example, "cn;binary", only attributes with all of
    * those subtypes are returned. If lang is null or empty, the
    * method behaves as getAttribute(attrName). If there are no matching
    * attributes, null is returned. </p>
    *
    *
    * <p>Assume the entry contains only the following attributes:</p>
    *
    *  <ul>
    *  <li>cn;lang-en</li>
    *  <li>cn;lang-ja-JP-kanji</li>
    *  <li>sn</li>
    *  </ul>
    *
    *  <p>Examples:</p>
    *  <ul>
    *  <li>getAttribute( "cn" )               returns null.</li>
    *  <li>getAttribute( "sn" )               returns the "sn" attribute.</li>
    *  <li>getAttribute( "cn", "lang-en-us" ) returns the "cn;lang-en"
    *                                         attribute.</li>
    *   <li>getAttribute( "cn", "lang-en" )   returns the "cn;lang-en"
    *                                         attribute.</li>
    *   <li>getAttribute( "cn", "lang-ja" )   returns null.</li>
    *   <li>getAttribute( "sn", "lang-en" )   returns the "sn" attribute.</li>
    *  </ul>
    *
    * @param attrName  The name of an attribute to retrieve, with or without
    * subtype specifications. For example, "cn", "cn;phonetic", and 
    * cn;binary" are valid attribute names.
    *
    * @param lang   A language specification with optional subtypes 
    * appended using "-" as separator. For example, "lang-en", "lang-en-us", 
    * "lang-ja", and "lang-ja-JP-kanji" are valid language specification.
    *    
    * @return A single best-match attribute, or null if no match is
    * found in the entry.
    *     
    */
   public LDAPAttribute[] getAttribute(String attrName, String lang) {
      return null;
   }

   /*
    * 4.3.6 getAttributes
    */

   /**
    * Returns an enumeration of the attributes in this attribute set.
    *
    * @return  An enumeration of the attributes in this attribute set.
    */
   public Enumeration getAttributes() {
      return attrs.elements();
   }

   /*
    * 4.3.7 getSubset
    */

   /**
    * Creates a new attribute set containing only the attributes that have
    * the specified subtypes.
    *
    * <p>For example, suppose an attribute set contains the following
    * attributes:</p>
    *
    * <ul>
    * <li>    cn</li>
    * <li>    cn;lang-ja</li>
    * <li>    sn;phonetic;lang-ja</li>
    * <li>    sn;lang-us</li>
    * </ul>
    *
    * <p>Calling the getSubset method and passing lang-ja as the argument, the
    * method returns an attribute set containing the following attributes:</p>
    *
    * <ul>
    *     <li>cn;lang-ja</li>
    *     <li>sn;phonetic;lang-ja</li>
    * </ul>
    *
    *  @param subtype - Semi-colon delimited list of subtypes to include. For
    *  example:
    * <ul>
    * <li> "lang-ja" specifies only Japanese language subtypes</li>
    * <li> "binary" specifies only binary subtypes</li>
    * <li> "binary;lang-ja" specifies only Japanese language subtypes
    *       which also are binary</li> 
    * </ul>               
    *                                          
    */
   public LDAPAttributeSet getSubset(String subtype) {
      return null;
   }

   /*
    * 4.3.8 remove
    */

   /**
    * Removes the specified attribute from the set. If the attribute is not
    * a member of the set, nothing happens.
    * 
    * <p> To remove an LDAPAttribute by object, the LDAPAttribute.getName 
    * method can be used: LDAPAttributeSet.remove( attr.getName() );</p>
    *
    * @param name  Name of the attribute to remove from this set. 
    *              
    */
   public synchronized void remove(String name) {
   }

   /*
    * 4.3.9 removeElementAt
    */

   /**
    * Removes the attribute at the position specified by the index.  The
    * index is 0-based.
    *
    * @param index  Index of the attribute to remove.
    *
    * @exception ArrayIndexOutOfBoundsException The value specified by the 
    * index is outside of the array.
    *
    */
   public void removeElementAt(int index)
   throws ArrayIndexOutOfBoundsException {
      attrs.removeElementAt(index);
   }

   /*
    * 4.3.10 size
    */

   /**
    * Returns the number of attributes in this set.
    *
    * @return The number of attributes in this set.
    */
   public int size() {
      return attrs.size();
   }

}

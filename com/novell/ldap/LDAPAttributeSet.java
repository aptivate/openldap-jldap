/* **************************************************************************
 * $Novell: LDAPAttributeSet.java,v 1.3 2000/05/22 20:36:17 bgudmundson Exp $
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

/**
 * 4.3 public class LDAPAttributeSet
 *                implements Cloneable
 *
 *  An LDAPAttributeSet is a collection of LDAPAttributes, as returned in
 *  an entry on a search or read operation, or used to construct an entry
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
    * attr Attribute to add to this set.
    */
   public synchronized void add(LDAPAttribute attr) {
      attrs.add(attr);
   }

   /*
    * 4.3.3 clone
    */

   /**
    * Returns a deep copy of this attribute set.
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
    * index           Index of the attribute to get.
    */
   public LDAPAttribute elementAt(int index)
    throws ArrayIndexOutOfBoundsException {
      return (LDAPAttribute)attrs.elementAt(index);
   }

   /*
    * 4.3.5 getAttribute
    */

   /**
    * Returns the attribute matching the specified attrName. For example,
    * getAttribute("cn")          returns only the "cn" attribute
    * getAttribute("cn;lang-en")  returns only the "cn;lang-en"
    *                             attribute.
    *
    * In both cases, null is returned if there is no exact match to the
    * specified attrName.
    *
    * attrName     The name of an attribute to retrieve, with or without
    * subtype specification(s). "cn", "cn;phonetic", and cn;binary" are
    * valid attribute names.
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
    * Returns a single best-match attribute, or none if no match is
    * available in the entry.
    *
    * LDAP version 3 allows adding a subtype specification to an attribute
    * name. "cn;lang-ja", for example, indicates a Japanese language
    * subtype of the "cn" attribute. "cn;lang-ja-JP-kanji" may be a subtype
    * of "cn;lang-ja". This feature may be used to provide multiple
    * localizations in the same Directory. For attributes which do not vary
    * among localizations, only the base attribute may be stored, whereas
    * for others there may be varying degrees of specialization.
    *
    * getAttribute(attrName,lang) returns the subtype that matches attrName
    * and that best matches lang. If there are subtypes other than "lang"
    * subtypes included in attrName, e.g. "cn;binary", only attributes with
    * all of those subtypes are returned. If lang is null or empty, the
    * method behaves as getAttribute(attrName). If there are no matching
    * attributes, null is returned.
    *
    * Example:
    *
    * Assume the entry contains only the following attributes:
    *
    *    cn;lang-en
    *    cn;lang-ja-JP-kanji
    *    sn
    *
    *    getAttribute( "cn" )               returns null.
    *    getAttribute( "sn" )               returns the "sn" attribute.
    *    getAttribute( "cn", "lang-en-us" ) returns the "cn;lang-en"
    *                                       attribute.
    *    getAttribute( "cn", "lang-en" )    returns the "cn;lang-en"
    *                                       attribute.
    *    getAttribute( "cn", "lang-ja" )    returns null.
    *    getAttribute( "sn", "lang-en" )    returns the "sn" attribute.
    *
    * attrName     The name of an attribute to retrieve, with or without
    * subtype specification(s). "cn", "cn;phonetic", and cn;binary" are
    * valid attribute names.
    *
    *  lang           A language specification as in [10], with
    *                  optional subtypes appended using "-" as
    *                  separator. "lang-en", "lang-en-us", "lang-ja",
    *                  and "lang-ja-JP-kanji" are valid language
    *                  specification.
    */
   public LDAPAttribute[] getAttribute(String attrName, String lang) {
      return null;
   }

   /*
    * 4.3.6 getAttributes
    */

   /**
    * Returns an enumeration of the attributes in this attribute set.
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
    * For example, suppose an attribute set contains the following
    * attributes:
    *
    *     cn
    *     cn;lang-ja
    *     sn;phonetic;lang-ja
    *     sn;lang-us
    *
    * Calling the getSubset method and passing lang-ja as the argument, the
    * method returns an attribute set containing the following attributes:
    *
    *     cn;lang-ja
    *     sn;phonetic;lang-ja
    *
    *       subtype - Semi-colon delimited list of subtypes to include. For
    *                       example:
    *                      "lang-ja"        // Only Japanese language
    *                       subtypes
    *                      "binary"         // Only binary subtypes
    *                      "binary;lang-ja" // Only Japanese language
    *                       subtypes
    *                                          which also are binary
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
    * name         Name of the attribute to remove from this set. To
    *              remove an LDAPAttribute by object, the
    *              LDAPAttribute.getName method can be used:
    *              LDAPAttributeSet.remove( attr.getName() );
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
    * index  Index of the attribute to remove.
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
    */
   public int size() {
      return attrs.size();
   }

}

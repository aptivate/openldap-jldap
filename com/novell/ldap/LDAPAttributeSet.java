/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPAttributeSet.java,v 1.20 2001/03/28 22:33:01 vtag Exp $
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

package com.novell.ldap;

import com.novell.ldap.client.ArrayList;
import com.novell.ldap.client.ArrayEnumeration;
import java.util.*;

/**
 *  Represents a collection of LDAPAttributes, either used to construct an entry
 *  to be added to a directory or returned in an entry on a search or read
 *  operation.
 */
public class LDAPAttributeSet implements Cloneable {
   private ArrayList attrs;

   /**
    * Constructs a new set of attributes. This set is initially empty.
    */
   public LDAPAttributeSet() {
      attrs = new ArrayList();
   }

   /**
    * Adds the specified attribute to this attribute set.
    *
    * @param attr Attribute to add to this set.
    */
   public void add(LDAPAttribute attr) {
      attrs.add(attr);
   }

   /**
    * Returns a deep copy of this attribute set.
    *
    * @return A deep copy of this attribute set.
    */
   public Object clone() {
      LDAPAttributeSet newAttrs = new LDAPAttributeSet();
      for(int i = 0; i < attrs.size(); i++){
         newAttrs.add( new LDAPAttribute( (LDAPAttribute)attrs.get(i)));
      }
      return (Object)newAttrs;
   }

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
   public LDAPAttribute ElementAt(int index)
    throws ArrayIndexOutOfBoundsException {
      return (LDAPAttribute)attrs.get(index);
   }

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
    * <p>NDS does not yet support language subtypes. It does support the "binary"
    *  subtype.</p>
    *
    * @param attrName   The name of an attribute to retrieve, with or without
    * subtype specifications. For example, "cn", "cn;phonetic", and
    * cn;binary" are valid attribute names.
    *
    * @return The attribute matching the specified attrName, or null if there
    * is no exact match.
    */
   public LDAPAttribute getAttribute(String attrName) {
      LDAPAttribute attrib;
      Object[] arrayAttr = attrs.toArray();
      for( int i=0; i < arrayAttr.length; i++) {
          attrib = (LDAPAttribute)arrayAttr[i];
          if(attrib.getName().equalsIgnoreCase(attrName)){
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
    * <p>NDS does not yet support language subtypes. It does support the "binary"
    *  subtype.</p>
    *
    * @param attrName  The name of an attribute to retrieve, with or without
    * subtype specifications. For example, "cn", "cn;phonetic", and
    * cn;binary" are valid attribute names.
    *<br><br>
    * @param lang   A language specification with optional subtypes
    * appended using "-" as separator. For example, "lang-en", "lang-en-us",
    * "lang-ja", and "lang-ja-JP-kanji" are valid language specification.
    *
    * @return A single best-match attribute, or null if no match is
    * found in the entry.
    *
    */
   public LDAPAttribute getAttribute(String attrName, String lang) {

	  LDAPAttribute attrib, partialMatch;
	  int partialMatchLen;

	  partialMatch = null;
	  partialMatchLen = 0;
      Object[] arrayAttr = attrs.toArray();

        for( int i = 0; i < arrayAttr.length; i++) {

	      attrib = (LDAPAttribute) arrayAttr[i];

          // Find a matching attribute
          if(attrib.getName().equals(attrName)){

			// Get the lang subtype for this attribute
			String attribSubType = attrib.getLangSubtype();

			// Return this attribute if this is a full match.
			if (attribSubType.equals(lang))
				return attrib;

			// Save this attribute off if we have a partial match
			if (lang.startsWith(attribSubType)) {

				// Get the length of the partial string that matched the subtype
				int matchedLen = attribSubType.length();

				// Was this a bettter (Longer) match, If yes the replace last matched
				// attribute with this better match
				if (matchedLen > partialMatchLen) {
					partialMatch = attrib;
					partialMatchLen = matchedLen;
				}
			}

			// else goto next attribute

		  }
      }

	  // Return a partial match if there was one,
	  //   else we will be returning a null pointer
      return partialMatch;

   }

   /**
    * Returns an enumeration of the attributes in this attribute set.
    *
    * @return  An enumeration of the attributes in this attribute set.
    */
   public Enumeration getAttributes() {
      return new ArrayEnumeration(attrs.toArray());
   }

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
    * <p>NDS does not yet support language subtypes. It does support the "binary"
    *  subtype.</p>
    *
    * @return An attribute set containing the attributes that match the specified
    *         subtype.
    */
   public LDAPAttributeSet getSubset(String subtype) {

	  // Create a new tempAttributeSet
      LDAPAttributeSet tempAttributeSet = new LDAPAttributeSet();

      // Cycle throught this.attributeSet
	  for(int i = 0; i < attrs.size(); i++) {

		 LDAPAttribute tempAttr = new LDAPAttribute( (LDAPAttribute)attrs.get(i));

		 // Does this attribute have the subtype we are looking for. If
		 // yes then add it to our AttributeSet, else next attribute
		 if (tempAttr.hasSubtype(subtype))
			tempAttributeSet.add(tempAttr);
      }

      return tempAttributeSet;

   }

   /**
    * Removes the specified attribute from the set. If the attribute is not
    * a member of the set, nothing happens.
    *
    * <p> To remove an LDAPAttribute by object, the LDAPAttribute.getName
    * method can be used: LDAPAttributeSet.remove( attr.getName );</p>
    *
    * @param name  Name of the attribute to remove from this set.
    *
    */
   public void remove(String name) {
       for(int i=0; i<attrs.size(); i++) {
           LDAPAttribute attr = (LDAPAttribute)attrs.get(i);
           if(attr.getName().equals(name)) {
               attrs.remove(i);
               break;
           }
       }
       return;
   }

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
      attrs.remove(index);
      return;
   }

   /**
    * Returns the number of attributes in this set.
    *
    * @return The number of attributes in this set.
    */
   public int size() {
      return attrs.size();
   }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPCompareAttrNames.java,v 1.7 2000/09/26 20:56:28 judy Exp $
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
 
package com.novell.ldap;

import java.util.Locale;

/*
 * 4.5 public class LDAPCompareAttrNames
 *                implements LDAPEntryComparator
*/
 
/**
 *
 *  Represents an object that supports sorting search results by attribute
 *  name, in ascending or descending order.
 *
 * <p>NDS supports only ascending sort order (A,B,C ...) and allows sorting only 
 * by one attribute. The NDS server must be configured to index this attribute.</p>
 */
public class LDAPCompareAttrNames implements LDAPEntryComparator {

   /*
    * 4.5.1 Constructors
    */

   /**
    * Constructs an object that sorts results by a single attribute, in
    * ascending order.
    *
    * @param attrName       Name of an attribute to sort by.
    *
    */
   public LDAPCompareAttrNames(String attrName) {
   }

   /**
    * Constructs an object that sorts results by a single attribute, in
    * either ascending or descending order.
    *
    * <p> NDS does not support descending sort order (Z,Y,X...).</p>
    *
    * @param attrName       Name of an attribute to sort by.
    *<br><br>
    * @param ascendingFlag  True specifies ascending order; false specifies
    *                       descending order.
    */
   public LDAPCompareAttrNames(String attrName, boolean ascendingFlag) {
   }


   /**
    * Constructs an object that sorts by one or more attributes, in the
    * order provided, in ascending order.
    *
    * <p> NDS allows sorting only by one attribute. The NDS server must also be 
    * configured to index the specified attribute.</p>
    *
    * @param attrNames      Array of names of attributes to sort by.
    *
    */
   public LDAPCompareAttrNames(String[] attrNames) {
   }

   /**
    * Constructs an object that sorts by one or more attributes, in the
    * order provided, in either ascending or descending order for each
    * attribute.
    *
    * <p>NDS supports only ascending sort order (A,B,C ...) and allows sorting  
    * only by one attribute. The NDS server must be configured to index this 
    * attribute.</p>
    *
    *
    * @param attrNames      Array of names of attributes to sort by.
    *<br><br>
    * @param ascendingFlags  Array of flags, one for each attrName, where
    *                true specifies ascending order and false specifies
    *                descending order. An LDAPException is thrown if
    *                the length of ascendingFlags is not greater than
    *                or equal to the length of attrNames.
    *
    * @exception LDAPException A general exception which includes an error 
    * message and an LDAP error code.
    */
   public LDAPCompareAttrNames(String[] attrNames,
                               boolean[] ascendingFlags)
                               throws LDAPException {
   }

   /*
    * 4.5.2 getLocale
    */

   /**
    * Returns the locale to be used for sorting, if a locale has been
    * specified. 
    *
    * <p>If locale is null, a basic String.compareTo method is used for collation.
    * If non-null, a locale-specific collation is used. </p>
    *
    * @return The locale if one has been specified
    */
   public Locale getLocale () {
      return null;
   }

   /*
    * 4.5.3 setLocale
    */

   /**
    * Sets the locale to be used for sorting.
    *
    * @param locale   The locale to be used for sorting.
    */
   public void setLocale (Locale locale) {
   }

   /*
    * 4.5.4 isGreater
    */

   /**
    * Returns true if entry1 is to be considered greater than entry2, for
    * the purpose of sorting, based on the attribute name or names provided
    * on object construction.
    *
    * @param entry1         Target entry for comparison.
    *<br><br>
    * @param entry2         Entry to be compared to.
    *
    * @return True if entry1 is greater than enter2; otherwise, false.
    */
   public boolean isGreater (LDAPEntry entry1, LDAPEntry entry2) {
      return false;
   }

}

/**
 * 4.5 public class LDAPCompareAttrNames
 *                implements LDAPEntryComparator
 *
 *  An object of this class supports sorting search results by attribute
 *  name, in ascending or descending order.
 */
package com.novell.ldap; 

import java.util.Locale;
 
public class LDAPCompareAttrNames implements LDAPEntryComparator {

   /*
    * 4.5.1 Constructors
    */

   /**
    * Constructs an object that will sort results by a single attribute, in
    * ascending order.
    */
   public LDAPCompareAttrNames(String attrName) {
   }

   /**
    * Constructs an object that will sort results by a single attribute, in
    * either ascending or descending order.
    */
   public LDAPCompareAttrNames(String attrName, boolean ascendingFlag) {
   }


   /**
    * Constructs an object that will sort by one or more attributes, in the
    * order provided, in ascending order.
    */
   public LDAPCompareAttrNames(String[] attrNames) {
   }

   /**
    * Constructs an object that will sort by one or more attributes, in the
    * order provided, in either ascending or descending order for each
    * attribute.
    *
    * attrName       Name of an attribute to sort by.
    *
    * attrNames      Array of names of attributes to sort by.
    *
    * ascendingFlag  true to sort in ascending order, false for
    *                 descending order.
    *
    * ascendingFlags Array of flags, one for each attrName, where each
    *                one is true to sort in ascending order, false for
    *                descending order. An LDAPException is thrown if
    *                the length of ascendingFlags is not greater than
    *                or equal to the length of attrNames.
    */
   public LDAPCompareAttrNames(String[] attrNames,
                               boolean[] ascendingFlags)
                               throws LDAPException {
   }

   /*
    * 4.5.2 getLocale
    */

   /**
    * Returns the Locale to be used for sorting, if a Locale has been
    * specified. If null, a basic String.compareTo() is used for collation.
    * If non-null, a Locale-specific collation is used.
    */
   public Locale getLocale () {
      return null;
   }

   /*
    * 4.5.3 setLocale
    */

   /**
    * Sets the Locale to be used for sorting.
    *
    * locale         The Locale to be used for sorting.
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
    * entry1         Target entry for comparison.
    *
    * entry2         Entry to be compared to.
    */
   public boolean isGreater (LDAPEntry entry1, LDAPEntry entry2) {
      return false;
   }

}

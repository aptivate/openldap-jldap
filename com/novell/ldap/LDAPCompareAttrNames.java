/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.util.Locale;
import java.text.Collator;
import com.novell.ldap.resources.*;


/**
 *  Compares LDAP entries based on attribute name.
 *
 *  <p>An object of this class defines ordering when sorting LDAPEntries,
 * usually from search results.  When using this Comparator, LDAPEntry objects
 * are sorted by the attribute names(s) passed in on the
 * constructor, in ascending or descending order.  The object is typically
 * supplied to an implementation of the collection interfaces such as
 * java.util.TreeSet which performs sorting. </p>
 *
 *  <p>Comparison is performed via locale-sensitive Java String comparison,
 * which may not correspond to the LDAP ordering rules by which an LDAP server
 * would sort them.
 *
 */
public class LDAPCompareAttrNames
        implements java.util.Comparator
{
   private String[] sortByNames;        //names to to sort by.
   private boolean[] sortAscending;     //true if sorting ascending
   private Locale location = Locale.getDefault();
   private Collator collator = Collator.getInstance();

   /**
    * Constructs an object that sorts results by a single attribute, in
    * ascending order.
    *
    * @param attrName       Name of an attribute by which to sort.
    *
    */
   public LDAPCompareAttrNames(String attrName) {
      sortByNames = new String[1];
      sortByNames[0] = attrName;
      sortAscending = new boolean[1];
      sortAscending[0] = true;
   }

   /**
    * Constructs an object that sorts results by a single attribute, in
    * either ascending or descending order.
    *
    * @param attrName       Name of an attribute to sort by.
    *<br><br>
    * @param ascendingFlag  True specifies ascending order; false specifies
    *                       descending order.
    */
   public LDAPCompareAttrNames(String attrName, boolean ascendingFlag) {
      sortByNames = new String[1];
      sortByNames[0] = attrName;
      sortAscending = new boolean[1];
      sortAscending[0] = ascendingFlag;
   }


   /**
    * Constructs an object that sorts by one or more attributes, in the
    * order provided, in ascending order.
    *
    * <p>Note: Novell eDirectory allows sorting by one attribute only. The
    * direcctory server must also be configured to index the specified
    * attribute.</p>
    *
    * @param attrNames      Array of names of attributes to sort by.
    *
    */
   public LDAPCompareAttrNames(String[] attrNames) {
      sortByNames = new String[attrNames.length];
      sortAscending = new boolean[attrNames.length];
      for(int i=0; i<attrNames.length; i++){
         sortByNames[i] = attrNames[i];
         sortAscending[i] = true;
      }
   }

   /**
    * Constructs an object that sorts by one or more attributes, in the
    * order provided, in either ascending or descending order for each
    * attribute.
    *
    * <p>Note: Novell eDirectory supports only ascending sort order (A,B,C ...)
    * and allows sorting only by one attribute. The directory server must be
    * configured to index this attribute.</p>
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
    *
    */
   public LDAPCompareAttrNames(String[] attrNames,
                               boolean[] ascendingFlags)
                               throws LDAPException {
      if (attrNames.length != ascendingFlags.length){
         throw new LDAPException( ExceptionMessages.UNEQUAL_LENGTHS,
              LDAPException.INAPPROPRIATE_MATCHING,(String)null);
         //"Length of attribute Name array does not equal length of Flags array"
      }
      sortByNames = new String[attrNames.length];
      sortAscending = new boolean[ascendingFlags.length];
      for(int i=0; i<attrNames.length; i++){
         sortByNames[i] = attrNames[i];
         sortAscending[i] = ascendingFlags[i];
      }
   }

   /**
    * Returns the locale to be used for sorting, if a locale has been
    * specified.
    *
    * <p>If locale is null, a basic String.compareTo method is used for
    * collation.  If non-null, a locale-specific collation is used. </p>
    *
    * @return The locale if one has been specified
    */
   public Locale getLocale () {
      //currently supports only English local.
      return location;
   }

   /**
    * Sets the locale to be used for sorting.
    *
    * @param locale   The locale to be used for sorting.
    */
   public void setLocale (Locale locale) {
    collator = Collator.getInstance(locale);
    location = locale;
   }

   /**
    * Compares the the attributes of the first LDAPEntry to the second.
    * <p>Only the values of the attributes named at the construction of this
    * object will be compared.  Multi-valued attributes compare on the first
    * value only.  </p>
    *
    * @param object1         Target entry for comparison.
    *
    * @param object2         Entry to be compared to.
    *
    * @return     Negative value if the first entry is less than the second and
    * positive if the first is greater than the second.  Zero is returned if all
    * attributes to be compared are the same.
    */
   public int compare (Object object1, Object object2) {
      LDAPEntry entry1 = (LDAPEntry)object1;
      LDAPEntry entry2 = (LDAPEntry)object2;
      LDAPAttribute one, two;
      String[] first;   //multivalued attributes are ignored.
      String[] second;  //we just use the first element
      int compare,i=0;
      if (collator == null){ //using default locale
         collator = Collator.getInstance();
      }

      do {//while first and second are equal
         one = entry1.getAttribute(sortByNames[i]);
         two = entry2.getAttribute(sortByNames[i]);
         if ((one != null) && (two != null)){
           first = one.getStringValueArray();
           second= two.getStringValueArray();
           compare = collator.compare(first[0], second[0]);
         }//We could also use the other multivalued attributes to break ties.
         else //one of the entries was null
         {
            if (one != null)
              compare = -1;   //one is greater than two
            else if (two != null)
              compare = 1;    //one is lesser than two
            else
              compare = 0;  //tie - break it with the next attribute name
         }

         i++;
      } while ((compare == 0) && (i < sortByNames.length));

      if (sortAscending[i-1]){
          // return the normal ascending comparison.
          return compare;
      }
      else{
          // negate the comparison for a descending comparison.
          return - compare;
      }
   }

   /**
    * Determines if this comparator is equal to the comparator passed in.
    *
    * <p> This will return true if the comparator is an instance of
    * LDAPCompareAttrNames and compares the same attributes names in the same
    * order.</p>
    *
    * @return true the comparators are equal
    */
   public boolean equals (Object comparator){
       if ( !(comparator instanceof LDAPCompareAttrNames)){
           return false;
       }
       LDAPCompareAttrNames comp = (LDAPCompareAttrNames) comparator;

       // Test to see if the attribute to compare are the same length
       if ((comp.sortByNames.length != this.sortByNames.length) ||
           (comp.sortAscending.length != this.sortAscending.length)){
           return false;
       }

       // Test to see if the attribute names and sorting orders are the same.
       for (int i=0; i< this.sortByNames.length; i++){
           if (comp.sortAscending[i] != this.sortAscending[i])
               return false;
           if (!comp.sortByNames[i].equalsIgnoreCase(this.sortByNames[i]))
               return false;
       }
       return true;
   }

  
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPCompareAttrNames.java,v 1.16 2001/01/25 22:03:03 cmorris Exp $
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
import java.text.Collator;
import java.lang.RuntimeException;
import com.novell.ldap.*;


/**
 *
 *  Represents an object that supports sorting search results by one or more
 *  attributes, in ascending or descending order.
 *
 *  <p>NDS supports only ascending sort order (A,B,C ...) and allows sorting only
 *  by one attribute. The NDS server must be configured to index this attribute.</p>
 *
 *  @see LDAPEntryComparator
 */
public class LDAPCompareAttrNames implements LDAPEntryComparator {
   private String[] sortByNames;  //names to to sort by.
   private boolean[] sortAscending; //true if sorting ascending
   private Locale location = Locale.getDefault();
   private Collator collator = Collator.getInstance();

   /**
    * Constructs an object that sorts results by a single attribute, in
    * ascending order.
    *
    * @param attrName       Name of an attribute to sort by.
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
    * <p> NDS does not support descending sort order (Z,Y,X...).</p>
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
    * <p> NDS allows sorting only by one attribute. The NDS server must also be
    * configured to index the specified attribute.</p>
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
    *
    */
   public LDAPCompareAttrNames(String[] attrNames,
                               boolean[] ascendingFlags)
                               throws LDAPException {
      if (attrNames.length != ascendingFlags.length){
         throw new LDAPException( LDAPExceptionMessageResource.UNEQUAL_LENGTHS, //"Length of attribute Name array does not equal length of Flags array"
              LDAPException.INAPPROPRIATE_MATCHING);
         //RFC 2251 lists error code. #18 == inappropriateMatching
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
    * <p>If locale is null, a basic String.compareTo method is used for collation.
    * If non-null, a locale-specific collation is used. </p>
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
    * Returns true if entry1 is to be considered greater than entry2, for
    * the purpose of sorting, based on the attribute name or names provided
    * on object construction.  Currently multivalues attributes compare on the
    * first value only.  If all attributes to be compared to are the same then
    * isGreater returns true.
    *
    * @param entry1         Target entry for comparison.
    *<br><br>
    * @param entry2         Entry to be compared to.
    *
    * @return True if entry1 is greater than enter2; otherwise, false.
    */
   public boolean isGreater (LDAPEntry entry1, LDAPEntry entry2) {
      LDAPAttribute one, two;
      String[] first;   //these are arrays because of multivalued attributes, which are ignored.
      String[] second;
      int compare,i=0;
      if (collator == null){ //using default locale
         collator = Collator.getInstance();
         //compare = first[0].compareToIgnoreCase(second[0]);
      }

      //throw new RuntimeException("isGreater is not implemented yet");
      do {//while first and second are equal
         one = entry1.getAttribute(sortByNames[i]);
         two = entry2.getAttribute(sortByNames[i]);
         if ((one != null) && (two != null)){
           first = one.getStringValueArray();
           second= two.getStringValueArray();
           compare = collator.compare(first[0], second[0]);
         }//We could also use the other multivalued attributes to break ties and such.
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

      if (compare > 0){
         return sortAscending[i-1]; //if we sort up then entry1 is greater otherwise it is lesser
      }
      else if (compare < 0){
         return !sortAscending[i-1];//if we sort up then entry1 is lesser otherwise it is greater
      }
      else return false; //trivial ordering

   }


}

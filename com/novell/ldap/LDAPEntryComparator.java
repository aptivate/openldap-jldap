/**
 * 4.12 public interface LDAPEntryComparator
 *
 *  An object of this class can implement arbitrary sorting algorithms
 *  for search results.
 */
package com.novell.ldap; 
 
public interface LDAPEntryComparator {

   /*
    * 4.12.1 isGreater
    */

   /**
    * Returns true if entry1 is to be considered greater than or equal to
    * entry2, for the purpose of sorting.
    *
    * Parameters are:
    *
    *  entry1         Target entry for comparison.
    *
    *  entry2         Entry to be compared to.
    */
   public boolean isGreater(LDAPEntry entry1, LDAPEntry entry2);

}





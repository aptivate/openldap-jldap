/**
 * 4.26 public class LDAPSortKey
 *
 *  Encapsulates parameters for sorting search results.
 */
package com.novell.ldap; 
 
public class LDAPSortKey {

   /*
    * 4.26.1 Constructors
    */

   /**
    * Constructs a new LDAPSortKey object using a, possibly complex,
    * sorting specification.
    */
   public LDAPSortKey( String keyDescription ) {
   }


   /**
    * Constructs a new LDAPSortKey object using an attribute name and a
    * sort order.
    */
   public LDAPSortKey( String key, boolean reverse) {
   }

   /**
    * Constructs a new LDAPSortKey object using an attribute name, a sort
    * order, and a matching rule.
    *
    * Parameters are:
    *
    *  keyDescription A single attribute specification to sort by. If
    *                  prefixed with "-", reverse order sorting is
    *                  requested. A matching rule OID may be appended
    *                  following ":".
    *
    *                  Examples:
    *                     "cn"
    *                     "-cn"
    *                     "-cn:1.2.3.4.5"
    *
    *  key            An attribute name, e.g. "cn".
    *
    *  reverse        True to sort in reverse collation order.
    *
    *  matchRule      The object ID (OID) of a matching rule used for
    *                  collation. If the object will be used to request
    *                  server-side sorting of search results, it should
    *                  be the OID of a matching rule known to be
    *                  supported by that server.
    */
   public LDAPSortKey( String key, boolean reverse, String matchRule) {
   }

   /*
    * 4.26.2 getKey
    */

   /**
    * Returns the attribute to be used for collation.
    */
   public String getKey() {
      return null;
   }

   /*
    * 4.26.3 getReverse
    */

   /**
    * Returns true if the sort key specifies reverse-order sorting.
    */
   public boolean getReverse() {
      return false;
   }

   /*
    * 4.26.4 getMatchRule
    */

   /**
    * Returns the OID to be used as matching rule, or null if none is to be
    * used.
    */
   public String getMatchRule() {
      return null;
   }

}



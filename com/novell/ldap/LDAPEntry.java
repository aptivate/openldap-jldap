/**
 * 4.10 public class LDAPEntry
 *
 *  An LDAPEntry represents a single entry in a directory, consisting of
 *  a distinguished name (DN) and zero or more attributes. An instance of
 *  LDAPEntry is created in order to add an entry to a Directory, and
 *  instances are returned on a search by enumerating an
 *  LDAPSearchResults.
 */
package com.novell.ldap; 
 
public class LDAPEntry {

   private String dn;
   private LDAPAttributeSet attrs;

   /*
    * 4.10.1 Constructors
    */

   /**
    * Constructs an empty entry.
    */
   public LDAPEntry() {
   }

   /**
    * Constructs a new entry with the specified distinguished name and with
    * an empty attribute set.
    */
   public LDAPEntry(String dn) {
      this.dn = dn;
   }

   /**
    * Constructs a new entry with the specified distinguished name and set
    * of attributes.
    *
    * Parameters are:
    *
    *  dn             The distinguished name of the new entry. The
    *                  value is not validated. An invalid distinguished
    *                  name will cause adding of the entry to a
    *                  directory to fail.
    *
    *  attrs          The initial set of attributes assigned to the
    *                  entry.
    */
   public LDAPEntry(String dn, LDAPAttributeSet attrs) {
      this.dn = dn;
      this.attrs = attrs;
   }

   /**
    * Returns the attributes matching the specified attrName.
    * 
    * @param attrName The name of the attribute or attributes to return.
    *                 See 4.3.5 for the syntax and semantics relevant to this
    *                 parameter.
    * @return An array of LDAPAttribute objects.
    */
   public LDAPAttribute[] getAttribute(String attrName) {
      return null;
   }

   /*
    * 4.10.3 getAttributeSet
    */

   /**
    * Returns the attribute set of the entry. All base and subtype variants
    * of all attributes are returned. The LDAPAttributeSet returned may be
    * empty if there are no attributes in the entry.
    */
   public LDAPAttributeSet getAttributeSet() {
      return attrs;
   }


   /**
    * Returns an attribute set from the entry, consisting of only those
    * attributes matching the specified subtype(s). This may be used to
    * extract only a particular language variant subtype of each attribute,
    * if it exists. "subtype" may be, for example, "lang-ja", "binary", or
    * "lang-ja;phonetic". If more than one subtype is specified, separated
    * with a semicolon, only those attributes with all of the named
    * subtypes will be returned.  The LDAPAttributeSet returned may be
    * empty if there are no matching attributes in the entry.
    *
    * Parameters are:
    *
    *  subtype        One or more subtype specification(s), separated
    *                  with semicolons.  "lang-ja" and
    *                  "lang-en;phonetic" are valid subtype
    *                  specifications.
    */
   public LDAPAttributeSet getAttributeSet(String subtype) {
      return null;
   }

   /*
    * 4.10.4 getDN
    */

   /**
    * Returns the distinguished name of the entry.
    */
   public String getDN() {
      return dn;
   }

}

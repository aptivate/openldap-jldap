/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPEntry.java,v 1.14 2001/06/13 17:51:06 jhammons Exp $
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

/**
 * Represents a single entry in a directory, consisting of
 * a distinguished name (DN) and zero or more attributes. 
 *
 * <p>An instance of
 * LDAPEntry is created in order to add an entry to a directory, and
 * instances of LDAPEntry are returned on a search by enumerating an
 * LDAPSearchResults.
 *
 * @see LDAPAttribute
 * @see LDAPAttributeSet
 */
public class LDAPEntry {

   private String dn;
   private LDAPAttributeSet attrs;

   /**
    * Constructs an empty entry.
    */
   public LDAPEntry() {
      this(null,null);
   }

   /**
    * Constructs a new entry with the specified distinguished name and with
    * an empty attribute set.
    *
    *  @param dn  The distinguished name of the entry. The
    *                  value is not validated. An invalid distinguished
    *                  name will cause operations using this entry to fail.
    *
    */
   public LDAPEntry(String dn) {
      this( dn, null);
   }

   /**
    * Constructs a new entry with the specified distinguished name and set
    * of attributes.
    *
    *  @param dn       The distinguished name of the new entry. The
    *                  value is not validated. An invalid distinguished
    *                  name will cause operations using this entry to fail.
    *<br><br>
    *  @param attrs    The initial set of attributes assigned to the
    *                  entry.
    */
   public LDAPEntry(String dn, LDAPAttributeSet attrs) {
      if( dn == null) {
          dn = "";
      }
      if( dn == null) {
          attrs = new LDAPAttributeSet();
      }
      this.dn = dn;
      this.attrs = attrs;
      return;
   }

   /**
    * Returns the attributes matching the specified attrName.
    *
    * @param attrName The name of the attribute or attributes to return.
    * <br><br>
    * @return An array of LDAPAttribute objects.
    */
   public LDAPAttribute getAttribute(String attrName) {

		return attrs.getAttribute(attrName);
   }

   /**
    * Returns the attribute set of the entry.
    *
    * <p>All base and subtype variants of all attributes are
    * returned. The LDAPAttributeSet returned may be
    * empty if there are no attributes in the entry. </p>
    *
    * @return The attribute set of the entry.
    */
   public LDAPAttributeSet getAttributeSet() {
      return attrs;
   }


   /**
    * Returns an attribute set from the entry, consisting of only those
    * attributes matching the specified subtypes.
    *
    * <p>The getAttributeSet method can be used to extract only
    * a particular language variant subtype of each attribute,
    * if it exists. The "subtype" may be, for example, "lang-ja", "binary",
    * or "lang-ja;phonetic". If more than one subtype is specified, separated
    * with a semicolon, only those attributes with all of the named
    * subtypes will be returned. The LDAPAttributeSet returned may be
    * empty if there are no matching attributes in the entry. </p>
    *
    *  @param subtype  One or more subtype specification(s), separated
    *                  with semicolons. The "lang-ja" and
    *                  "lang-en;phonetic" are valid subtype
    *                  specifications.
    *
    * @return An attribute set from the entry with the attributes that
    *         match the specified subtypes or an empty set if no attributes
    *         match.
    */
   public LDAPAttributeSet getAttributeSet(String subtype) {
		return attrs.getSubset(subtype);
   }

   /**
    * Returns the distinguished name of the entry.
    *
    * @return The distinguished name of the entry.
    */
   public String getDN() {
      return dn;
   }

}

/**
 * 4.2 public class LDAPAttributeSchema
 *                extends LDAPSchemaElement
 *
 *  The LDAPAttributeSchema class represents the definition of an
 *  attribute. It is used to query attribute syntax, and to add or delete
 *  an attribute definition in a Directory. See [2] for a description of
 *  attribute representation in LDAP.
 *
 *  [2]  M. Wahl, A. Coulbeck, T. Howes, S. Kille, "Lightweight Directory
 *       Access Protocol: Attribute Syntax Definitions",
 *       RFC 2252, December 1997
 */
 
package com.novell.ldap;

public class LDAPAttributeSchema extends LDAPSchemaElement {

   /*
    * 4.2.1 Constructors
    */

   /**
    * Constructs an attribute definition for adding to or deleting from a
    * Directory.
    * name           Name of the attribute.
    *
    * oid            Unique Object Identifer of the attribute - in
    *              dotted numerical format.
    *
    * description    Optional description of the attribute.
    *
    * syntaxString   Unique Object Identifer of the syntax of the
    *              attribute - in dotted numerical format.
    *
    * single         True if the attribute is to be single-valued.
    *
    * superior       Optional name of the attribute type which this
    *              attribute type derives from; null if there is no
    *              superior attribute type.
    *
    * aliases        Optional list of additional names by which the
    *              attribute may be known; null if there are no
    *              aliases.
    */
   public LDAPAttributeSchema(String name, String oid, String description,
                              String syntaxString, boolean single,
                              String superior, String[] aliases) {
   }

   /**
    * Constructs an attribute definition from the raw String value returned
    * on a Directory query for "attributetypes".
    *
    *  raw            The raw String value returned on a Directory
    *                  query for "attributetypes".
    */
   public LDAPAttributeSchema(String raw) {
   }

   /*
    * 4.2.2 getSyntaxString
    */

   /**
    * Returns the Unique Object Identifer of the syntax of the attribute in
    * dotted numerical format.
    */
   public String getSyntaxString() {
      return null;
   }

   /*
    * 4.2.3 getSuperior
    */

   /**
    * Returns the name of the attribute type which this attribute derives
    * from, or null if there is no superior attribute.
    */
   public String getSuperior() {
      return null;
   }

   /*
    * 4.2.4 isSingleValued
    */

   /**
    * Returns true if the attribute is single-valued.
    */
   public boolean isSingleValued() {
      return false;
   }

}

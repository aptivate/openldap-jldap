/**
 * 4.22 public abstract class LDAPSchemaElement
 *
 *  The LDAPSchemaElement class is the base class for representing schema
 *  elements in LDAP.
 */
package com.novell.ldap; 
 
import java.util.Enumeration;

public abstract class LDAPSchemaElement {

   /*
    * 4.22.1 getAliases
    */

   /**
    * Returns an array of alternative names of the element, or null if
    * there are none. With respect to the protocol-level schema element
    * syntax definition of [2], the array consists of all values but the
    * first of the NAME qualifier.
    */
   public String[] getAliases() {
      return null;
   }

   /*
    * 4.22.2 getDescription
    */

   /**
    * Returns the description of the element. With respect to the protocol-
    * level schema element syntax definition of [2], the value is that of
    * the DESC qualifier.
    */
   public String getDescription() {
      return null;
   }

   /*
    * 4.22.3 getName
    */

   /**
    * Returns the name of the element. With respect to the protocol-level
    * schema element syntax definition of [2], the value is that of the
    * first NAME qualifier.
    */
   public String getName() {
      return null;
   }

   /*
    * 4.22.4 getID
    */

   /**
    * Returns the Unique Object Identifier of the element.
    */
   public String getID() {
      return null;
   }

   /*
    * 4.22.5 getQualifier
    */

   /**
    * Returns an array of all values of a specified optional or non-
    * standard qualifier of the element. This method may be used to access
    * the values of vendor-specific qualifiers (which begin with "X-" [2]).
    *
    * Parameters are:
    *
    *  name           The name of the qualifier, case-sensitive.
    */
   public String[] getQualifier(String name) {
      return null;
   }

   /*
    * 4.22.6 getQualifierNames
    */

   /**
    * Returns an enumeration of all qualifiers of the element which are not
    * defined in [2].
    */
   public Enumeration getQualifierNames() {
      return null;
   }

   /*
    * 4.22.7 isObsolete
    */

   /**
    * Returns true if the element is obsolete (has the OBSOLETE qualifier
    * in its LDAP definition [2].
    */
   public boolean isObsolete() {
      return false;
   }

   /*
    * 4.22.8 getValue
    */

   /**
    * Returns a String in a format suitable for directly adding to a
    * Directory, as a value of the particular schema element attribute.
    */
   public String getValue() {
      return null;
   }

   /*
    * 4.22.9 setQualifier
    */

   /**
    * Sets the values of a specified optional or non-standard qualifier of
    * the element. This method may be used to set the values of vendor-
    * specific qualifiers (which begin with "X-" [2]).
    *
    * Parameters are:
    *
    *  name           The name of the qualifier, case-sensitive.
    *
    *  values         The values to set for the qualifier.
    */
   public void setQualifier(String name, String[] values) {
   }

   /*
    * 4.22.10 add
    */

   /**
    * Adds the definition to a Directory. An exception is thrown if the
    * definition can't be added.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to add a schema definition.
    */
   public void add(LDAPConnection ld) throws LDAPException {
   }

   /**
    * Adds the definition to a Directory. An exception is thrown if the
    * definition can't be added.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to add a schema definition.
    *
    *  dn             Entry at which to determine the SubschemaSubentry
    *                  to which the schema element is to be added.
    */
   public void add(LDAPConnection ld, String dn) throws LDAPException {
   }

   /*
    * 4.22.11 remove
    */

   /**
    * Removes the definition from a Directory. An exception is thrown if
    * the definition can't be removed.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to remove a schema definition.
    */
   public void remove(LDAPConnection ld) throws LDAPException {
   }

   /**
    * Removes the definition from a Directory. An exception is thrown if
    * the definition can't be removed.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to remove a schema definition.
    *
    *  dn             Entry at which to determine the SubschemaSubentry
    *                  to remove the schema element from.
    */
   public void remove(LDAPConnection ld, String dn) throws LDAPException {
   }

   /*
    * 4.22.12 modify
    */

   /**
    * Replace a single value of the schema element definition in the
    * schema. An exception is thrown if the definition can't be modified.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to modify a schema definition.
    *
    *  newValue       The new schema element value.
    */
   public void modify(LDAPConnection ld,
                      LDAPSchemaElement newValue) throws LDAPException {
   }

   /**
    * Replace a single value of the schema element definition in the
    * schema. An exception is thrown if the definition can't be modified.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *                  Typically the connection must have been
    *                  authenticated to modify a schema definition.
    *
    *  newValue       The new schema element value.
    *
    *  dn             Entry at which to determine the SubschemaSubentry
    *                  to store the schema change in.
    */
   public void modify(LDAPConnection ld,
                      LDAPSchemaElement newValue,
                      String dn) throws LDAPException {
   }

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSchemaElement.java,v 1.10 2000/10/23 18:49:06 judy Exp $
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import com.novell.ldap.client.AttributeQualifier;

/**
 *  The base class for representing LDAP schema elements.
 *  
 */
public abstract class LDAPSchemaElement {

   /**
    * The name for the schema element.
    */
    protected String name;
    
   /**
    * The OID for the schema element.
    */
	protected String oid;

   /**
    * The description for the schema element.
    */
	protected String description;

   /**
    * A string array of alternative names for the schema element.
    */
	protected String[] aliases;

   /**
    * If present, indicates that the element is obsolete.
    */
    protected boolean obsolete;
    
   /**
    * A string array of optional, or vendor-specific, qualifiers for the 
    * schema element.
    *
    * <p> These optional qualifiers begin with "X-"; the NDS-specific qualifiers
    * begin with "X-NDS". </p>
    */
	protected String[] qualifier;
    
   /**
    * A string value for the schema element in a format that can be used to add 
    * the element to the directory.
    */
	protected String value;
    
   /**
   * A hash table that contains the vendor-specific qualifiers (for example, 
   * the X-NDS flags).
   */
    protected Hashtable hashQualifier = new Hashtable();

   /**
    * Returns an array of alternative names for the element, or null if
    * none is found. 
    *
    * <p>The getAliases method accesses the NAME qualifier (from the BNF  
    * descriptions of LDAP schema definitions). The array consists of all 
    * values but the first value of the NAME qualifier. </p>
    *
    *  @return An array of alternative names for the element, or null if none
    *          is found.
    */
   public String[] getAliases() {
    if( aliases != null){
      String[] retVal = new String[aliases.length];
      for( int i = 0; i < aliases.length; i++ )
        retVal[i] = aliases[i];
      return retVal;
    }
    return null;
   }

   /**
    * Returns the description of the element. 
    *
    * <p>The getDescription method returns the value of the DESC qualifier  
    * (from the BNF descriptions of LDAP schema definitions). </p>
    *
    * @return The description of the element.
    * 
    */
   public String getDescription() {
      return description;
   }

   /**
    * Returns the name of the element. 
    *
    * <p>The getName method accesses the NAME qualifier (from the BNF  
    * descriptions of LDAP schema definitions). This method returns the first 
    * value of the NAME field.</p>
    *
    *  @return The name of the element. 
    */
   public String getName() {
      return name;
   }

   /**
    * Returns the unique object identifier (OID) of the element.
    *
    * @return The OID of the element.
    */
   public String getID() {
      return oid;
   }

   /**
    * Returns an array of all values of a specified optional or non-
    * standard qualifier of the element. 
    *
    * <p>The getQualifier method may be used to access the values of
    * vendor-specific qualifiers (which begin with "X-").</p>
    *
    *  @param name      The name of the qualifier, case-sensitive.
    *
    *  @return An array of values for the specified non-standard qualifier.
    */
   public String[] getQualifier(String name) {
      AttributeQualifier attr = (AttributeQualifier) hashQualifier.get(name);
      if(attr != null){
        return attr.getValues();
      }
      return null;
   }

   /**
    * Returns an enumeration of all qualifiers of the element which are 
    * vendor specific (begin with "X-").
    *
    *@return An enumeration of all qualifiers of the element.
    */
   public Enumeration getQualifierNames() {
      int size;
      Vector qualNames = new Vector();
      if((size = hashQualifier.size()) > 0){
        Enumeration en = hashQualifier.elements();
        for( int i = 0; en.hasMoreElements(); i++){
          qualNames.addElement( ((AttributeQualifier)en.nextElement()).getName());
        }
      }
      return qualNames.elements();
   }

   /**
    * Returns whether the element has the OBSOLETE qualifier
    * in its LDAP definition.
    *
    * @return True if the LDAP definition contains the OBSOLETE qualifier; 
    *         false if OBSOLETE qualifier is not present.
    */
   public boolean isObsolete() {
      return obsolete;
   }

   /**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element.
    *
    * @return A string that can be used to add the element to the directory.
    */
   public String getValue() {
      return value;
   }

   /**
    * Sets the values of a specified optional or non-standard qualifier of
    * the element. 
    *
    * <p>The setQualifier method is used to set the values of vendor-
    * specific qualifiers (which begin with "X-").
    *
    *  @param name           The name of the qualifier, case-sensitive.
    *<br><br>
    *  @param values         The values to set for the qualifier.
    */
   public void setQualifier(String name, String[] values) {
    AttributeQualifier attrQualifier = new AttributeQualifier( name );
    if(values != null){
    	for(int i = 0; i < values.length; i++){
		attrQualifier.addValue(values[i]);
    	}
    }
    hashQualifier.put(name, attrQualifier);
   }

   /**
    * Adds the definition to a directory. An exception is thrown if the
    * definition cannot be added.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to add a schema definition.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public void add(LDAPConnection ld) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.add not implemented");
   }

   /**
    * Adds the definition to a directory, at a specified location. An exception 
    * is thrown if the definition cannot be added.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to add a schema definition.
    *<br><br>
    *  @param dn       The entry at which to determine the SubschemaSubentry
    *                  to which the schema element is to be added.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public void add(LDAPConnection ld, String dn) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.add not implemented");
   }

   /**
    * Removes the definition from a directory. An exception is thrown if
    * the definition cannot be removed.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to remove a schema definition.
    *
    *  @exception LDAPException A general exception which includes an error \
    *                           message and an LDAP error code.
    */
   public void remove(LDAPConnection ld) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.remove not implemented");
   }

   /**
    * Removes the definition from a directory, at a specified location.  
    * An exception is thrown if the definition cannot be removed.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to remove a schema definition.
    *<br><br>
    *  @param dn       The entry at which to determine the SubschemaSubentry
    *                  to remove the schema element from.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public void remove(LDAPConnection ld, String dn) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.remove not implemented");
   }

   /**
    * Replaces a single value of the schema element definition in the
    * schema. An exception is thrown if the definition cannot be modified.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to modify a schema definition.
    *<br><br>
    *  @param newValue  The new schema element value.
    *
    *  @exception LDAPException A general exception which includes an error 
    *                           message and an LDAP error code.
    */
   public void modify(LDAPConnection ld,
                      LDAPSchemaElement newValue) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.modify not implemented");
   }

   /**
    * Replaces a single value of the schema element definition in the
    * schema, at a specified location in the directory. An exception is thrown 
    * if the definition cannot be modified.
    *
    *  @param ld       An open connection to a directory server.
    *                  Typically the connection must have been
    *                  authenticated to modify a schema definition.
    *<br><br>
    *  @param newValue  The new schema element value.
    *<br><br>
    *  @param dn       The entry at which to determine the SubschemaSubentry
    *                  to store the schema change in.
    *
    *  @exception LDAPException A general exception which includes an error
    *                           message and an LDAP error code.
    */
   public void modify(LDAPConnection ld,
                      LDAPSchemaElement newValue,
                      String dn) throws LDAPException {
       throw new RuntimeException("Method LDAPSchemaElement.modify not implemented");
   }
}

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

import java.util.Enumeration;
import java.util.HashMap;
import com.novell.ldap.client.AttributeQualifier;
import com.novell.ldap.client.EnumeratedIterator;

/**
 *  The LDAPSchemaElement class is the base class representing schema
 *  elements (definitions) in LDAP.
 *
 *  <p>An LDAPSchemaElement is read-only, single-valued LDAPAttribute.
 *  Therefore, it does not support the addValue and removeValue methods from
 *  LDAPAttribute.  This class overrides those methods and throws
 *  <code>UnsupportedOperationException<code> if either of those methods are
 *  invoked by an application.<p>
 *
 * @see LDAPSchema
 * @see LDAPConnection#fetchSchema
 */
public abstract class LDAPSchemaElement extends LDAPAttribute implements 
    java.io.Serializable {
    /**
     * Creates an LDAPSchemaElement by setting the name of the LDAPAttribute.
     * Because this is the only constructor, all extended classes are expected
     * to call this constructor.  The value of the LDAPAttribute must be set
     * by the setValue method.
     * @param attrName  The attribute name of the schema definition. Valid
     *      names are one of the following:
     *              "attributeTypes", "objectClasses", "ldapSyntaxes",
     *              "nameForms", "dITContentRules", "dITStructureRules",
     *              "matchingRules", or "matchingRuleUse"
     */
    protected LDAPSchemaElement(String attrName){
        super(attrName);
    }
   /**
    * The names of the schema element.
    */
    protected String[] names = {""};

   /**
    * The OID for the schema element.
    */
     protected String oid = "";

   /**
    * The description for the schema element.
    */
     protected String description = "";

   /**
    * If present, indicates that the element is obsolete, no longer in use in
    * the directory.
    */
    protected boolean obsolete = false;

   /**
    * A string array of optional, or vendor-specific, qualifiers for the
    * schema element.
    *
    * <p> These optional qualifiers begin with "X-"; the Novell eDirectory
    * specific qualifiers begin with "X-NDS". </p>
    */
     protected String[] qualifier = {""};

   /**
   * A hash table that contains the vendor-specific qualifiers (for example,
   * the X-NDS flags).
   */
    protected HashMap hashQualifier = new HashMap();

   /**
    * Returns an array of names for the element, or null if
    * none is found.
    *
    * <p>The getNames method accesses the NAME qualifier (from the BNF
    * descriptions of LDAP schema definitions). The array consists of all
    * values of the NAME qualifier. </p>
    *
    *  @return An array of names for the element, or null if none
    *          is found.
    */
    public String[] getNames()
    {
        if (names == null)
            return null;
        return (String[]) names.clone();
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
   public String getDescription()
   {
      return description;
   }

   /**
    * Returns the unique object identifier (OID) of the element.
    *
    * @return The OID of the element.
    */
   public String getID()
   {
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
   public String[] getQualifier(String name)
   {
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
    public Enumeration getQualifierNames()
    {
        return new EnumeratedIterator(hashQualifier.keySet().iterator() );
    }

   /**
    * Returns whether the element has the OBSOLETE qualifier
    * in its LDAP definition.
    *
    * @return True if the LDAP definition contains the OBSOLETE qualifier;
    *         false if OBSOLETE qualifier is not present.
    */
   public boolean isObsolete()
   {
      return obsolete;
   }

   /**
     * Returns a string in a format suitable for directly adding to a directory,
     * as a value of the particular schema element.
     *
     * @return A string that can be used to add the element to the directory.
     */
    public String toString()
    {
        return formatString();
    }

    /**
     * Implementations of formatString format a schema element into a string
     * suitable for using in a modify (ADD) operation to the directory.
     * toString uses this method.  This method is needed because a call to
     * setQualifier requires reconstructing the string value of the schema
     * element.
     */
    abstract protected String formatString();

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
    public void setQualifier(String name, String[] values)
    {
        AttributeQualifier attrQualifier =
                new AttributeQualifier( name, values );
        hashQualifier.put(name, attrQualifier);

        /* 
         * This is the only method that modifies the schema element.
         * We need to reset the attribute value since it has changed.
         */
        super.setValue( formatString() );
        return;
    }

    /**
     *  LDAPSchemaElement is read-only and this method is over-ridden to
     *  throw an exception.
     *  @throws UnsupportedOperationException always thrown since
     *          LDAPSchemaElement is read-only
     */
    public void addValue(String value){
        throw new UnsupportedOperationException(
                "addValue is not supported by LDAPSchemaElement");
    }

    /**
     *  LDAPSchemaElement is read-only and this method is over-ridden to
     *  throw an exception.
     *  @throws UnsupportedOperationException always thrown since
     *          LDAPSchemaElement is read-only
     */
    public void addValue(Byte[] value){
        throw new UnsupportedOperationException(
                "addValue is not supported by LDAPSchemaElement");
    }
    /**
     *  LDAPSchemaElement is read-only and this method is over-ridden to
     *  throw an exception.
     *  @throws UnsupportedOperationException always thrown since
     *          LDAPSchemaElement is read-only
     */
    public void removeValue(String value){
        throw new UnsupportedOperationException(
                "removeValue is not supported by LDAPSchemaElement");
    }

    /**
     *  LDAPSchemaElement is read-only and this method is over-ridden to
     *  throw an exception.
     *  @throws UnsupportedOperationException always thrown since
     *          LDAPSchemaElement is read-only
     */
    public void removeValue(Byte[] value){
        throw new UnsupportedOperationException(
                "removeValue is not supported by LDAPSchemaElement");
    }
    /**
    *  Writes the object state to a stream in standard Default Binary format
    *  This function wraps ObjectOutputStream' s defaultWriteObject() to write
    *  the non-static and non-transient fields of the current class to the stream
    *   
    *  @param objectOStrm  The OutputSteam where the Object need to be written
    */
    private void writeObject(java.io.ObjectOutputStream objectOStrm)
	    throws java.io.IOException {
		objectOStrm.defaultWriteObject();
    }
    
    /**
    *  Reads the serialized object from the underlying input stream.
    *  This function wraps ObjectInputStream's  defaultReadObject() function
    *
    *  @param objectIStrm  InputStream used to recover those objects previously serialized. 
    */
    private void readObject(java.io.ObjectInputStream objectIStrm)
         throws java.io.IOException, ClassNotFoundException
    {
	  objectIStrm.defaultReadObject();
    }

}

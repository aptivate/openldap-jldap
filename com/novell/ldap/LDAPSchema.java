/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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
import java.util.Hashtable;

/**
 *  The schema of a particular directory server.
 *
 * <p>It supports querying a directory server for its schema
 * and for definitions of individual schema elements.
 *
 * <p>The fetchSchema methods are the only methods that interact with a
 * directory server. The other methods access information acquired
 * through fetchSchema.</p>
 *
 * <p><a href="../../../overview-summary.html#sc">Sample Code</a>: ExtendSchema.java</p>
 *
 * @see LDAPAttributeSchema
 */
public class LDAPSchema {

	private Hashtable objectClassHashtable;
	private Hashtable attributeHashtable;
	private Hashtable matchingRuleHashtable;
	private Hashtable matchingRuleUseHashtable;
	private Hashtable syntaxHashtable;
	private Hashtable dITContentRuleHashtable;
	private Hashtable dITStructureRuleHashtable;
	private Hashtable dITStructureRuleIDHashtable;
	private Hashtable nameFormHashtable;

	//private static final int objectClass = 0;
    //private static final int attribute = 1;
	//private static final int superior = 2;

   /**
    * Constructs an empty LDAPSchema object.
    */
   public LDAPSchema() {
		objectClassHashtable = new Hashtable();
		attributeHashtable = new Hashtable();
		matchingRuleHashtable = new Hashtable();
		matchingRuleUseHashtable = new Hashtable();
		syntaxHashtable = new Hashtable();
		dITContentRuleHashtable = new Hashtable();
		dITStructureRuleHashtable = new Hashtable();
		dITStructureRuleIDHashtable = new Hashtable();
		nameFormHashtable = new Hashtable();
   }

   /**
    * Retrieves the entire schema from a directory server.
    *
    * <p>An LDAPException is thrown as for LDAPConnection.search method
    * if the schema cannot be retrieved with the specified connection.</p>
    *
    *  @param ld       An open connection to a directory server.
    *
    *  @exception LDAPException A general exception which includes an error
    *                           message and an LDAP error code.
    */
   public void fetchSchema(LDAPConnection ld) throws LDAPException {
		try{
			fetchSchema(ld,"");
		}
		catch(LDAPException e){
			throw e;
		}

   }
    /**
    * Retrieves the schema in effect at a particular entry in the directory
    * server.
    *
    * <p>An LDAPException is thrown as for LDAPConnection.search method
    * if the schema cannot be retrieved with the specified connection.</p>
    *
    *  @param ld       An open connection to a directory server.
    *<br><br>
    *  @param dn       The distinguished name of the entry from which to
    *                  return schema. The subschemasubentry attribute of
    *                  the entry is queried to find the location of the
    *                  schema to be returned.
    *
    *  @exception LDAPException A general exception which includes an error
    *   message and an LDAP error code.
    */
   public void fetchSchema(LDAPConnection ld,
                           String dn) throws LDAPException {

		objectClassHashtable.clear();
		attributeHashtable.clear();
		matchingRuleHashtable.clear();
		matchingRuleUseHashtable.clear();
		syntaxHashtable.clear();
		dITContentRuleHashtable.clear();
		dITStructureRuleHashtable.clear();
		dITStructureRuleIDHashtable.clear();
		nameFormHashtable.clear();

		try{

			String attrSubSchema[] = { "subschemaSubentry" };

			LDAPSearchResults sr = ld.search( dn,
									LDAPConnection.SCOPE_BASE,
									"objectclass=*",
									attrSubSchema,
									false);

			if(sr != null && sr.hasMoreElements())
			{
				String schemaDN;
				LDAPEntry ent = sr.next();
				// It would be better to call getAttributeSet(String attr) when implemented
				LDAPAttributeSet attrSet = ent.getAttributeSet();
				Enumeration en = attrSet.getAttributes();
				LDAPAttribute attr;
				String[] attrNames = {"*","+"};
				if(en.hasMoreElements())
				{
					attr = (LDAPAttribute) en.nextElement();
					Enumeration enumString = attr.getStringValues();
					if(enumString.hasMoreElements())
					{
						schemaDN = (String) enumString.nextElement();
						sr = ld.search( schemaDN,
									LDAPConnection.SCOPE_BASE,
									"objectclass=*",
									attrNames,
									false);

						String attrName;
						while(sr != null && sr.hasMoreElements())
						{
							ent = sr.next();
							attrSet = ent.getAttributeSet();
							en = attrSet.getAttributes();
							while(en.hasMoreElements())
							{
								attr = (LDAPAttribute) en.nextElement();
								attrName = attr.getName();

								if(attrName.equalsIgnoreCase("objectClass")){
									enumString = attr.getStringValues();
									String value;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
									}
								}
								else if(attrName.equalsIgnoreCase("objectClasses")){
									enumString = attr.getStringValues();
									String value;
         								LDAPObjectClassSchema classSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
          								classSchema = new LDAPObjectClassSchema( value );
                   						objectClassHashtable.put(classSchema.getName().toUpperCase(), classSchema);
									}
								}
								else if(attrName.equalsIgnoreCase("attributeTypes")){
									enumString = attr.getStringValues();
									String value;
									LDAPAttributeSchema attrSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										attrSchema = new LDAPAttributeSchema( value );
          								attributeHashtable.put(attrSchema.getName().toUpperCase(), attrSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("matchingRules")){
									enumString = attr.getStringValues();
									String value;
									LDAPMatchingRuleSchema matchingRuleSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										matchingRuleSchema = new LDAPMatchingRuleSchema( value, null );
          								matchingRuleHashtable.put(matchingRuleSchema.getName().toUpperCase(), matchingRuleSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("matchingRuleUse")){
									enumString = attr.getStringValues();
									String value;
									LDAPMatchingRuleUseSchema matchingRuleUseSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										matchingRuleUseSchema = new LDAPMatchingRuleUseSchema( value );
          								matchingRuleUseHashtable.put(matchingRuleUseSchema.getName().toUpperCase(), matchingRuleUseSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("ldapSyntaxes")){
									enumString = attr.getStringValues();
									String value;
									LDAPSyntaxSchema syntaxSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										syntaxSchema = new LDAPSyntaxSchema( value );
          								syntaxHashtable.put(syntaxSchema.getID(), syntaxSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("dITContentRules")){
									enumString = attr.getStringValues();
									String value;
									LDAPDITContentRuleSchema dITContentRuleSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										dITContentRuleSchema = new LDAPDITContentRuleSchema( value );
          								dITContentRuleHashtable.put(dITContentRuleSchema.getName().toUpperCase(), dITContentRuleSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("dITStructureRules")){
									enumString = attr.getStringValues();
									String value;
									LDAPDITStructureRuleSchema dITStructureRuleSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										dITStructureRuleSchema = new LDAPDITStructureRuleSchema( value );
          								dITStructureRuleHashtable.put(dITStructureRuleSchema.getName().toUpperCase(), dITStructureRuleSchema );
										dITStructureRuleIDHashtable.put(dITStructureRuleSchema.getID(), dITStructureRuleSchema );
									}
								}
								else if(attrName.equalsIgnoreCase("nameForms")){
									enumString = attr.getStringValues();
									String value;
									LDAPNameFormSchema nameFormSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										nameFormSchema = new LDAPNameFormSchema( value );
          								nameFormHashtable.put(nameFormSchema.getID(), nameFormSchema );
									}
								}
								else{
									enumString = attr.getStringValues();
									String value;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
									}

									continue;
								}

								enumString = attr.getStringValues();
								String value;
								while(enumString.hasMoreElements())
								{
									value = (String) enumString.nextElement();

								}
							}

						}
					}
				}
			}

		}
		catch( LDAPException e){
			throw e;
		}

   }

   /**
    * Returns a particular attribute definition, or null if not found.
    *
    *  @param name     Name of the attribute for which a definition is
    *                  to be returned.
    *
    *  @return The attribute definition, or null if not found.
    */
   public LDAPAttributeSchema getAttribute( String name ) {
      if( attributeHashtable.isEmpty() == true || attributeHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPAttributeSchema) attributeHashtable.get(name.toUpperCase());
   }

   /**
    * Returns a particular object class definition, or null if not found.
    *
    *  @param name    The name of the object class for which a definition
    *                 is to be returned.
    *
    * @return The object class definition, or null if not found.
    */
   public LDAPObjectClassSchema getObjectClass( String name ) {
      if( objectClassHashtable.isEmpty() == true || objectClassHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPObjectClassSchema) objectClassHashtable.get(name.toUpperCase());
   }

   /**
    * Returns a particular matching rule definition, or null if not found.
    *
    *  @param name     The name of the matching rule for which a definition
    *                  is to be returned.
    *
    *  @return The matching rule definition, or null if not found.
    */
   public LDAPMatchingRuleSchema getMatchingRule( String name ) {
      if( matchingRuleHashtable.isEmpty() == true || matchingRuleHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPMatchingRuleSchema) matchingRuleHashtable.get(name.toUpperCase());
   }

   /**
    * Returns a particular matching rule use definition, or null if not found.
    *
    *  @param name     The name of the matching rule use for which a definition
    *                  is to be returned.
    *
    *  @return The matching rule use definition, or null if not found.
    */
   public LDAPMatchingRuleUseSchema getMatchingRuleUse( String name ) {
      if( matchingRuleUseHashtable.isEmpty() == true || matchingRuleUseHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPMatchingRuleUseSchema) matchingRuleUseHashtable.get(name.toUpperCase());
   }

	/**
    * Returns a particular DIT structure rule definition, or null if not found.
    *
    *  @param name     The name of the DIT structure rule use for which a definition
    *                  is to be returned.
    *
    *  @return The DIT structure rule definition, or null if not found.
    */
   public LDAPDITStructureRuleSchema getDITStructureRule( String name ) {

      if( dITStructureRuleHashtable.isEmpty() == true || dITStructureRuleHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPDITStructureRuleSchema) dITStructureRuleHashtable.get(name.toUpperCase());

   }

   /**
    * Returns a particular DIT structure rule definition, or null if not found.
    *
    *  @param name     The ID of the DIT structure rule use for which a definition
    *                  is to be returned.
    *
    *  @return The DIT structure rule definition, or null if not found.
    */
   public LDAPDITStructureRuleSchema getDITStructureRule( int ID ) {

		Integer IDKey = new Integer(ID);
      if( dITStructureRuleIDHashtable.isEmpty() == true || dITStructureRuleIDHashtable.containsKey(IDKey) == false)
      	return null;
      return (LDAPDITStructureRuleSchema) dITStructureRuleIDHashtable.get(IDKey);

   }

   /**
    * Returns a particular DIT content rule definition, or null if not found.
    *
    *  @param name     The name of the DIT content rule use for which a definition
    *                  is to be returned.
    *
    *  @return The DIT content rule definition, or null if not found.
    */
   public LDAPDITContentRuleSchema getDITContentRule( String name ) {

      if( dITContentRuleHashtable.isEmpty() == true || dITContentRuleHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPDITContentRuleSchema) dITContentRuleHashtable.get(name.toUpperCase());

   }

   /**
    * Returns a particular name form definition, or null if not found.
    *
    *  @param name     The name of the name form for which a definition
    *                  is to be returned.
    *
    *  @return The name form definition, or null if not found.
    */
   public LDAPNameFormSchema getNameForm( String name ) {

      if( nameFormHashtable.isEmpty() == true || nameFormHashtable.containsKey(name.toUpperCase()) == false)
      	return null;
      return (LDAPNameFormSchema) nameFormHashtable.get(name.toUpperCase());

   }

   /**
    * Returns a particular syntax definition, or null if not found.
    *
    *  @param name     The oid of the syntax for which a definition
    *                  is to be returned.
    *
    *  @return The syntax definition, or null if not found.
    */
   public LDAPSyntaxSchema getSyntax( String oid ) {

      if( syntaxHashtable.isEmpty() == true || syntaxHashtable.containsKey(oid) == false)
      	return null;
      return (LDAPSyntaxSchema) syntaxHashtable.get(oid);

   }

   /**
    * Returns an enumeration of attribute definitions.
    *
    * @return An enumeration of attribute definitions.
    */
   public Enumeration getAttributes() {
      return attributeHashtable.elements();
   }

   /**
    * Returns an enumeration of object class definitions.
    *
    * @return An enumeration of object class definitions.
    */
   public Enumeration getObjectClasses() {
      return objectClassHashtable.elements();
   }

   /**
    * Returns an enumeration of matching rule definitions.
    *
    * @return An enumeration of matching rule definitions.
    */
   public Enumeration getMatchingRules() {
      return matchingRuleHashtable.elements();
   }

   /**
    * Returns an enumeration of matching rule use definitions.
    *
    * @return An enumeration of matching rule use definitions.
    */
   public Enumeration getMatchingUseRules() {
      return matchingRuleUseHashtable.elements();
   }

   /**
    * Returns an enumeration of DIT structure rule definitions.
    *
    * @return An enumeration of DIT structure rule definitions.
    */
   public Enumeration getDITStructureRules() {
      return dITStructureRuleHashtable.elements();
   }

   /**
    * Returns an enumeration of DIT content rule definitions.
    *
    * @return An enumeration of DIT content rule definitions.
    */
   public Enumeration getDITContentRules() {
      return dITContentRuleHashtable.elements();
   }

   /**
    * Returns an enumeration of name form definitions.
    *
    * @return An enumeration of name form definitions.
    */
   public Enumeration getNameForms() {
      return nameFormHashtable.elements();
   }

   /**
    * Returns an enumeration of syntax definitions.
    *
    * @return An enumeration of syntax definitions.
    */
   public Enumeration getSyntaxes() {
      return syntaxHashtable.elements();
   }

   /**
    * Returns an enumeration of attribute names.
    *
    * @return An enumeration of attribute names.
    */
   public Enumeration getAttributeNames() {
      java.util.Vector names = new java.util.Vector(attributeHashtable.size());
      Enumeration elements = attributeHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

   /**
    * Returns an enumeration of object class names.
    *
    * @return An enumeration of object class names.
    */
   public Enumeration getObjectClassNames() {
      java.util.Vector names = new java.util.Vector(objectClassHashtable.size());
      Enumeration elements = objectClassHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

   /**
    * Returns an enumeration of matching rule names.
    *
    * @return An enumeration of matching rule names.
    */
   public Enumeration getMatchingRuleNames() {
      java.util.Vector names = new java.util.Vector(matchingRuleHashtable.size());
      Enumeration elements = matchingRuleHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

   /**
  	* Returns an enumeration of matching rule use names.
    *
    * @return An enumeration of matching rule use names.
    */
   public Enumeration getMatchingRuleUseNames() {
      java.util.Vector names = new java.util.Vector(matchingRuleUseHashtable.size());
      Enumeration elements = matchingRuleUseHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

   /**
  	* Returns an enumeration of DIT structure rule names.
    *
    * @return An enumeration of DIT structure rule names.
    */
   public Enumeration getDITStructureRuleNames() {
      java.util.Vector names = new java.util.Vector(dITStructureRuleHashtable.size());
      Enumeration elements = dITStructureRuleHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

  /**
  	* Returns an enumeration of DIT content rule names.
    *
    * @return An enumeration of DIT content rule names.
    */
   public Enumeration getDITContentRuleNames() {
      java.util.Vector names = new java.util.Vector(dITContentRuleHashtable.size());
      Enumeration elements = dITContentRuleHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }

  /**
  	* Returns an enumeration of name form names.
    *
    * @return An enumeration of name form names.
    */
   public Enumeration getNameFormNames() {
      java.util.Vector names = new java.util.Vector(nameFormHashtable.size());
      Enumeration elements = nameFormHashtable.elements();

      while (elements.hasMoreElements()){
        LDAPSchemaElement el = (LDAPSchemaElement)elements.nextElement();
        names.add(el.getName());
      }
      return names.elements();
   }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSchema.java,v 1.15 2000/10/31 23:52:24 vtag Exp $
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
import java.util.*;

/**
 *  Represents the schema of a particular directory server.
 *
 *  It supports querying a directory server for its schema
 *  and for definitions of individual schema elements.
 *
 * <p>The fetchSchema methods are the only methods that interact with a
 * directory server. The other methods access information acquired
 * through fetchSchema.</p>
 */
public class LDAPSchema {

	private Hashtable objectClassHashtable;
	private Hashtable attributeHashtable;
	private Hashtable matchingRuleHashtable;
	private Hashtable matchingRuleUseHashtable;

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
									null,
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

								if(attrName.equals("objectClass")){
									enumString = attr.getStringValues();
									String value;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
									}
								}
								else if(attrName.equals("objectClasses")){
									enumString = attr.getStringValues();
									String value;
         								LDAPObjectClassSchema classSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
          								classSchema = new LDAPObjectClassSchema( value );
                   						objectClassHashtable.put(classSchema.getName(), classSchema);
									}
								}
								else if(attrName.equals("attributeTypes")){
									enumString = attr.getStringValues();
									String value;
									LDAPAttributeSchema attrSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										attrSchema = new LDAPAttributeSchema( value );
          								attributeHashtable.put(attrSchema.getName(), attrSchema );
									}
								}
								else if(attrName.equals("matchingRules")){
									enumString = attr.getStringValues();
									String value;
									LDAPMatchingRuleSchema matchingRuleSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										matchingRuleSchema = new LDAPMatchingRuleSchema( value, null );
          								matchingRuleHashtable.put(matchingRuleSchema.getName(), matchingRuleSchema );
									}
								}
								else if(attrName.equals("matchingRuleUse")){
									enumString = attr.getStringValues();
									String value;
									LDAPMatchingRuleUseSchema matchingRuleUseSchema;
									while(enumString.hasMoreElements())
									{
										value = (String) enumString.nextElement();
										matchingRuleUseSchema = new LDAPMatchingRuleUseSchema( value );
          								matchingRuleUseHashtable.put(matchingRuleUseSchema.getName(), matchingRuleUseSchema );
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
      if( attributeHashtable.isEmpty() == true || attributeHashtable.containsKey(name) == false)
      	return null;
      return (LDAPAttributeSchema) attributeHashtable.get(name);
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
      if( objectClassHashtable.isEmpty() == true || objectClassHashtable.containsKey(name) == false)
      	return null;
      return (LDAPObjectClassSchema) objectClassHashtable.get(name);
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
      if( matchingRuleHashtable.isEmpty() == true || matchingRuleHashtable.containsKey(name) == false)
      	return null;
      return (LDAPMatchingRuleSchema) matchingRuleHashtable.get(name);
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
      if( matchingRuleUseHashtable.isEmpty() == true || matchingRuleUseHashtable.containsKey(name) == false)
      	return null;
      return (LDAPMatchingRuleUseSchema) matchingRuleUseHashtable.get(name);
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
    * @exception An enumeration of matching rule definitions.
    */
   public Enumeration getMatchingRules() {
      return matchingRuleHashtable.elements();
   }

   /**
    * Returns an enumeration of matching rule use definitions.
    *
    * @exception An enumeration of matching rule use definitions.
    */
   public Enumeration getMatchingUseRules() {
      return matchingRuleUseHashtable.elements();
   }

   /**
    * Returns an enumeration of attribute names.
    *
    * @return An enumeration of attribute names.
    */
   public Enumeration getAttributeNames() {
      return attributeHashtable.keys();
   }

   /**
    * Returns an enumeration of object class names.
    *
    * @return An enumeration of object class names.
    */
   public Enumeration getObjectClassNames() {
      return objectClassHashtable.keys();
   }

   /**
    * Returns an enumeration of matching rule names.
    *
    * @return An enumeration of matching rule names.
    */
   public Enumeration getMatchingRuleNames() {
      return matchingRuleHashtable.keys();
   }

   /**
  	* Returns an enumeration of matching rule use names.
    *
    * @return An enumeration of matching rule use names.
    */
   public Enumeration getMatchingRuleUseNames() {
      return matchingRuleUseHashtable.keys();
   }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMatchingRuleUseSchema.java,v 1.12 2001/06/13 17:51:06 jhammons Exp $
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

import java.io.IOException;
import java.util.Enumeration;
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.resources.*;

/*  Represents the definition of a specific matching rule use in the
 *  directory schema.
 *
 * <p>The LDAPMatchingRuleUseSchema class is used to discover or modify which
 * attributes are suitable for use with an extensible matching rule. It contains
 * the name and identifier of a matching rule, and a list of attributes which
 * it applies to.</p>
 *
 * @see LDAPAttributeSchema
 * @see LDAPSchemaElement
 * @see LDAPSchema 
 */


public class LDAPMatchingRuleUseSchema
                extends LDAPSchemaElement
{
    private String[] attributes;
    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @param name        The name of the matching rule.
     *</br></br>
     * @param oid         The unique object identifier of the matching rule
     *                    in dotted numerical format.
     *</br></br>
     * @param description An optional description of the matching rule use.
     *</br></br>
     * @param obsolete    True if the matching rule use is obsolete.
     *</br></br>
     * @param attributes  A list of attributes that this matching rule
     *                    applies to. These values may be either the
     *                    names or numeric oids of the attributes.
     *</br></br>
     * @param aliases     Optional list of additional names by which the
     *                    matching rule use may be known; null if there
     *                    are no aliases.
     */
    public LDAPMatchingRuleUseSchema(String name,
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes,
                                     String[] aliases)
    {
        super.name = name;
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        super.aliases = aliases;
        this.attributes = attributes;
    }



    /**
     * Constructs a matching rule use definition from the raw string value
     * returned on a schema query for matchingRuleUse.
     *
     * @param raw        The raw string value returned on a schema
     *                   query for matchingRuleUse.
     */
    public LDAPMatchingRuleUseSchema(String raw)
    {
        try{
            SchemaParser matchParser = new SchemaParser(raw);
            super.name = matchParser.getName();
            super.oid = matchParser.getID();
            super.description = matchParser.getDescription();
            super.aliases = matchParser.getAliases();
            super.obsolete = matchParser.getObsolete();
            this.attributes = matchParser.getApplies();
        }
        catch( IOException e){
        }
    }

    /**
     * Returns an array of all the attributes which this matching rule
     * applies to.
     *
     * @return An array of all the attributes which this matching rule applies to.
     */
    public String[] getAttributes()
    {
        return attributes;
    }

	/**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element attribute.
    *
    * @return A string representation of the attribute's definition.
    */
   public String getValue() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      strArray = getAliases();
      if( (token = getName()) != null){
        valueBuffer.append(" NAME ");
        if(strArray != null){
          valueBuffer.append("( ");
        }
        valueBuffer.append("'" + token + "'");
        if(strArray != null){
          for( int i = 0; i < strArray.length; i++ ){
            valueBuffer.append(" '" + strArray[i] + "'");
          }
          valueBuffer.append(" )");
        }
      }
      if( (token = getDescription()) != null){
        valueBuffer.append(" DESC ");
        valueBuffer.append("'" + token + "'");
      }
      if( isObsolete()){
        valueBuffer.append(" OBSOLETE");
      }
      if( (strArray = getAttributes()) != null){
      	valueBuffer.append(" APPLIES ");
       	if( strArray.length > 1)
        	valueBuffer.append("( ");
        for( int i =0; i < strArray.length; i++){
        	if( i > 0)
         		valueBuffer.append(" $ ");
           	valueBuffer.append(strArray[i]);
        }
        if( strArray.length > 1)
        	valueBuffer.append(" )");
      }
      valueBuffer.append(" )");
      return valueBuffer.toString();
   }

  public void add(LDAPConnection ld) throws LDAPException {
    try{
        add(ld,"");
    }
    catch(LDAPException e){
        throw e;
    }
  }
  public void add(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn, LDAPConnection.SCOPE_BASE,
					                    "objectclass=*", attrSubSchema,
					                    false);
	    if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "matchingRuleUse" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE,
	                        "objectclass=*",
			                attrSearchName,
			                false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                    attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
		                    if(attrName.equalsIgnoreCase("matchingRuleUse")){
                                // add the value to the matchingRuleUse values
                                LDAPAttribute newValue = new LDAPAttribute(
                                        "matchingRuleUse",getValue());
                                LDAPModification lModify = new LDAPModification(
                                    LDAPModification.ADD,newValue);
                                ld.modify(schemaDN,lModify);
		                    }
		                    continue;
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

  public void remove(LDAPConnection ld) throws LDAPException {
    try{
        remove(ld,"");
    }
    catch(LDAPException e){
        throw e;
    }
  }

  public void remove(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
	                                LDAPConnection.SCOPE_BASE, "objectclass=*",
					                attrSubSchema, false);
	    if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "matchingRuleUse" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE,
	                        "objectclass=*",
			                attrSearchName,
			                false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                attr = (LDAPAttribute) en.nextElement();
                        attrName = attr.getName();
		                if(attrName.equalsIgnoreCase("matchingRuleUse")){
                        // remove the value from the matchingRuleUse values
                            LDAPAttribute newValue = new LDAPAttribute(
                                "matchingRuleUse",getValue());
                            LDAPModification lModify = new LDAPModification(
                                LDAPModification.DELETE,newValue);
                            ld.modify(schemaDN,lModify);
                        }
		                continue;
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

  public void modify(LDAPConnection ld, LDAPSchemaElement newValue) throws LDAPException {
    try{
        modify(ld, newValue, "");
    }
    catch(LDAPException e){
        throw e;
    }
  }

  public void modify(LDAPConnection ld, LDAPSchemaElement newValue, String dn) throws LDAPException {
    if( newValue instanceof LDAPMatchingRuleUseSchema != true ){
        throw new LDAPException(ExceptionMessages.NOT_A_RULEUSESHCEMA, //"Schema element is not an LDAPMatchingRuleUseSchema object",
                LDAPException.INVALID_ATTRIBUTE_SYNTAX);
    }

    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
	                                LDAPConnection.SCOPE_BASE, "objectclass=*",
					                attrSubSchema, false);
	    if(sr != null && sr.hasMoreElements()){
            String schemaDN;
	        LDAPEntry ent = sr.next();
	        LDAPAttributeSet attrSet = ent.getAttributeSet();
	        Enumeration en = attrSet.getAttributes();
	        LDAPAttribute attr;
	        if(en.hasMoreElements()){
	            attr = (LDAPAttribute) en.nextElement();
	            Enumeration enumString = attr.getStringValues();
	            if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "matchingRuleUse" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE,
	                        "objectclass=*",
			                attrSearchName,
			                false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                    attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
		                    if(attrName.equalsIgnoreCase("matchingRuleUse")){
                            // modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "matchingRuleUse", newValue.getValue());
                            LDAPModificationSet mods = new LDAPModificationSet();
                            mods.add(LDAPModification.DELETE, modValue);
							mods.add(LDAPModification.ADD, modValue);
                            ld.modify(schemaDN,mods);
                        }
		                continue;
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

}

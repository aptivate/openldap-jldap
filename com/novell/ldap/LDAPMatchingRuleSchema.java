/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMatchingRuleSchema.java,v 1.14 2000/12/09 00:19:50 bgudmundson Exp $
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
import com.novell.ldap.client.SchemaParser;
import java.util.Enumeration;
import java.io.IOException;
/**
 *
 *  Represents the schematic definition of a particular matching rule
 *  in a particular Directory Server.
 *
 *  <p>The LDAPMatchingRuleSchema class is used to query
 *  matching rule syntax, and to add or delete a matching rule definition
 *  in a directory.
 *
 * <p>NDS does not currently allow matching rules to be added or deleted from
 * the schema.</p>
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement {

    private String syntaxString;
    private String[] attributes;
   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a directory.
    *
    *  @param name        The name of the attribute.
    *<br><br>
    *  @param oid         The unique object identifer of the attribute, in
    *                     dotted numerical format.
    *<br><br>
    *  @param description    An optional description of the attribute.
    *<br><br>
    *  @param attributes     The OIDs of attributes to which the rule applies. All
    *                        attributes added to this array must use the same
    *                        syntax.
    *<br><br>
    *  @param syntaxString   The unique object identifer of the syntax of the
    *                        attribute, in dotted numerical format.
    *<br><br>
    *  @param aliases     An optional list of additional names by which the
    *                     matching rule may be known; null if there are
    *                     no aliases.
    */
   public LDAPMatchingRuleSchema(String name,
                                 String oid,
                                 String description,
                                 String[] attributes,
                                 String syntaxString,
                                 String[] aliases) {
      super.name = name;
      super.oid = oid;
      super.description = description;
      super.aliases = aliases;
      this.attributes = attributes;
      this.syntaxString = syntaxString;
   }

   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a directory.
    *
    *  @param name        The name of the attribute.
    *<br><br>
    *  @param oid         The unique object identifer of the attribute, in
    *                     dotted numerical format.
    *<br><br>
    *  @param description    An optional description of the attribute.
    *<br><br>
    *  @param obsolete     States if this matching rule is obsoleted.  Default
    *                      value is false.
    *<br><br>
    *  @param syntaxString   The unique object identifer of the syntax of the
    *                        attribute, in dotted numerical format.
    *<br><br>
    *  @param aliases     An optional list of additional names by which the
    *                     matching rule may be known; null if there are
    *                     no aliases.
    */
   public LDAPMatchingRuleSchema(String name,
                                 String oid,
                                 String description,
                                 boolean obsolete,
                                 String syntaxString,
                                 String[] aliases) {
      super.name = name;
      super.oid = oid;
      super.description = description;
      super.aliases = aliases;
      super.obsolete = obsolete;
      this.syntaxString = syntaxString;
   }


   /**
    * Constructs a matching rule definition from the raw string values
    * returned from a schema query for "matchingRule" and for
    * "matchingRuleUse" for the same rule.
    *
    *  @param rawMatchingRule    The raw string value returned on a directory
    *                            query for "matchingRule".
    *<br><br>
    *  @param rawMatchingRuleUse  The raw string value returned on a directory
    *                             query for "matchingRuleUse".
    */
   public LDAPMatchingRuleSchema(String rawMatchingRule,
                                 String rawMatchingRuleUse) {
    try{
        SchemaParser matchParser = new SchemaParser(rawMatchingRule);
        super.name = matchParser.getName();
        super.oid = matchParser.getID();
        super.description = matchParser.getDescription();
        super.aliases = matchParser.getAliases();
        super.obsolete = matchParser.getObsolete();
        this.syntaxString = matchParser.getSyntax();
        if( rawMatchingRuleUse != null ){
            SchemaParser matchUseParser = new SchemaParser(rawMatchingRuleUse);
            this.attributes = matchUseParser.getApplies();
        }
    }
    catch( IOException e){
    }

   }

   /**
    * Returns the OIDs of the attributes to which this rule applies.
    *
    *@return The OIDs of the attributes to which this matching rule applies.
    */
   public String[] getAttributes() {
      return attributes;
   }

   /**
    * Returns the OID of the syntax that this matching rule is valid for.
    *
    *@return The OID of the syntax that this matching rule is valid for.
    */
   public String getSyntaxString() {
      return syntaxString;
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
      if( (token = getSyntaxString()) != null){
        valueBuffer.append(" SYNTAX ");
        valueBuffer.append(token);
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
                    String[] attrSearchName= { "matchingRules" };
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
		                    if(attrName.equals("matchingRules")){
                                // add the value to the matchingRules values
                                LDAPAttribute newValue = new LDAPAttribute(
                                        "matchingRules",getValue());
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
                    String[] attrSearchName= { "matchingRules" };
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
		                if(attrName.equals("matchingRules")){
                        // remove the value from the attributes values
                            LDAPAttribute newValue = new LDAPAttribute(
                                "matchingRules",getValue());
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
    if( newValue instanceof LDAPMatchingRuleSchema != true ){
        throw new LDAPException(LDAPExceptionMessageResource.NOT_A_RULESCHEMA, //"Schema element is not an LDAPMatchingRuleSchema object",
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
                    String[] attrSearchName= { "matchingRules" };
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
		                    if(attrName.equals("matchingRules")){
                            // modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "matchingRules", newValue.getValue());
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
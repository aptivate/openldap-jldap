/* **************************************************************************
 * $Novell:
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
import com.novell.ldap.client.AttributeQualifier;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Represents the definition of a specific DIT (Directory Information Tree)
 * structure rule in the directory schema.
 *  
 * <p>The LDAPDITStructureRuleSchema class is used to discover or modify which 
 * object classes a particular object class may be subordinate to in the DIT.</p>
 * 
 */

public class LDAPDITStructureRuleSchema
                extends LDAPSchemaElement
{
	private int ruleID;
	private String nameForm;
	private String[] superiorIDs;

    /**  Constructs a DIT structure rule for adding to or deleting from the
     *   schema.
     *
     * @parameter name    The name of the structure rule.
     *<br><br>
     * @param ruleID      The unique identifier of the structure rule. NOTE:
     *                    this is an integer, not a dotted numerical
     *                    identifier. Structure rules aren't identified
     *                    by OID.
     *<br><br>
     * @param description An optional description of the structure rule.
     *<br><br>
     * @param obsolete    True if the structure rule is obsolete.
     *<br><br>
     * @param nameForm    Either the identifier or name of a name form.
     *                    This is used to indirectly refer to the object
     *                    class that this structure rule applies to.
     *<br><br>
     * @param superiorIDs A list of superior structure rules - specified
     *                    by their integer ID. The object class
     *                    specified by this structure rule (via the
     *                    nameForm parameter) may only be subordinate in
     *                    the DIT to object classes of those represented
     *                    by the structure rules here; it may be null.
     *<br><br>
     * @param aliases     An optional list of additional names by which the
     *                    structure rule may be known; null if there are
     *                    no aliases.
     *
     */
    public LDAPDITStructureRuleSchema(String name,
                                      int ruleID,
                                      String description,
                                      boolean obsolete,
                                      String nameForm,
                                      String[] superiorIDs,
                                      String[] aliases)
    {
        super.name = name;
		this.ruleID = ruleID;
		super.description = description;
		super.obsolete = obsolete;
		this.nameForm = nameForm;
		this.superiorIDs = superiorIDs;
		super.aliases = aliases;
    }

    /** 
     * Constructs a DIT structure rule from the raw string value returned from
     * a schema query for dITStructureRules.
     *
     * @param raw         The raw string value returned from a schema
     *                    query for dITStructureRules.
     */
    public LDAPDITStructureRuleSchema(String raw)
    {
        super.obsolete = false;
        try{
		    SchemaParser parser = new SchemaParser( raw );

	        if( parser.getName() != null)
			    super.name = new String(parser.getName());
	        super.aliases = parser.getAliases();
	        if( parser.getID() != null)
	            ruleID = Integer.getInteger(parser.getID()).intValue();
	        if( parser.getDescription() != null)
	            super.description = new String(parser.getDescription());
	        if( parser.getSuperiors() != null)
	            superiorIDs = (String[])parser.getSuperiors().clone();
			if( parser.getNameForm() != null)
	            nameForm = new String(parser.getNameForm());
	        super.obsolete = parser.getObsolete();
	        Enumeration qualifiers = parser.getQualifiers();
	        AttributeQualifier attrQualifier;
	        while(qualifiers.hasMoreElements()){
	            attrQualifier = (AttributeQualifier) qualifiers.nextElement();
	            setQualifier(attrQualifier.getName(), attrQualifier.getValues());
        	}
    	}
    	catch( IOException e){
    	}
    }

    /**
     * Returns the rule ID for this structure rule. 
     *
     * <p>The getRuleID method returns an integer rather than a dotted
     * decimal OID. Objects of this class do not have an OID,
     * thus getID can return null. </p>
     * 
     *
     * @return The rule ID for this structure rule.
     */

    public int getRuleID()
    {
        return ruleID;
    }

    /**
     * Returns the NameForm that this structure rule controls. 
     *
     * <p>You can get the actual object class that this structure rule controls
     *  by calling the getNameForm.getObjectClass method.</p>
     *
     * @return The NameForm that this structure rule controls.
     */
    public String getNameForm()
    {
        return nameForm;
    }

    /**
     * Returns a list of all structure rules that are superior to this
     * structure rule. 
     *
     * <p>To resolve to an object class, you need to first
     * resolve the superior ID to another structure rule, then call
     * the getNameForm.getObjectClass method on that structure rule.</p>
     *
     * @return A list of all structure rules that are superior to this structure rule.
     */
     public String[] getSuperiors()
     {
        return superiorIDs;
     }

	/**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element class.
    *
    * @return A string representation of the class' definition.
    */
   public String getValue() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

	  token = String.valueOf( getRuleID());
      valueBuffer.append(token);

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
      if( (token = getNameForm()) != null){
      	valueBuffer.append(" FORM ");
      	valueBuffer.append("'" + token + "'");
      }
      if( (strArray = getSuperiors()) != null){
      	valueBuffer.append(" SUP ");
       	if( strArray.length > 1)
        	valueBuffer.append("( ");
        for( int i =0; i < strArray.length; i++){
        	if( i > 0)
         		valueBuffer.append(" ");
           	valueBuffer.append(strArray[i]);
        }
        if( strArray.length > 1)
        	valueBuffer.append(" )");
      }

      Enumeration en;
      if( (en = getQualifierNames()) != null){
      	String qualName;
       	String[] qualValue;
      	while( en.hasMoreElements() ) {
       		qualName = (String)en.nextElement();
         	valueBuffer.append( " " + qualName + " ");
          	if((qualValue = getQualifier( qualName )) != null){
           		if( qualValue.length > 1)
             			valueBuffer.append("( ");
                       	for(int i = 0; i < qualValue.length; i++ ){
                          	if( i > 0 )
                           		valueBuffer.append(" ");
                        	valueBuffer.append( "'" + qualValue[i] + "'");
                      	}
                       if( qualValue.length > 1)
                       	valueBuffer.append(" )");
            	}
      	}
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
        LDAPSearchResults sr = ld.search( dn,
	                  LDAPConnection.SCOPE_BASE,
					  "objectclass=*",
					  attrSubSchema,
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
                    String[] attrSearchName= { "dITStructureRules" };
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
		                    if(attrName.equals("dITStructureRules")){
                            // add the value to the object class values
							LDAPAttribute newValue = new LDAPAttribute(
                                        "dITStructureRules",getValue());
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
                    String[] attrSearchName= { "dITStructureRules" };
	                sr = ld.search( schemaDN,
	                        LDAPConnection.SCOPE_BASE, "objectclass=*",
			                attrSearchName, false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		                while(en.hasMoreElements()){
		                    attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
		                    if(attrName.equals("dITStructureRules")){
                                // remove the value from the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                	"dITStructureRules",getValue());
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
                    String[] attrSearchName= { "dITStructureRules" };
	                sr = ld.search( schemaDN, LDAPConnection.SCOPE_BASE,
	                      "objectclass=*", attrSearchName, false);
	                String attrName;
	                if(sr != null && sr.hasMoreElements()){
	                    ent = sr.next();
		                attrSet = ent.getAttributeSet();
		                en = attrSet.getAttributes();
		            while(en.hasMoreElements()){
		                attr = (LDAPAttribute) en.nextElement();
                        attrName = attr.getName();
		                if(attrName.equals("dITStructureRules")){
                        	// modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "dITStructureRules", newValue.getValue());
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

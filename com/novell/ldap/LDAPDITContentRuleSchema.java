/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPDITContentRuleSchema.java,v 1.8 2001/04/23 21:09:30 cmorris Exp $
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

import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.client.AttributeQualifier;
import java.util.Enumeration;
import java.io.IOException;

/**
 *  A specific DIT (Directory Information Tree) content rule
 *  in the directory schema.
 *
 *  <p>The LDAPDITContentRuleSchema class is used to discover or modify additional
 *  auxiliary classes, mandatory and optional attributes, and restricted attributes
 *  in effect for an object class. </p>
 */


public class LDAPDITContentRuleSchema
                extends LDAPSchemaElement
{
	private String[] auxiliary;
    private String[] required;
    private String[] optional;
    private String[] precluded;

    /**
     * Constructs a DIT content rule for adding to or deleting from the
     * schema.
     *
     * @param name        The name of the content rule.
     *<br><br>
     * @param oid         The unique object identifier of the content rule -
     *                    in dotted numerical format.
     *<br><br>
     * @param description The optional description of the content rule.
     *<br><br>
     * @param obsolete    True if the content rule is obsolete.
     *<br><br>
     * @param auxiliary   A list of auxiliary object classes allowed for
     *                    an entry to which this content rule applies.
     *                    These may either be specified by name or
     *                    numeric oid.
     *<br><br>
     * @param required    A list of attributes that an entry
     *                    to which this content rule applies must
     *                    contain in addition to its normal set of
     *                    mandatory attributes. These attributes may be
     *                    specified by either name or numeric oid.
     *<br><br>
     * @param optional    A list of attributes that an entry
     *                    to which this content rule applies may contain
     *                    in addition to its normal set of optional
     *                    attributes. These attributes may be specified by
     *                    either name or numeric oid.
     *<br><br>
     * @param precluded   A list, consisting of a subset of the optional
     *                    attributes of the structural and
     *                    auxiliary object classes which are precluded
     *                    from an entry to which this content rule
     *                    applies. These may be specified by either name
     *                    or numeric oid.
     *<br><br>
     * @param aliases     An optional list of additional names by which the
     *                    content rule may be known; null if there are
     *                    no aliases.
     */
    public LDAPDITContentRuleSchema(String name,
                                    String oid,
                                    String description,
                                    boolean obsolete,
                                    String[] auxiliary,
                                    String[] required,
                                    String[] optional,
                                    String[] precluded,
                                    String[] aliases)
    {
        super.name = name;
		super.oid = oid;
		super.description = description;
		super.obsolete = obsolete;
		this.auxiliary = auxiliary;
		this.required = required;
		this.optional = optional;
		this.precluded = precluded;
		super.aliases = aliases;

    }

    /**
     * Constructs a DIT content rule from the raw string value returned from a
     * schema query for dITContentRules.
     *
     * @param raw         The raw string value returned from a schema query
     *                    for content rules.
     */
    public LDAPDITContentRuleSchema(String raw)
    {
		super.obsolete = false;
        try{
		    SchemaParser parser = new SchemaParser( raw );

	        if( parser.getName() != null)
			    super.name = new String(parser.getName());
	        super.aliases = parser.getAliases();
	        if( parser.getID() != null)
	            super.oid = new String(parser.getID());
	        if( parser.getDescription() != null)
	            super.description = new String(parser.getDescription());
	        if( parser.getAuxiliary() != null)
	            auxiliary = (String[])parser.getAuxiliary().clone();
	        if( parser.getRequired() != null)
	            required = (String[])parser.getRequired().clone();
	        if( parser.getOptional() != null)
	            optional = (String[])parser.getOptional().clone();
			if( parser.getPrecluded() != null)
	            precluded = (String[])parser.getPrecluded().clone();
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
     * Returns the list of allowed auxiliary classes.
     *
     * @return The list of allowed auxiliary classes.
     */
    public String[] getAuxiliaryClasses()
    {
        return auxiliary;
    }

    /**
     * Returns the list of additional required attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of additional required attributes.
     */
    public String[] getRequiredAttributes()
    {
        return required;
    }

    /**
     * Returns the list of additional optional attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of additional optional attributes.
     */
    public String[] getOptionalAttributes()
    {
        return optional;
    }

    /**
     * Returns the list of precluded attributes for an entry controlled by
     * this content rule.
     *
     * @return The list of precluded attributes.
     */
    public String[] getPrecludedAttributes()
    {
        return precluded;
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
      if( (strArray = getAuxiliaryClasses()) != null){
      	valueBuffer.append(" AUX ");
      	if( strArray.length > 1)
       		valueBuffer.append("( ");
        for(int i = 0; i < strArray.length; i++){
      		if( i > 0)
        		valueBuffer.append(" $ ");
          	valueBuffer.append(strArray[i]);
        }
        if( strArray.length > 1)
      		valueBuffer.append(" )");
      }
      if( (strArray = getRequiredAttributes()) != null){
      	valueBuffer.append(" MUST ");
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
      if( (strArray = getOptionalAttributes()) != null){
      	valueBuffer.append(" MAY ");
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
	  if( (strArray = getPrecludedAttributes()) != null){
      	valueBuffer.append(" NOT ");
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
                    String[] attrSearchName= { "dITContentRules" };
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
		                    if(attrName.equalsIgnoreCase("dITContentRules")){
                            // add the value to the object class values
							LDAPAttribute newValue = new LDAPAttribute(
                                        "dITContentRules",getValue());
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
                    String[] attrSearchName= { "dITContentRules" };
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
		                    if(attrName.equalsIgnoreCase("dITContentRules")){
                                // remove the value from the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                	"dITContentRules",getValue());
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
                    String[] attrSearchName= { "dITContentRules" };
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
		                if(attrName.equalsIgnoreCase("dITContentRules")){
                        	// modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "dITContentRules", newValue.getValue());
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

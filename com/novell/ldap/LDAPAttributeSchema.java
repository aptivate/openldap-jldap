/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPAttributeSchema.java,v 1.20 2001/03/01 00:29:46 cmorris Exp $
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
 * Represents the schematic definition of a
 * particular attribute in a particular Directory Server.
 *
 *  <p>The LDAPAttributeSchema class is used to query for an attribute's syntax,
 *  to add an attribute definition to a directory's schema, and to delete an
 *  attribute definition from the schema. See RFC 2252 for a description of
 *  attribute representation in LDAP.</p>
 *
 */
public class LDAPAttributeSchema extends LDAPSchemaElement {

    private String syntaxString;
    private boolean single = false;
    private String superior;
    private boolean obsolete = false;
    private String equality;
    private String ordering;
    private String substring;
    private boolean collective = false;
    private boolean userMod = true;
    private int usage = USER_APPLICATIONS;

 /**
  * This attribute definition defines that the attribute usage is applications.
  */
    public final static int USER_APPLICATIONS = 0;
 /**
  * This attribute definition defines the attribute usage is directory operations.
  */
    public final static int DIRECTORY_OPERATION = 1;
 /**
  * This attribute definition defines the attribute usage is shared dsa.
  */
    public final static int DISTRIBUTED_OPERATION = 2;
/**
  * This class definition defines the attribute usage is local dsa.
  */
    public final static int DSA_OPERATION = 3;

   /**
    * Constructs an attribute definition for adding to or deleting from a
    * directory's schema.
    *
    * @param name  Name of the attribute.
    *<br><br>
    * @param oid   Object identifer of the attribute, in
    *              dotted numerical format.
    *<br><br>
    * @param description   Optional description of the attribute.
    *<br><br>
    * @param syntaxString  Object identifer of the syntax of the
    *              attribute, in dotted numerical format.
    *<br><br>
    * @param single    True if the attribute is to be single-valued.
    *<br><br>
    * @param superior  Optional name of the attribute type which this
    *              attribute type derives from; null if there is no
    *              superior attribute type.
    *<br><br>
    * @param aliases   Optional list of additional names by which the
    *              attribute may be known; null if there are no
    *              aliases.
	*<br><br>
    * @param obsolete  True if the attribute is obsolete.
    *<br><br>
    * @param equality  Optional matching rule name; null if there is not
	*				an equality matching rule for this attribute.
	*<br><br>
    * @param ordering	Optional matching rule name; null if there is not
	*				an ordering matching rule for this attribute.
	*<br><br>
    * @param substring	Optional matching rule name; null if there is not
	*				a substring matching rule for this attribute.
	*<br><br>
    * @param collective	True of this attribute is a collective attribute
	*<br><br>
    * @param userMod	False if this attribute is a read-only attribute
	*<br><br>
    * @param useage		Describes what the attribute is used for. Must be
	*				one of the following: USER_APPLICATIONS,
	*				DIRECTORY_OPERATION, DISTRIBUTED_OPERATION or
	*				DSA_OPERATION.
    */
   public LDAPAttributeSchema(String name, String oid, String description,
                              String syntaxString, boolean single,
                              String superior, String[] aliases,
                              boolean obsolete, String equality, String ordering,
                              String substring, boolean collective, boolean userMod,
                              int usage) {

		super.name = name;
		super.oid = oid;
		super.description = description;
		this.syntaxString = syntaxString;
		this.single = single;
		this.superior = superior;
        if( aliases != null){
            super.aliases = new String[aliases.length];
		    for( int i = 0; i < super.aliases.length; i++ ){
	  	        super.aliases[i] = aliases[i];
		    }
        }
        this.obsolete = obsolete;
        this.equality = equality;
        this.ordering = ordering;
        this.substring = substring;
        this.collective = collective;
        this.userMod = userMod;
        this.usage = usage;
   }

   /**
    * Constructs an attribute definition from the raw string value returned
    * on a directory query for "attributetypes".
    *
    *  @param raw      The raw string value returned on a directory
    *                  query for "attributetypes".
    */
   public LDAPAttributeSchema(String raw) {
    try{
	    SchemaParser parser = new SchemaParser( raw );

        if( parser.getName() != null)
		    super.name = new String(parser.getName());
        super.aliases = parser.getAliases();
        if( parser.getID() != null)
            super.oid = new String(parser.getID());
        if( parser.getDescription() != null)
            super.description = new String(parser.getDescription());
        if( parser.getSyntax() != null)
            syntaxString = new String(parser.getSyntax());
        if( parser.getSuperior() != null)
            syntaxString = new String(parser.getSuperior());
        single = parser.getSingle();
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
    * Returns the object identifer of the syntax of the attribute, in
    * dotted numerical format.
    *
    * @return The object identifer of the attribute's syntax.
    */
   public String getSyntaxString() {
		return syntaxString;
   }

   /**
    * Returns the name of the attribute type which this attribute derives
    * from, or null if there is no superior attribute.
    *
    * @return The attribute's superior attribute, or null if there is none.
    */
   public String getSuperior() {
      return superior;
   }

   /**
    * Returns true if the attribute is single-valued.
    *
    * @return True if the attribute is single-valued; false if the attribute
    *         is multi-valued.
    */
   public boolean isSingleValued() {
      return single;
   }

   /**
    * Returns the matching rule for this attribute.
    *
    * @return The attribute's equality matching rule; null if it has no equality
    *		  matching rule.
    */
   public String getEqualityMatchingRule() {
      return equality;
   }

   /**
    * Returns the ordering matching rule for this attribute.
    *
    * @return The attribute's ordering matching rule; null if it has no ordering
    *		  matching rule.
    */

   public String getOrderingMatchingRule() {
      return ordering;
   }

  /**
    * Returns the substring matching rule for this attribute.
    *
    * @return The attribute's substring matching rule; null if it has no substring
    *		  matching rule.
    */

   public String getSubstringMatchingRule() {
      return substring;
   }

   /**
    * Returns true if the attribute is a collective attribute.
    *
    * @return True if the attribute is a collective; false if the attribute
    *         is not a collective attribute.
    */

   public boolean isCollective() {
      return collective;
   }

   /**
    * Returns false if the attribute is read-only.
    *
    * @return False if the attribute is read-only; true if the attribute
    *         is read-write.
    */

   public boolean isModifiable() {
      return userMod;
   }

   /**
    * Returns the usage of the attribute.
    *
    * @return Returns one of the following values: USER_APPLICATIONS,
	*		  DIRECTORY_OPERATION, DISTRIBUTED_OPERATION or
	*		  DSA_OPERATION.
    */

   public int getUsage() {
      return usage;
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
      if( (token = getSuperior()) != null){
        valueBuffer.append(" SUP ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getEqualityMatchingRule()) != null){
        valueBuffer.append(" EQUALITY ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getOrderingMatchingRule()) != null){
        valueBuffer.append(" ORDERING ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getSubstringMatchingRule()) != null){
        valueBuffer.append(" SUBSTR ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getSyntaxString()) != null){
        valueBuffer.append(" SYNTAX ");
        valueBuffer.append(token);
      }
      if( isSingleValued()){
        valueBuffer.append(" SINGLE-VALUE");
      }
      if( isCollective()){
        valueBuffer.append(" COLLECTIVE");
      }
      if( isModifiable() == false){
        valueBuffer.append(" NO-USER-MODIFICATION");
      }
      int useType;
      if( (useType = getUsage()) != USER_APPLICATIONS ){
        switch( useType){
        	case DIRECTORY_OPERATION :
           		valueBuffer.append( " USAGE directoryOperation" );
           		break;
          	case DISTRIBUTED_OPERATION :
           		valueBuffer.append( " USAGE distributedOperation" );
           		break;
        	case DSA_OPERATION :
         		valueBuffer.append( " USAGE dSAOperation" );
           		break;
             	default:
              		break;
        }
      }
      Enumeration en = getQualifierNames();
      while( en.hasMoreElements()){
        token = (String) en.nextElement();
        if( (token != null)){
          valueBuffer.append(" " + token );
          strArray = getQualifier(token);
          if(strArray != null){
            if(strArray.length > 1)
            	valueBuffer.append("(");
            for( int i = 0; i < strArray.length; i++ ){
              valueBuffer.append(" '" + strArray[i] + "'");
            }
            if(strArray.length > 1)
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
                    String[] attrSearchName= { "attributeTypes" };
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
		                    if(attrName.equalsIgnoreCase("attributeTypes")){
                                // add the value to the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                        "attributeTypes",getValue());
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
                    String[] attrSearchName= { "attributeTypes" };
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
		                if(attrName.equalsIgnoreCase("attributeTypes")){
                        // remove the value from the attributes values
                            LDAPAttribute newValue = new LDAPAttribute(
                                "attributeTypes",getValue());
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
    if( newValue instanceof LDAPAttributeSchema != true ){
        throw new LDAPException(LDAPExceptionMessageResource.NOT_AN_ATTRIBUTE, //"Schema element is not an LDAPAttributeSchema object",
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
                    String[] attrSearchName= { "attributeTypes" };
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
		                    if(attrName.equalsIgnoreCase("attributeTypes")){
                            // modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "attributeTypes", newValue.getValue());
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
/* **************************************************************************
 * $Novell$
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

/* public class LDAPSyntaxSchema
 *              extends LDAPSchemaElement
 */

/**
 * Represents a specific syntax definition in the directory schema.
 *
 * <p>The LDAPSyntaxSchema class is used to discover the known set of syntaxes
 * in effect for the subschema. </p>
 *
 * <p>Although this extends LDAPSchemaElement, it does not use the name or
 * obsolete members. Therefore, calls to the getName method always return
 * null and to the isObsolete method always returns false. There is also no
 * matching getSyntaxNames method in LDAPSchema. </p>
 */

public class LDAPSyntaxSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a syntax for adding to or deleting from the schema.
     *
     * <p>Adding and removing syntaxes is not typically a supported
     * feature of LDAP servers. NDS does not allow syntaxes to be added
     * or removed.</p>
     *
     * @param oid         The unique object identifier of the syntax - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the syntax.
     */
    public LDAPSyntaxSchema(String oid,
                           String description)
    {
        super.oid = oid;
      	super.description = description;
    }

    /**
     * Constructs a syntax from the raw string value returned on a schema
     * query for LDAPSyntaxes.
     *
     * @param raw           The raw string value returned from a schema
     *                      query for ldapSyntaxes.
     */
    public LDAPSyntaxSchema(String raw)
    {
    	try{
			SchemaParser parser = new SchemaParser( raw );

    		if( parser.getID() != null)
        		super.oid = new String(parser.getID());
        	if( parser.getDescription() != null)
        		super.description = new String(parser.getDescription());
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
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element class.
    *
    * @return A string representation of the syntax's definition.
    */
   public String getValue() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      if( (token = getDescription()) != null){
        valueBuffer.append(" DESC ");
        valueBuffer.append("'" + token + "'");
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
                    String[] attrSearchName= { "ldapSyntaxes" };
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
		                    if(attrName.equals("ldapSyntaxes")){
                            // add the value to the syntax values
							LDAPAttribute newValue = new LDAPAttribute(
                                        "ldapSyntaxes",getValue());
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
                    String[] attrSearchName= { "ldapSyntaxes" };
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
		                    if(attrName.equals("ldapSyntaxes")){
                                // remove the value from the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                	"ldapSyntaxes",getValue());
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
                    String[] attrSearchName= { "ldapSyntaxes" };
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
		                if(attrName.equals("ldapSyntaxes")){
                        	// modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "ldapSyntaxes", newValue.getValue());
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

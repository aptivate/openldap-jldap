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
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.client.AttributeQualifier;
import java.util.Enumeration;
import java.io.IOException;

/**
 *
 *  The schematic definition of a particular object class in
 *  a particular directory server.
 *
 *  <p>The LDAPObjectClassSchema class is used to query for the definition of an
 *  object class, and to add or delete an object class definition from a
 *  directory's schema. </p>
 *
 * @see LDAPSchemaElement
 * @see LDAPSchema
 */
public class LDAPObjectClassSchema extends LDAPSchemaElement{
  String[] superiors;
  String[] required;
  String[] optional;
  int type = -1;

 /**
  * This class definition defines an abstract schema class.
  *
  * <p>This is equivalent to setting the NDS effective class flag to true.</p>
  */
  public final static int ABSTRACT = 0;

 /**
  * This class definition defines a structural schema class.
  *
  * <p>This is equivalent to setting the NDS effective class flag to true.</p>
  */
  public final static int STRUCTURAL = 1;

 /**
  * This class definition defines an auxiliary schema class.
  */
  public final static int AUXILIARY = 2;

   /**
    * Constructs an object class definition for adding to or deleting from
    * a directory's schema.
    *
    *  @param name      Name of the object class.
    *<br><br>
    *  @param oid       Unique object identifer of the object class, in
    *                   dotted numerical format.
    *<br><br>
    *  @param description    Optional description of the object class.
    *<br><br>
    *  @param superiors      An array of names for object classes from which
    *                        this one inherits.
    *<br><br>
    *  @param required       An array of names for attributes which are required
    *                        for an entry with this object class.
    *<br><br>
    *  @param optional       An array of names for attributes which are optional
    *                        but not required for an entry with this object class.
    *<br><br>
    *  @param type           The type of class. Must be one of following:
    *                        ABSTRACT, AUXILIARY, or STRUCTURAL. These
    *                        constants are defined in LDAPObjectClassSchema.
    *<br><br>
    *  @param aliases        Optional list of additional names by which the
    *                        object class may be known; null if there are no
    *                        aliases.
	*<br><br>
    *  @param obsolete       true if this object is obsolete
    *
    */
   public LDAPObjectClassSchema(String name,
                                String oid,
                                String[] superiors,
                                String description,
                                String[] required,
                                String[] optional,
                                int type,
                                String[] aliases,
                                boolean obsolete) {
      super.name = name;
      super.oid = oid;
      super.description = description;
      this.type = type;
	  this.obsolete = obsolete;
      if( superiors != null){
      	this.superiors = new String[superiors.length];
		for( int i = 0; i < this.superiors.length; i++ ){
	  	this.superiors[i] = superiors[i];
       	 }
      }
	  if( required != null){
        this.required = new String[required.length];
		for( int i = 0; i < this.required.length; i++ ){
	  		this.required[i] = required[i];
        }
      }
      if( optional != null){
        this.optional = new String[optional.length];
		for( int i = 0; i < this.optional.length; i++ ){
	  		this.optional[i] = optional[i];
        }
      }

      if( aliases != null){
        super.aliases = new String[aliases.length];
	for( int i = 0; i < super.aliases.length; i++ ){
	  super.aliases[i] = aliases[i];
        }
       }
   }

   /**
    * Constructs an object class definition from the raw string value
    * returned from a directory query for "objectClasses".
    *
    *  @param raw      The raw string value returned from a directory
    *                  query for "objectClasses".
    */
   public LDAPObjectClassSchema(String raw) {
     try{
	SchemaParser parser = new SchemaParser( raw );

    	if( parser.getName() != null)
		super.name = new String(parser.getName());
  	super.aliases = parser.getAliases();
   	if( parser.getID() != null)
        	super.oid = new String(parser.getID());
        if( parser.getDescription() != null)
        	super.description = new String(parser.getDescription());
        super.obsolete = parser.getObsolete();
        if( parser.getRequired() != null)
        	required = (String[]) parser.getRequired().clone();
        if( parser.getOptional() != null)
        	optional = (String[])parser.getOptional().clone();
        if( parser.getSuperiors() != null)
        	superiors = (String[])parser.getSuperiors().clone();
        type = parser.getType();
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
    * Returns the object classes from which this one derives.
    *
    * @return The object classes superior to this class.
    */
   public String[] getSuperiors() {
      return superiors;
   }

   /**
    * Returns a list of attributes required for an entry with this object
    * class.
    *
    * @return The list of required attributes defined for this class.
    */
   public String[] getRequiredAttributes() {
      return required;
   }

   /**
    * Returns a list of optional attributes but not required of an entry
    * with this object class.
    *
    * @return The list of optional attributes defined for this class.
    */
   public String[] getOptionalAttributes() {
      return optional;
   }

   /**
    * Returns the type of object class.
    *
    *  <p>The getType method returns one of the following constants defined in
    *  LDAPObjectClassSchema:
    * <ul>
    *   <li>ABSTRACT</li>
    *   <li>AUXILIARY</li>
    *   <li>STRUCTURAL</li>
    *</ul>
    *<p>See the LDAPSchemaElement.getQualifier method for information on
    * obtaining the X-NDS flags.</p>
    *
    * @return The type of object class.
    */
   public int getType() {
      return type;
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
      if( (strArray = getSuperiors()) != null){
      	valueBuffer.append(" SUP ");
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
      if( getType() != -1){
      	if( getType() == LDAPObjectClassSchema.ABSTRACT)
       		valueBuffer.append( " ABSTRACT" );
        else if( getType() == LDAPObjectClassSchema.AUXILIARY)
        	valueBuffer.append( " AUXILIARY" );
      	else if( getType() == LDAPObjectClassSchema.STRUCTURAL)
       		valueBuffer.append( " STRUCTURAL" );
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
                    String[] attrSearchName= { "objectClasses" };
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
		                    if(attrName.equalsIgnoreCase("objectClasses")){
                            // add the value to the object class values
							LDAPAttribute newValue = new LDAPAttribute(
                                        "objectClasses",getValue());
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
                    String[] attrSearchName= { "objectClasses" };
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
		                    if(attrName.equalsIgnoreCase("objectClasses")){
                                // remove the value from the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                	"objectClasses",getValue());
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
                    String[] attrSearchName= { "objectClasses" };
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
		                if(attrName.equalsIgnoreCase("objectClasses")){
                        	// modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "objectClasses", newValue.getValue());
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

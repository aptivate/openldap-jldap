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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;
/**
 *  The schematic definition of a particular matching rule
 *  in a particular Directory Server.
 *
 *  <p>The LDAPMatchingRuleSchema class represents the definition of a mathcing
 *  rule.  It is used to query matching rule syntax, and to add or delete a
 *  matching rule definition in a directory.
 *
 * <p>Novell eDirectory does not currently allow matching rules to be added
 * or deleted from the schema.</p>
 *
 * @see LDAPAttributeSchema
 * @see LDAPSchemaElement
 * @see LDAPSchema
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement
{
    private String syntaxString;
    private String[] attributes;
   
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPMatchingRuleSchema()
	{
		super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING]);
	}
   
   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a directory.
    *
    *  @param names       The names of the attribute.
    *<br><br>
    *  @param oid         Object Identifier of the attribute - in
    *                     dotted-decimal format.
    *<br><br>
    *  @param description   Optional description of the attribute.
    *<br><br>
    *  @param attributes    The OIDs of attributes to which the rule applies.
    *                       This parameter may be null. All attributes added to
    *                       this array must use the same syntax.
    *<br><br>
    *  @param obsolete      true if this matching rule is obsolete.
    *<br><br>
    *
    *  @param syntaxString   The unique object identifer of the syntax of the
    *                        attribute, in dotted numerical format.
    *<br><br>
    */
   public LDAPMatchingRuleSchema(String[] names,
                                 String oid,
                                 String description,
                                 String[] attributes,
                                 boolean obsolete,
                                 String syntaxString)
   {
      super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING]);   
      super.names = (String[]) names.clone();
      super.oid = oid;
      super.description = description;
      super.obsolete = obsolete;
      this.attributes = (String[]) attributes.clone();
      this.syntaxString = syntaxString;
      super.setValue(formatString());
      return;
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
    super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING]);                                 
    try{
        SchemaParser matchParser = new SchemaParser(rawMatchingRule);
        super.names = (String[])matchParser.getNames().clone();
        super.oid = matchParser.getID();
        super.description = matchParser.getDescription();
        super.obsolete = matchParser.getObsolete();
        this.syntaxString = matchParser.getSyntax();
        if( rawMatchingRuleUse != null ){
            SchemaParser matchUseParser = new SchemaParser(rawMatchingRuleUse);
            this.attributes = matchUseParser.getApplies();
        }
        super.setValue(formatString());
    }
    catch( IOException e){
    }
    return;
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
   protected String formatString() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      strArray = getNames();
      if( strArray != null){
        valueBuffer.append(" NAME ");
        if (strArray.length == 1){
            valueBuffer.append("'" + strArray[0] + "'");
        }
        else {
           valueBuffer.append("( ");

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

	 protected void setDeserializedValues(BufferedInputStream istream)
	 throws IOException {
	   LDAPMatchingRuleSchema readObject = 
					   (LDAPMatchingRuleSchema)LDAPMatchingRuleSchema.readDSML(istream); 
		//	super classes properties 
		 this.oid = readObject.getID();	
		 this.names = readObject.getNames();
		 this.description = readObject.getDescription();
		 this.obsolete = readObject.isObsolete(); 
		 Enumeration enumer = readObject.getQualifierNames();
		 while(enumer.hasMoreElements()){
			 String xname = (String)enumer.nextElement();
			 String[] qualifierVals = readObject.getQualifier(xname);
			 this.setQualifier(xname, qualifierVals);		
		 }
		 super.setValue(formatString());
		  
		//class specific properties
		this.syntaxString = readObject.getSyntaxString();
		this.attributes = readObject.getAttributes();
				
		 //Garbage collect the readObject from readDSML()..	
		 readObject = null;
	 } 
    
   //Overloaded function for DSML..
   protected void writeValue(java.io.Writer out) throws IOException {
  	
	 String value = formatString();
	 out.write(ValueXMLhandler.newLine(2));
	 out.write("<value>");
	 out.write(value);
	 out.write("</value>");
  
   }        
  
   protected void writeValue(StringBuffer buff){
  	
	String value = formatString();
	 buff.append(ValueXMLhandler.newLine(2));
	 buff.append("<value>");
	 buff.append(value);
	 buff.append("</value>"); 
   }
	
   /**
	   * This method is used to deserialize the DSML encoded representation of
	   * this class.
	   * @param input InputStream for the DSML formatted data. 
	   * @return Deserialized form of this class.
	   * @throws IOException when serialization fails.
	   */    
	   public static Object readDSML(InputStream input)throws IOException    
	   {
		   SAXEventMultiplexer xmlreader = new SAXEventMultiplexer();
		   xmlreader.setLDAPXMLHandler(getTopXMLHandler("LDAPAttribute",null));		
		   return (LDAPMatchingRuleSchema) xmlreader.parseXML(input);
	   }
    
	   //This is added to fix the bug in parsing logic written in 
	   //getXMLHandler() method of this class 
	   private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
		 return new LDAPXMLHandler(tagname, parenthandler) {

		   java.util.List valuelist = new ArrayList();
		   protected void initHandler() {
			 //set LDAPAttribute handler.
			 setchildelement(LDAPMatchingRuleSchema.getXMLHandler("attr",this));
		   }

		   protected void endElement() {
				setObject((LDAPMatchingRuleSchema)valuelist.get(0));
		   }
		   protected void addValue(String tag, Object value) {
			 if (tag.equals("attr")) {
			   valuelist.add(value);
			 }
		   }
		 };

	   }

	   /**
	   * This method return the LDAPHandler which handles the XML (DSML) tags
	   * for this class
	   * @param tagname Name of the Root tag used to represent this class.
	   * @param parenthandler Parent LDAPXMLHandler for this tag.
	   * @return LDAPXMLHandler to handle this element.
	   */    
	   static LDAPXMLHandler getXMLHandler(String tagname,LDAPXMLHandler parenthandler)
	   {
		   return new LDAPXMLHandler(tagname,parenthandler){
		   String attrName;
		   java.util.List valuelist= new ArrayList();
		   protected void initHandler() {
			 //set value handler.
			 setchildelement(new ValueXMLhandler(this));          
		   }

		   protected void endElement() {
			
			   Iterator valueiterator = valuelist.iterator();
			LDAPMatchingRuleSchema attr = new LDAPMatchingRuleSchema();
			
			   byte[] temp = (byte[])valueiterator.next();
			   StringBuffer bf = new StringBuffer(temp.length);
			   for(int i=0; i < temp.length; i++)
				   bf.append((char)temp[i]);
         	
			   try {
				   
				SchemaParser matchParser = new SchemaParser(bf.toString());
				attr.names = (String[])matchParser.getNames().clone();
				attr.oid = matchParser.getID();
				attr.description = matchParser.getDescription();
				attr.obsolete = matchParser.getObsolete();
				attr.syntaxString = matchParser.getSyntax();
				
			   attr.setValue(attr.formatString());
			   } catch( IOException e) {
				   throw new RuntimeException(e.toString());
			   }
			   setObject(attr);
			   
			 valuelist.clear();
		   }
		   protected void addValue(String tag,Object value)
		   {
			   if (tag.equals("value"))
			   {
				   valuelist.add(value);
			   }
		   }

		   protected void handleAttributes(Attributes attributes)throws SAXException {
			   attrName = attributes.getValue("name");
			   if (attrName== null)
				   throw new SAXException("invalid attr Tag, name is mandatory element: ");
		   }
    		
		   };
    	
	   }

}

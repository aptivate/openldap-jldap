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

/**  Represents the definition of a specific matching rule use in the
 *  directory schema.
 *
 * <p>The LDAPMatchingRuleUseSchema class represents the definition of a
 * matching rule use.  It is used to discover or modify which attributes are
 * suitable for use with an extensible matching rule. It contains the name and
 * identifier of a matching rule, and a list of attributes which
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
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPMatchingRuleUseSchema()
	{
		super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING_USE]);
	}
	
    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @param names       Name(s) of the matching rule.
     *</br></br>
     * @param oid         Object Identifier of the the matching rule
     *                    in dotted-decimal format.
     *</br></br>
     * @param description Optional description of the matching rule use.
     *</br></br>
     * @param obsolete    True if the matching rule use is obsolete.
     *</br></br>
     * @param attributes  List of attributes that this matching rule
     *                    applies to. These values may be either the
     *                    names or numeric oids of the attributes.
     */
    public LDAPMatchingRuleUseSchema(String names[],
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING_USE]);
        super.names = (String[]) names.clone();
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        this.attributes = (String[]) attributes.clone();
        super.setValue(formatString());
        return;
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
        super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING_USE]);    
        try{
            SchemaParser matchParser = new SchemaParser(raw);
            super.names = (String[])matchParser.getNames().clone();
            super.oid = matchParser.getID();
            super.description = matchParser.getDescription();
            super.obsolete = matchParser.getObsolete();
            this.attributes = matchParser.getApplies();
            super.setValue(formatString());
        }
        catch( IOException e){
        }
        return;
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

	 protected void setDeserializedValues(BufferedInputStream istream)
	 throws IOException {
	   LDAPMatchingRuleUseSchema readObject = 
					   (LDAPMatchingRuleUseSchema)LDAPMatchingRuleUseSchema.readDSML(istream); 
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
		   return (LDAPMatchingRuleUseSchema) xmlreader.parseXML(input);
	   }
    
	   //This is added to fix the bug in parsing logic written in 
	   //getXMLHandler() method of this class 
	   private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
		 return new LDAPXMLHandler(tagname, parenthandler) {

		   java.util.List valuelist = new ArrayList();
		   protected void initHandler() {
			 //set LDAPAttribute handler.
			 setchildelement(LDAPMatchingRuleUseSchema.getXMLHandler("attr",this));
		   }

		   protected void endElement() {
				setObject((LDAPMatchingRuleUseSchema)valuelist.get(0));
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
			LDAPMatchingRuleUseSchema attr = new LDAPMatchingRuleUseSchema(attrName);
			
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
				attr.attributes = matchParser.getApplies();
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

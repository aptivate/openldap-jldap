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

import com.novell.ldap.client.AttributeQualifier;
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;

/**
 * Represents the definition of a specific DIT (Directory Information Tree)
 * structure rule in the directory schema.
 *
 * <p>The LDAPDITStructureRuleSchema class represents the definition of a DIT
 * Structure Rule.  It is used to discover or modify which
 * object classes a particular object class may be subordinate to in the DIT.</p>
 *
 */

public class LDAPDITStructureRuleSchema
                extends LDAPSchemaElement
{
    private int ruleID = 0;
    private String nameForm = "";
    private String[] superiorIDs = {""};

	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPDITStructureRuleSchema()
	{
		super(LDAPSchema.schemaTypeNames[LDAPSchema.DITSTRUCTURE]);
	}
    
    /**  Constructs a DIT structure rule for adding to or deleting from the
     *   schema.
     *
     * @param names       The names of the structure rule.
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
     *
     */
    public LDAPDITStructureRuleSchema(String[] names,
                                      int ruleID,
                                      String description,
                                      boolean obsolete,
                                      String nameForm,
                                      String[] superiorIDs)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.DITSTRUCTURE]);    
        super.names = (String[]) names.clone();
        this.ruleID = ruleID;
        super.description = description;
        super.obsolete = obsolete;
        this.nameForm = nameForm;
        this.superiorIDs = superiorIDs;
        super.setValue(formatString());
        return;
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
        super(LDAPSchema.schemaTypeNames[LDAPSchema.DITSTRUCTURE]);        
        super.obsolete = false;
        try{
            SchemaParser parser = new SchemaParser( raw );

            if( parser.getNames() != null)
                super.names = (String[])parser.getNames().clone();

            if( parser.getID() != null)
                ruleID = Integer.parseInt(parser.getID());
            if( parser.getDescription() != null)
                super.description = parser.getDescription();
            if( parser.getSuperiors() != null)
                superiorIDs = (String[])parser.getSuperiors().clone();
            if( parser.getNameForm() != null)
                nameForm = parser.getNameForm();
            super.obsolete = parser.getObsolete();
            Enumeration qualifiers = parser.getQualifiers();
            AttributeQualifier attrQualifier;
            while(qualifiers.hasMoreElements()){
                attrQualifier = (AttributeQualifier) qualifiers.nextElement();
                setQualifier(attrQualifier.getName(), attrQualifier.getValues());
            }
            super.setValue(formatString());
        }
        catch( IOException e){
        }
        return;
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
   protected String formatString() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      token = String.valueOf( getRuleID());
      valueBuffer.append(token);

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

	 protected void setDeserializedValues(BufferedInputStream istream)
	 throws IOException {
	   LDAPDITStructureRuleSchema readObject = 
					   (LDAPDITStructureRuleSchema)LDAPDITStructureRuleSchema.readDSML(istream); 
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
		this.ruleID = readObject.getRuleID();
		this.nameForm = readObject.getName();
		this.superiorIDs = readObject.getSuperiors();	 
	
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
		   return (LDAPDITStructureRuleSchema) xmlreader.parseXML(input);
	   }
    
	   //This is added to fix the bug in parsing logic written in 
	   //getXMLHandler() method of this class 
	   private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
		 return new LDAPXMLHandler(tagname, parenthandler) {

		   java.util.List valuelist = new ArrayList();
		   protected void initHandler() {
			 //set LDAPAttribute handler.
			 setchildelement(LDAPDITStructureRuleSchema.getXMLHandler("attr",this));
		   }

		   protected void endElement() {
				setObject((LDAPDITStructureRuleSchema)valuelist.get(0));
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
			LDAPDITStructureRuleSchema attr = new LDAPDITStructureRuleSchema(attrName);
			
			   byte[] temp = (byte[])valueiterator.next();
			   StringBuffer bf = new StringBuffer(temp.length);
			   for(int i=0; i < temp.length; i++)
				   bf.append((char)temp[i]);
         	
			   try {
				   SchemaParser parser = new SchemaParser(bf.toString());

				if( parser.getNames() != null)
				attr.names = (String[])parser.getNames().clone();

				if( parser.getID() != null)
					attr.ruleID = Integer.parseInt(parser.getID());
				if( parser.getDescription() != null)
					attr.description = parser.getDescription();
				if( parser.getSuperiors() != null)
					attr.superiorIDs = (String[])parser.getSuperiors().clone();
				if( parser.getNameForm() != null)
					attr.nameForm = parser.getNameForm();
				attr.obsolete = parser.getObsolete();
				Enumeration qualifiers = parser.getQualifiers();
				AttributeQualifier attrQualifier;
				while(qualifiers.hasMoreElements()){
					attrQualifier = (AttributeQualifier) qualifiers.nextElement();
					attr.setQualifier(attrQualifier.getName(), attrQualifier.getValues());
				}
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

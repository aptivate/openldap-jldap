/* **************************************************************************
 * $OpenLDAP:
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
 * A specific a name form in the directory schema.
 *
 * <p>The LDAPNameFormSchema class represents the definition of a Name Form.  It
 * is used to discover or modify the allowed naming attributes for a particular
 * object class.</p>
 *
 * @see LDAPSchemaElement
 * @see LDAPSchema
 */

public class LDAPNameFormSchema
                extends LDAPSchemaElement
{
    private String objectClass;
    private String[] required;
    private String[] optional;

	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPNameFormSchema()
	{
		super(LDAPSchema.schemaTypeNames[LDAPSchema.NAME_FORM]);    
		super.obsolete = false;
	}
	
    /**
     * Constructs a name form for adding to or deleting from the schema.
     *
     * @param names       The name(s) of the name form.</br></br>
     *
     * @param oid         The unique object identifier of the name form - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the name form.</br></br>
     *
     * @param obsolete    True if the name form is obsolete.</br></br>
     *
     * @param objectClass The object to which this name form applies.
     *                    This may be specified by either name or
     *                    numeric oid.</br></br>
     *
     * @param required    A list of the attributes that must be present
     *                    in the RDN of an entry that this name form
     *                    controls. These attributes may be specified by
     *                    either name or numeric oid.</br></br>
     *
     * @param optional    A list of the attributes that may be present
     *                    in the RDN of an entry that this name form
     *                    controls. These attributes may be specified by
     *                    either name or numeric oid.</br></br>
     */
    public LDAPNameFormSchema(String[] names,
                              String oid,
                              String description,
                              boolean obsolete,
                              String objectClass,
                              String[] required,
                              String[] optional)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.NAME_FORM]);
        super.names = (String[]) names.clone();
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        this.objectClass = objectClass;
        this.required = (String[])required.clone();
        this.optional = (String[])optional.clone();
        super.setValue( formatString() );
        return;
    }

    /*
    }

    /**
     * Constructs a Name Form from the raw string value returned on a
     * schema query for nameForms.
     *
     * @param raw        The raw string value returned on a schema
     *                   query for nameForms.
     */
    public LDAPNameFormSchema(String raw)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.NAME_FORM]);    
        super.obsolete = false;
        try{
            SchemaParser parser = new SchemaParser( raw );

            if( parser.getNames() != null)
                super.names = (String[])parser.getNames().clone();
            if( parser.getID() != null)
                super.oid = new String(parser.getID());
            if( parser.getDescription() != null)
                super.description = new String(parser.getDescription());
            if( parser.getRequired() != null)
                required = (String[])parser.getRequired().clone();
            if( parser.getOptional() != null)
                optional = (String[])parser.getOptional().clone();
            if( parser.getObjectClass() != null)
                objectClass = parser.getObjectClass();
            super.obsolete = parser.getObsolete();
            Enumeration qualifiers = parser.getQualifiers();
            AttributeQualifier attrQualifier;
            while(qualifiers.hasMoreElements()){
                attrQualifier = (AttributeQualifier) qualifiers.nextElement();
                setQualifier(attrQualifier.getName(), attrQualifier.getValues());
            }
            super.setValue( formatString() );
        }
        catch( IOException e){
        }
        return;
    }

    /**
     * Returns the name of the object class which this name form applies to.
     *
     * @return The name of the object class.
     */
    public String getObjectClass()
    {
        return objectClass;
    }


    /**
     * Returns the list of required naming attributes for an entry
     * controlled by this name form.
     *
     * @return The list of required naming attributes.
     */
    public String[]getRequiredNamingAttributes()
    {
        return required;
    }

    /**
     * Returns the list of optional naming attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of the optional naming attributes.
     */
    public String[]getOptionalNamingAttributes()
    {
        return optional;
    }

    /**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element class.
    *
    * @return A string representation of the class' definition.
    */
   protected String formatString()
   {
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
      if( (token = getObjectClass()) != null){
        valueBuffer.append(" OC ");
        valueBuffer.append("'" + token + "'");
      }
      if( (strArray = getRequiredNamingAttributes()) != null){
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
      if( (strArray = getOptionalNamingAttributes()) != null){
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
   
  protected void setDeserializedValues(BufferedInputStream istream)
  throws IOException {
	LDAPNameFormSchema readObject = 
					(LDAPNameFormSchema)LDAPNameFormSchema.readDSML(istream); 
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
	 this.objectClass = readObject.getObjectClass();
	 this.required = readObject.getRequiredNamingAttributes();
	 this.optional = readObject.getOptionalNamingAttributes();
	 	
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
		return (LDAPNameFormSchema) xmlreader.parseXML(input);
	}
    
	//This is added to fix the bug in parsing logic written in 
	//getXMLHandler() method of this class 
	private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
	  return new LDAPXMLHandler(tagname, parenthandler) {

		java.util.List valuelist = new ArrayList();
		protected void initHandler() {
		  //set LDAPAttribute handler.
		  setchildelement(LDAPNameFormSchema.getXMLHandler("attr",this));
		}

		protected void endElement() {
			 setObject((LDAPNameFormSchema)valuelist.get(0));
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
		 LDAPNameFormSchema attr = new LDAPNameFormSchema(attrName);
			
			byte[] temp = (byte[])valueiterator.next();
			StringBuffer bf = new StringBuffer(temp.length);
			for(int i=0; i < temp.length; i++)
				bf.append((char)temp[i]);
         	
			try {
				SchemaParser parser = new SchemaParser(bf.toString());

				if( parser.getNames() != null)
				   attr.names = (String[])parser.getNames().clone();
			   if( parser.getID() != null)
			   		attr.oid = new String(parser.getID());
			   if( parser.getDescription() != null)
			   		attr.description = new String(parser.getDescription());
			   if( parser.getRequired() != null)
			   		attr.required = (String[])parser.getRequired().clone();
			   if( parser.getOptional() != null)
			   		attr.optional = (String[])parser.getOptional().clone();
			   if( parser.getObjectClass() != null)
			   		attr.objectClass = parser.getObjectClass();
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

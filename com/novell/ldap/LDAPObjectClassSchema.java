/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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
 *  The schema definition of an object class in a directory server.
 *
 *  <p>The LDAPObjectClassSchema class represents the definition of an object
 *  class.  It is used to query the syntax of an object class.
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
  * <p>This is equivalent to setting the Novell eDirectory effective class
  * flag to true.</p>
  */
  public final static int ABSTRACT = 0;

 /**
  * This class definition defines a structural schema class.
  *
  * <p>This is equivalent to setting the Novell eDirectory effective class
  * flag to true.</p>
  */
  public final static int STRUCTURAL = 1;

 /**
  * This class definition defines an auxiliary schema class.
  */
  public final static int AUXILIARY = 2;

  /**
   * This constructor was added to support default Serialization
   *
   */
  public LDAPObjectClassSchema()
  {
	super(LDAPSchema.schemaTypeNames[LDAPSchema.OBJECT_CLASS]);
  }
   
   /**
    * Constructs an object class definition for adding to or deleting from
    * a directory's schema.
    *
    *  @param names     Name(s) of the object class.
    *<br><br>
    *  @param oid       Object Identifer of the object class - in
    *                   dotted-decimal format.
    *<br><br>
    *  @param description    Optional description of the object class.
    *<br><br>
    *  @param superiors      The object classes from which this one derives.
    *<br><br>
    *  @param required       A list of attributes required
    *                        for an entry with this object class.
    *<br><br>
    *  @param optional       A list of attributes acceptable but not required
    *                       for an entry with this object class.
    *<br><br>
    *  @param type           One of ABSTRACT, AUXILIARY, or STRUCTURAL. These
    *                        constants are defined in LDAPObjectClassSchema.
    *<br><br>
    *  @param obsolete       true if this object is obsolete
    *
    */
   public LDAPObjectClassSchema(String[] names,
                                String oid,
                                String[] superiors,
                                String description,
                                String[] required,
                                String[] optional,
                                int type,
                                boolean obsolete)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.OBJECT_CLASS]);
        super.names = (String[]) names.clone();
        super.oid = oid;
        super.description = description;
        this.type = type;
        this.obsolete = obsolete;
        if( superiors != null){
            this.superiors = (String[]) superiors.clone();
        }
        if( required != null){
            this.required = (String[]) required.clone();
        }
        if( optional != null){
            this.optional = (String[]) optional.clone();
        }
        super.setValue(formatString());
        return;
   }



   /**
    * Constructs an object class definition from the raw string value
    * returned from a directory query for "objectClasses".
    *
    *  @param raw      The raw string value returned from a directory
    *                  query for "objectClasses".
    */
    public LDAPObjectClassSchema(String raw)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.OBJECT_CLASS]);
        try{
            SchemaParser parser = new SchemaParser( raw );

            if( parser.getNames() != null)
                super.names = (String[])parser.getNames().clone();

            if( parser.getID() != null)
                super.oid = parser.getID();
            if( parser.getDescription() != null)
                super.description = parser.getDescription();
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
            super.setValue( formatString() );
        }
        catch( IOException e){
        }
        return;
    }

   /**
    * Returns the object classes from which this one derives.
    *
    * @return The object classes superior to this class.
    */
   public String[] getSuperiors()
   {
      return superiors;
   }

   /**
    * Returns a list of attributes required for an entry with this object
    * class.
    *
    * @return The list of required attributes defined for this class.
    */
   public String[] getRequiredAttributes()
   {
      return required;
   }

   /**
    * Returns a list of optional attributes but not required of an entry
    * with this object class.
    *
    * @return The list of optional attributes defined for this class.
    */
   public String[] getOptionalAttributes()
   {
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
   public int getType()
   {
      return type;
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

	 protected void setDeserializedValues(BufferedInputStream istream)
	 throws IOException {
	   LDAPObjectClassSchema readObject = 
					   (LDAPObjectClassSchema)LDAPObjectClassSchema.readDSML(istream); 
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
		this.superiors = readObject.getSuperiors();
		this.required = readObject.getRequiredAttributes();
		this.optional = readObject.getOptionalAttributes();
		this.type = readObject.getType();	 
	
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
		   return (LDAPObjectClassSchema) xmlreader.parseXML(input);
	   }
    
	   //This is added to fix the bug in parsing logic written in 
	   //getXMLHandler() method of this class 
	   private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
		 return new LDAPXMLHandler(tagname, parenthandler) {

		   java.util.List valuelist = new ArrayList();
		   protected void initHandler() {
			 //set LDAPAttribute handler.
			 setchildelement(LDAPObjectClassSchema.getXMLHandler("attr",this));
		   }

		   protected void endElement() {
				setObject((LDAPObjectClassSchema)valuelist.get(0));
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
			   LDAPObjectClassSchema attr = new LDAPObjectClassSchema(attrName);
			
			   byte[] temp = (byte[])valueiterator.next();
			   StringBuffer bf = new StringBuffer(temp.length);
			   for(int i=0; i < temp.length; i++)
				   bf.append((char)temp[i]);
         	
			   try {
				   SchemaParser parser = new SchemaParser(bf.toString());

				if( parser.getNames() != null)
				attr.names = (String[])parser.getNames().clone();

				if( parser.getID() != null)
					attr.oid = parser.getID();
				if( parser.getDescription() != null)
					attr.description = parser.getDescription();
				attr.obsolete = parser.getObsolete();
				if( parser.getRequired() != null)
					attr.required = (String[]) parser.getRequired().clone();
				if( parser.getOptional() != null)
					attr.optional = (String[])parser.getOptional().clone();
				if( parser.getSuperiors() != null)
					attr.superiors = (String[])parser.getSuperiors().clone();
				attr.type = parser.getType();
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

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
import java.io.Externalizable;
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
 * Represents a syntax definition in the directory schema.
 *
 * <p>The LDAPSyntaxSchema class represents the definition of a syntax.  It is
 * used to discover the known set of syntaxes in effect for the subschema. </p>
 *
 * <p>Although this extends LDAPSchemaElement, it does not use the name or
 * obsolete members. Therefore, calls to the getName method always return
 * null and to the isObsolete method always returns false. There is also no
 * matching getSyntaxNames method in LDAPSchema. Note also that adding and
 * removing syntaxes is not typically a supported feature of LDAP servers.</p>
 */

public class LDAPSyntaxSchema extends LDAPSchemaElement
	implements Externalizable
{
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPSyntaxSchema()
	{
		super(LDAPSchema.schemaTypeNames[LDAPSchema.SYNTAX]);
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
        super(LDAPSchema.schemaTypeNames[LDAPSchema.SYNTAX]);
        try {
            SchemaParser parser = new SchemaParser( raw );

            if( parser.getID() != null)
                super.oid = parser.getID();
            if( parser.getDescription() != null)
                super.description = parser.getDescription();
            Enumeration qualifiers = parser.getQualifiers();
            AttributeQualifier attrQualifier;
            while(qualifiers.hasMoreElements()){
                attrQualifier = (AttributeQualifier) qualifiers.nextElement();
                setQualifier(attrQualifier.getName(),attrQualifier.getValues());
            }
            super.setValue( formatString() );
        } catch( IOException e) {
            throw new RuntimeException(e.toString());
        }
        return;
    }
    
    /**
     * Constructs a syntax for adding to or deleting from the schema.
     *
     * <p>Adding and removing syntaxes is not typically a supported
     * feature of LDAP servers. Novell eDirectory does not allow syntaxes to
     * be added or removed.</p>
     *
     * @param oid         The unique object identifier of the syntax - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the syntax.
     */
    public LDAPSyntaxSchema(String oid, String description)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.SYNTAX]);
        super.oid = oid;
        super.description = description;
        super.setValue(formatString());
        return;
    }

    /**
     * Returns a string in a format suitable for directly adding to a
     * directory, as a value of the particular schema element class.
     *
     * @return A string representation of the syntax's definition.
     */
    protected String formatString()
    {
        StringBuffer valueBuffer = new StringBuffer("( ");
        String token;

        if( (token = getID()) != null){
            valueBuffer.append(token);
        }
        if( (token = getDescription()) != null){
            valueBuffer.append(" DESC ");
            valueBuffer.append("'" + token + "'");
        }

        Enumeration en;
        if( (en = getQualifierNames()) != null) {
            String qualName;
            String[] qualValue;
            while( en.hasMoreElements() ) {
                qualName = (String)en.nextElement();
                valueBuffer.append( " " + qualName + " ");
                if((qualValue = getQualifier( qualName )) != null) {
                    if( qualValue.length > 1) {
                        valueBuffer.append("( ");
                        for(int i = 0; i < qualValue.length; i++ ) {
                            if( i > 0 ) {
                                valueBuffer.append(" ");
                            }    
                            valueBuffer.append( "'" + qualValue[i] + "'");
                        }
                        if( qualValue.length > 1) {
                            valueBuffer.append(" )");
                        }        
                    }            
                }
            }
        }
        valueBuffer.append(" )");
		return valueBuffer.toString();
    }
	 
	  protected void setDeserializedValues(BufferedInputStream istream)
	  throws IOException {
		LDAPSyntaxSchema readObject = 
						(LDAPSyntaxSchema)LDAPSyntaxSchema.readDSML(istream);
		  this.oid = readObject.oid;	
		  this.names = readObject.getNames();
		  this.obsolete = readObject.obsolete;
		  this.description = readObject.description;
		  Enumeration enumer = readObject.getQualifierNames();
		  while(enumer.hasMoreElements()){
			  String xname = (String)enumer.nextElement();
			  String[] qualifierVals = readObject.getQualifier(xname);
			  this.setQualifier(xname, qualifierVals);		
		  }
		  super.setValue(formatString());
	
		  //Garbage collect the readObject from readDSML()..	
		  readObject = null;
	  } 
    
	//Overloaded function for DSML..
	protected void writeValue(java.io.Writer out) throws IOException {
  	
		String oid = this.getID();
	  //String desc = this.getDescription();
	  //String names[] = this.getNames();
	  //boolean obs = this.isObsolete();
	  Enumeration enumer = this.getQualifierNames();
	  String xname = (String)enumer.nextElement(); //only single element in this
	  String[] qualifierVals = this.getQualifier(xname); //only single element in this
	
	  String value = 
		  "( " + oid + " " + xname + " '" + qualifierVals[0] + "' )";
	  out.write(ValueXMLhandler.newLine(2));
	  out.write("<value>");
	  out.write(value);
	  out.write("</value>");
  
	}        
  
	protected void writeValue(StringBuffer buff){
  	
	  String oid = this.getID();
	  Enumeration enumer = this.getQualifierNames();
	  String xname = (String)enumer.nextElement();
	  String[] qualifierVals = this.getQualifier(xname);
	
	  String value = 
		  "( " + oid + " " + xname + " '" + qualifierVals[0] + "' )";
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
			return (LDAPSyntaxSchema) xmlreader.parseXML(input);
		}
    
		//This is added to fix the bug in parsing logic written in 
		//getXMLHandler() method of this class 
		private static LDAPXMLHandler getTopXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
		  return new LDAPXMLHandler(tagname, parenthandler) {

			java.util.List valuelist = new ArrayList();
			protected void initHandler() {
			  //set LDAPAttribute handler.
			  setchildelement(LDAPSyntaxSchema.getXMLHandler("attr",this));
			}

			protected void endElement() {
				 setObject((LDAPSyntaxSchema)valuelist.get(0));
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
				LDAPSyntaxSchema attr = new LDAPSyntaxSchema(attrName);
			
				byte[] temp = (byte[])valueiterator.next();
				StringBuffer bf = new StringBuffer(temp.length);
				for(int i=0; i < temp.length; i++)
					bf.append((char)temp[i]);
         	
				try {
					SchemaParser parser = new SchemaParser(bf.toString());

					if( parser.getID() != null)
						attr.oid = parser.getID();
					if( parser.getDescription() != null)
						attr.description = parser.getDescription();
					Enumeration qualifiers = parser.getQualifiers();
					AttributeQualifier attrQualifier;
					while(qualifiers.hasMoreElements()){
						attrQualifier = (AttributeQualifier) qualifiers.nextElement();
						attr.setQualifier(attrQualifier.getName(),attrQualifier.getValues());
					}
					attr.setValue( attr.formatString() );
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

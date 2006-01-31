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
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.novell.ldap.util.Base64;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;
/**
 *
 * A set of {@link LDAPAttribute} objects.
 *
 * <p>An <code>LDAPAttributeSet</code> is a collection of <code>LDAPAttribute</code>
 * classes as returned from an <code>LDAPEntry</code> on a search or read
 * operation. <code>LDAPAttributeSet</code> may be also used to contruct an entry
 * to be added to a directory.  If the <code>add()</code> or <code>addAll()</code>
 * methods are called and one or more of the objects to be added is not
 * an <code>LDAPAttribute, ClassCastException</code> is thrown (as discussed in the
 * documentation for <code>java.util.Collection</code>).
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/AddEntry.java.html">AddEntry.java</p>
 *
 * @see LDAPAttribute
 * @see LDAPEntry
 */
public class LDAPAttributeSet
        extends java.util.AbstractSet
        implements Cloneable, java.util.Set, Externalizable
{

    /**
     * This is the underlying data structure for this set.
     * <p>HashSet is similar to the functionality of this set.  The difference
     * is we use the name of an attribute as keys in the Map and LDAPAttributes
     * as the values.  We also do not declare the map as transient, making the
     * map serializable.</p>
     */
    private HashMap map;

    /**
     * Constructs an empty set of attributes.
     */
    public LDAPAttributeSet() {
        super();
        this.clear();
    }

// ---  methods not defined in Set ---

    /**
     * Returns a deep copy of this attribute set.
     *
     * @return A deep copy of this attribute set.
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
			((LDAPAttributeSet)newObj).clear();
            Iterator i = this.iterator();
            while (i.hasNext()){
                ((LDAPAttributeSet)newObj).add( ((LDAPAttribute)i.next()).clone());
            }
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Returns the attribute matching the specified attrName.
     *
     * <p>For example:</p>
     * <ul>
     * <li><code>getAttribute("cn")</code>      returns only the "cn" attribute</li>
     * <li><code>getAttribute("cn;lang-en")</code> returns only the "cn;lang-en"
     *                                 attribute.</li>
     * </ul>
     * <p>In both cases, <code>null</code> is returned if there is no exact match to
     * the specified attrName.</p>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @param attrName   The name of an attribute to retrieve, with or without
     * subtype specifications. For example, "cn", "cn;phonetic", and
     * "cn;binary" are valid attribute names.
     *
     * @return The attribute matching the specified attrName, or <code>null</code>
     * if there is no exact match.
     */
    public LDAPAttribute getAttribute(String attrName) {
        return (LDAPAttribute)map.get(attrName.toUpperCase());
    }

    /**
     * Returns a single best-match attribute, or <code>null</code> if no match is
     * available in the entry.
     *
     * <p>LDAP version 3 allows adding a subtype specification to an attribute
     * name. For example, "cn;lang-ja" indicates a Japanese language
     * subtype of the "cn" attribute and "cn;lang-ja-JP-kanji" may be a subtype
     * of "cn;lang-ja". This feature may be used to provide multiple
     * localizations in the same directory. For attributes which do not vary
     * among localizations, only the base attribute may be stored, whereas
     * for others there may be varying degrees of specialization.</p>
     *
     * <p>For example, <code>getAttribute(attrName,lang)</code> returns the
     * <code>LDAPAttribute</code> that exactly matches attrName and that
     * best matches lang.</p>
     *
     * <p>If there are subtypes other than "lang" subtypes included
     * in attrName, for example, "cn;binary", only attributes with all of
     * those subtypes are returned. If lang is <code>null</code> or empty, the
     * method behaves as getAttribute(attrName). If there are no matching
     * attributes, <code>null</code> is returned. </p>
     *
     *
     * <p>Assume the entry contains only the following attributes:</p>
     *
     *  <ul>
     *  <li>cn;lang-en</li>
     *  <li>cn;lang-ja-JP-kanji</li>
     *  <li>sn</li>
     *  </ul>
     *
     *  <p>Examples:</p>
     *  <ul>
     *  <li><code>getAttribute( "cn" )</code>       returns <code>null</code>.</li>
     *  <li><code>getAttribute( "sn" )</code>       returns the "sn" attribute.</li>
     *  <li><code>getAttribute( "cn", "lang-en-us" )</code>
     *                              returns the "cn;lang-en" attribute.</li>
     *   <li><code>getAttribute( "cn", "lang-en" )</code>
     *                              returns the "cn;lang-en" attribute.</li>
     *   <li><code>getAttribute( "cn", "lang-ja" )</code>
     *                              returns <code>null</code>.</li>
     *   <li><code>getAttribute( "sn", "lang-en" )</code>
     *                              returns the "sn" attribute.</li>
     *  </ul>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @param attrName  The name of an attribute to retrieve, with or without
     * subtype specifications. For example, "cn", "cn;phonetic", and
     * cn;binary" are valid attribute names.
     *<br><br>
     * @param lang   A language specification with optional subtypes
     * appended using "-" as separator. For example, "lang-en", "lang-en-us",
     * "lang-ja", and "lang-ja-JP-kanji" are valid language specification.
     *
     * @return A single best-match <code>LDAPAttribute</code>, or <code>null</code>
     * if no match is found in the entry.
     *
     */
    public LDAPAttribute getAttribute(String attrName, String lang) {
        String key = attrName + ";" + lang;
        return (LDAPAttribute)map.get(key.toUpperCase());
    }

    /**
     * Creates a new attribute set containing only the attributes that have
     * the specified subtypes.
     *
     * <p>For example, suppose an attribute set contains the following
     * attributes:</p>
     *
     * <ul>
     * <li>    cn</li>
     * <li>    cn;lang-ja</li>
     * <li>    sn;phonetic;lang-ja</li>
     * <li>    sn;lang-us</li>
     * </ul>
     *
     * <p>Calling the <code>getSubset</code> method and passing lang-ja as the
     * argument, the method returns an attribute set containing the following
     * attributes:</p>
     *
     * <ul>
     *     <li>cn;lang-ja</li>
     *     <li>sn;phonetic;lang-ja</li>
     * </ul>
     *
     *  @param subtype    Semi-colon delimited list of subtypes to include. For
     *  example:
     * <ul>
     * <li> "lang-ja" specifies only Japanese language subtypes</li>
     * <li> "binary" specifies only binary subtypes</li>
     * <li> "binary;lang-ja" specifies only Japanese language subtypes
     *       which also are binary</li>
     * </ul>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @return An attribute set containing the attributes that match the
     *          specified subtype.
     */
    public LDAPAttributeSet getSubset(String subtype) {

        // Create a new tempAttributeSet
        LDAPAttributeSet tempAttributeSet = new LDAPAttributeSet();
        Iterator i = this.iterator();

        // Cycle throught this.attributeSet
        while (i.hasNext()){
            LDAPAttribute attr = (LDAPAttribute)i.next();

            // Does this attribute have the subtype we are looking for. If
            // yes then add it to our AttributeSet, else next attribute
            if (attr.hasSubtype(subtype))
                tempAttributeSet.add(attr.clone());
        }
        return tempAttributeSet;
    }

// --- methods defined in set ---

    /**
     * Returns an iterator over the attributes in this set.  The attributes
     * returned from this iterator are not in any particular order.
     *
     * @return iterator over the attributes in this set
     */
    public Iterator iterator(){
        return this.map.values().iterator();
    }

    /**
     * Returns the number of attributes in this set.
     *
     * @return number of attributes in this set.
     */
    public int size(){
        return this.map.size();
    }

    /**
     * Returns <code>true</code> if this set contains no elements
     *
     * @return <code>true</code> if this set contains no elements
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Returns <code>true</code> if this set contains an attribute of the same name
     * as the specified attribute.
     *
     * @param attr   Object of type <code>LDAPAttribute</code>
     *
     * @return true if this set contains the specified attribute
     *
     * @throws ClassCastException occurs the specified Object
     * is not of type LDAPAttribute.
     */
    public boolean contains(Object attr) {
        LDAPAttribute attribute = (LDAPAttribute) attr;
        return this.map.containsKey( attribute.getName().toUpperCase() );
    }

    /**
     * Adds the specified attribute to this set if it is not already present.
     * <p>If an attribute with the same name already exists in the set then the
     * specified attribute will not be added.</p>
     *
     * @param attr   Object of type <code>LDAPAttribute</code>
     *
     * @return true if the attribute was added.
     *
     * @throws ClassCastException occurs the specified Object
     * is not of type <code>LDAPAttribute</code>.
     */
    public boolean add(Object attr) {
        //We must enforce that attr is an LDAPAttribute
        LDAPAttribute attribute = (LDAPAttribute) attr;
        String name = attribute.getName().toUpperCase();
        if( this.map.containsKey(name))
            return false;
        else{
            this.map.put(name, attribute);
            return true;
        }
    }

    /**
     * Removes the specified object from this set if it is present.
     *
     * <p>If the specified object is of type <code>LDAPAttribute</code>, the
     * specified attribute will be removed.  If the specified object is of type
     * <code>String</code>, the attribute with a name that matches the string will
     * be removed.</p>
     *
     * @param object LDAPAttribute to be removed or <code>String</code> naming
     * the attribute to be removed.
     *
     * @return true if the object was removed.
     *
     * @throws ClassCastException occurs the specified Object
     * is not of type <code>LDAPAttribute</code> or of type <code>String</code>.
     */
    public boolean remove(Object object) {
        String attributeName; //the name is the key to object in the HashMap
        if (object instanceof String){
            attributeName = (String)object;
        }
        else {
            attributeName = ((LDAPAttribute) object).getName();
        }
        if (attributeName == null){
            return false;
        }
        return (this.map.remove( attributeName.toUpperCase() ) != null );
    }

    /**
     * Removes all of the elements from this set.
     */
    public void clear(){
        this.map = new HashMap();
    }

    /**
     * Adds all <code>LDAPAttribute</code> objects in the specified collection to
     * this collection.
     *
     * @param c  Collection of <code>LDAPAttribute</code> objects.
     *
     * @throws ClassCastException occurs when an element in the
     * collection is not of type <code>LDAPAttribute</code>.
     *
     * @return true if this set changed as a result of the call.
     */
    public boolean addAll(java.util.Collection c){
        boolean setChanged = false;
        Iterator i = c.iterator();

        while (i.hasNext()){
            // we must enforce that everything in c is an LDAPAttribute
            // add will return true if the attribute was added
            if (this.add( i.next() )){
                setChanged = true;
            }
        }
        return setChanged;
    }

    /**
     * Returns a string representation of this LDAPAttributeSet
     *
     * @return a string representation of this LDAPAttributeSet
     */
    public String toString()
    {
        StringBuffer retValue = new StringBuffer("LDAPAttributeSet: ");
        Iterator attrs = iterator();
        boolean first = true;
        while( attrs.hasNext()) {
            if( ! first) {
                retValue.append(" ");
            }
            first = false;
            LDAPAttribute attr = (LDAPAttribute)attrs.next();
            retValue.append(attr.toString());
        }
        return retValue.toString();
    }
    
    void newLine(int indentTabs,java.io.Writer out) throws java.io.IOException
    {
        String tabString = "    ";    
        
        out.write("\n");
        for (int i=0; i< indentTabs; i++){
            out.write(tabString);
        }
        
    }

    private void writeAttribute(LDAPAttribute attr,java.io.Writer out) 
    throws java.io.IOException
    {
        newLine(1,out);
        out.write("<attr name=\"");
        out.write(attr.getName());
        out.write("\">");
        String values[] = attr.getStringValueArray();
        byte bytevalues[][] = attr.getByteValueArray();
        for(int i=0; i<values.length; i++){
            newLine(2,out);
            if (Base64.isValidUTF8(bytevalues[i], false)){
                out.write("<value>");
                out.write(values[i]);
                out.write("</value>");
            } else {
                out.write("<value xsi:type=\"xsd:base64Binary\">");
                out.write(Base64.encode(bytevalues[i]));
                out.write("</value>");
            }

        }
        newLine(1,out);
        out.write("</attr>");
        
    }

    /**
     * This method does DSML serialization of the instance.
     *
     * @param oout Outputstream where the serialzed data has to be written
     *
     * @throws IOException if write fails on OutputStream 
     */    
    public void writeDSML(java.io.OutputStream  oout) throws java.io.IOException
    {
    	java.io.Writer out=new java.io.OutputStreamWriter(oout,"UTF-8");
        out.write("<LDAPAttributeSet>");
        Iterator i=iterator();
        while (i.hasNext()){
            writeAttribute((LDAPAttribute) i.next(),out);
        }
        newLine(0,out);
        out.write("</LDAPAttributeSet>"); 
        out.close();           
    }

	/**
	* This method is used to deserialize the DSML encoded representation of
	* this class.
	* @param input InputStream for the DSML formatted data. 
	* @return Deserialized form of this class.
	* @throws IOException when serialization fails.
	*/ 
	public static Object readDSML(InputStream input) throws IOException {
    SAXEventMultiplexer xmlreader = new SAXEventMultiplexer();
    xmlreader.setLDAPXMLHandler(getXMLHandler("LDAPAttributeSet",null));
    return (LDAPAttributeSet) xmlreader.parseXML(input);
  }

	/**
	* This method return the LDAPHandler which handles the XML (DSML) tags
	* for this class
	* @param tagname Name of the Root tag used to represent this class.
	* @param parenthandler Parent LDAPXMLHandler for this tag.
	* @return LDAPXMLHandler to handle this element.
	*/    
  static LDAPXMLHandler getXMLHandler(String tagname,LDAPXMLHandler parenthandler) {
    return new LDAPXMLHandler(tagname, parenthandler) {

      List valuelist = new ArrayList();
      protected void initHandler() {
        //set LDAPAttribute handler.
        setchildelement(LDAPAttribute.getXMLHandler("attr",this));
      }

      protected void endElement() {
        LDAPAttributeSet attrset = new LDAPAttributeSet();
        attrset.addAll(valuelist);
        setObject(attrset);
      }
      protected void addValue(String tag, Object value) {
        if (tag.equals("attr")) {
          valuelist.add(value);
        }
      }
    };

  }
    //Overloaded method added for supporting XML Serialization
	private void writeAttribute(LDAPAttribute attr, StringBuffer buff) 
			throws java.io.IOException
	{
		buff.append(ValueXMLhandler.newLine(1));
		buff.append("<attr name=\"");
		buff.append(attr.getName());
		buff.append("\">");
	
		  String values[] = attr.getStringValueArray();
		  byte bytevalues[][] = attr.getByteValueArray();
		  for(int i=0; i<values.length; i++){
			  buff.append(ValueXMLhandler.newLine(2));
			  if (Base64.isValidUTF8(bytevalues[i], false)){
				  buff.append("<value>");
				  buff.append(values[i]);
				  buff.append("</value>");
			  } else {
				  buff.append("<value xsi:type=\"xsd:base64Binary\">");
				  buff.append(Base64.encode(bytevalues[i]));
				  buff.append("</value>");
			  }
	
		  }
		  buff.append(ValueXMLhandler.newLine(1));
		  buff.append("</attr>");        
	}

   /**
   * Writes the object state to a stream in XML format  
   * @param out The ObjectOutput stream where the Object in XML format 
   * is being written to
   * @throws IOException - If I/O errors occur
   */  
   public void writeExternal(ObjectOutput out) throws IOException
   {
		StringBuffer buff = new StringBuffer();
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String header = "";
		header += "*************************************************************************\n";
		header += "** The encrypted data above and below is the Class definition and  ******\n";
		header += "** other data specific to Java Serialization Protocol. The data  ********\n";
		header += "** which is of most application specific interest is as follows... ******\n";
		header += "*************************************************************************\n";
		header += "****************** Start of application data ****************************\n";
		header += "*************************************************************************\n";
		  
		buff.append(header);
		buff.append(ValueXMLhandler.newLine(0));
		buff.append("<LDAPAttributeSet>");
		  
		Iterator i=iterator();
		while (i.hasNext()){
			writeAttribute((LDAPAttribute) i.next(),buff);
		}
		buff.append(ValueXMLhandler.newLine(0));
		buff.append("</LDAPAttributeSet>"); 
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String tail = "";
		tail += "*************************************************************************\n";
		tail += "****************** End of application data ******************************\n";
		tail += "*************************************************************************\n";
		  
		buff.append(tail);
		buff.append(ValueXMLhandler.newLine(0));       
		out.writeUTF(buff.toString());
		
   }
   /**
   * Reads the serialized object from the underlying input stream.
   * @param in The ObjectInput stream where the Serialized Object is being read from
   * @throws IOException - If I/O errors occur
   * @throws ClassNotFoundException - If the class for an object being restored 
   * cannot be found.
   */ 
   public void readExternal(ObjectInput in) 
		  throws IOException, ClassNotFoundException
   {
	  String readData = in.readUTF();
	  String readProperties = readData.substring(readData.indexOf('<'), 
	  			(readData.lastIndexOf('>') + 1));
	  			
	  //Insert  parsing logic here for separating whitespaces in non-text nodes
	  StringBuffer parsedBuff = new StringBuffer();
	  ValueXMLhandler.parseInput(readProperties, parsedBuff);
	    
	  BufferedInputStream istream = 
			  new BufferedInputStream(
					  new ByteArrayInputStream((parsedBuff.toString()).getBytes()));
	
	  LDAPAttributeSet readObject = 
					(LDAPAttributeSet)LDAPAttributeSet.readDSML(istream);
	
	  Iterator i = readObject.iterator();
	  while (i.hasNext()){
			this.add( ((LDAPAttribute)i.next()).clone());
		}
	  //Garbage collect the readObject from readDSML()..	
	  readObject = null;
   }   
}

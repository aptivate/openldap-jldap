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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.novell.ldap.client.ArrayEnumeration;
import com.novell.ldap.util.Base64;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;
/**
 * The name and values of one attribute of a directory entry.
 *
 * <p>LDAPAttribute objects are used when searching for, adding,
 * modifying, and deleting attributes from the directory.
 * LDAPAttributes are often used in conjunction with an
 * {@link LDAPAttributeSet} when retrieving or adding multiple
 * attributes to an entry.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/AddEntry.java.html">AddEntry.java</p>
 *
 * @see LDAPEntry
 * @see LDAPAttributeSet
 * @see LDAPModification
 */

public class LDAPAttribute implements java.lang.Cloneable,
                                      java.lang.Comparable,
										Externalizable {
    private String name;              // full attribute name
    private String baseName;          // cn of cn;lang-ja;phonetic
    private String[] subTypes = null; // lang-ja of cn;lang-ja
    private Object[] values = null;   // Array of byte[] attribute values

	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPAttribute()
	{
		super();
	}
    
    /**
     * Constructs an attribute with copies of all values of the input
     * attribute.
     *
     * @param attr  An LDAPAttribute to use as a template.
     *
     * @throws IllegalArgumentException if attr is null
     */
    public LDAPAttribute( LDAPAttribute attr )
    {
        if( attr == null) {
            throw new IllegalArgumentException("LDAPAttribute class cannot be null");
        }
        // Do a deep copy of the LDAPAttribute template
        this.name = attr.name;
        this.baseName = attr.baseName;
        if( null != attr.subTypes ) {
            this.subTypes = new String[ attr.subTypes.length ];
            System.arraycopy( attr.subTypes, 0, this.subTypes, 0,
                    this.subTypes.length );
        }
        // OK to just copy attributes, as the app only sees a deep copy of them
        if( null != attr.values) {
            this.values = new Object[ attr.values.length ];
            System.arraycopy( attr.values, 0, this.values, 0, this.values.length );
        }
        return;
    }

    /**
     * Constructs an attribute with no values.
     *
     * @param attrName Name of the attribute.
     *
     * @throws IllegalArgumentException if attrName is null
     */
    public LDAPAttribute( String attrName )
    {
        if( attrName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        this.name = attrName;
        this.baseName = this.getBaseName( attrName );
        this.subTypes = this.getSubtypes( attrName );
        return;
    }

    /**
     * Constructs an attribute with a byte-formatted value.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrBytes Value of the attribute as raw bytes.
     *
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     *
     * @throws IllegalArgumentException if attrName or attrBytes is null
     */
    public LDAPAttribute( String attrName, byte[] attrBytes )
    {
        this( attrName );
        if( attrBytes == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        // Make our own copy of the byte array to prevent app from changing it
        byte[] tmp = new byte[attrBytes.length];
        System.arraycopy( attrBytes, 0, tmp, 0, attrBytes.length );
        this.add( tmp);
        return;
    }

    /**
     * Constructs an attribute with a single string value.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrString Value of the attribute as a string.
     *
     * @throws IllegalArgumentException if attrName or attrString is null
     */
    public LDAPAttribute(String attrName, String attrString)
    {
        this( attrName );
        if( attrString == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        try {
            this.add( attrString.getBytes( "UTF-8" ) );
        } catch( UnsupportedEncodingException e ){
            throw new RuntimeException( e.toString());
        }
        return;
    }

    /**
     * Constructs an attribute with an array of string values.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrStrings Array of values as strings.
     *
     * @throws IllegalArgumentException if attrName, attrStrings, or a member
     *         of attrStrings is null
     */
    public LDAPAttribute(String attrName, String[] attrStrings)
    {
        this( attrName );
        if( attrStrings == null) {
            throw new IllegalArgumentException("Attribute values array cannot be null");
        }
        for( int i = 0, u = attrStrings.length; i < u; i++) {
            try {
                if( attrStrings[ i ] == null) {
                    throw new IllegalArgumentException("Attribute value " +
                    "at array index " + i + " cannot be null");
                }
                this.add( attrStrings[ i ].getBytes( "UTF-8" ) );
            } catch( UnsupportedEncodingException e ){
                throw new RuntimeException( e.toString());
            }
        }
        return;
    }

    /**
     * Returns a clone of this LDAPAttribute.
     *
     * @return clone of this LDAPAttribute.
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            if( values != null) {
                System.arraycopy( this.values, 0,
                                  ((LDAPAttribute)newObj).values, 0,
                                  this.values.length);
            }
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Adds a string value to the attribute.
     *
     * @param attrString Value of the attribute as a String.
     *
     * @throws IllegalArgumentException if attrString is null
     */
    public void addValue(String attrString)
    {
        if( attrString == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        try {
            this.add( attrString.getBytes( "UTF-8" ) );
        } catch( UnsupportedEncodingException ue ) {
            throw new RuntimeException( ue.toString());
        }
        return;
    }

    /**
     * Adds a byte-formatted value to the attribute.
     *
     * @param attrBytes Value of the attribute as raw bytes.
     *
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     *
     * @throws IllegalArgumentException if attrBytes is null
     */
    public void addValue(byte[] attrBytes)
    {
        if( attrBytes == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        this.add(attrBytes);
        return;
    }

    /**
     * Adds a base64 encoded value to the attribute.
     * The value will be decoded and stored as bytes.  String
     * data encoded as a base64 value must be UTF-8 characters.
     *
     * @param attrString The base64 value of the attribute as a String.
     *
     * @throws IllegalArgumentException if attrString is null
     */
    public void addBase64Value(String attrString)
    {
        if( attrString == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }

        this.add( Base64.decode(attrString));
        return;
    }

    /**
     * Adds a base64 encoded value to the attribute.
     * The value will be decoded and stored as bytes.  Character
     * data encoded as a base64 value must be UTF-8 characters.
     *
     * @param attrString The base64 value of the attribute as a StringBuffer.
     * @param start  The start index of base64 encoded part, inclusive.
     * @param end  The end index of base encoded part, exclusive.
     *
     * @throws IllegalArgumentException if attrString is null
     */
    public void addBase64Value(StringBuffer attrString, int start, int end)
    {
        if( attrString == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }

        this.add( Base64.decode(attrString, start, end));

        return;
    }

    /**
     * Adds a base64 encoded value to the attribute.
     * The value will be decoded and stored as bytes.  Character
     * data encoded as a base64 value must be UTF-8 characters.
     *
     * @param attrChars The base64 value of the attribute as an array of
     * characters.
     *
     * @throws IllegalArgumentException if attrString is null
     */
    public void addBase64Value(char[] attrChars)
    {
        if( attrChars == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }

        this.add( Base64.decode(attrChars));
        return;
    }

    /**
     * Adds a URL, indicating a file or other resource that contains
     * the value of the attribute.
     *
     * @param url String value of a URL pointing to the resource containing
     * the value of the attribute.
     *
     * @throws IllegalArgumentException if url is null
     */
    public void addURLValue(String url) throws MalformedURLException, IOException
    {
        if( url == null) {
            throw new IllegalArgumentException("Attribute URL cannot be null");
        }
        addURLValue( new URL( url));
        return;
    }

    /**
     * Adds a URL, indicating a file or other resource that contains
     * the value of the attribute.
     *
     * @param url A URL class pointing to the resource containing the value
     * of the attribute.
     *
     * @throws IllegalArgumentException if url is null
     */
    public void addURLValue(URL url) throws MalformedURLException, IOException
    {
        // Class to encapsulate the data bytes and the length
        class URLData
        {
            private int length;
            private byte[] data;
            private URLData(byte[] data, int length)
            {
                this.length = length;
                this.data = data;
                return;
            }
            private int getLength()
            {
                return length;
            }
            private byte[] getData()
            {
                return data;
            }
        }
        if( url == null) {
            throw new IllegalArgumentException("Attribute URL cannot be null");
        }
        try {
            // Get InputStream from the URL
            InputStream in = url.openStream();
            // Read the bytes into buffers and store the them in an arraylist
            ArrayList bufs = new ArrayList();
            byte[] buf = new byte[4096];
            int len, totalLength = 0;
            while( (len = in.read(buf,0, 4096)) != -1) {
                bufs.add( new URLData(buf, len));
                buf = new byte[4096];
                totalLength += len;
            }
            /*
             * Now that the length is known, allocate an array to hold all
             * the bytes of data and copy the data to that array, store
             * it in this LDAPAttribute
             */
            byte[] data = new byte[totalLength];
            int offset = 0; //
            for( int i=0; i < bufs.size(); i++) {
                URLData b = (URLData)bufs.get(i);
                len = b.getLength();
                System.arraycopy( b.getData(), 0, data, offset, len);
                offset += len;
            }
            this.add( data);
        } catch( UnsupportedEncodingException ue ) {
            throw new RuntimeException( ue.toString());
        }
        return;
    }

    /**
     * Returns an enumerator for the values of the attribute in byte format.
     *
     * @return The values of the attribute in byte format.
     * <P> Note: All string values will be UTF-8 encoded. To decode use the
     * String constructor. Example: new String( byteArray, "UTF-8" );
     */
    public Enumeration getByteValues()
    {
        return new ArrayEnumeration( getByteValueArray());
    }

    /**
     * Returns an enumerator for the string values of an attribute.
     *
     * @return The string values of an attribute.
     */
    public Enumeration getStringValues()
    {
        return new ArrayEnumeration( getStringValueArray());
    }

    /**
     * Returns the values of the attribute as an array of bytes.
     *
     * @return The values as an array of bytes or an empty array if there are
     * no values.
     */
    public byte[][] getByteValueArray()
    {
        if( null == this.values )
            return new byte[ 0 ][];
        int size = this.values.length;
        byte[][] bva = new byte[ size ][];
        // Deep copy so application cannot change values
        for( int i = 0, u = size; i < u; i++) {
            bva[i] = new byte[((byte[])values[i]).length];
            System.arraycopy( this.values[i], 0, bva[i], 0, bva[i].length );
        }
        return bva;
    }

    /**
     * Returns the values of the attribute as an array of strings.
     *
     * @return The values as an array of strings or an empty array if there are
     * no values
     */
    public String[] getStringValueArray()
    {
        if( null == this.values )
            return new String[ 0 ];
        int size = values.length;
        String[] sva = new String[ size ];
        for( int j = 0; j < size; j++ ) {
            try {
                sva[ j ] = new String( (byte[])values[ j ], "UTF-8" );
            } catch( UnsupportedEncodingException uee ) {
                // Exception should NEVER get thrown but just in case it does ...
                throw new RuntimeException( uee.toString());
            }
        }
        return sva;
    }

    /**
     * Returns the the first value of the attribute as a <code>String</code>.
     *
     * @return  The UTF-8 encoded<code>String</code> value of the attribute's
     *          value.  If the value wasn't a UTF-8 encoded <code>String</code>
     *          to begin with the value of the returned <code>String</code> is
     *          non deterministic.
     *
     *          <p>If <code>this</code> attribute has more than one value the
     *          first value is converted to a UTF-8 encoded <code>String</code>
     *          and returned. It should be noted, that the directory may
     *          return attribute values in any order, so that the first
     *          value may vary from one call to another.
     *
     *          <p>If the attribute has no values <code>null</code> is returned
     */
    public String getStringValue()
    {
        String rval = null;
        if( this.values != null) {
            try {
                rval = new String( (byte[])this.values[ 0 ], "UTF-8" );
            } catch( UnsupportedEncodingException use ) {
                throw new RuntimeException( use.toString());
            }
        }
        return rval;
    }

    /**
     * Returns the the first value of the attribute as a byte array.
     *
     * @return  The binary value of <code>this</code> attribute or
     * <code>null</code> if <code>this</code> attribute doesn't have a value.
     *
     * <p>If the attribute has no values <code>null</code> is returned
     */
     public byte[] getByteValue()
     {
        byte[] bva = null;
        if( this.values != null) {
            // Deep copy so app can't change the value
            bva = new byte[((byte[])values[0]).length];
            System.arraycopy( this.values[0], 0, bva, 0, bva.length );
        }
        return bva;
     }

    /**
     * Returns the language subtype of the attribute, if any.
     *
     * <p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns the string, lang-ja.</p>
     *
     * @return The language subtype of the attribute or null if the attribute
     *         has none.
     */
    public String getLangSubtype()
    {
        if( subTypes != null) {
            for(int i=0; i<subTypes.length; i++) {
                if(subTypes[i].startsWith("lang-")) {
                    return subTypes[i];
                }
            }
        }
        return null;
    }

    /**
     * Returns the base name of the attribute.
     *
     *<p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns cn.</p>
     *
     * @return The base name of the attribute.
     */
    public String getBaseName()
    {
        return baseName;
    }

    /**
     * Returns the base name of the specified attribute name.
     *
     * <p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns cn.</p>
     *
     * @param attrName Name of the attribute from which to extract the
     * base name.
     *
     * @return The base name of the attribute.
     *
     * @throws IllegalArgumentException if attrName is null
     */
    public static String getBaseName(String attrName)
    {
        if( attrName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        int idx = attrName.indexOf( ';' );
        if( -1 == idx ) {
            return attrName;
        }
        return attrName.substring( 0, idx );
    }

    /**
     * Returns the name of the attribute.
     *
     * @return The name of the attribute.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Extracts the subtypes from the attribute name.
     *
     *<p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns an array containing lang-ja and phonetic.
     *
     * @return An array subtypes or null if the attribute has none.
     */
    public String[] getSubtypes()
    {
        return subTypes;
    }

    /**
     * Extracts the subtypes from the specified attribute name.
     *
     * <p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns an array containing lang-ja and phonetic.</p>
     *
     * @param attrName   Name of the attribute from which to extract
     * the subtypes.
     *
     * @return An array subtypes or null if the attribute has none.
     *
     * @throws IllegalArgumentException if attrName is null
     */
    public static String[] getSubtypes(String attrName)
    {
        if( attrName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        StringTokenizer st = new StringTokenizer(attrName, ";");
        String subTypes[] = null;
        int cnt = st.countTokens();
        if(cnt > 0) {
            st.nextToken(); // skip over basename
            subTypes = new String[cnt - 1];
            int i=0;
            while(st.hasMoreTokens()) {
                subTypes[i++] = st.nextToken();
            }
        }
        return subTypes;
    }

    /**
     * Reports if the attribute name contains the specified subtype.
     *
     * <p>For example, if you check for the subtype lang-en and the
     * attribute name is cn;lang-en, this method returns true.</p>
     *
     * @param subtype  The single subtype to check for.
     *
     * @return True, if the attribute has the specified subtype;
     *         false, if it doesn't.
     *
     * @throws IllegalArgumentException if subtype is null
     */
    public boolean hasSubtype(String subtype)
    {
        if( subtype == null) {
            throw new IllegalArgumentException("subtype cannot be null");
        }
        if( null != this.subTypes) {
            for(int i=0; i<subTypes.length; i++) {
                if(subTypes[i].equalsIgnoreCase(subtype))
                    return true;
            }
        }
        return false;
    }

    /**
     * Reports if the attribute name contains all the specified subtypes.
     *
     * <p> For example, if you check for the subtypes lang-en and phonetic
     * and if the attribute name is cn;lang-en;phonetic, this method
     * returns true. If the attribute name is cn;phonetic or cn;lang-en,
     * this method returns false.</p>
     *
     * @param subtypes   An array of subtypes to check for.
     *
     * @return True, if the attribute has all the specified subtypes;
     *         false, if it doesn't have all the subtypes.
     *
     * @throws IllegalArgumentException if subtypes is null or if array member
     *         is null.
     */
    public boolean hasSubtypes(String[] subtypes)
    {
        if( subtypes == null) {
            throw new IllegalArgumentException("subtypes cannot be null");
        }
        gotSubType:
        for(int i=0; i<subtypes.length; i++) {
            for(int j=0; j<subTypes.length; j++) {
                if( subTypes[j] == null) {
                    throw new IllegalArgumentException("subtype " +
                        "at array index " + i + " cannot be null");
                }
                if(subTypes[j].equalsIgnoreCase(subtypes[i])) {
                    continue gotSubType;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Removes a string value from the attribute.
     *
     * @param attrString   Value of the attribute as a string.
     *
     * <p>Note: Removing a value which is not present in the attribute has
     * no effect.</p>
     *
     * @throws IllegalArgumentException if attrString is null
     */
    public void removeValue( String attrString )
    {
        if( null == attrString ) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        try {
            this.removeValue( attrString.getBytes( "UTF-8" ) );
        } catch( UnsupportedEncodingException uee ) {
            // This should NEVER happen but just in case ...
            throw new RuntimeException( uee.toString());
        }
        return;
    }

    /**
     * Removes a byte-formatted value from the attribute.
     *
     * @param attrBytes    Value of the attribute as raw bytes.
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     * Example: <code>String.getBytes("UTF-8");</code></P>
     *
     * <p>Note: Removing a value which is not present in the attribute has
     * no effect.</p>
     *
     * @throws IllegalArgumentException if attrBytes is null
     */
    public void removeValue( byte[] attrBytes )
    {
        if( null == attrBytes ) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        for( int i = 0; i < this.values.length; i++ ) {
            if( equals( attrBytes, (byte[])this.values[ i ] ) ) {
                if( 0 == i && 1 == this.values.length ) {
                        // Optimize if first element of a single valued attr
                        this.values = null;
                        return;
                }
                if( this.values.length == 1) {
                    this.values = null;
                } else {
                    int moved = this.values.length - i - 1;
                    Object[] tmp = new Object[ this.values.length - 1 ];
                    if( i != 0) {
                        System.arraycopy( values, 0, tmp, 0, i );
                    }
                    if( moved != 0) {
                        System.arraycopy( values, i + 1, tmp, i, moved );
                    }
                    this.values = tmp;
                    tmp = null;
                }
                break;
            }
        }
        return;
    }

    /**
     * Returns the number of values in the attribute.
     *
     * @return The number of values in the attribute.
     */
    public int size()
    {
        return null == this.values ? 0 : this.values.length;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * <p> Ordering is determined by comparing attribute names (see
     * {@link #getName() }) using the method compareTo() of the String class.
     * </p>
     *
     * @param attribute   The LDAPAttribute to be compared to this object.
     *
     * @return            Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the
     * specified object.
     */
    public int compareTo(Object attribute){

        return name.compareTo( ((LDAPAttribute)attribute).name );
    }

    /**
     * Adds an object to <code>this</code> object's list of attribute values
     *
     * @param   bytes   Ultimately all of this attribute's values are treated
     *                  as binary data so we simplify the process by requiring
     *                  that all data added to our list is in binary form.
     *
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     */
    private void add( byte[] bytes )
    {
        if( null == this.values ) {
            this.values = new Object[]{ bytes };
        } else {
            // Duplicate attribute values not allowed
            for( int i = 0; i < this.values.length; i++ ) {
                if( equals( bytes, (byte[])this.values[i] ) ) {
                    return; // Duplicate, don't add
                }
            }
            Object[] tmp = new Object[ this.values.length + 1 ];
            System.arraycopy( this.values, 0, tmp, 0, this.values.length );
            tmp[ this.values.length ] = bytes;
            this.values = tmp;
            tmp = null;
        }
        return;
    }

    /**
     * Replaces all values with the specified value. This protected method is
     * used by sub-classes of LDAPSchemaElement because the value cannot be set
     * with a contructor.
     */
    protected void setValue(String value){
        values = null;
        try {
            this.add( value.getBytes( "UTF-8" ) );
        } catch( UnsupportedEncodingException ue ) {
            throw new RuntimeException( ue.toString());
        }
        return;
    }

   /**
    * Returns true if the two specified arrays of bytes are equal to each
    * another.  Matches the logic of Arrays.equals which is not available
    * in jdk 1.1.x.
    *
    * @param e1 the first array to be tested
    * @param e2 the second array to be tested
    * @return true if the two arrays are equal
    */
    private boolean equals(byte[] e1, byte[] e2)
    {
        // If same object, they compare true
        if (e1==e2)
            return true;

        // If either but not both are null, they compare false
        if (e1==null || e2==null)
            return false;

        // If arrays have different length, they compare false
        int length = e1.length;
        if (e2.length != length)
            return false;

        // If any of the bytes are different, they compare false
        for (int i=0; i<length; i++) {
            if (e1[i] != e2[i])
                return false;
        }

        return true;
    }

    /**
     * Returns a string representation of this LDAPAttribute
     *
     * @return a string representation of this LDAPAttribute
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer("LDAPAttribute: ");
        try {
            result.append("{type='" + name + "'");
            if( values != null) {
                result.append(", ");
                if( values.length == 1) {
                    result.append("value='");
                } else {
                    result.append("values='");
                }
                for(int i=0; i < values.length; i++) {
                    if( i != 0) {
                        result.append("','");
                    }
                    if( ((byte[])values[i]).length == 0) {
                        continue;
                    }
                    String sval = new String( (byte[])values[ i ], "UTF-8" );
                    if( sval.length() == 0) {
                        // didn't decode well, must be binary
                        result.append("<binary value, length:" + sval.length());
                        continue;
                    }
                    result.append(sval);
                }
                result.append("'");
            }
            result.append("}");
        } catch( Exception e) {
            throw new RuntimeException(e.toString());
        }
        return result.toString();
    }

    void newLine(int indentTabs,java.io.Writer out) throws java.io.IOException
    {
        String tabString = "    ";    
        
        out.write("\n");
        for (int i=0; i< indentTabs; i++){
            out.write(tabString);
        }
        
    }
    
    /**
     * This method does DSML serialization of the instance.
     *
     * @param oout Outputstream where the serialzed data has to be written
     *
     * @throws IOException if write fails on OutputStream 
     */    
    public void writeDSML(java.io.OutputStream oout) throws java.io.IOException
    {
        java.io.Writer out=new java.io.OutputStreamWriter(oout,"UTF-8");
        out.write("<LDAPAttribute>");
        newLine(1,out);
        out.write("<attr name=\"");
        out.write(getName());
        out.write("\">");
        
		//sub classes override this..
		writeValue(out);
        
        newLine(1,out);
        out.write("</attr>");
        newLine(0,out);
        out.write("</LDAPAttribute>");        
        out.close();
    }
    
//	Sub classes override this method..
	protected void writeValue(java.io.Writer out) throws IOException {
  	      String values[] = getStringValueArray();
		  byte bytevalues[][] = getByteValueArray();
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
		return (LDAPAttribute) xmlreader.parseXML(input);
    }
    
    //This is added to fix the bug in parsing logic written in 
    //getXMLHandler() method of this class 
	private static LDAPXMLHandler getTopXMLHandler(String tagname, LDAPXMLHandler
	 parenthandler) {
	  return new LDAPXMLHandler(tagname, parenthandler) {

		List valuelist = new ArrayList();
		protected void initHandler() {
		  //set LDAPAttribute handler.
		  setchildelement(LDAPAttribute.getXMLHandler("attr",this));
		}

		protected void endElement() {
			setObject((LDAPAttribute)valuelist.get(0));
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
		List valuelist= new ArrayList();
        protected void initHandler() {
          //set value handler.
          setchildelement(new ValueXMLhandler(this));          
        }

        protected void endElement() {
			Iterator valueiterator = valuelist.iterator();
			LDAPAttribute attr = new LDAPAttribute(attrName);  
			  while (valueiterator.hasNext())
			  {
				attr.addValue((byte[])valueiterator.next());
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
		buff.append("<LDAPAttribute>");
		buff.append(ValueXMLhandler.newLine(1));
		buff.append("<attr name=\"");
		buff.append(getName());
		buff.append("\">");
		
//		Sub classes override this method..
		writeValue(buff);
  			
		buff.append(ValueXMLhandler.newLine(1));
		buff.append("</attr>");
		buff.append(ValueXMLhandler.newLine(0));
		buff.append("</LDAPAttribute>"); 
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String tail = "";
		tail += "*************************************************************************\n";
		tail += "****************** End of application data ******************************\n";
		tail += "*************************************************************************\n";
		
		buff.append(tail);
		buff.append(ValueXMLhandler.newLine(0));       
		out.writeUTF(buff.toString());
		
		//clean and garbage the buffer
		buff.delete(0, buff.length());
		buff = null;
  }
  
  //Sub classes override this method..
  protected void writeValue(StringBuffer buff){
  	
	String values[] = getStringValueArray();
	byte bytevalues[][] = getByteValueArray();
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
				  
		//Insert  parsing logic here for separating whitespace text nodes
		StringBuffer parsedBuff = new StringBuffer();
		ValueXMLhandler.parseInput(readProperties, parsedBuff);
	    
		BufferedInputStream istream = 
				new BufferedInputStream(
						new ByteArrayInputStream((parsedBuff.toString()).getBytes()));
//		Sub classes need to override this method
		setDeserializedValues(istream);
		
//		Close the DSML reader stream
		istream.close(); 
	
	}
	
	//Sub classes need to override this method
	protected void setDeserializedValues(BufferedInputStream istream)
	throws IOException {
		LDAPAttribute readObject = 
			(LDAPAttribute)LDAPAttribute.readDSML(istream);
	
//		Do a deep copy of the LDAPAttribute template
		 this.name = readObject.name;
		 this.baseName = readObject.baseName;
		 if( null != readObject.subTypes ) {
			 this.subTypes = new String[ readObject.subTypes.length ];
			 System.arraycopy( readObject.subTypes, 0, this.subTypes, 0,
					 this.subTypes.length );
		 }

		 if( null != readObject.values) {
			 this.values = new Object[ readObject.values.length ];
			 System.arraycopy( readObject.values, 0, this.values, 0, this.values.length );
		 }
	
		//Garbage collect the readObject from readDSML()..	
		readObject = null;	   
	}
}

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


import com.novell.ldap.client.ArrayEnumeration;

import java.util.*;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
/**
 * The name and values of one attribute of a directory entry.
 *
 * <p>LDAPAttribute objects are used when searching for, adding,
 * modifying, and deleting attributes from the directory.
 * LDAPAttributes are often used in conjunction with an
 * {@link LDAPAttributeSet} when retrieving or adding multiple
 * attributes to an entry.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/jldap_sample/AddEntry.java.html">AddEntry.java</p>
 *
 * @see LDAPEntry
 * @see LDAPAttributeSet
 * @see LDAPModification
 */
public class LDAPAttribute {
    private String name;              // full attribute name
    private String baseName;          // cn of cn;lang-ja;phonetic
    private String[] subTypes = null; // lang-ja of cn;lang-ja
    private Object[] values = null;   // Array of byte[] attribute values

    /**
     * Constructs an attribute with copies of all values of the input
     * attribute.
     *
     * @param attr  An LDAPAttribute to use as a template.
     */
    public LDAPAttribute( LDAPAttribute attr )
    {
        if( attr == null) {
            throw new RuntimeException("LDAPAttribute class must be specified");
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
        this.values = new Object[ attr.values.length ];
        System.arraycopy( attr.values, 0, this.values, 0, this.values.length );
        return;
    }

    /**
     * Constructs an attribute with no values.
     *
     * @param attrName Name of the attribute.
     */
    public LDAPAttribute( String attrName )
    {
        if( attrName == null) {
            throw new RuntimeException("Attribute name must be specified");
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
     * If attrBytes is null, no value is added to the attribute.
     *
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     */
    public LDAPAttribute( String attrName, byte[] attrBytes )
    {
        this( attrName );
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
     * If attrString is null, no value is added to the attribute.
     */
    public LDAPAttribute(String attrName, String attrString)
    {
        this( attrName );
        if( null != attrString ) {
            try {
                this.add( attrString.getBytes( "UTF-8" ) );
            } catch( UnsupportedEncodingException e ){
                throw new RuntimeException( e.toString());
            }
        }
        return;
    }

    /**
     * Constructs an attribute with an array of string values.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrStrings Array of values as strings.
     * If attrStrings is null, no value is added to the attribute.
     * String values that are null are not added to the attribute.
     */
    public LDAPAttribute(String attrName, String[] attrStrings)
    {
        this( attrName );
        if( attrStrings != null) {
            for( int i = 0, u = attrStrings.length; i < u; i++) {
                try {
                    this.add( attrStrings[ i ].getBytes( "UTF-8" ) );
                } catch( UnsupportedEncodingException e ){
                    throw new RuntimeException( e.toString());
                }
            }
        }
        return;
    }

    /**
     * Adds a string value to the attribute.
     *
     * @param attrString Value of the attribute as a string.
     */
    public void addValue(String attrString)
    {
        if( null != attrString )
        {
            try
            {
                this.add( attrString.getBytes( "UTF-8" ) );
            }
            catch( UnsupportedEncodingException ue ){}
        }
        return;
    }

    /**
     * Adds a byte-formatted value to the attribute.
     *
     * @param attrBytes Value of the attribute as raw bytes.
     *
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     */
    public void addValue(byte[] attrBytes)
    {
        if( null != attrBytes) {
            this.add(attrBytes);
        }
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
     * @return The values as an array of bytes.
     */
    public byte[][] getByteValueArray()
    {
        byte[][] bva = new byte[ values.length ][];
        // Deep copy so application cannot change values
        for( int i = 0, u = this.values.length; i < u; i++) {
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
        for( int j = 0; j < size; j++ )
        {
            try
            {
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
     *          and returned.
     *
     *          <p>If the attribute has no values <code>null</code> is returned
     *
     *
     */
    public String getStringValue()
    {
        String rval = null;
        if( null != this.values )
        {
            try {
                rval = new String( (byte[])this.values[ 0 ], "UTF-8" );
            } catch( UnsupportedEncodingException use ) {
                rval = new String( (byte[])this.values[ 0 ] );
            }
        }
        return rval;
    }

    /**
     * Returns the the first value of the attribute as a byte array.
     *
     * @return  The binary value of <code>this</code> attribute or
     * <code>null</code> if <code>this</code> attribute doesn't have a value.
     */
     public byte[] getByteValue()
     {
        if( (this.values == null) || (this.values.length == 0)) {
            return null;
        } else {
            // Deep copy so app can't change the value
            byte[] bva = new byte[((byte[])values[0]).length];
            System.arraycopy( this.values[0], 0, bva, 0, bva.length );
            return bva;
        }
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
     */
    public static String getBaseName(String attrName)
    {
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
    public String getName() {
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
    public String[] getSubtypes() {
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
     */
    public static String[] getSubtypes(String attrName) {
        StringTokenizer st = new StringTokenizer(attrName, ";");
        String baseName, subTypes[] = null;
        int cnt = st.countTokens();
        if(cnt > 0) {
            baseName = st.nextToken();
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
     */
    public boolean hasSubtype(String subtype)
    {
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
     */
    public boolean hasSubtypes(String[] subtypes) {
        gotSubType:
        for(int i=0; i<subtypes.length; i++) {
            for(int j=0; j<subTypes.length; j++) {
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
     */
    public void removeValue( String attrString )
    {
        if( null != attrString ) {
            try {
                this.removeValue( attrString.getBytes( "UTF-8" ) );
            } catch( UnsupportedEncodingException uee ) {
                // This should NEVER happend but just in case ...
                throw new RuntimeException( uee.toString());
            }
        }
    }

    /**
     * Removes a byte-formatted value from the attribute.
     *
     * @param attrBytes    Value of the attribute as raw bytes.
     * <P> Note: If attrBytes represents a string it should be UTF-8 encoded.
     * Example: <code>String.getBytes("UTF-8");</code></P>
     */
    public void removeValue( byte[] attrBytes )
    {
        if( null != attrBytes ) {
            for( int i = 0; i < this.values.length; i++ ) {
                if( Arrays.equals( attrBytes, (byte[])this.values[ i ] ) ) {
                    if( 0 == i && 1 == this.values.length ) {
                            // Optimize if first element of a single valued attr
                            this.values = null;
                            return;
                    }
                    int moved = this.values.length - i - 1;
                    Object[] tmp = new Object[ this.values.length - 1 ];
                    System.arraycopy( values, 0, tmp, 0, i );
                    System.arraycopy( values, i + 1, tmp, i, moved );
                    this.values = tmp;
                    tmp = null;
                    break;
                }
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
        if( null != bytes ) {
            if( null == this.values ) {
                this.values = new Object[]{ bytes };
            else {
                // Duplicate attribute values not allowed
                for( int i = 0; i < this.values.length; i++ ) {
                    if( Arrays.equals( bytes, (byte[])this.values[i] ) ) {
                        return; // Duplicate
                    }
                }
                Object[] tmp = new Object[ this.values.length + 1 ];
                System.arraycopy( this.values, 0, tmp, 0, this.values.length );
                tmp[ this.values.length ] = bytes;
                this.values = tmp;
                tmp = null;
            }
        }
        return;
    }
}

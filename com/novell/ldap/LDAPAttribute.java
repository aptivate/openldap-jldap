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
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import com.novell.ldap.client.ArrayList;
import com.novell.ldap.client.ArrayEnumeration;

import java.util.Enumeration;
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
    private String baseName = null;   // cn of cn;lang-ja;phonetic
    private String[] subTypes = null; // lang-ja of cn;lang-ja
    private ArrayList values = new ArrayList(5);

    /**
     * Constructs an attribute with copies of all values of the input
     * attribute.
     *
     * @param attr  An LDAPAttribute to use as a template.
     */
    public LDAPAttribute(LDAPAttribute attr) {
        this(attr.getName(), attr.getStringValueArray());
    }

    /**
     * Constructs an attribute with no values.
     *
     * @param attrName Name of the attribute.
     */
    public LDAPAttribute(String attrName) {
        this(attrName, (String[])null);
    }

    /**
     * Constructs an attribute with a byte-formatted value.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrBytes Value of the attribute as raw bytes.
     * <P> Note: If attrBytes is a string if should be UTF8 encoded. </P>
     */
    public LDAPAttribute(String attrName, byte attrBytes[]) {
        //this(attrName, new String[]{new String(attrBytes)});
        //NOTE: raw bytes are taken in straight and not converted to a string
        //This is because we don't know if it should be a UTF8 string or
        //just raw bytes
        name = attrName;
        subTypes = getSubtypes(attrName);
        baseName = getBaseName(attrName);
        values.add(attrBytes);
    }

    /**
     * Constructs an attribute that has a single string value.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrString Value of the attribute as a string.
     */
    public LDAPAttribute(String attrName, String attrString) {
        this(attrName, new String[]{attrString});
    }

    /**
     * Constructs an attribute that has an array of string values.
     *
     * @param attrName Name of the attribute.<br><br>
     * @param attrStrings Array of values as strings.
     */
    public LDAPAttribute(String attrName, String[] attrStrings) {
        name = attrName;

        getSubtypes(attrName);

        if(attrStrings != null) {
            for(int i=0; i<attrStrings.length; i++) {
                values.add(attrStrings[i]);
            }
        }
    }

    /**
     * Adds a string value to the attribute.
     *
     * @param attrString Value of the attribute as a string.
     */
    public void addValue(String attrString) {
        values.add(attrString);
    }

    /**
     * Adds a byte-formatted value to the attribute.
     *
     * @param attrBytes Value of the attribute as raw bytes.
     * <P> Note: If attrBytes is a string if should be UTF8 encoded. </P>
     */
    public void addValue(byte[] attrBytes) {
        values.add(attrBytes);
    }

    /**
     * Returns an enumerator for the values of the attribute in byte
     * format.
     *
     * @return The values of the attribute in byte format.
     * <P> Note: All string values will be UTF8 encoded. To decode use the
     * toString method. Example:  byteArray.toString("UTF8"); </P>
     */
    public Enumeration getByteValues() {
        ArrayList bv = new ArrayList(values.size());
        Object[] a = values.toArray();
        for( int i=0; i < a.length; i++) {
            Object o = a[i];
            if(o instanceof String) {
                try{
                bv.add(((String)o).getBytes("UTF8"));
                }catch (UnsupportedEncodingException uee){}
            }
            else {//if this byte[] is a string it will be UTF8 encoded also
                bv.add(o);
            }
        }
        return new ArrayEnumeration( bv.toArray());
    }

    /**
     * Returns an enumerator for the string values of an attribute.
     *
     * @return The string values of an attribute.
     */
    public Enumeration getStringValues() {
        ArrayList sv = new ArrayList(values.size());
        Object[] a = values.toArray();
        for( int i=0; i < a.length; i++) {
            Object o = a[i];
            if(o instanceof String) {
                sv.add(o);
            }
            else {
                try{
                  sv.add(new String((byte[])o, "UTF8"));
                }catch(UnsupportedEncodingException uee){}
            }
        }
        return new ArrayEnumeration(sv.toArray());
    }

    /**
     * Returns the values of the attribute as an array of bytes.
     *
     * @return The values as an array of bytes.
     */
    public byte[][] getByteValueArray() {
        byte[][] bva = new byte[values.size()][];
        int i=0;
        Object[] a = values.toArray();
        for( int j=0; j < a.length; j++) {
            Object o = a[j];
            bva[i++] = (o instanceof String) ? ((String)o).getBytes() : (byte[])o;
        }//do you want these bytes in UTF8??? if so ^^^ needs ,"UTF8"
        return bva;
    }

    /**
     * Returns the values of the attribute as an array of strings.
     *
     * @return The values as an array of strings.
     */
    public String[] getStringValueArray() {
        String[] sva = new String[values.size()];
        int i=0;
        Object[] a = values.toArray();
        for( int j=0; j < a.length; j++) {
            Object o = a[j];
            try{
            sva[i++] = (o instanceof String) ? (String)o : new String((byte[])o, "UTF8");
            }catch(UnsupportedEncodingException uee){}
        }
        return sva;
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
    public String getLangSubtype() {
        for(int i=0; i<subTypes.length; i++) {
            if(subTypes[i].startsWith("lang-")) {
                return subTypes[i];
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
    public String getBaseName() {
        return baseName;
    }

    /**
     * Returns the base name of the specified attribute.
     *
     * <p>For example, if the attribute name is cn;lang-ja;phonetic,
     * this method returns cn.</p>
     *
     * @param attrName Name of the attribute from which to extract the
     * base name.
     *
     * @return The base name of the attribute.
     */
    public static String getBaseName(String attrName) {
        StringTokenizer st = new StringTokenizer(attrName, ";");
        if(st.hasMoreElements())
            return (String)st.nextElement();
        return attrName;
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
    public boolean hasSubtype(String subtype) {
        for(int i=0; i<subTypes.length; i++) {
            if(subTypes[i].equalsIgnoreCase(subtype))
                return true;
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
    public void removeValue(String attrString) {
        String[] a = (String [])values.toArray();
        for( int i=0; i < a.length; i++) {
            if( attrString.equalsIgnoreCase(a[i])) {
                values.remove(i);
                break;
            }
        }
        return;
    }

    /**
     * Removes a byte-formatted value from the attribute.
     *
     * @param attrBytes    Value of the attribute as raw bytes.
     * <P> Note: If attrBytes is a string if should be UTF8 encoded. Example:
     * aString.getBytes("UTF8"); </P>
     */
    public void removeValue(byte[] attrBytes) {
        Object[] a = values.toArray();
        for( int i=0; i < a.length; i++) {
            if( attrBytes.equals(a[i])) {
                values.remove(i);
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
    public int size() {
        return values.size();
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/org/ietf/ldap/LDAPAttribute.java,v 1.1 2001/06/26 15:48:39 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package org.ietf.ldap;

import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
/**
 * Represents the name and values of one attribute of a directory entry.
 *
 * @see com.novell.ldap.LDAPAttribute
 */
public class LDAPAttribute {
    private com.novell.ldap.LDAPAttribute attr;

    /**
     * Constructs an attribute from a com.novell.ldap.LDAPAttribute
     */
    /* package */
    LDAPAttribute(com.novell.ldap.LDAPAttribute attr) {
        this.attr = attr;
        return; 
    }

    /**
     * Constructs an attribute with copies of all values of the input
     * attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#LDAPAttribute(LDAPAttribute)
     */
    public LDAPAttribute(LDAPAttribute attr) {
        com.novell.ldap.LDAPAttribute at = null;
        if( at != null) {
            at = attr.getWrappedObject();
        }
        this.attr = new com.novell.ldap.LDAPAttribute(at);
        return; 
    }

    /**
     * Constructs an attribute with no values.
     *
     * @see com.novell.ldap.LDAPAttribute#LDAPAttribute(String)
     */
    public LDAPAttribute(String attrName) {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName);
        return;
    }

    /**
     * Constructs an attribute with a byte-formatted value.
     *
     * @see com.novell.ldap.LDAPAttribute#LDAPAttribute(String, byte[])
     */
    public LDAPAttribute(String attrName, byte[] attrBytes) {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName, attrBytes);
        return;
    }

    /**
     * Constructs an attribute that has a single string value.
     *
     * @see com.novell.ldap.LDAPAttribute#LDAPAttribute(String, String)
     */
    public LDAPAttribute(String attrName, String attrString) {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName, attrString);
        return;
    }

    /**
     * Constructs an attribute that has an array of string values.
     *
     * @see com.novell.ldap.LDAPAttribute#LDAPAttribute(String, String[])
     */
    public LDAPAttribute(String attrName, String[] attrStrings) {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName, attrStrings);
        return;
    }

    /**
     * Gets the Novell LDAPAttribute class
     *
     * @return the com.novell.ldap.LDAPAttribute class
     */
    /* package */
    com.novell.ldap.LDAPAttribute getWrappedObject()
    {
        return attr;
    }

    /**
     * Adds a string value to the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#addValue(String)
     */
    public void addValue(String attrString) {
        attr.addValue( attrString);
        return;
    }

    /**
     * Adds a byte-formatted value to the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#addValue(byte[])
     */
    public void addValue(byte[] attrBytes) {
        attr.addValue( attrBytes);
        return;
    }

    /**
     * Returns an enumerator for the values of the attribute in byte
     * format.
     *
     * @see com.novell.ldap.LDAPAttribute#getByteValues()
     */
    public Enumeration getByteValues() {
        return attr.getByteValues();
    }

    /**
     * Returns an enumerator for the string values of an attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#getStringValues()
     */
    public Enumeration getStringValues() {
        return attr.getStringValues();
    }

    /**
     * Returns the values of the attribute as an array of bytes.
     *
     * @see com.novell.ldap.LDAPAttribute#getByteValueArray()
     */
    public byte[][] getByteValueArray() {
        return attr.getByteValueArray();
    }

    /**
     * Returns the values of the attribute as an array of strings.
     *
     * @see com.novell.ldap.LDAPAttribute#getStringValueArray()
     */
    public String[] getStringValueArray() {
        return attr.getStringValueArray();
    }

    /**
     * Returns the language subtype of the attribute, if any.
     *
     * @see com.novell.ldap.LDAPAttribute#getLangSubtype()
     */
    public String getLangSubtype() {
        return attr.getLangSubtype();
    }

    /**
     * Returns the base name of the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#getBaseName()
     */
    public String getBaseName() {
        return attr.getBaseName();
    }

    /**
     * Returns the base name of the specified attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#getBaseName(String)
     */
    public static String getBaseName(String attrName) {
        return com.novell.ldap.LDAPAttribute.getBaseName(attrName);
    }

    /**
     * Returns the name of the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#getName()
     */
    public String getName() {
        return attr.getName();
    }

    /**
     * Extracts the subtypes from the attribute name.
     *
     * @see com.novell.ldap.LDAPAttribute#getName()
     */
    public String[] getSubtypes() {
        return attr.getSubtypes();
    }

    /**
     * Extracts the subtypes from the specified attribute name.
     *
     * @see com.novell.ldap.LDAPAttribute#getSubtypes(String)
     */
    public static String[] getSubtypes(String attrName) {
        return com.novell.ldap.LDAPAttribute.getSubtypes(attrName);
    }

    /**
     * Reports if the attribute name contains the specified subtype.
     *
     * @see com.novell.ldap.LDAPAttribute#hasSubtype(String)
     */
    public boolean hasSubtype(String subtype) {
        return attr.hasSubtype(subtype);
    }

    /**
     * Reports if the attribute name contains all the specified subtypes.
     *
     * @see com.novell.ldap.LDAPAttribute#hasSubtypes(String[])
     */
    public boolean hasSubtypes(String[] subtypes) {
        return attr.hasSubtypes(subtypes);
    }

    /**
     * Removes a string value from the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#removeValue(String)
     */
    public void removeValue(String attrString) {
        attr.removeValue(attrString);
        return;
    }

    /**
     * Removes a byte-formatted value from the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#removeValue(byte[])
     */
    public void removeValue(byte[] attrBytes) {
        attr.removeValue(attrBytes);
        return;
    }

    /**
     * Returns the number of values in the attribute.
     *
     * @see com.novell.ldap.LDAPAttribute#size()
     */
    public int size() {
        return attr.size();
    }
}

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

package org.ietf.ldap;

import java.util.Enumeration;

/**
 * Represents the name and values of one attribute of a directory entry.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html">
            com.novell.ldap.LDAPAttribute</a>
 */
public class LDAPAttribute implements java.lang.Cloneable,
                                      java.lang.Comparable
{
    private com.novell.ldap.LDAPAttribute attr;

    /**
     * Constructs an attribute from a com.novell.ldap.LDAPAttribute
     */
    /* package */
    LDAPAttribute(com.novell.ldap.LDAPAttribute attr)
    {
       if( attr == null) {
            throw new 
            IllegalArgumentException("LDAPAttribute class cannot be null");
        }
        this.attr = attr;
        return;
    }

    /**
     * Constructs an attribute with copies of all values of the input
     * attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#LDAPAttribute(com.novell.ldap.LDAPAttribute)">
            com.novell.ldap.LDAPAttribute.LDAPAttribute(LDAPAttribute)</a>
     */
    public LDAPAttribute(LDAPAttribute attr)
    {
        com.novell.ldap.LDAPAttribute at = null;
        if( attr != null) {
            at = attr.getWrappedObject();
        }
        this.attr = new com.novell.ldap.LDAPAttribute(at);
        return;
    }

    /**
     * Constructs an attribute with no values.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#LDAPAttribute(java.lang.String)">
            com.novell.ldap.LDAPAttribute.LDAPAttribute(String)</a>
     */
    public LDAPAttribute(String attrName)
    {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName);
        return;
    }

    /**
     * Constructs an attribute with a byte-formatted value.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#LDAPAttribute(java.lang.String, byte[])">
            com.novell.ldap.LDAPAttribute.LDAPAttribute(String, byte[])</a>
     */
    public LDAPAttribute(String attrName, byte[] attrBytes)
    {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName, attrBytes);
        return;
    }

    /**
     * Constructs an attribute that has a single string value.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#LDAPAttribute(java.lang.String, java.lang.String)">
            com.novell.ldap.LDAPAttribute.LDAPAttribute(String, String)</a>
     */
    public LDAPAttribute(String attrName, String attrString)
    {
        this.attr = new com.novell.ldap.LDAPAttribute(attrName, attrString);
        return;
    }

    /**
     * Constructs an attribute that has an array of string values.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#LDAPAttribute(java.lang.String, java.lang.String[])">
            com.novell.ldap.LDAPAttribute.LDAPAttribute(String, String[])</a>
     */
    public LDAPAttribute(String attrName, String[] attrStrings)
    {
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#addValue(java.lang.String)">
            com.novell.ldap.LDAPAttribute.addValue(String)</a>
     */
    public void addValue(String attrString)
    {
        attr.addValue( attrString);
        return;
    }

    /**
     * Adds a byte-formatted value to the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#addValue(byte[])">
            com.novell.ldap.LDAPAttribute.addValue(byte[])</a>
     */
    public void addValue(byte[] attrBytes)
    {
        attr.addValue( attrBytes);
        return;
    }

    /**
     * Returns an enumerator for the values of the attribute in byte
     * format.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getByteValues()">
            com.novell.ldap.LDAPAttribute.getByteValues()</a>
     */
    public Enumeration getByteValues()
    {
        return attr.getByteValues();
    }

    /**
     * Returns an enumerator for the string values of an attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getStringValues()">
            com.novell.ldap.LDAPAttribute.getStringValues()</a>
     */
    public Enumeration getStringValues()
    {
        return attr.getStringValues();
    }

    /**
     * Returns the values of the attribute as an array of bytes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getByteValueArray()">
            com.novell.ldap.LDAPAttribute.getByteValueArray()</a>
     */
    public byte[][] getByteValueArray()
    {
        return attr.getByteValueArray();
    }

    /**
     * Returns the values of the attribute as an array of strings.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getStringValueArray()">
            com.novell.ldap.LDAPAttribute.getStringValueArray()</a>
     */
    public String[] getStringValueArray()
    {
        return attr.getStringValueArray();
    }

    /**
     * Returns the language subtype of the attribute, if any.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getLangSubtype()">
            com.novell.ldap.LDAPAttribute.getLangSubtype()</a>
     */
    public String getLangSubtype()
    {
        return attr.getLangSubtype();
    }

    /**
     * Returns the base name of the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getBaseName()">
            com.novell.ldap.LDAPAttribute.getBaseName()</a>
     */
    public String getBaseName()
    {
        return attr.getBaseName();
    }

    /**
     * Returns the base name of the specified attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getBaseName(java.lang.String)">
            com.novell.ldap.LDAPAttribute.getBaseName(String)</a>
     */
    public static String getBaseName(String attrName)
    {
        return com.novell.ldap.LDAPAttribute.getBaseName(attrName);
    }

    /**
     * Returns the name of the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getName()">
            com.novell.ldap.LDAPAttribute.getName()</a>
     */
    public String getName()
    {
        return attr.getName();
    }

    /**
     * Extracts the subtypes from the attribute name.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getSubtypes()">
            com.novell.ldap.LDAPAttribute.getSubtypes()</a>
     */
    public String[] getSubtypes()
    {
        return attr.getSubtypes();
    }

    /**
     * Extracts the subtypes from the specified attribute name.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#getSubtypes(java.lang.String)">
            com.novell.ldap.LDAPAttribute.getSubtypes(String)</a>
     */
    public static String[] getSubtypes(String attrName)
    {
        return com.novell.ldap.LDAPAttribute.getSubtypes(attrName);
    }

    /**
     * Reports if the attribute name contains the specified subtype.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#hasSubtype(java.lang.String)">
            com.novell.ldap.LDAPAttribute.hasSubtype(String)</a>
     */
    public boolean hasSubtype(String subtype)
    {
        return attr.hasSubtype(subtype);
    }

    /**
     * Reports if the attribute name contains all the specified subtypes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#hasSubtypes(java.lang.String[])">
            com.novell.ldap.LDAPAttribute.hasSubtypes(String[])</a>
     */
    public boolean hasSubtypes(String[] subtypes)
    {
        return attr.hasSubtypes(subtypes);
    }

    /**
     * Removes a string value from the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#removeValue(java.lang.String)">
            com.novell.ldap.LDAPAttribute.removeValue(String)</a>
     */
    public void removeValue(String attrString)
    {
        attr.removeValue(attrString);
        return;
    }

    /**
     * Removes a byte-formatted value from the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#removeValue(byte[])">
            com.novell.ldap.LDAPAttribute.removeValue(byte[])</a>
     */
    public void removeValue(byte[] attrBytes)
    {
        attr.removeValue(attrBytes);
        return;
    }

    /**
     * Returns the number of values in the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#size()">
            com.novell.ldap.LDAPAttribute.size()</a>
     */
    public int size()
    {
        return attr.size();
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#compareTo(java.lang.Object)">
            com.novell.ldap.LDAPAttribute.compareTo(Object)</a>
     */
    public int compareTo(Object attribute)
    {
        return this.attr.compareTo(
                ((LDAPAttribute)attribute).getWrappedObject() );
    }

    /**
     * Returns a clone of this object
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttribute.html#clone()">
            com.novell.ldap.LDAPAttribute.clone()</a>
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            ((LDAPAttribute)newObj).attr = (com.novell.ldap.LDAPAttribute)this.attr.clone();
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }
}

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
 *  The base class for representing LDAP schema elements.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html">
            com.novell.ldap.LDAPSchemaElement</a>
 */
public abstract class LDAPSchemaElement extends LDAPAttribute
{
    /* package */
    com.novell.ldap.LDAPSchemaElement schemaElement;

    /**
     * Set the Novell class that implements LDAPSchemaElement
     */
    /* package */
    LDAPSchemaElement( com.novell.ldap.LDAPSchemaElement element)
    {
        super(element);
        schemaElement = element;
        return;
    }

    /**
     * Returns an array of names for the element, or null if
     * none is found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#getNames()">
            com.novell.ldap.LDAPSchemaElement.getNames()</a>
     */
    public String[] getNames()
    {
        return schemaElement.getNames();
    }

    /**
     * Returns the description of the element.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#getDescription()">
            com.novell.ldap.LDAPSchemaElement.getDescription()</a>
     */
    public String getDescription()
    {
        return schemaElement.getDescription();
    }

    /**
     * Returns the unique object identifier (OID) of the element.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#getID()">
            com.novell.ldap.LDAPSchemaElement.getID()</a>
     */
    public String getID()
    {
        return schemaElement.getID();
    }

    /**
     * Returns an array of all values of a specified optional or non-
     * standard qualifier of the element.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#getQualifier(java.lang.String)">
            com.novell.ldap.LDAPSchemaElement.getID(java.lang.String)</a>
     */
    public String[] getQualifier(String name)
    {
        return schemaElement.getQualifier( name);
    }

    /**
     * Returns an enumeration of all qualifiers of the element which are
     * vendor specific (begin with "X-").
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#getQualifierNames()">
            com.novell.ldap.LDAPSchemaElement.getQualifierNames()</a>
     */
    public Enumeration getQualifierNames()
    {
        return schemaElement.getQualifierNames();
    }

    /**
     * Returns whether the element has the OBSOLETE qualifier
     * in its LDAP definition.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#isObsolete()">
            com.novell.ldap.LDAPSchemaElement.isObsolete()</a>
     */
    public boolean isObsolete()
    {
        return schemaElement.isObsolete();
    }

    /**
     * Returns a string in a format suitable for directly adding to a
     * directory, as a value of the particular schema element.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#toString()">
            com.novell.ldap.LDAPSchemaElement.toString()</a>
     */
    public String toString()
    {
        return schemaElement.toString();
    }

    /**
     * Sets the values of a specified optional or non-standard qualifier of
     * the element.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchemaElement.html#setQualifier(java.lang.String, java.lang.String[])">
            com.novell.ldap.LDAPSchemaElement.setQualifier( java.lang.String,
            java.lang.String[])</a>
     */
    public void setQualifier(String name, String[] values)
    {
        schemaElement.setQualifier( name, values);
        return;
    }
}

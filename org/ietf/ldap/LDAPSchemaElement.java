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
 *  @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html">
            com.novell.ldap.LDAPSchemaElement</a>
 */
public abstract class LDAPSchemaElement
{
    /* package */
    com.novell.ldap.LDAPSchemaElement schemaElement;

    /**
     * Set the Novell class that implements LDAPSchemaElement
     */
    /* package */
    LDAPSchemaElement( com.novell.ldap.LDAPSchemaElement element)
    {
        schemaElement = element;
        return;
    }

    /**
     * Get the Novell class that implements LDAPSchemaElement
     */
    /* package */
    com.novell.ldap.LDAPSchemaElement getWrappedObject( )
    {
        return schemaElement;
    }

    /**
     * Returns an array of alternative names for the element, or null if
     * none is found.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getAliases()">
            com.novell.ldap.LDAPSchemaElement.getAliases()</a>
     */
    public String[] getAliases()
    {
        return schemaElement.getAliases();
    }

    /**
     * Returns the description of the element.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getDescription()">
            com.novell.ldap.LDAPSchemaElement.getDescription()</a>
     */
    public String getDescription()
    {
        return schemaElement.getDescription();
    }

    /**
     * Returns the name of the element.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getName()">
            com.novell.ldap.LDAPSchemaElement.getName()</a>
     */
    public String getName()
    {
        return schemaElement.getName();
    }

    /**
     * Returns the unique object identifier (OID) of the element.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getID()">
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
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getQualifier(java.lang.String)">
            com.novell.ldap.LDAPSchemaElement.getID(String)</a>
     */
    public String[] getQualifier(String name)
    {
        return schemaElement.getQualifier( name);
    }

    /**
     * Returns an enumeration of all qualifiers of the element which are
     * vendor specific (begin with "X-").
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getQualifierNames()">
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
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #isObsolete()">
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
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #getValue()">
            com.novell.ldap.LDAPSchemaElement.getValue()</a>
     */
    public String getValue()
    {
        return schemaElement.getValue();
    }

    /**
     * Sets the values of a specified optional or non-standard qualifier of
     * the element.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #setQualifier(java.lang.String, java.lang.String[])">
            com.novell.ldap.LDAPSchemaElement.setQualifier(String, String[])</a>
     */
    public void setQualifier(String name, String[] values)
    {
        schemaElement.setQualifier( name, values);
        return;
    }

    /**
     * Adds the definition to a directory. An exception is thrown if the
     * definition cannot be added.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #add(com.novell.ldap.LDAPConnection)">
            com.novell.ldap.LDAPSchemaElement.add(LDAPConnection)</a>
     */
    public void add(LDAPConnection ld) throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        try {
            schemaElement.add( l);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Adds the definition to a directory, at a specified location. An exception
     * is thrown if the definition cannot be added.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #add(com.novell.ldap.LDAPConnection, java.lang.String)">
            com.novell.ldap.LDAPSchemaElement.add(LDAPConnection, String)</a>
     */
    public void add(LDAPConnection ld, String dn) throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        try {
            schemaElement.add( l, dn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Removes the definition from a directory. An exception is thrown if
     * the definition cannot be removed.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #remove(com.novell.ldap.LDAPConnection)">
            com.novell.ldap.LDAPSchemaElement.remove(LDAPConnection)</a>
     */
    public void remove(LDAPConnection ld) throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        try {
            schemaElement.remove( l);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Removes the definition from a directory, at a specified location.
     * An exception is thrown if the definition cannot be removed.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #remove(com.novell.ldap.LDAPConnection, java.lang.String)">
            com.novell.ldap.LDAPSchemaElement.remove(LDAPConnection, String)</a>
     */
    public void remove(LDAPConnection ld, String dn) throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        try {
            schemaElement.remove( l, dn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Replaces a single value of the schema element definition in the
     * schema. An exception is thrown if the definition cannot be modified.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #modify(com.novell.ldap.LDAPConnection, 
            com.novell.ldap.LDAPSchemaElement)">
            com.novell.ldap.LDAPSchemaElement.modify(LDAPConnection,
            LDAPSchemaElement)</a>
     */
    public void modify(LDAPConnection ld, LDAPSchemaElement newValue)
                    throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        com.novell.ldap.LDAPSchemaElement e = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        if( newValue != null) {
            e = newValue.getWrappedObject();
        }
        try {
            schemaElement.modify( l, e);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }

    /**
     * Replaces a single value of the schema element definition in the
     * schema, at a specified location in the directory. An exception is thrown
     * if the definition cannot be modified.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPSchemaElement.html
            #modify(com.novell.ldap.LDAPConnection, 
            com.novell.ldap.LDAPSchemaElement, java.lang.String)">
            com.novell.ldap.LDAPSchemaElement.modify(LDAPConnection,
            LDAPSchemaElement, String)</a>
     */
    public void modify(LDAPConnection ld, LDAPSchemaElement newValue, String dn)
                    throws LDAPException
    {
        com.novell.ldap.LDAPConnection l = null;
        com.novell.ldap.LDAPSchemaElement e = null;
        if( ld != null) {
            l = ld.getWrappedObject();
        }
        if( newValue != null) {
            e = newValue.getWrappedObject();
        }
        try {
            schemaElement.modify( l, e, dn);
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
        return;
    }
}

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

/**
 *  Represents a single entry in a directory, consisting of
 *  a distinguished name (DN) and zero or more attributes.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html">
            com.novell.ldap.LDAPEntry</a>
 */
public class LDAPEntry implements java.lang.Comparable
{
    private com.novell.ldap.LDAPEntry entry;
    /**
     * Constructs an Entry from com.novell.ldap.LDAPEntry
     */
    /* package */
    LDAPEntry( com.novell.ldap.LDAPEntry entry)
    {
        this.entry = entry;
        return;
    }

    /**
     * Constructs an empty entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#LDAPEntry()">
            com.novell.ldap.LDAPEntry.LDAPEntry()</a>
     */
    public LDAPEntry()
    {
        entry = new com.novell.ldap.LDAPEntry();
        return;
    }

    /**
     * Constructs a new entry with the specified distinguished name and with
     * an empty attribute set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#LDAPEntry(java.lang.String)">
            com.novell.ldap.LDAPEntry.LDAPEntry(String)</a>
     */
    public LDAPEntry(String dn)
    {
        entry = new com.novell.ldap.LDAPEntry(dn);
        return;
    }

    /**
     * Constructs a new entry with the specified distinguished name and set
     * of attributes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#LDAPEntry(java.lang.String, com.novell.ldap.LDAPAttributeSet)">
            com.novell.ldap.LDAPEntry.LDAPEntry(String, LDAPAttributeSet)</a>
     */
    public LDAPEntry(String dn, LDAPAttributeSet attrs)
    {
        com.novell.ldap.LDAPAttributeSet attrset = null;
        if( attrs != null) {
            attrset = attrs.getWrappedObject();
        }
        entry = new com.novell.ldap.LDAPEntry(dn, attrset);
        return;
    }

    /**
     * Gets the com.novell.ldap.LDAPEntry object
     */
    com.novell.ldap.LDAPEntry getWrappedObject()
    {
        return entry;
    }

    /**
     * Returns the attributes matching the specified attrName.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#getAttribute(java.lang.String)">
            com.novell.ldap.LDAPEntry.getAttribute(String)</a>
     */
    public LDAPAttribute getAttribute(String attrName)
    {
        com.novell.ldap.LDAPAttribute attr;
        if( (attr = entry.getAttribute(attrName)) == null) {
            return null;
        }
        return new LDAPAttribute( attr);
    }

    /**
     * Returns the attribute set of the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#getAttributeSet()">
            com.novell.ldap.LDAPEntry.getAttributeSet()</a>
     */
    public LDAPAttributeSet getAttributeSet()
    {
        return new LDAPAttributeSet( entry.getAttributeSet());
    }


    /**
     * Returns an attribute set from the entry, consisting of only those
     * attributes matching the specified subtypes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#getAttributeSet(java.lang.String)">
            com.novell.ldap.LDAPEntry.getAttributeSet(String)</a>
     */
    public LDAPAttributeSet getAttributeSet(String subtype)
    {
        return new LDAPAttributeSet( entry.getAttributeSet( subtype));
    }

    /**
     * Returns the distinguished name of the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#getDN()">
            com.novell.ldap.LDAPEntry.getDN()</a>
     */
    public String getDN()
    {
        return entry.getDN();
    }

    /**
     * Compares this object to the specified object for order.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPEntry.html#compareTo(java.lang.Object)">
            com.novell.ldap.LDAPEntry.compareTo(Object)</a>
     */
    public int compareTo(Object entry){
        return this.entry.compareTo( ((LDAPEntry)entry).getWrappedObject() );
    }
}

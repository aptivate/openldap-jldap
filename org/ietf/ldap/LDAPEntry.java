/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPEntry.java,v 1.13 2001/03/01 00:29:49 cmorris Exp $
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

/**
 *  Represents a single entry in a directory, consisting of
 *  a distinguished name (DN) and zero or more attributes.
 *
 * @see com.novell.ldap.LDAPEntry
 */
public class LDAPEntry
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
     * @see com.novell.ldap.LDAPEntry#LDAPEntry()
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
     * @see com.novell.ldap.LDAPEntry#LDAPEntry(String)
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
     * @see com.novell.ldap.LDAPEntry#LDAPEntry(String, LDAPAttributeSet)
     */
    public LDAPEntry(String dn, LDAPAttributeSet attrs)
    {
        com.novell.ldap.LDAPAttributeSet a = null;
        if( attrs != null) {
            attrs.getWrappedObject();
        }
        entry = new com.novell.ldap.LDAPEntry(dn, a);
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
     * @see com.novell.ldap.LDAPEntry#getAttribute(String)
     */
    public LDAPAttribute getAttribute(String attrName)
    {
        return new LDAPAttribute(entry.getAttribute(attrName));
    }

    /**
     * Returns the attribute set of the entry.
     *
     * @see com.novell.ldap.LDAPEntry#getAttributeSet()
     */
    public LDAPAttributeSet getAttributeSet()
    {
        return new LDAPAttributeSet( entry.getAttributeSet());
    }


    /**
     * Returns an attribute set from the entry, consisting of only those
     * attributes matching the specified subtypes.
     *
     * @see com.novell.ldap.LDAPEntry#getAttributeSet(String)
     */
    public LDAPAttributeSet getAttributeSet(String subtype)
    {
        return new LDAPAttributeSet( entry.getAttributeSet( subtype));
    }

    /**
     * Returns the distinguished name of the entry.
     *
     * @see com.novell.ldap.LDAPEntry#getDN()
     */
    public String getDN()
    {
        return entry.getDN();
    }
}

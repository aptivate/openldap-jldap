/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPCompareAttrNames.java,v 1.21 2001/04/19 18:55:52 cmorris Exp $
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

import java.util.Locale;

/**
 *  An object of this class supports sorting search results by attribute
 *  name, in ascending or descending order.
 *
 *  @see com.novell.ldap.LDAPCompareAttrNames
 */
public class LDAPCompareAttrNames implements LDAPEntryComparator
{

    private com.novell.ldap.LDAPCompareAttrNames comp;

    /**
     * Returns the com.novell.ldap.LDAPCompareAttrNames object
     */
    /* package */
    com.novell.ldap.LDAPCompareAttrNames getWrappedObject()
    {
        return comp;
    }

    /**
     * Constructs an object that sorts results by a single attribute, in
     * ascending order.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#LDAPCompareAttrNames(String)
     */
    public LDAPCompareAttrNames(String attrName)
    {
        comp = new com.novell.ldap.LDAPCompareAttrNames( attrName);
        return;
    }

    /**
     * Constructs an object that sorts results by a single attribute, in
     * either ascending or descending order.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#LDAPCompareAttrNames(String,
            boolean)
     */
    public LDAPCompareAttrNames(String attrName, boolean ascendingFlag)
    {
        comp = new com.novell.ldap.LDAPCompareAttrNames(
        attrName, ascendingFlag);
        return;
    }


    /**
     * Constructs an object that sorts by one or more attributes, in the
     * order provided, in ascending order.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#LDAPCompareAttrNames(String[])
     */
    public LDAPCompareAttrNames(String[] attrNames)
    {
        comp = new com.novell.ldap.LDAPCompareAttrNames( attrNames);
        return;
    }

    /**
     * Constructs an object that sorts by one or more attributes, in the
     * order provided, in either ascending or descending order for each
     * attribute.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#LDAPCompareAttrNames(
    String[], boolean[])
     */
    public LDAPCompareAttrNames(String[] attrNames, boolean[] ascendingFlags)
                            throws LDAPException
    {
        try {
            comp = new com.novell.ldap.LDAPCompareAttrNames(
            attrNames, ascendingFlags);
        } catch( com.novell.ldap.LDAPException ex) {
            throw new LDAPException( ex);
        }
        return;
    }

    /**
     * Returns the locale to be used for sorting, if a locale has been
     * specified.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#getLocale()
     */
    public Locale getLocale ()
    {
        return comp.getLocale();
    }

    /**
     * Sets the locale to be used for sorting.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#getLocale()
     */
    public void setLocale (Locale locale)
    {
        comp.setLocale( locale);
        return;
    }

    /**
     * Returns true if entry1 is to be considered greater than entry2, for
     * the purpose of sorting, based on the attribute name or names provided
     * isGreater returns true.
     *
     *  @see com.novell.ldap.LDAPCompareAttrNames#isGreater(LDAPEntry,LDAPEntry)
     */
    public boolean isGreater (LDAPEntry entry1, LDAPEntry entry2)
    {
        com.novell.ldap.LDAPEntry e1 = null;
        com.novell.ldap.LDAPEntry e2 = null;
        if( entry1 != null) {
            e1 = entry1.getWrappedObject();
        }
        if( entry2 != null) {
            e2 = entry2.getWrappedObject();
        }
        return comp.isGreater( e1,e2);
    }
}

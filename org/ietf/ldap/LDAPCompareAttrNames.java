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

import java.util.Locale;

/**
 *  An object of this class supports sorting search results by attribute
 *  name, in ascending or descending order.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html">
            com.novell.ldap.CompareAttrNames</a>
 */
public class LDAPCompareAttrNames implements java.util.Comparator
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#LDAPCompareAttrNames(java.lang.String)">
            com.novell.ldap.LDAPCompareAttrNames.LDAPCompareAttrNames(String)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#LDAPCompareAttrNames(java.lang.String, boolean)">
            com.novell.ldap.LDAPCompareAttrNames.LDAPCompareAttrNames(String,
            boolean)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#LDAPCompareAttrNames(java.lang.String[])">
            com.novell.ldap.LDAPCompareAttrNames.LDAPCompareAttrNames(String[])</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#LDAPCompareAttrNames(java.lang.String[], boolean[])">
            com.novell.ldap.LDAPCompareAttrNames.LDAPCompareAttrNames(String[],
            boolean[])</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#getLocale()">
            com.novell.ldap.LDAPCompareAttrNames.getLocale()</a>
     */
    public Locale getLocale ()
    {
        return comp.getLocale();
    }

    /**
     * Sets the locale to be used for sorting.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#setLocale(java.util.Locale)">
            com.novell.ldap.LDAPCompareAttrNames.setLocale(Locale)</a>
     */
    public void setLocale (Locale locale)
    {
        comp.setLocale( locale);
        return;
    }

    /**
     * Compares the the attributes of the first LDAPEntry to the second.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#compare(java.lang.Object, java.lang.Object)">
            com.novell.ldap.LDAPCompareAttrNames.Compare(Object, Object)</a>
     */
    public int compare (Object entry1, Object entry2)
    {
        com.novell.ldap.LDAPEntry e1 = ((LDAPEntry) entry1).getWrappedObject();
        com.novell.ldap.LDAPEntry e2 = ((LDAPEntry) entry2).getWrappedObject();
        return comp.compare( e1, e2);
    }

    /**
     * Returns true if this comparator is equal to the specified comparator.
     * @see <a href="../../../../api/com/novell/ldap/LDAPCompareAttrNames.html#equals(java.lang.Object)">
            com.novell.ldap.LDAPCompareAttrNames.equals(Object)</a>
     */
    public boolean equals (Object comparator){
        if (!(comparator instanceof LDAPCompareAttrNames)){
            return false;
        }
        return comp.equals( ((LDAPEntry)comparator).getWrappedObject() );
    }
}

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
 *  Holds a collection of LDAPModification objects, representing
 *  changes to be made to attributes of a single entry.
 *
 * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html">
            com.novell.ldap.LDAPModificationSet</a>
 */
public class LDAPModificationSet {

    private com.novell.ldap.LDAPModificationSet set;

    /**
     * Constructs modification set from a com.novell.ldap.LDAPModificationSet
     */
    /* package */
    LDAPModificationSet( com.novell.ldap.LDAPModificationSet set)
    {
        this.set = set;
        return;
    }

    /**
     * Constructs a new, empty set of modifications.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #LDAPModificationSet()">
            com.novell.ldap.LDAPModification.LDAPModificationSet()</a>
     */
    public LDAPModificationSet()
    {
        set = new com.novell.ldap.LDAPModificationSet();
        return;
    }

	/**
	 * returns a com.novell.ldap.LDAPModificationSet object
	 */
	/* package */
	com.novell.ldap.LDAPModificationSet getWrappedObject()
	{
		return set;
	}

    /**
     * Specifies a modification to be added to the set of
     * modifications.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #add(int, com.novell.ldap.LDAPAttribute)">
            com.novell.ldap.LDAPModification.add(int, LDAPAttribute)</a>
     */
    public void add(int op, LDAPAttribute attr)
    {
        set.add( op, attr.getWrappedObject());
        return;
    }

    /**
     * Retrieves a particular LDAPModification object at the position
     * specified by the index.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #elementAt(int)">
            com.novell.ldap.LDAPModification.elementAt(int)</a>
     */
    public LDAPModification elementAt(int index)
        throws ArrayIndexOutOfBoundsException
    {
        return new LDAPModification( set.elementAt( index));
    }

    /**
     * Removes the first attribute with the specified name in the set of
     * modifications.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #remove(java.lang.String)">
            com.novell.ldap.LDAPModification.remove(String)</a>
     */
    public void remove(String name)
    {
        set.remove( name);
        return;
    }

    /**
     * Removes a particular LDAPModification object at the position
     * specified by the index.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #removeElementAt(int)">
            com.novell.ldap.LDAPModification.removeElementAt(int)</a>
     */
    public void removeElementAt(int index)
        throws ArrayIndexOutOfBoundsException
    {
        set.removeElementAt( index);
        return;
    }

    /**
     * Retrieves the number of LDAPModification objects in this set.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPModificationSet.html
            #size()">
            com.novell.ldap.LDAPModification.size()</a>
     */
    public int size()
    {
        return set.size();
    }
}

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
 * @see com.novell.ldap.LDAPModificationSet
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
     * @see com.novell.ldap.LDAPModificationSet#LDAPModificationSet()
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
     * @see com.novell.ldap.LDAPModificationSet#add(int,LDAPAttribute)
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
     * @see com.novell.ldap.LDAPModificationSet#elementAt(int)
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
     * @see com.novell.ldap.LDAPModificationSet#remove(String)
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
     * @see com.novell.ldap.LDAPModificationSet#removeElementAt(int)
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
     * @see com.novell.ldap.LDAPModificationSet#size()
     */
    public int size()
    {
        return set.size();
    }
}

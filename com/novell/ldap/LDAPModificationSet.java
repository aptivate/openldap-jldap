/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import com.novell.ldap.client.ArrayList;

/**
 * A set of {@link LDAPModification} objects.
 *
 * <p>This class has been removed as of draft 17 of the Java LDAP API
 * (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
 * from the LDAP Classes for JavaAPI in the fall of 2003.</p>
 *
 * @see LDAPModification
 * @see LDAPConnection#modify(String, LDAPModification[])
 *
 *  @deprecated replaced by {@link LDAPModification}[], i.e., an array of
 * {@link LDAPModification} objects.
 */
public class LDAPModificationSet {

    private ArrayList modSet;

    /**
     * Constructs a new, empty set of modifications.
     */
    public LDAPModificationSet()
    {
        modSet = new ArrayList();
    }

    /**
     * Specifies a modification to be added to the set of
     * modifications.
     *
     *  @param op       The type of modification to make, which can be
     *                  one of the following:
     *<ul>
     *         <li>LDAPModification.ADD - The value should be added to
     *                                    the attribute</li>
     *
     *         <li>LDAPModification.DELETE - The value should be removed
     *                                       from the attribute </li>
     *
     *         <li>LDAPModification.REPLACE - The value should replace all
     *                                        existing values of the
     *                                        attribute </li>
     *</ul><br>
     *  @param attr     The attribute to modify.
     */
    public void add(int op, LDAPAttribute attr)
    {
        modSet.add(new LDAPModification(op, attr));
    }

    /*
     *  This convenience method is not in the internet draft
     */

    /**
    * Adds an LDAPModification object to the set.
    *
    * @param mod The LDAPModification object to add to the set.
    */
    /*package*/ void add(LDAPModification mod)
    {
        modSet.add(mod);
    }

    /**
     * Retrieves a particular LDAPModification object at the position
     * specified by the index.
     *
     *  @param index      Index of the modification to get.
     *
     * @exception ArrayIndexOutOfBoundsException The index value is out of
     *            range for the array.
     */
    public LDAPModification elementAt(int index)
        throws ArrayIndexOutOfBoundsException
    {
        return(LDAPModification)modSet.get(index);
    }

    /**
     * Removes the first attribute with the specified name in the set of
     * modifications.
     *
     *  @param name    Name of the attribute to remove.
     */
    public void remove(String name)
    {
        for(int i=0; i<modSet.size(); i++) {
            LDAPModification mod = (LDAPModification)modSet.get(i);
            LDAPAttribute attr = mod.getAttribute();
            if(attr.getName().equalsIgnoreCase(name)) {
                modSet.remove(i);
            }
        }
    }

    /**
     * Removes a particular LDAPModification object at the position
     * specified by the index.
     *
     *  @param index     Index of the modification object to remove.
     *
     * @exception ArrayIndexOutOfBoundsException The index value is out of
     *            range for the array.
     */
    public void removeElementAt(int index)
        throws ArrayIndexOutOfBoundsException
    {
        modSet.remove(index);
    }

    /**
     * Retrieves the number of LDAPModification objects in this set.
     *
     * @return The number of objects in this set.
     */
    public int size()
    {
        return modSet.size();
    }

    /**
     * Returns an the modifications as an array
     *
     * @return The modifications as an array
     */
    /* package */ LDAPModification[] toArray()
    {
        LDAPModification[] mods = new LDAPModification[modSet.size()];
        return (LDAPModification[])modSet.toArray(mods);
    }
}

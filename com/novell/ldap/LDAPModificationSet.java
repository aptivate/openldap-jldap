/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPModificationSet.java,v 1.6 2000/09/14 20:32:30 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/

package com.novell.ldap;

import java.util.Vector;

/*
 * 4.21 public class LDAPModificationSet
 */

/**
 *
 *  Represents a collection of modifications to be made to the attributes 
 *  of a single entry.
 */
public class LDAPModificationSet {

    private Vector modSet;

    /*
     * 4.21.1 Constructors
     */

    /**
     * Constructs a new, empty set of modifications.
     */
    public LDAPModificationSet()
    {
        modSet = new Vector();
    }

    /*
     * 4.21.2 add
     */

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
     *</ul><br><br>
     *  @param attr     The attribute to modify.
     */
    public synchronized void add(int op, LDAPAttribute attr)
    {
        modSet.addElement(new LDAPModification(op, attr));
    }

    /*
     *  This convenience method is not in the internet draft
     */

    /**
    * Adds an LDAPModification object to the set.
    *
    * @param mod The LDAPModification object to add to the set.
    */
    public synchronized void add(LDAPModification mod)
    {
        modSet.addElement(mod);
    }

    /*
     * 4.21.3 elementAt
     */

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
        return(LDAPModification)modSet.elementAt(index);
    }

    /*
     * 4.21.4 remove
     */

    /**
     * Removes the first attribute with the specified name in the set of
     * modifications.
     *
     *  @param name    Name of the attribute to be removed.
     */
    public synchronized void remove(String name)
    {
        for(int i=0; i<modSet.size(); i++) {
            LDAPModification mod = (LDAPModification)modSet.elementAt(i);
            LDAPAttribute attr = mod.getAttribute();
            if(attr.getName().equalsIgnoreCase(name)) {
                modSet.removeElementAt(i);
            }
        }
    }

    /*
     * 4.21.5 removeElementAt
     */

    /**
     * Removes a particular LDAPModification object at the position
     * specified by the index.
     *
     *  @param index     Index of the modification to remove.
     *
     * @exception ArrayIndexOutOfBoundsException The index value is out of
     *            range for the array.
     */
    public void removeElementAt(int index)
        throws ArrayIndexOutOfBoundsException
    {
        modSet.removeElementAt(index);
    }

    /*
     * 4.21.6 size
     */

    /**
     * Retrieves the number of LDAPModification objects in this set.
     *
     * @return The number of objects in this set.
     */
    public int size()
    {
        return modSet.size();
    }

}


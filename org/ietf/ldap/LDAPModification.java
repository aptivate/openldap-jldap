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
 * Represents a single add, delete, or replace operation to an LDAPAttribute.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html">
            com.novell.ldap.LDAPModification</a>
 */
public class LDAPModification
{
    private com.novell.ldap.LDAPModification mod;
 
    /**
     * Adds the listed values to the given attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#ADD">
            com.novell.ldap.LDAPModification.ADD</a>
     */
    public static final int ADD = 0;
 
    /**
     * Deletes the listed values from the given attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#DELETE">
            com.novell.ldap.LDAPModification.DELETE</a>
     */
    public static final int DELETE = 1;
 
    /**
     * Replaces all existing values of the given attribute
     * with the new values listed.
     * 
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#REPLACE">
            com.novell.ldap.LDAPModification.REPLACE</a>
     */
    public static final int REPLACE = 2;
 
    /**
     * Constructs an LDAPModification from a com.novell.ldap.LDAPModification
     */
    /* package */
	LDAPModification(com.novell.ldap.LDAPModification mod)
    {
        this.mod = mod;
        return;
    }
 
    /**
     * Specifies a modification to be made to an attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#LDAPModification(int, com.novell.ldap.LDAPAttribute)">
            com.novell.ldap.LDAPModification.LDAPModification(int,
            LDAPAttribute)</a>
     */
    public LDAPModification(int op, LDAPAttribute attr)
    {
        mod = new com.novell.ldap.LDAPModification(op, attr.getWrappedObject());
        return;
    }

    /**
     * Returns the com.novell.ldap.LDAPModification object
     */
    /* package */
    com.novell.ldap.LDAPModification getWrappedObject()
    {
        return mod;
    }

    /**
     * Returns the attribute to modify, with any existing values.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#getAttribute()">
            com.novell.ldap.LDAPModification.getAttribute()</a>
     */
    public LDAPAttribute getAttribute()
    {
        com.novell.ldap.LDAPAttribute attr;
        if( (attr = mod.getAttribute()) == null) {
            return null;
        }
        return new LDAPAttribute( attr);
    }
 
    /**
     * Returns the type of modification specified by this object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPModification.html#getOp()">
            com.novell.ldap.LDAPModification.getOp()</a>
     */
    public int getOp()
    {
        return mod.getOp();
    }
}

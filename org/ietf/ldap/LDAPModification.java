/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPModification.java,v 1.9 2001/03/01 00:29:52 cmorris Exp $
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
 * Represents a single add, delete, or replace operation to an LDAPAttribute.
 *
 * @see com.novell.ldap.LDAPModification
 */
public class LDAPModification
{
    private com.novell.ldap.LDAPModification mod;
 
    /**
     * Adds the listed values to the given attribute.
     *
     * @see com.novell.ldap.LDAPModification#ADD
     */
    public static final int ADD = 0;
 
    /**
     * Deletes the listed values from the given attribute.
     *
     * @see com.novell.ldap.LDAPModification#DELETE
     */
    public static final int DELETE = 1;
 
    /**
     * Replaces all existing values of the given attribute
     * with the new values listed.
     * 
     * @see com.novell.ldap.LDAPModification#REPLACE
     */
    public static final int REPLACE = 2;
 
    /**
     * Constructs an LDAPModification from a com.novell.ldap.LDAPModification
     *
     * @see com.novell.ldap.LDAPModification#LDAPModification(int,LDAPAttribute)
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
     * @see com.novell.ldap.LDAPModification#LDAPModification(int,LDAPAttribute)
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
     * @see com.novell.ldap.LDAPModification#getAttribute()
     */
    public LDAPAttribute getAttribute()
    {
        return new LDAPAttribute( mod.getAttribute());
    }
 
    /**
     * Returns the type of modification specified by this object.
     *
     * @see com.novell.ldap.LDAPModification#getOp()
     */
    public int getOp()
    {
        return mod.getOp();
    }
}


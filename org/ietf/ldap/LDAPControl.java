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
 *  Encapsulates additional optional parameters for an
 *  LDAP operation, either on the server or on the client.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPControl.html">
            com.novell.ldap.LDAPControl</a>
 */
public class LDAPControl
                extends com.novell.ldap.LDAPControl
                implements Cloneable
{
    /**
     * Constructs a control from a com.novell.ldap.LDAPControl object
     */
    protected LDAPControl( com.novell.ldap.LDAPControl control)
    {
        super( control.getID(), control.isCritical(), control.getValue());
        return;
    }

    /**
     * Constructs a new LDAPControl object using the specified values.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPControl.html#LDAPControl(java.lang.String, boolean, byte[])">
            com.novell.ldap.LDAPControl.LDAPControl(String, boolean, byte[])</a>
     */
    public LDAPControl(String id, boolean critical, byte[] vals)
    {
        super( id, critical, vals);
        return;
    }

    /**
     * Returns a clone of this object.
     *
     * @return a clone of this object
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPControl.html#clone()">com.novell.ldap.LDAPControl.clone()</a>
     */
    public Object clone()
    {
        return super.clone();
    }

    /**
     * Sets the control-specific data of the object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPControl.html#setValue(byte[])">
            com.novell.ldap.LDAPControl.setValue(byte[])</a>
     */
    protected void setValue(byte[] controlValue)
    {
        super.setValue( controlValue);
        return;
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/org/ietf/ldap/LDAPControl.java,v 1.1 2001/06/26 15:48:43 vtag Exp $
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

import com.novell.ldap.rfc2251.RfcControl;

/**
 *  Encapsulates additional optional parameters for an
 *  LDAP operation, either on the server or on the client.
 *
 * @see com.novell.ldap.LDAPControl
 */
public class LDAPControl
                implements Cloneable
{
    ControlImpl control;

    /**
     * Create an LDAPControl from an existing rfcControl.
     */
    protected LDAPControl(RfcControl control)
    {
        this.control = new ControlImpl( control);
        return;
    }

    /**
     * Constructs a control from a com.novell.ldap.LDAPControl object
     */
    protected LDAPControl( com.novell.ldap.LDAPControl control)
    {
        this.control = new ControlImpl( control.getID(),
                                        control.isCritical(),
                                        control.getValue());
        return;
    }

    /**
     * Constructs a new LDAPControl object using the specified values.
     *
     * @see com.novell.ldap.LDAPControl#LDAPControl(String,boolean,byte[])
     */
    public LDAPControl(String id, boolean critical, byte[] vals)
    {
        control = new ControlImpl( id, critical, vals);
        return;
    }

    /**
     * Returns the Control object
     */
    protected com.novell.ldap.LDAPControl getWrappedObject()
    {
        return control;
    }

    /**
     * Returns a copy of the current LDAPControl object.
     *
     * @see com.novell.ldap.LDAPControl#clone()
     */
    public Object clone()
    {
       return new ControlImpl(control);
    }

    /**
     * Returns the identifier of the control.
     *
     * @see com.novell.ldap.LDAPControl#getID()
     */
    public String getID()
    {
        return control.getID();
    }

    /**
     * Returns the control-specific data of the object.
     *
     * @see com.novell.ldap.LDAPControl#getValue()
     */
    public byte[] getValue()
    {
        return control.getValue();
    }


    /**
     * Sets the control-specific data of the object.
     *
     * @see com.novell.ldap.LDAPControl#setValue(byte[])
     */
    protected void setValue(byte[] controlValue)
    {
        control.setMyValue( controlValue);
        return;
    }


    /**
     * Returns whether the control is critical for the operation.
     *
     * @see com.novell.ldap.LDAPControl#isCritical()
     */
    public boolean isCritical()
    {
        return control.isCritical();
    }

    /**
     * Registers a class to be instantiated on receipt of a control with the
     * given OID.
     *
     * @see com.novell.ldap.LDAPControl#register(String,Class)
     */
    public static void register(String oid, Class controlClass)
    {
        com.novell.ldap.LDAPControl.register( oid, controlClass);
        return;
    }

    /*
     * Internal implementation of the com.novell.ldap.LDAPControl object
     * Extended so it is possible to access the setValue function
     */
    private class ControlImpl extends com.novell.ldap.LDAPControl
    {
        /**
         * Create an LDAPControl from an existing rfcControl.
         */
        private ControlImpl(RfcControl control)
        {
            super( control);
            return;
        }

        /**
         * Constructs a control from a com.novell.ldap.LDAPControl object
         */
        private ControlImpl( com.novell.ldap.LDAPControl control)
        {
            super( control.getID(), control.isCritical(), control.getValue());
            return;
        }

        /**
         * Constructs a new LDAPControl object using the specified values.
         *
         * @see com.novell.ldap.LDAPControl#LDAPControl(String,boolean,byte[])
         */
        private ControlImpl(String id, boolean critical, byte[] vals)
        {
            super( id, critical, vals);
            return;
        }

        /**
         * Sets the control-specific data of the object.
         * Allows the function to call the protected method setValue()
         *
         * @see com.novell.ldap.LDAPControl#setValue(byte[])
         */
        private void setMyValue(byte[] controlValue)
        {
            super.setValue( controlValue);
            return;
        }
    }
}

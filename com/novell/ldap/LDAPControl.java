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

package com.novell.ldap;

import com.novell.ldap.controls.*;
import com.novell.ldap.client.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 *  Encapsulates optional additional parameters or constraints to be applied to
 *  an LDAP operation.
 *
 * <p>When included with LDAPConstraints or LDAPSearchConstraints
 * on an LDAPConnection or with a specific operation request, it is
 * sent to the server along with operation requests.</p>
 *
 * @see LDAPConnection#getResponseControls
 * @see LDAPSearchConstraints#getControls
 * @see LDAPSearchConstraints#setControls
 */
public class LDAPControl implements Cloneable {

    private static RespControlVector registeredControls =
                                                    new RespControlVector(5, 5);

    private RfcControl control; // An RFC 2251 Control

    /**
     * Constructs a new LDAPControl object using the specified values.
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param values     The control-specific data.
     */
    public LDAPControl(String oid, boolean critical, byte[] values)
    {
        if( oid == null) {
            throw new IllegalArgumentException("An OID must be specified");
        }
        if( values == null) {
            values = new byte[]{};
        }
        control = new RfcControl(new RfcLDAPOID(oid), new ASN1Boolean(critical),
                              new ASN1OctetString(values));
        return;
    }

    /**
     * Create an LDAPControl from an existing control.
     */
    protected LDAPControl(RfcControl control)
    {
        this.control = control;
        return;
    }

    /**
     * Returns a copy of the current LDAPControl object.
     *
     * @return A copy of the current LDAPControl object.
     */
    public Object clone()
    {
       byte[] vals = this.getValue();
       byte[] twin = new byte[vals.length];
       for(int i = 0; i < vals.length; i++){
         twin[i]=vals[i];
       }//is this necessary?  Yes even though the contructor above allocates a
       //new ASN1OctetString.  vals in that constuctor is only copied by reference

       return (Object)( new LDAPControl(this.getID(), this.isCritical(), twin));
    }

    /**
     * Returns the identifier of the control.
     *
     * @return The object ID of the control.
     */
    public String getID()
    {
        return new String(control.getControlType().getContent());
    }

    /**
     * Returns the control-specific data of the object.
     *
     * @return The control-specific data of the object as a byte array.
     */
    public byte[] getValue()
    {
        return control.getControlValue().getContent();
    }


    /**
     * Sets the control-specific data of the object.  This method is for
     * use by extension of LDAPControl.
     */
    protected void setValue(byte[] controlValue)
    {
        control.setControlValue(new ASN1OctetString(controlValue));
        return;
    }


    /**
     * Returns whether the control is critical for the operation.
     *
     * @return Returns true if the control must be supported for an associated
     * operation to be executed, and false if the control is not required for
     * the operation.
     */
    public boolean isCritical()
    {
        return control.getCriticality().getContent();
    }

    /**
     * Registers a class to be instantiated on receipt of a control with the
     * given OID.
     *
     * <p>Any previous registration for the OID is overridden. The
     * controlClass must be an extension of LDAPControl.</p>
     *
     *  @param oid            The object identifier of the control.
     *<br><br>
     *  @param controlClass   A class which can instantiate an LDAPControl.
     */
    public static void register(String oid, Class controlClass)
    {
        registeredControls.registerResponseControl(oid, controlClass);
        return;
    }

    /* package */
    static RespControlVector getRegisteredControls()
    {
        return registeredControls;
    }
    
    /**
     * Returns the RFC 2251 Control object.
     *
     * @return An ASN.1 RFC 2251 Control.
     */
    /*package*/ RfcControl getASN1Object()
    {
        return control;
    }
}

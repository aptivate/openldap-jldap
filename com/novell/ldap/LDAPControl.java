/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPControl.java,v 1.16 2000/11/09 18:27:16 vtag Exp $
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

import com.novell.ldap.asn1.*;
import com.novell.ldap.protocol.*;

/**
 *  Encapsulates additional optional parameters for an 
 *  LDAP operation, either on the server or on the client.
 *
 * <p>If set as a server control, it is sent to the server along with the operation
 * request. If set as a client control, it is not sent to the server, but 
 * rather interpreted locally by the client. LDAPControl is an LDAPv3 extension,
 * and is not supported in an LDAPv2 environment.</p>
 */
public class LDAPControl implements Cloneable {

    private RfcControl control; // An RFC 2251 Control

    /**
     * Constructs a new LDAPControl object using the specified values.
     *
     *  @param id     The ID of the control, as a dotted string.
     *<br><br> 
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if 
     *                    the operation can be processed without the control.
     *<br><br> 
     *  @param vals     The control-specific data.
     */
    public LDAPControl(String id, boolean critical, byte[] vals)
    {
        control = new RfcControl(new RfcLDAPOID(id), new ASN1Boolean(critical),
                              new ASN1OctetString(vals));
    }

    /**
     * Create an LDAPControl from an existing control. 
     */
    /*package*/ LDAPControl(RfcControl control)
    {
        this.control = control;
    }

    /**
     * Returns a copy of the current LDAPControl object.
     *
     * @return A copy of the current LDAPControl object.
     */
    public Object clone()
    {
       throw new RuntimeException("Method LDAPControl.clone not implemented");
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
     * Instantiates a control, given the raw data representing it in an LDAP
     * message.
     *
     * @param data An array of data bytes for the control.
     */
    public static LDAPControl newInstance(byte[] data)
    {
        throw new RuntimeException("Method LDAPControl.newInstance not implemented");
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
        throw new RuntimeException("Method LDAPControl.register not implemented");
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

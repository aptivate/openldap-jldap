/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPControl.java,v 1.6 2000/08/28 22:18:56 vtag Exp $
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
 
import com.novell.asn1.*;
import com.novell.asn1.ldap.*;

/**
 *  Encapsulates optional additional parameters or constraints to be 
 *  applied to LDAP operations. 
 *
 * <p>If set as a server control, it is sent to the server along with operation
 * requests. If set as a client control, it is not sent to the server, but 
 * rather interpreted locally by the client. LDAPControl is an LDAPv3 extension,
 * and is not supported in an LDAPv2 environment.</p>
 */
public class LDAPControl implements Cloneable {

	private Control control; // An RFC 2251 Control

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
   public LDAPControl(String id, boolean critical, byte vals[]) {
		control = new Control(new LDAPOID(id), new ASN1Boolean(critical),
		                      new ASN1OctetString(vals));
   }

	/**
	 * Create an LDAPControl from an existing control. 
	 */
	public LDAPControl(Control control)
	{
		this.control = control;
	}

   /**
    * Returns a copy of the current LDAPControl object.
    *
    * @return A copy of the current LDAPControl object.
    */
   public Object clone() {
      return null;
   }

   /**
    * Returns the identifier of the control.
    *
    * @return The object ID of the control.
    */
   public String getID() {
		return new String(control.getControlType().getContent());
   }

   /**
    * Returns the control-specific data of the object.
    *
    * @return The control-specific data of the object as a byte array.
    */
   public byte[] getValue() {
		return control.getControlValue().getContent();
   }

   /**
    * Returns whether the control is critical for the operation.
    *
    * @return Returns true if the control must be supported for an associated
    * operation to be executed, and false is the control is not required for 
    * the operation.
    */
   public boolean isCritical() {
		return control.getCriticality().getContent();
   }

   /**
    * Instantiates a control, given the raw data representing it in an LDAP
    * message.
    *
    * @param data An array of data bytes for the control.
    */
   public static LDAPControl newInstance(byte[] data) {
      return null;
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
   public static void register(String oid, Class controlClass) {
   }

	/**
	 * Returns the object identifier of the control.
	 */
	public Control getASN1Object()
	{
		return control;
	}
}


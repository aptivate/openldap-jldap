/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPControl.java,v 1.3 2000/08/03 22:06:14 smerrill Exp $
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
 
package org.ietf.ldap;
 
import org.ietf.asn1.*;
import org.ietf.asn1.ldap.*;

/**
 *  An LDAPControl encapsulates optional additional parameters or
 *  constraints to be applied to LDAP operations. If set as a Server
 *  Control, it is sent to the server along with operation requests. If
 *  set as a Client Control, it is not sent to the server, but rather
 *  interpreted locally by the client. LDAPControl is an LDAPv3
 *  extension, and is not supported in an LDAPv2 environment.
 */
public class LDAPControl implements Cloneable {

	private Control control; // An RFC 2251 Control

   /**
    * Parameters are:
    *
    *  id             The type of the Control, as a string.
    *
    *  critical       True if the LDAP operation should be discarded if
    *                 the server does not support this Control.
    *
    *  vals           Control-specific data.
    */
   public LDAPControl(String id, boolean critical, byte vals[]) {
		control = new Control(new LDAPOID(id), new ASN1Boolean(critical),
		                      new ASN1OctetString(vals));
   }

	/**
	 * Create an LDAPControl from a Control. (not in draft)
	 */
	public LDAPControl(Control control)
	{
		this.control = control;
	}

   /**
    * Returns a deep copy of the object.
    */
   public Object clone() {
      return null;
   }

   /**
    * Returns the identifier of the control.
    */
   public String getID() {
		return new String(control.getControlType().getContent());
   }

   /**
    * Returns the control-specific data of the object.
    */
   public byte[] getValue() {
		return control.getControlValue().getContent();
   }

   /**
    * Returns true if the control must be supported for an associated
    * operation to be executed.
    */
   public boolean isCritical() {
		return control.getCriticality().getContent();
   }

   /**
    * Instantiates a control, given the raw data representing it in an LDAP
    * message.
    */
   public static LDAPControl newInstance(byte[] data) {
      return null;
   }

   /**
    * Registers a class to be instantiated on receipt of a control with the
    * given oid. Any previous registration for the oid is overridden. The
    * controlClass must be an extension of LDAPControl.
    *
    * Parameters are:
    *
    *  oid            The Object Identifier of the Control.
    *
    *  controlClass   A class which can instantiate an LDAPControl.
    */
   public static void register(String oid, Class controlClass) {
   }

	/**
	 *
	 */
	public Control getASN1Object()
	{
		return control;
	}
}


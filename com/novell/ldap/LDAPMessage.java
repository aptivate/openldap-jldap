/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPMessage.java,v 1.6 2000/08/21 18:35:42 vtag Exp $
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

//import com.novell.asn1.ASN1Object;
import com.novell.asn1.ldap.*;

/**
 *  Base class for LDAP request and response messages.
 */
public class LDAPMessage {

	protected com.novell.asn1.ldap.LDAPMessage message;

   public final static int BIND_REQUEST            = 0;
   public final static int BIND_RESPONSE           = 1;
   public final static int UNBIND_REQUEST          = 2;
   public final static int SEARCH_REQUEST          = 3;
   public final static int SEARCH_RESPONSE         = 4;
   public final static int SEARCH_RESULT           = 5;
   public final static int MODIFY_REQUEST          = 6;
   public final static int MODIFY_RESPONSE         = 7;
   public final static int ADD_REQUEST             = 8;
   public final static int ADD_RESPONSE            = 9;
   public final static int DEL_REQUEST             = 10;
   public final static int DEL_RESPONSE            = 11;
   public final static int MODIFY_RDN_REQUEST      = 12;
   public final static int MODIFY_RDN_RESPONSE     = 13;
   public final static int COMPARE_REQUEST         = 14;
   public final static int COMPARE_RESPONSE        = 15;
   public final static int ABANDON_REQUEST         = 16;
   public final static int SEARCH_RESULT_REFERENCE = 19;
   public final static int EXTENDED_REQUEST        = 23;
   public final static int EXTENDED_RESPONSE       = 24;

	/**
	 * Creates an LDAPMessage when sending a Protocol Operation.
	 */
	public LDAPMessage(Request op)
	{
		this(op, null);
	}

	/**
	 * Creates an LDAPMessage when sending a Protocol Operation along with
	 * some optional controls.
	 */
	public LDAPMessage(Request op, LDAPControl[] controls)
	{
		Controls asn1Ctrls = null;

		if(controls != null) {
			// Move LDAPControls into an RFC 2251 Controls object.
			asn1Ctrls = new Controls();
			for(int i=0; i<controls.length; i++) {
				asn1Ctrls.add(controls[i].getASN1Object());
			}
		}

		// create RFC 2251 LDAPMessage
		message = new com.novell.asn1.ldap.LDAPMessage(op, asn1Ctrls);
	}

	/**
	 * Creates an LDAPMessage when receiving an RFC 2251 LDAPMessage from a
	 * server.
	 */
	public LDAPMessage(com.novell.asn1.ldap.LDAPMessage message)
	{
		this.message = message;
	}

/*
	protected LDAPMessage(int messageID, LDAPControl[] controls, int type)
	{
		this.messageID = messageID;
		if(controls != null && controls.length > 0) {
			this.controls = new Vector(controls.length);
			for(int i=0; i<controls.length; i++) {
				this.controls.addElement(controls[i]);
			}
		}
		this.type = type;
	}
*/	

   /**
    * Returns any controls in the message.
    */
   public LDAPControl[] getControls() {
		LDAPControl[] controls = null;
		Controls asn1Ctrls = message.getControls();

		// convert from RFC 2251 Controls to LDAPControl[].
		if(asn1Ctrls != null) {
			controls = new LDAPControl[asn1Ctrls.size()];
			for(int i=0; i<asn1Ctrls.size(); i++) {
				controls[i] = new LDAPControl((Control)asn1Ctrls.get(i));
			}
		}

		return controls;
   }

   /**
    * Returns the message ID.
    */
   public int getMessageID() {
      return message.getMessageID();
   }

   /**
    * Returns the LDAP operation type of the message. The type is one of
    * the following:
    * 
    *   BIND_REQUEST            = 0;
    *   BIND_RESPONSE           = 1;
    *   UNBIND_REQUEST          = 2;
    *   SEARCH_REQUEST          = 3;
    *   SEARCH_RESPONSE         = 4;
    *   SEARCH_RESULT           = 5;
    *   MODIFY_REQUEST          = 6;
    *   MODIFY_RESPONSE         = 7;
    *   ADD_REQUEST             = 8;
    *   ADD_RESPONSE            = 9;
    *   DEL_REQUEST             = 10;
    *   DEL_RESPONSE            = 11;
    *   MODIFY_RDN_REQUEST      = 12;
    *   MODIFY_RDN_RESPONSE     = 13;
    *   COMPARE_REQUEST         = 14;
    *   COMPARE_RESPONSE        = 15;
    *   ABANDON_REQUEST         = 16;
    *   SEARCH_RESULT_REFERENCE = 19;
    *   EXTENDED_REQUEST        = 23;
    *   EXTENDED_RESPONSE       = 24;
    */
   public int getType()
	{
		return message.getProtocolOp().getIdentifier().getTag();
   }

	/**
	 * Returns the RFC 2251 LDAPMessage composed in this object.
	 */
	public com.novell.asn1.ldap.LDAPMessage getASN1Object()
	{
		return message;
	}

}


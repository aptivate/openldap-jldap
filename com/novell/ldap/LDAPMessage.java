/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMessage.java,v 1.12 2000/10/23 18:49:06 judy Exp $
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

import com.novell.ldap.protocol.*;

/**
 *  Represents the base class for LDAP request and response messages.
 *  Subclassed by response messages used in asynchronous operations.
 */
public class LDAPMessage {

  /**
   * A request or response message for an asynchronous LDAP operation.
   */
	protected com.novell.ldap.protocol.LDAPMessage message;

  /**
   * A bind request operation.
   */
   public final static int BIND_REQUEST            = 0;
   
  /**
   * A bind response operation.
   */
   public final static int BIND_RESPONSE           = 1;
   
  /**
   * An unbind request operation.
   */
   public final static int UNBIND_REQUEST          = 2;
   
  /**
   * A search request operation.
   */
   public final static int SEARCH_REQUEST          = 3;
   
  /**
   * A search response operation.
   */
   public final static int SEARCH_RESPONSE         = 4;
   
  /**
   * A search result message.
   */
   public final static int SEARCH_RESULT           = 5;
   
  /**
   * A modify request operation.
   */
   public final static int MODIFY_REQUEST          = 6;
   
  /**
   * A modify response operation.
   */
   public final static int MODIFY_RESPONSE         = 7;
   
  /**
   * An add request operation.
   */
   public final static int ADD_REQUEST             = 8;
   
  /**
   * An add response operation.
   */
   public final static int ADD_RESPONSE            = 9;
   
  /**
   * A delete request operation.
   */
   public final static int DEL_REQUEST             = 10;
   
  /**
   * A delete response operation.
   */
   public final static int DEL_RESPONSE            = 11;
   
  /**
   * A modify RDN request operation.
   */
   public final static int MODIFY_RDN_REQUEST      = 12;
   
  /**
   * A modify RDN response operation.
   */
   public final static int MODIFY_RDN_RESPONSE     = 13;
   
  /**
   * A compare result operation.
   */
   public final static int COMPARE_REQUEST         = 14;
   
  /**
   * A compare response operation.
   */
   public final static int COMPARE_RESPONSE        = 15;
   
  /**
   * An abandon request operation.
   */
   public final static int ABANDON_REQUEST         = 16;
   
   
  /**
   * A search result reference operation.
   */
   public final static int SEARCH_RESULT_REFERENCE = 19;
   
  /**
   * An extended request operation.
   */
   public final static int EXTENDED_REQUEST        = 23;
   
  /**
   * An extended response operation.
   */
   public final static int EXTENDED_RESPONSE       = 24;

	/**
	 * Creates an LDAPMessage when sending a protocol operation.
     *
     * @param op The operation type of message.
     *
     * @see #getType
	 */
	public LDAPMessage(Request op)
	{
		this(op, null);
	}

	/**
	 * Creates an LDAPMessage when sending a protocol operation and sends
	 * some optional controls with the message.
     *
     * @param op The operation type of message.
     *<br><br>
     * @param controls The controls to use with the operation.
     *
     * @see #getType
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
		message = new com.novell.ldap.protocol.LDAPMessage(op, asn1Ctrls);
	}

	/**
	 * Creates an LDAPMessage when the libraries receive a response from a command.
     *
     * @param message A response message.
	 */
	public LDAPMessage(com.novell.ldap.protocol.LDAPMessage message)
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
    * Returns the LDAP operation type of the message. 
    *
    * <p>The type is one of the following:</p>
    * <ul>
    *   <li>BIND_REQUEST            = 0;</li>
    *   <li>BIND_RESPONSE           = 1;</li>
    *   <li>UNBIND_REQUEST          = 2;</li>
    *   <li>SEARCH_REQUEST          = 3;</li>
    *   <li>SEARCH_RESPONSE         = 4;</li>
    *   <li>SEARCH_RESULT           = 5;</li>
    *   <li>MODIFY_REQUEST          = 6;</li>
    *   <li>MODIFY_RESPONSE         = 7;</li>
    *   <li>ADD_REQUEST             = 8;</li>
    *   <li>ADD_RESPONSE            = 9;</li>
    *   <li>DEL_REQUEST             = 10;</li>
    *   <li>DEL_RESPONSE            = 11;</li>
    *   <li>MODIFY_RDN_REQUEST      = 12;</li>
    *   <li>MODIFY_RDN_RESPONSE     = 13;</li>
    *   <li>COMPARE_REQUEST         = 14;</li>
    *   <li>COMPARE_RESPONSE        = 15;</li>
    *   <li>ABANDON_REQUEST         = 16;</li>
    *   <li>SEARCH_RESULT_REFERENCE = 19;</li>
    *   <li>EXTENDED_REQUEST        = 23;</li>
    *   <li>EXTENDED_RESPONSE       = 24;</li>
    * </ul>
    *
    *@return The operation type of the message.
    */
   public int getType()
	{
		return message.getProtocolOp().getIdentifier().getTag();
   }

	/**
	 * Returns the RFC 2251 LDAPMessage composed in this object.
	 */
	public com.novell.ldap.protocol.LDAPMessage getASN1Object()
	{
		return message;
	}

}


/* **************************************************************************
 * $Id$
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

import java.util.Vector;

/**
 * 4.3 public class LDAPMessage
 *
 *  Base class for LDAP request and response messages.
 */
public class LDAPMessage {

	protected int messageID;
	protected Vector controls;
	protected int type;

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

	protected LDAPMessage()
	{
	}

	protected LDAPMessage(int messageID, int type)
	{
		this(messageID, null, type);
	}

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

   /*
    * 4.3.1 getControls
    */

   /**
    * Returns any controls in the message.
    */
   public LDAPControl[] getControls() {
		LDAPControl[] ctrls = null;
		if(controls != null) {
			ctrls = new LDAPControl[controls.size()];
			for(int i=0; i<controls.size(); i++) {
				ctrls[i] = (LDAPControl)controls.elementAt(i);
			}
		}
		else {
			ctrls = null;
		}
      return ctrls;
   }

   /*
    * 4.3.2 getMessageID
    */

   /**
    * Returns the message ID.
    */
   public int getMessageID() {
      return messageID;
   }

   /**
    * Sets the message ID.
    */
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

   /*
    * 4.3.3 getType
    */

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
   public int getType() {
      return type;
   }

	/**
	 * Sets the type.
	 */
	public void setType(int type) {
		this.type = type;
	}

   /**
	 * LDAPv3 Controls
	 */

   public static final int LDAP_CONTROLS = 0xa0;      // ctx + constructed    (LDAPv3)

}

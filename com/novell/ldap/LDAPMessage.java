/**
 * 4.3 public class LDAPMessage
 *
 *  Base class for LDAP request and response messages.
 */
package com.novell.ldap;

public class LDAPMessage {

	public LDAPControl[] controls;
	public int messageID;
	public int type;

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

	LDAPMessage() {
	}

	LDAPMessage(int messageID, int type, LDAPControl[] controls) {
		this.messageID = messageID;
		this.type = type;
		this.controls = controls;
	}

   /*
    * 4.3.1 getControls
    */

   /**
    * Returns any controls in the message.
    */
   public LDAPControl[] getControls() {
      return controls;
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

}

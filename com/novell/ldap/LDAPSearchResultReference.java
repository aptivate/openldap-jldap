/**
 * 4.8 public class LDAPSearchResultReference extends LDAPMessage
 *
 *  An LDAPSearchResultReference object encapsulates a continuation
 *  reference from a search operation.
 */
package com.novell.ldap;

public class LDAPSearchResultReference extends LDAPMessage {

	private String[] urls; // referrals

	public LDAPSearchResultReference(int messageID,
		                       String[] urls,
		                       LDAPControl[] ctrls) {
		super(messageID, LDAPMessage.SEARCH_RESULT_REFERENCE, ctrls);
		this.urls = urls;
	}

   /*
    * 4.8.1 getUrls
    */

   /**
    * Returns any URLs in the object.
    */
   public String[] getUrls() {
		return urls;
   }

}

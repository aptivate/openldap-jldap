/**
 * 4.7 public class LDAPSearchResult extends LDAPMessage
 *
 *  An LDAPSearchResult object encapsulates a single search result.
 */
package com.novell.ldap;

public class LDAPSearchResult extends LDAPMessage {

	private LDAPEntry entry;

	public LDAPSearchResult(int messageID,
		                     LDAPEntry entry,
		                     LDAPControl[] controls) {
		super(messageID, LDAPMessage.SEARCH_RESPONSE, controls);
		this.entry = entry;
	}

   /*
    * 4.7.1 getEntry
    */

   /**
    * Returns the entry of a server search response.
    */
   public LDAPEntry getEntry() {
		return entry;
   }

} /* LDAPSearchResult */

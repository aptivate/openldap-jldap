/**
 * 4.6 public class LDAPSearchListener
 *
 *  An LDAPSearchListener manages search results and references returned
 *  on one or more search requests.
 */
package com.novell.ldap;

import com.novell.ldap.client.*;
import java.util.*;
import java.io.*;

public class LDAPSearchListener extends LDAPListener {
 
	/**
	 * Constructor
	 */
	public LDAPSearchListener(LDAPClient ldapClient) {
		this.ldapClient = ldapClient;
		this.conn = ldapClient.getConn();
		this.isLdapv3 = ldapClient.isLdapv3();
		this.queue = new LDAPMessageQueue();
		this.exceptions = new Vector(5);
		conn.addLDAPListener(this);
	}

   /*
    * 4.6.2 getResponse
    */

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response. The response may be a search result, a search
    * reference, a search response, or null (if there are no more
    * outstanding requests). LDAPException is thrown on network errors.
	 *
	 * The only time this method should return a null is if there is no
	 * response in the message queue and there are no message ids pending.
    */
   public LDAPMessage getResponse()
		throws LDAPException {

		LDAPMessage message = queue.getLDAPMessage(); // blocks

		if(message != null && message.getType() == LDAPClient.LDAP_REP_RESULT) {
			queue.removeMessageID(message.getMessageID());
		}

		// network error exceptions... (LDAP_TIMEOUT for example)
		if(!exceptions.isEmpty()) {
			LDAPException e = (LDAPException)exceptions.firstElement();
			exceptions.removeElementAt(0);
			throw e;
		}

		return message;
   }

} /* LDAPSearchListener */

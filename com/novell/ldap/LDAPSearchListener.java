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

import com.novell.ldap.client.*;
import java.util.Vector;

/**
 * 4.6 public class LDAPSearchListener
 *
 *  An LDAPSearchListener manages search results and references returned
 *  on one or more search requests.
 */
public class LDAPSearchListener extends LDAPListener {
 
	/**
	 * Constructor
	 */
	public LDAPSearchListener(Connection conn)
	{
		this.conn = conn;
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
		throws LDAPException
	{
		LDAPMessage message = queue.getLDAPMessage(); // blocks

		if(message != null &&
			message.getType() == LDAPMessage.SEARCH_RESULT) {
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

}

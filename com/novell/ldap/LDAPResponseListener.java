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
 * 4.5 public class LDAPResponseListener
 *
 *  Represents the message queue associated with a particular LDAP
 *  operation or operations.
 */
public class LDAPResponseListener extends LDAPListener {

	/**
	 * Constructor
	 */
	public LDAPResponseListener(Connection conn)
	{
		this.conn = conn;
		this.queue = new LDAPMessageQueue();
		this.exceptions = new Vector(5);
		conn.addLDAPListener(this);
	}

   /*
    * 4.5.2 getResponse
    */

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response. It is the responsibility of the client to
	 * process the responses returned from a listener.
    */
   public LDAPResponse getResponse()
		throws LDAPException
	{
		LDAPMessage message = queue.getLDAPMessage();
		queue.removeMessageID(message.getMessageID());
		return (LDAPResponse)message;
   }

}

/**
 * Not in the draft.
 *
 *  Represents the message queue associated with a particular LDAP
 *  operation or operations.
 */
package com.novell.ldap;

import java.io.*;
import java.util.Vector;
import com.novell.ldap.client.*;

public abstract class LDAPListener implements TimerListener {

	protected Connection conn;
	protected LDAPClient ldapClient;
	protected boolean isLdapv3;
	protected LDAPMessageQueue queue;
	protected Vector exceptions;

   /*
    * 4.5.1 getMessageIDs
    */

   /**
    * Returns the message IDs for all outstanding requests. The last ID in
    * the array is the messageID of the latest submitted request.
    */
   public int[] getMessageIDs() {
		return queue.getMessageIDs();
   }

	/**
	 * Returns a boolean value indicating whether or not the specified
	 * message id exists in the message id list.
	 */
	protected boolean messageIDExists(int msgId) {
		return queue.messageIDExists(msgId);
	}

   /*
    * 4.5.3 isResponseReceived
    */

   /**
    * Reports true if a response has been received from the server.
    */
   public boolean isResponseReceived() {
		return queue.isResponseReceived();
   }

	public void removeResponses(int msgId) {
		queue.removeResponses(msgId);
	}

   /*
    * 4.5.4 merge
    */

   /**
    * Merges two response listeners. Moves/appends the content from another
    * listener to this one.
    */
   public void merge(LDAPResponseListener listener2) {
   }

	/**
	 * @internal
	 */
	public void writeMessage(BerEncoder ber, int msgID, int msLimit)
	throws IOException {
		Timer timer = null;

		if(msLimit > 0) {
			timer = new Timer(msgID, msLimit, this);
			timer.start();
		}

		queue.addMessageID(msgID, timer);
		conn.writeMessage(ber);
	}

	/**
	 * @internal
	 */
	public void timedOut(int msgId) {
		exceptions.addElement(
			new LDAPException("Client timeout", LDAPException.LDAP_TIMEOUT));
		queue.removeTimer(msgId); // timer thread does not need to be stopped.
		ldapClient.abandon(msgId, (LDAPControl[])null); // will remove id and notify
	}

	/**
	 * @internal
	 * called by LDAPSearchResults
	 * Will abandon all message IDs for this LDAPListener.
	 */
	public void abandonAll() {
		int[] ids = queue.getMessageIDs();
		for(int i=0; i<ids.length; i++) {
			ldapClient.abandon(ids[i], (LDAPControl[])null);
		}
	}
	
	/**
	 *
	 */
	public void addLDAPMessage(LDAPMessage message) {
		queue.addLDAPMessage(message);
	}

	public LDAPMessage getLDAPMessage() {
		return queue.getLDAPMessage();
	}

	protected void removeMessageID(int msgId) { // does this need to be synchronized !!!!
		queue.removeMessageID(msgId);
	}

	/**
	 * In the event of an abandon, we want to remove the message id and notify
	 * in case a thread is waiting for a response.
	 */ 
	public void removeMessageIDAndNotify(int id) {
		queue.removeMessageIDAndNotify(id);
	}

	public void finalize() {
		conn.removeLDAPListener(this);
	}

} /* LDAPListener */

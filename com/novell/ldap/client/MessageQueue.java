/* **************************************************************************
 * $Id: LDAPMessageQueue.java,v 1.3 2000/08/10 17:53:01 smerrill Exp $
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
 
package com.novell.ldap.client;

import java.util.Vector;
import com.novell.asn1.ldap.*;

public class LDAPMessageQueue {

	private Vector responses  = new Vector(20); // vector of LDAPMessages
	private Vector messageIDs = new Vector(1);  // preserve fifo order

   /*
    * getMessageIDs
    */

   /**
    * Returns the message IDs for all outstanding requests. The last ID in
    * the array is the messageID of the latest submitted request.
    */
   public int[] getMessageIDs() {
		int[] ids = new int[messageIDs.size()];
		for(int i=0; i<messageIDs.size(); i++) {
			TimedMessage tm = (TimedMessage)messageIDs.elementAt(i);
			ids[i] = tm.getMessageID();
		}
		return ids;
   }

	/**
	 * Returns a boolean value indicating whether or not the specified
	 * message id exists in the message id list.
	 */
	public boolean messageIDExists(int msgId) {
		for(int i=0; i<messageIDs.size(); i++) {
			TimedMessage tm = (TimedMessage)messageIDs.elementAt(i);
			if(tm.getMessageID() == msgId) {
				return true;
			}
		}
		return false;
	}

   /**
    * Reports true if a response has been received from the server.
    */
   public boolean isResponseReceived() {
		synchronized(responses) { // do I need to do this!!!!
			return !responses.isEmpty();
		}
   }

	/**
	 * If a particular message id has been abandoned, we need to go through
	 * the list of reponses already received and pull out any repsonse with
	 * a matching id.
	 */
	public void removeResponses(int msgId) {
		for(int i=responses.size()-1; i>=0; i--) {
			LDAPMessage message = (LDAPMessage)responses.elementAt(i);
			if(message.getMessageID().getInt() == msgId) {
				responses.removeElementAt(i);
			}
		}
	}

	/**
	 *
	 */
	public synchronized void addLDAPMessage(LDAPMessage message) {
		responses.addElement(message);
		notify(); //notifyAll() ???
	}

	public synchronized LDAPMessage getLDAPMessage() {
		LDAPMessage message = null;

		try {
			// Abandon can remove message IDs at any time, but we still need
			// to wait if there are other messages pending...
			while(responses.isEmpty() && !messageIDs.isEmpty()) {
				wait();
			}

			if(!responses.isEmpty()) {
				message = (LDAPMessage)responses.firstElement();
				responses.removeElementAt(0);
			}
		}
		catch(InterruptedException e) {
		}

		return message;
	}

	/**
	 *
	 */
	public synchronized void addMessageID(int id, Timer timer) { // does this need to be synchronized !!!!
		messageIDs.addElement(new TimedMessage(id, timer));
	}

	/**
	 *
	 */
	public synchronized void removeMessageID(int id) { // does this need to be synchronized !!!!
		for(int i=0; i<messageIDs.size(); i++) {
			TimedMessage tm = (TimedMessage)messageIDs.elementAt(i);
			if(tm.getMessageID() == id) {
				Timer t = tm.getTimer();
				if(t != null) {
					t.requestStop(); // stop the timer thread for this id
				}
				messageIDs.removeElementAt(i);
				break;
			}
		}
	}

	/**
	 *
	 */
	public synchronized void removeTimer(int id) { // does this need to be synchronized!!!
		for(int i=0; i<messageIDs.size(); i++) {
			TimedMessage tm = (TimedMessage)messageIDs.elementAt(i);
			if(tm.getMessageID() == id) {
				tm.setTimer(null);
			}
			break;
		}
	}

	/**
	 * In the event of an abandon, we want to remove the message id and notify
	 * in case a thread is waiting for a response.
	 */ 
	public synchronized void removeMessageIDAndNotify(int id) {
		removeMessageID(id);
		notify();
	}

}


class TimedMessage {
	private int messageID;
	private Timer timer;

	TimedMessage(int messageID, Timer timer) {
		this.messageID = messageID;
		this.timer = timer;
	}

	int getMessageID() {
		return messageID;
	}

	Timer getTimer() {
		return timer;
	}

	void setTimer(Timer t) {
		this.timer = t;
	}

}

package com.novell.ldap.client;

import java.util.Vector;
import com.novell.ldap.*;

public class LDAPMessageQueue {

	private Vector responses  = new Vector(20); // vector of LDAPMessages
	private Vector messageIDs = new Vector(1);  // preserve fifo order

   /*
    * 4.5.1 getMessageIDs
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
			if(message.getMessageID() == msgId) {
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

/* **************************************************************************
 * $Id: MessageQueue.java,v 1.11 2000/11/10 20:24:27 vtag Exp $
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
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.LDAPException;

/* package */ class MessageQueue {

	private Vector responses  = new Vector(20); // vector of RfcLDAPMessages
	private Vector messageIDs = new Vector(3,3);  // preserve fifo order

   /**
    * Returns the message IDs for all outstanding requests. The last ID in
    * the array is the messageID of the latest submitted request.
    */
   /* package */ int[] getMessageIDs()
   {
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
	/* package */ boolean messageIDExists(int msgId)
	{
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
    * and not yet retrieved with getLDAPMessage.
    */
   /* package */ boolean isResponseReceived()
   {
		synchronized(responses) { // do I need to do this!!!!
			return !responses.isEmpty();
		}
   }

	/**
	 * Goes through the list of reponses already received and pulls out
     * any repsonse with an matching the specified id.  Called when a
	 * particular message id has been abandoned.
	 */
	/* package */ void removeResponses(int msgId)
	{
		for(int i=responses.size()-1; i>=0; i--) {
			RfcLDAPMessage message = (RfcLDAPMessage)responses.elementAt(i);
			if(message.getMessageID() == msgId) {
				responses.removeElementAt(i);
			}
		}
	}

	/**
	 * Add a message to the response queue
	 */
	/* package */ synchronized void addLDAPMessage(RfcLDAPMessage message)
	{
		responses.addElement(message);
		notify(); //notifyAll() ???
	}

	/**
	 * Add an exception to the response queue
	 */
	/* package */ synchronized void addLDAPException(LDAPException ex)
	{
		responses.addElement(ex);
		notify(); //notifyAll() ???
	}

    /**
     * Retrieves the next message from the response queue.  IF the
     * queue is empty, it waits for one to be received from the server.
     */
	/* package */ synchronized RfcLDAPMessage getLDAPMessage()
            throws LDAPException
	{
		Object message = null;

		try {
			// Abandon can remove message IDs at any time, but we still need
			// to wait if there are other messages pending...
			while(responses.isEmpty() && !messageIDs.isEmpty()) {
				wait();
			}

			if(!responses.isEmpty()) {
                message = (RfcLDAPMessage)responses.firstElement();
                if( message instanceof LDAPException ) {
                    // Let caller figure out what to do with this exception
                    throw (LDAPException)message;
                }
				responses.removeElementAt(0);
			}
		}
		catch(InterruptedException e) {
		}

		return (RfcLDAPMessage)message;
	}

	/**
	 * Creates a timed message class associating a timer thread class
     * and a messageID and adds it to the message id list.
	 */
	/* package */ synchronized void addMessageID(int id, Timer timer) { // does this need to be synchronized !!!!
		messageIDs.addElement(new TimedMessage(id, timer));
	}

	/**
	 * Removes a timed message class from the messageID list.
     * The timer thread is stopped if it exists, and the entry is removed
     * from the list.
	 */
	/* package */ synchronized void removeMessageID(int id) // does this need to be synchronized !!!!
    {
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
	 * The timer thread associated with a message is removed.  This is called
     * when the message has timed out.  The timer thread has already terminated.
	 */
	/* package */ synchronized void removeTimer(int id) // does this need to be synchronized!!!
    {
		for(int i=0; i<messageIDs.size(); i++) {
			TimedMessage tm = (TimedMessage)messageIDs.elementAt(i);
			if(tm.getMessageID() == id) {
				tm.clearTimer();
			}
			break;
		}
	}

	/**
	 * In the event of an abandon, we want to remove the message id and notify
	 * in case a thread is waiting for a response.
	 */ 
	/* package */ synchronized void removeMessageIDAndNotify(int id)
	{
		removeMessageID(id);
		notify();
	}
}

/**
 * This class associates a message with a timer thread class.
 */
/* package */ class TimedMessage
{
	private int messageID;
	private Timer timer;

	TimedMessage(int messageID, Timer timer) {
		this.messageID = messageID;
		this.timer = timer;
	}

    /**
     * Return the messageid associated with this timed message.
     *
     * @return the messageID
     */
	/* package */ int getMessageID() {
		return messageID;
	}

    /**
     * Return the timer class associated with this timed message.
     *
     * @return the timer class
     */
	/* package */ Timer getTimer() {
		return timer;
	}

    /**
     *  Removes the timer associated with this message
     */
	/* package */ void clearTimer() {
		timer = null;
	}
}

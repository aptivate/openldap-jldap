/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPListener.java,v 1.8 2000/08/28 22:18:56 vtag Exp $
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

import java.io.*;
import java.util.Vector;

import com.novell.asn1.ldap.*;
import com.novell.ldap.client.*;
//import com.novell.ldap.client.protocol.AbandonRequest;

/*
 * Not in the draft.
 */
 
/**
 *
 * Represents the message queue associated with a particular LDAP
 * operation or operations.
 */
public abstract class LDAPListener implements TimerListener {
    
   /**
    * Manages the listener's connection to the server.
    */
	protected Connection conn;

   /**
    * Specifies the associated message queue for the listener.
    */
	protected LDAPMessageQueue queue;

   /**
    * Specifies the associated vector for the listener's exceptions.
    */
	protected Vector exceptions;

   /**
    * Returns the message IDs for all outstanding requests. 
    *
    * <p>The last ID in the array is the messageID of the 
    * latest submitted request.</p>
    *
    * @return The message IDs for all outstanding requests.
    */
   public int[] getMessageIDs()
	{
		return queue.getMessageIDs();
   }

	/**
	 * Returns a boolean value indicating whether or not the specified
	 * message ID exists in the message ID list.
     *
     * @param msgId The message ID to check.
     *
     * @return True if the message ID exists in the list; false if the 
     *         message ID is not in the list.
     *         
	 */
	protected boolean messageIDExists(int msgId)
	{
		return queue.messageIDExists(msgId);
	}

   /**
    * Reports whether a response has been received from the server.
    *
    * @return True if a response has been received from the server. 
    */
   public boolean isResponseReceived()
	{
		return queue.isResponseReceived();
   }

	/**
	 * Removes the specified response from the queue.
     *
     * @param msgId  The message to remove.
	 */
	public void removeResponses(int msgId)
	{
		queue.removeResponses(msgId);
	}

   /**
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @param listener2 The listener that receives the contents from the
    *                  other listener.
    */
   public void merge(LDAPResponseListener listener2)
	{
   }

	/**
	 * Writes the message and starts the timer (if any) for the 
	 * client-side timeout.
     *
     * @param msg The message to write.
     * <br><br>
     * @param msLimit  The client-side timeout, or null if the client 
     *                 has no time limit.
	 */
	public void writeMessage(LDAPMessage msg, int msLimit)
		throws IOException
	{
		int messageID = msg.getMessageID();
		Timer timer = null;

		if(msLimit > 0) {
			timer = new Timer(messageID, msLimit, this);
			timer.start();
		}

		queue.addMessageID(messageID, timer);
		conn.writeMessage(msg);
	}

	/**
	 * @exclude
	 * Called by Timer when the timer for a given messageID has expired.
	 */
	public void timedOut(int msgId)
	{
		exceptions.addElement(
			new LDAPException("Client timeout", LDAPException.LDAP_TIMEOUT));
		queue.removeTimer(msgId); // timer thread does not need to be stopped.
		try {
			conn.writeMessage(new LDAPMessage(new AbandonRequest(msgId)));
//				new AbandonRequest(conn.getMessageID(), msgId,
//										 (LDAPControl[])null, true
//										).getLber());
		}
		catch(IOException ioe) {
			// communication error
		}
		removeResponses(msgId);
		removeMessageIDAndNotify(msgId);
	}

	/**
	 * @exclude
	 * called by LDAPSearchResults
	 * Will abandon all message IDs for this LDAPListener.
	 */
	public void abandonAll()
	{
		int[] ids = queue.getMessageIDs();
		try {
			for(int i=0; i<ids.length; i++) {
				conn.writeMessage(new LDAPMessage(new AbandonRequest(ids[i])));
//				conn.writeMessage(
//					new AbandonRequest(conn.getMessageID(), ids[i],
//											 (LDAPControl[])null, true
//											).getLber());
			}
		}
		catch(IOException ioe) {
			// communication error
		}
	}
	
	/**
	 * Adds an LDAPMessage to the listener's queue.
	 */
	public void addLDAPMessage(com.novell.asn1.ldap.LDAPMessage message)
	{
		queue.addLDAPMessage(message);
	}

   /**
    * Returns the next message in the queue.
    *
    * @return The next message in the queue.
    */
    public com.novell.asn1.ldap.LDAPMessage getLDAPMessage()
	{
		return queue.getLDAPMessage();
	}

   /**
    * Removes the specified message ID.
    *
    * @param msgId The message to remove.
    */
	protected void removeMessageID(int msgId) { // does this need to be synchronized !!!!
		queue.removeMessageID(msgId);
	}

	/**
	 * In the event of an abandon, removes the message ID and notifies
	 * in case a thread is waiting for a response.
     *
     * @param id The message ID to remove from the list.
	 */ 
	public void removeMessageIDAndNotify(int id)
	{
		queue.removeMessageIDAndNotify(id);
	}
    
   /**
    * Cleans up the listener's resources when the object goes out of scope.
    */
	public void finalize()
	{
		conn.removeLDAPListener(this);
	}

}


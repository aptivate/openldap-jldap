/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/ClientListener.java,v 1.3 2000/11/08 22:41:33 vtag Exp $
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

import java.io.*;
import java.util.Vector;

import com.novell.ldap.protocol.*;
import com.novell.ldap.*;
//import com.novell.ldap.client.protocol.AbandonRequest;

/*
 * Not in the draft.
 */
 
/**
 *
 * Represents the message queue associated with a particular LDAP
 * operation or operations.
 */
public class ClientListener implements TimerListener {
    
   /**
    * Manages the listener's connection to the server.
    */
    private Connection conn;

   /**
    * Specifies the associated message queue for the listener.
    */
    private MessageQueue queue;

   /**
    * Specifies the associated vector for the listener's exceptions.
    */
    private Vector exceptions;

    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param conn The connection for the listener.
     */
    public ClientListener(Connection conn)
    {
        this.conn = conn;
        this.queue = new MessageQueue();
        this.exceptions = new Vector(5);
        conn.addClientListener(this);
    }
    
    /**
     * Returns any network exceptions, if any
     *
     *  @return returns the Exception class
     */
    public LDAPException getException()
    {
      if(exceptions.isEmpty())
         return null;
      LDAPException e = (LDAPException)exceptions.firstElement();
      exceptions.removeElementAt(0);
      return e;
    }
    
   /**
    * Returns the message IDs for all outstanding requests. 
    *
    * <p>The last ID in the array is the messageID of the 
    * last submitted request.</p>
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
    public boolean messageIDExists(int msgId)
    {
        return queue.messageIDExists(msgId);
    }

   /**
    * Reports whether a response has been received from the server.
    *
    * @return True if a response has been received from the server; false if
    *         a response has not been received. 
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
        throw new RuntimeException("merge() not implemented");
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
     * Called by Timer when the timer for a given messageID has expired.
     */
    public void timedOut(int msgId)
    {
        exceptions.addElement(
            new LDAPException("Client timeout", LDAPException.LDAP_TIMEOUT));
        queue.removeTimer(msgId); // timer thread does not need to be stopped.
        try {
            conn.writeMessage(new LDAPMessage(new AbandonRequest(msgId)));
//                new AbandonRequest(conn.getMessageID(), msgId,
//                                         (LDAPControl[])null, true
//                                        ).getLber());
        }
        catch(IOException ioe) {
            // communication error
        }
        removeResponses(msgId);
        removeMessageIDAndNotify(msgId);
    }

    /**
     * Called by LDAPSearchResults
     * This abandon method abandons all message IDs for this ClientListener.
     */
    public void abandonAll()
    {
        int[] ids = queue.getMessageIDs();
        try {
            for(int i=0; i<ids.length; i++) {
                conn.writeMessage(new LDAPMessage(new AbandonRequest(ids[i])));
//                conn.writeMessage(
//                    new AbandonRequest(conn.getMessageID(), ids[i],
//                                             (LDAPControl[])null, true
//                                            ).getLber());
            }
        }
        catch(IOException ioe) {
            // communication error
        }
    }
    
    /**
     * Adds an RfcLDAPMessage to the listener's queue.
     */
    public void addLDAPMessage(RfcLDAPMessage message)
    {
        queue.addLDAPMessage(message);
    }
    
    /**
     * Adds an LDAPException to the listener's queue.
     */
    public void addLDAPException(com.novell.ldap.LDAPException ex)
    {
        queue.addLDAPException(ex);
    }

   /**
    * Returns the next message in the queue.
    *
    * @return The next message in the queue.
    */
    public RfcLDAPMessage getLDAPMessage()
            throws com.novell.ldap.LDAPException
    {
        return queue.getLDAPMessage();
    }

   /**
    * Removes the specified message ID.
    *
    * @param msgId The message to remove.
    */
    public void removeMessageID(int msgId) { // does this need to be synchronized !!!!
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
        conn.removeClientListener(this);
    }
}

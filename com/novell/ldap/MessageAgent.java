/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import com.novell.ldap.client.*;

/* package */
class MessageAgent
{
    private MessageVector messages = new MessageVector(5,5);
    private int indexLastRead =0;
    private static Object nameLock = new Object(); // protect agentNum
    private static int agentNum = 0; // Debug, agent number
    private String name;             // String name for debug

    /* package */
    MessageAgent()
    {
        // Get a unique agent id for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "MessageAgent(" + ++agentNum + "): ";
            }
            Debug.trace( Debug.messages, name + "Created");
        }
    }

    /**
     * empty and return all messages owned by this agent
     *
     * 
     */
    /* package */
    Object[] getMessageArray()
    {
        return messages.getObjectArray();
    }

    /**
     * merges two message agents
     *
     * @param fromAgent the agent to be merged into this one
     */
    /* package */
    final void merge( MessageAgent fromAgent)
    {
        Object[] msgs = fromAgent.getMessageArray();
        for(int i = 0; i < msgs.length; i++) {
            messages.addElement( msgs[i]);
            ((Message)(msgs[i])).setAgent( this);
            if( Debug.LDAP_DEBUG) {
                Message info = (Message)msgs[i];
                Debug.trace( Debug.messages, name +
                    "Merging Message(" + info.getMessageID() + "), total " +
                    messages.size());
            }
        }
        synchronized(messages) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "Messages in queue");
                debugDisplayMessages();
            }
            if( msgs.length > 1) {
                messages.notifyAll();  // wake all threads waiting for messages
            } else
            if( msgs.length == 1) {
                messages.notify();    // only wake one thread
            }
        }
        return;
    }


    /**
     * Wakes up any threads waiting for messages in the message agent
     *
     */
     /* package */
     final void sleepersAwake(boolean all)
     {
        synchronized(messages) {
            if( all)
                messages.notifyAll();
            else
                messages.notify();
        }
        return;
     }

    /**
     * Returns true if any responses are queued for any of the agent's messages
     *
     * return false if no responses are queued, otherwise true
     */
    /* package */
    final boolean isResponseReceived()
    {
        int size = messages.size();
        int next = indexLastRead + 1;
        Message info;
        for( int i = 0; i < size; i++) {
           if( next == size ) {
               next = 0;
           }
           info = (Message)messages.elementAt(next);
           if( info.hasReplies() ) {
              return true;
           }
        }
        return false;
    }

    /**
     * Returns true if any responses are queued for the specified msgId
     *
     * return false if no responses are queued, otherwise true
     */
    /* package */
    final boolean isResponseReceived( int msgId)
    {
        try {
            Message info = messages.findMessageById( msgId);
            return  info.hasReplies();
        } catch( NoSuchFieldException ex ) {
            return false;
        }
    }

    /**
     * Abandon the request associated with MsgId
     *
     * @param msgId the message id to abandon
     *<br><br>
     * @param cons constraints associated with this request
     */
    /* package */
    final void abandon(int msgId, LDAPConstraints cons) //, boolean informUser)
    {
        Message info = null;
        try {
            // Send abandon request and remove from connection list
            info = messages.findMessageById( msgId);
            messages.removeElement( info);  // This message is now dead
            info.abandon( cons, null);

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "abandon: Removed abandoned Message(" +
                    info.getMessageID() + ")" + " Messages in queue");
                debugDisplayMessages();
            }
            return;
        } catch( NoSuchFieldException ex ) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Abandon of Message(" + msgId + ") failed");
            }
        }
        return;
    }

    /**
     * Abandon all requests on this MessageAgent
     */
    /* package */
    final void abandonAll()
    {
        int size = messages.size();
        Message info;

        for( int i = 0; i < size; i++ ) {
            info = (Message)messages.elementAt(i);
            // Message complete and no more replies, remove from id list
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "abandonAll: Removing abandoned Message(" + info.getMessageID() + ")");
            }
            messages.removeElement( info);
            info.abandon( null, null);
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "Messages in queue");
            debugDisplayMessages();
        }
        return;
    }

    /**
     * Get a list of message ids controlled by this agent
     *
     * @return an array of integers representing the message ids
     */
    /* package */
    final int[] getMessageIDs()
    {
        int size = messages.size();
        int[] ids = new int[size];
        Message info;

        for( int i = 0; i < size; i++ ) {
            info = (Message)messages.elementAt(i);
            ids[i] = info.getMessageID();
        }
        return ids;
    }

    /**
     * Indicates whether a specific operation is complete
     *
     * @return true if a specific operation is complete
     */
    /* package */
    final boolean isComplete(int msgid)
    {
        try {
            Message info = messages.findMessageById( msgid);
            if( ! info.isComplete()) {
                return false;
            }
        } catch( NoSuchFieldException ex) {
            ;   // return true, if no message, it must be complete
        }
        return true;
    }

    /**
     * Returns the Message object for a given messageID
     *
     * @param msgid the message ID.
     */
    /* package */
    final Message getMessage(int msgid)
            throws NoSuchFieldException
    {
        return messages.findMessageById( msgid);
    }

    /**
     * Send a request to the server.  A Message class is created
     * for the specified request which causes the message to be sent.
     * The request is added to the list of messages being managed by
     * this agent.
     *
     * @param conn the connection that identifies the server.
     *<br><br>
     * @param msg the LDAPMessage to send
     *<br><br>
     * @param timeOut the interval to wait for the message to complete or
     * <code>null</code> if infinite.
     * @param queue the LDAPMessageQueue associated with this request.
     */
    /* package */
    final void sendMessage(
                            Connection       conn,
                            LDAPMessage      msg,
                            int              timeOut,
                            LDAPMessageQueue queue,
                            BindProperties   bindProps)
            throws LDAPException
    {
        // creating a messageInfo causes the message to be sent
        // and a timer to be started if needed.
        Message message = new Message( msg, timeOut, conn,
                                    this, queue, bindProps);
        messages.addElement( message);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
              "sendMessage: Added new Message(" + message.getMessageID() + ")");
            debugDisplayMessages();
        }
        message.sendMessage(); // Now send message to server
        return;
    }

    /**
     * Returns a response queued, or waits if none queued
     *
     */
    /* package */
    final Object getLDAPMessage( Integer msgId)
    {
        Object rfcMsg;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "getLDAPMessage(" + msgId + "), " +
                messages.size() + " messages active");
        }
        // If no messages for this agent, just return null
        if( messages.size() == 0) {
            return null;
        }
        if( msgId != null ) {
            // Request messages for a specific ID
            try {
                // Get message for this ID
                Message info = messages.findMessageById( msgId.intValue());
                rfcMsg = info.waitForReply(); // blocks for a response
                if( ! info.acceptsReplies() && ! info.hasReplies()) {
                    // Message complete and no more replies, remove from id list
                    messages.removeElement( info);
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "getLDAPMessage: By ID Return Last Message(" +
                            info.getMessageID() + ")");
                        debugDisplayMessages();
                    }
                    info.abandon(null, null);      // Get rid of resources
                } else {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "getLDAPMessage: By ID Return Message(" +
                            info.getMessageID() + ")");
                        debugDisplayMessages();

                    }
                }
                return rfcMsg;
            } catch( NoSuchFieldException ex ) { // no such message id
                return null;
            }
        } else {
            // A msgId was NOT specified, any message will do
            synchronized( messages ) {
                while( true) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "getLDAPMessage: Look for any reply, " +
                            messages.size() + " messages active");
                    }
                    int next = indexLastRead + 1;
                    Message info;
                    for( int i = 0; i < messages.size(); i++) {
                       if( next >= messages.size() ) {
                           next = 0;
                       }
                       info = (Message)messages.elementAt(next);
                       indexLastRead = next++;
                       rfcMsg = info.getReply();
                       // Check this request is complete
                       if( ! info.acceptsReplies() && ! info.hasReplies()) {
                          // Message complete & no more replies, remove from id list
                          if( Debug.LDAP_DEBUG) {
                             Debug.trace( Debug.messages, name +
                                 "getLDAPMessage: cleanup Message(" +
                                 info.getMessageID() + ")");
                          }
                          messages.removeElement( info); // remove from list
                          info.abandon(null, null); // Get rid of resources
                          // Start loop at next message that is now moved
                          // to the current position in the Vector.
                          i -= 1;
                       }
                       if( rfcMsg != null) {
                          // We got a reply
                          if( Debug.LDAP_DEBUG) {
                             Debug.trace( Debug.messages, name +
                                 "getLDAPMessage: Return response to Message(" +
                                 info.getMessageID() + ")");
                             debugDisplayMessages();
                          }
                          return rfcMsg;
                       } else {
                          // We found no reply here
                          if( Debug.LDAP_DEBUG) {
                             Debug.trace( Debug.messages, name +
                             "getLDAPMessage: no messages queued for Message(" +
                             info.getMessageID() + ")");
                          }
                       }
                    } // end for loop */

                    // Messages can be removed in this loop, we we must
                    // check if any messages left for this agent
                    if( messages.size() == 0) {
                        return null;
                    }

                    // No data, wait for something to come in.
                    try {
                        if( Debug.LDAP_DEBUG) {
                           Debug.trace( Debug.messages, name +
                           "getLDAPMessage: waiting for incoming messages");
                        }
                        messages.wait();
                        if( Debug.LDAP_DEBUG) {
                           Debug.trace( Debug.messages, name +
                           "getLDAPMessage: wake up from wait");
                        }
                    } catch( InterruptedException ex) {
                        if( Debug.LDAP_DEBUG) {
                           Debug.trace( Debug.messages, name +
                           "getLDAPMessage: interrupted up from wait");
                        }
                    }
                } /* end while */
            } /* end synchronized */
        }
    }

    /**
     * Get the maessage agent number for debugging
     *
     * @return the agent number
     */
    /*packge*/
    String getAgentName()
    {
        return name;
    }

    /**
     * Get a count of all messages queued
     */
    /* package */
    int getCount()
    {
        int count = 0;
        Object[] msgs = messages.toArray();
        for(int i = 0; i < msgs.length; i++) {
            Message m = (Message)msgs[i];
            count += m.getCount();
        }
        return count;
    }
    
    /**
     * Debug code to print messages in message vector
     */
    private void debugDisplayMessages()
    {
        if( Debug.LDAP_DEBUG) {
            Object[] dbgmsgs = messages.toArray();
            Debug.trace( Debug.messages, name + "Queue Status");
            if( dbgmsgs.length == 0) {
                Debug.trace( Debug.messages, name + "\t" + "No messages queued");
            }
            for(int i = 0; i < dbgmsgs.length; i++) {
                Message m = (Message)dbgmsgs[i];
                Debug.trace( Debug.messages, name +
                    "\t" + i + ".: Message(" + m.getMessageID() + ")");
            }
        }
        return;
    }
}

/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/client/MessageAgent.java,v 1.3 2000/12/06 19:30:07 vtag Exp $
*
* Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
* 
* THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
* TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
* TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
* AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
* IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
* PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
* THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
***************************************************************************/
 
package com.novell.ldap.client;

import com.novell.ldap.client.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.*;
import java.io.*;


public class MessageAgent
{
    private MessageVector messages = new MessageVector(5,5);
    private int indexLastRead =0;
    private static Object nameLock = new Object(); // protect agentNum
    private static int agentNum = 0; // Debug, agent number
    private String name;             // String name for debug

    public MessageAgent()
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
     * @param all messages owned by this agent
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
    public void merge( MessageAgent fromAgent)
    {
        Object[] msgs = fromAgent.getMessageArray();
        for(int i = 0; i < msgs.length; i++) {
            if( Debug.LDAP_DEBUG) {
                Message info = (Message)msgs[i];
                Debug.trace( Debug.messages, name +
                "Merging Message(" + info.getMessageID() + ")");
            }
            ((Message)(msgs[i])).setAgent( this);
            messages.addElement( msgs[i]);
        }
        synchronized(messages) {
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
     public void sleepersAwake(boolean all)
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
    public boolean isResponseReceived()
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
    public boolean isResponseReceived( int msgId)
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
     * @param msgid the message id to abandon
     *<br><br>
     * @param cons constraints associated with this request
     */
    public void abandon(int msgId, LDAPConstraints cons)
    {
        Message info = null;
        try {
            // Send abandon request and remove from connection list
            info = messages.findMessageById( msgId);
            info.abandon( cons );
            // Message class has already removed message from my queue
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Abandoned Message(" + info.getMessageID() + ")");
            }
            return;
        } catch( NoSuchFieldException ex ) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Abandon of Message(" + info.getMessageID() + ") failed");
            }
        }
    }

    /**
     * Abandon all requests on this MessageAgent
     */
    public void abandonAll()
    {
        int size = messages.size();
        int[] ids = new int[size];
        Message info;

        for( int i = 0; i < size; i++ ) {
            info = (Message)messages.elementAt(i);
            info.abandon( null );
            // Message class has already removed message from my queue
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Abandoned Message(" + info.getMessageID() + ")");
            }
        }
        return;        
    }

    /**
     * Get a list of message ids controlled by this agent
     *
     * @return an array of integers representing the message ids
     */
	public int[] getMessageIDs()
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
	public boolean isComplete(int msgid)
	{
        try {
            Message info = (Message)messages.findMessageById( msgid);
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
     * @param the message ID.
     */
    public Message getMessage(int msgid)
            throws NoSuchFieldException
    {
        return (Message)messages.findMessageById( msgid);
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
     * @param listen the LDAPListener associated with this request.
     */
    public void sendMessage(
                            Connection      conn,
                            LDAPMessage     msg,
                            int             timeOut,
                            LDAPListener    listen,
                            BindProperties  bindProps)
            throws IOException
    {
        // creating a messageInfo causes the message to be sent
        // and a timer to be started if needed.
        Message message = new Message( msg, timeOut, conn,
                                    this, listen, bindProps);
        messages.addElement( message);
        return;
    }
                            
    /**
     * Removes the Message class from the agents list
     *
     * info the Message class to remove
     */
    /* package */
    void removeMessage( Message info)
    {
        if( ! messages.remove( info )) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Message(" + info.getMessageID() + ") " + "NOT removed");
            }
        } else {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                "Message(" + info.getMessageID() + ") " + "removed");
            }
        }
        return;
    }

    /**
     * Returns a response queued, or waits if none queued
     *
     */
    public RfcLDAPMessage getLDAPMessage( Integer msgId)
    {
        RfcLDAPMessage rfcMsg;
        if( msgId != null ) {
            // Request messages for a specific ID
            // If no messages for this agent, just return null
            if( messages.size() == 0) {
                return null;
            }
            try {
                // Get message for this ID
                Message info = (Message)messages.findMessageById( msgId.intValue());
                rfcMsg = (RfcLDAPMessage)info.waitForReply(); // blocks for a response
                if( info.isComplete() & ! info.hasReplies()) {
                    // Message complete and no more replies, remove from id list
                    messages.remove( info);
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                        "getLDAPMessage: Remove completed Message(" + info.getMessageID() + ")");
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
                    // If no messages for this agent, just return null
                    if( messages.size() == 0) {
                        return null;
                    }
                    int size = messages.size();
                    int next = indexLastRead + 1;
                    Message info;
                    for( int i = 0; i < size; i++) {
                       if( next == size ) {
                           next = 0;
                       }
                       info = (Message)messages.elementAt(next);
                       try {
                          rfcMsg = (RfcLDAPMessage)info.getReply();
                          indexLastRead = next;
                          if( info.isComplete() & ! info.hasReplies()) {
                             // Message complete & no more replies, remove from id list
                             messages.remove( info);
                             if( Debug.LDAP_DEBUG) {
                                Debug.trace( Debug.messages, name +
                                "getLDAPMessage: Remove completed Message(" +
                                info.getMessageID() + ")");
                             }
                          }
                          return rfcMsg;
                       } catch( ArrayIndexOutOfBoundsException ex) {
                             if( Debug.LDAP_DEBUG) {
                                Debug.trace( Debug.messages, name +
                                "getLDAPMessage: no messages queued for Message(" +
                                info.getMessageID() + ")");
                             }
                          continue;
                       }
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
                        // Look for any new queued message
                        continue;
                    }
                }
            }
        }
    }
}

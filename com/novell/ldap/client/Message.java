/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/client/Message.java,v 1.1 2000/11/22 22:17:41 vtag Exp $
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

import java.util.*;
import java.io.*;

/**
 * Encapsulates an LDAP message, its state, and its replies.
 */
public class Message extends Thread
{
    private LDAPMessage msg;             // msg request sent to server
    private Connection conn;             // Connection object where msg sent
    private MessageAgent agent;          // MessageAgent handling this request
    private LDAPListener listen;         // Application listener 
    private int mslimit;                 // client time limit in milliseconds
    // Note: MessageVector is synchronized
    private MessageVector replies = new MessageVector(5,5); // place to store replies
    private int msgId;                   // message ID of this request
    private boolean acceptReplies = true;// false if no longer accepting replies
    private boolean terminate = false;   // true if don't wait for reply
    private boolean complete = false;    // true LDAPResult received
    private String name;                 // String name used for Debug

    /**
     * Constructs a Message class encapsulating information about this message.
     *
     * @param msg       the message to send to the server          
     *<br><br>
     * @param mslimit   number of milliseconds to wait before the message times out.
     *<br><br>
     * @param conn      the connection used to send this message
     *<br><br>
     * @param agent     the MessageAgent handling this message.
     *<br><br>
     * @param listen    the application LDAPListener for this message
     */
    public Message( 
                        LDAPMessage msg,
                        int mslimit,
                        Connection conn,
                        MessageAgent agent,
                        LDAPListener listen)
                throws IOException
    {
        this.msg = msg;
        this.conn = conn;
        this.agent = agent;
        this.listen = listen;
        this.mslimit = mslimit;
        this.msgId = msg.getMessageID();

        if( Debug.LDAP_DEBUG) {
            name = "Message(" + this.msgId + "): ";
            Debug.trace( Debug.messages, name +
                " Created with mslimit " + this.mslimit);
        }
        conn.writeMessage( this );
        // Start the timer thread
        if( mslimit != 0 ) {
            this.start();   
        }
        return;
    }

    /**
     * Returns true if replies are queued
     *
     * @return false if no replies are queued, otherwise true
     */
    /* package */
    boolean hasReplies()
    {
        return (replies.size() > 0);
    }

    /**
     * Returns true if replies are accepted for this request.
     *
     * @return false if replies are no longer accepted for this request
     */
    /* package */
    boolean acceptsReplies()
    {
        return acceptReplies;
    }

    /**
     * prevents future replies from being accepted for this request
     */
    /* package */
    void refuseReplies()
    {
        acceptReplies = false;
        return;
    }

    /**
     * stops the timeout timer from running
     */
    /* package */
    void stopTimer()
    {
        // If timer thread started, stop it
        if( mslimit > 0) {
            interrupt();
        }
        return;
    }

    /**
     * Notifies all waiting threads
     */
    private void sleepersAwake()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Sleepers Awake");
        }
        // Notify any thread waiting for this message id
        synchronized( replies) {
            replies.notify();
        }
        // Notify any thread waiting for any message id
        synchronized( agent) {
            agent.notify();
        }
        return;
    }

    /**
     * gets the Connection associated with this message
     *
     * @return the Connection associated with this message.
     */
    /* package */
    Connection getConnection()
    {
        return( conn );
    }

    /**
     * gets the operation complete status for this message
     *
     * @return the true if the operation is complete, i.e.
     * the LDAPResult has been received.
     */
    /* package */
    boolean isComplete()
    {
        return( complete );
    }

    /**
     * gets the LDAPListener associated with this message
     *
     * @return the LDAPListener associated with this message
     */
    /* package */
    LDAPListener getLDAPListener()
    {
        return( listen );
    }

    /**
     * gets the MessageAgent associated with this message
     *
     * @return the MessageAgent associated with this message
     */
    /* package */
    MessageAgent getMessageAgent()
    {
        return( agent );
    }

    /**
     * gets the RfcLDAPMessage request associated with this message
     *
     * @return the RfcLDAPMessage request associated with this message
     */
    /* package */
    LDAPMessage getRequest()
    {
        return( msg );
    }

    /**
     * gets the Message ID associated with this message request
     *
     * @return the Message ID associated with this message request
     */
    /* package */
    int getMessageID()
    {
        return( msgId );
    }

    /**
     * Puts a reply on the reply queue
     *
     * @param message the RfcLDAPMessage to put on the reply queue.
     */
    /* package */
    void putReply( RfcLDAPMessage message)
    {
        stopTimer();
        replies.addElement( message); 
        if( message.getProtocolOp() instanceof RfcLDAPResult) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "Queuing LDAPResult, message complete");
            }
            complete = true;
        } else
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Queuing message");
        }
        // wake up waiting threads
        sleepersAwake();
        return;
    }
    
    /**
     * Puts an exception on the reply queue
     *
     * @param ex the LDAPException to put on the reply queue.
     */
    /* package */
    void putException( LDAPException ex)
    {
        replies.addElement( ex); 
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Queuing exception");
        }
        // wake up waiting threads
        sleepersAwake();
        return;
    }

    /**
     * Gets the next reply from the reply queue or waits until one is there
     *
     * @return the next reply message on the reply queue
     */
    /* package */
    Object waitForReply()
    {
        // sync on message so don't confuse with timer thread
        synchronized( replies ) {  
            while( ! terminate ) {
                try {
                    Object msg;
                    // We use remove and catch the exception because
                    // it is an atomic get and remove. isEmpty, getFirstElement,
                    // and removeElementAt are multiple statements.
                    // Another thread could remove the object between statements.
                    msg = replies.remove(0); // Atomic get and remove
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "Get reply from queue");
                    }
                    return msg;
                } catch( ArrayIndexOutOfBoundsException ex ) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "No replies queued, terminate=" + terminate);
                    }
                    if( ! terminate) {
                        try {
                            if( Debug.LDAP_DEBUG) {
                                Debug.trace( Debug.messages, name +
                                    "Wait for a reply");
                            }
                            wait();
                        } catch(InterruptedException ir) {
                            break;
                        }
                    } 
                }
            }
            return null;
        }
    }

    /**
     * Gets the next reply from the reply queue if one exists, otherwise
     * throws ArrayIndexOutOfBoundsException
     *
     * @return the next reply message on the reply queue
     *
     * @throws ArrayIndexOutOfBoundsException when no replies exist
     */
    /* package */
    Object getReply()
                throws ArrayIndexOutOfBoundsException
    {
            Object msg;
            // We use remove and catch the exception because
            // it is an atomic get and remove. isEmpty, getFirstElement,
            // and removeElementAt are multiple statements.
            // Another thread could remove the object between statements.
            msg = replies.remove(0); // Atomic get and remove
            try {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name + "Got reply from queue");
                }
            } catch( ArrayIndexOutOfBoundsException ex ) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name +
                        "No replies for id, throw ArrayIndexOutOfBoundsException");
                }
                throw ex;
            }
            return msg;
    }
    

    /**
     * abandon a request.
     * All queued replies are discarded.  The message is removed
     * from the connection and agent lists. Any client threads waiting
     * on this request are notified.
     *
     * @param cons and LDAPConstraints associated with the abandon.
     *<br><br>
     */
    /* package */
    void abandon( LDAPConstraints cons)
    {
        acceptReplies = false;  // don't listen to anyone 
        terminate = true;       // don't let sleeping threads lie 
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Abandon request");
        }
        try {
            // Create the abandon message, don't track it. 
            LDAPMessage msg = new LDAPMessage( new RfcAbandonRequest( msgId));
            // Send abandon message to server       
            conn.writeMessage( msg);
        } catch (IOException ex) {
            ; // do nothing
        }
        // remove message id from Connection list
        conn.removeMessage( this);
        // Remove message id from agent list
        agent.removeMessage( this);
        // Get rid of all replies queued
        cleanup();
        // Wake up any waitin threads
        sleepersAwake();
        return;
    }
    
    /**
     * The timeout thread.  If it wakes from the sleep, future input
     * is stopped and the request is timed out.  
    */
    public void run()
    {
        try {
            sleep(mslimit);
            acceptReplies = false;
            agent.abandon( msgId, null );
            putException( new LDAPException("Client timeout", LDAPException.LDAP_TIMEOUT));
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name + "client timeout");
            }
        } catch ( InterruptedException ie ) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name + "timer stopped");
            }
            // the timer was stopped, do nothing
        }
        return;
    }

    /**
     * cleanup - release reply messages
     */
    /* package */
    void cleanup()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, "Message: Clean up message " + msgId);
        }
        try {
            super.finalize();
            acceptReplies = false;
            conn.removeMessage( this );
            replies.remove(0);
        } catch ( Throwable ex ) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "cleanup exception:" + ex.toString());
            }
            ;// nothing
        }
        return;
    }
     
    /**
     * finalize
     */
    public void finalize()
    {
        cleanup();
        return;
    }
}

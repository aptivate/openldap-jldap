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
import com.novell.ldap.rfc2251.*;

/**
 * Encapsulates an LDAP message, its state, and its replies.
 */
/* package */
class Message
{
    private LDAPMessage msg;             // msg request sent to server
    private Connection conn;             // Connection object where msg sent
    private MessageAgent agent;          // MessageAgent handling this request
    private LDAPMessageQueue queue;      // Application message queue
    private int mslimit;                 // client time limit in milliseconds
    private Thread timer = null;         // Timeout thread
    // Note: MessageVector is synchronized
    private MessageVector replies = new MessageVector(5,5); // place to store replies
    private int msgId;                   // message ID of this request
    private boolean acceptReplies = true;// false if no longer accepting replies
    private boolean waitForReply = true;   // true if wait for reply
    private boolean complete = false;    // true LDAPResult received
    private String name;                 // String name used for Debug
    private BindProperties bindprops;    // Bind properties if a bind request

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
     * @param queue     the application LDAPMessageQueue for this message
     */
    /* package */
    Message(
                        LDAPMessage      msg,
                        int              mslimit,
                        Connection       conn,
                        MessageAgent     agent,
                        LDAPMessageQueue queue,
                        BindProperties   bindprops)
    {
        this.msg = msg;
        this.conn = conn;
        this.agent = agent;
        this.queue = queue;
        this.mslimit = mslimit;
        this.msgId = msg.getMessageID();
        this.bindprops = bindprops;

        if( Debug.LDAP_DEBUG) {
            name = "Message(" + this.msgId + "): ";
            Debug.trace( Debug.messages, name +
                " Created with mslimit " + this.mslimit);
        }
        return;
    }

    /**
     * This method write the message on the wire.  It MUST never be called
     * more than once.  Previously we were sending the message in the
     * constructor, but that opens a small timing window where a reply
     * could return before the code returns and this object gets queued
     * on the MessageAgentQueue.  In that small case, the application
     * would not wake up on the reply.  Making this method separate, closes
     * that window but opens the possibility for misuse.  We do not
     * enforce the requirement that it be called only once as that adds
     * extra synchronization.  We depend on the interal API to act correctly.
     * When the message is sent, the timer thread is started to time
     * the message.
     */
     /* package */
     final void sendMessage()
                throws LDAPException
     {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Sending request to " +
                conn.getConnectionName());
        }
        conn.writeMessage( this );
        // Start the timer thread
        if( mslimit != 0 ) {
            // Don't start the timer thread for abandon or Unbind
            switch( msg.getType())
            {
                case LDAPMessage.ABANDON_REQUEST:
                case LDAPMessage.UNBIND_REQUEST:
                    mslimit = 0;
                    break;
                default:
                    // start the timer thread
                    timer = new Timeout( mslimit, this);
                    timer.setDaemon(true); // If this is the last thread running, allow exit.
                    timer.start();
                    break;
            }
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
        if( replies == null) {
            // abandoned request
            return false;
        }
        return (replies.size() > 0);
    }

    /**
     * Get number of messages queued.
     * Don't count the last message containing result code.
     */
    /* package */
    int getCount()
    {
        int size = replies.size();
        if( complete) {
            return (size > 0 ? (size -1) : size);
        } else {
            return size;
        }
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
     * sets the agent for this message
     */
    /* package */
    void setAgent( MessageAgent agent)
    {
        this.agent = agent;
        return;
    }

    /**
     * stops the timeout timer from running
     */
    /* package */
    void stopTimer()
    {
        // If timer thread started, stop it
        if( timer != null) {
            timer.interrupt();
        }
        return;
    }

    /**
     * Notifies all waiting threads
     */
    private void sleepersAwake()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Sleepers Awake, " +
                agent.getAgentName());
        }
        // Notify any thread waiting for this message id
        synchronized( replies) {
            replies.notify();
        }
        // Notify a thread waiting for any message id
        agent.sleepersAwake(false);
        return;
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
        return complete;
    }

    /**
     * gets the MessageAgent associated with this message
     *
     * @return the MessageAgent associated with this message
     */
    /* package */
    MessageAgent getMessageAgent()
    {
        return agent;
    }

    /**
     * gets the LDAPMessage request associated with this message
     *
     * @return the LDAPMessage request associated with this message
     */
    /*package*/
    LDAPMessage getRequest()
    {
        return msg;
    }

    /**
     * gets the Message ID associated with this message request
     *
     * @return the Message ID associated with this message request
     */
    /* package */
    int getMessageID()
    {
        return msgId;
    }

    /**
     * gets the Message Type associated with this message request
     *
     * @return the Message Type associated with this message request
     */
    /* package */
    int getMessageType()
    {
        if( msg == null) {
            return -1;
        }
        return msg.getType();
    }

    /**
     * Puts a reply on the reply queue
     *
     * @param message the RfcLDAPMessage to put on the reply queue.
     */
    /* package */
    void putReply( RfcLDAPMessage message)
    {
        if( ! acceptReplies) {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "not accepting replies, discarding reply");
            }
            return;
        }
        replies.addElement( message);
        message.setRequestingMessage( msg); // Save request message info
        switch( message.getType()) {
        case LDAPMessage.SEARCH_RESPONSE:
        case LDAPMessage.SEARCH_RESULT_REFERENCE:
		case LDAPMessage.INTERMEDIATE_RESPONSE:
            // SearchResultEntry or SearchResultReference
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "Reply Queued (" + replies.size() + " in queue)");
            }
            break;
        
        	
        default:
            // All Responses with a result code
            int res;
            if( Debug.LDAP_DEBUG) {
                res = ((RfcResponse)message.getResponse()).getResultCode().intValue();
                Debug.trace( Debug.messages, name +
                    "Queued LDAPResult (" + replies.size() +
                    " in queue), message complete stopping timer, status " + res);
            }
            stopTimer();
            // Accept no more results for this message
            // Leave on connection queue so we can abandon if necessary
            acceptReplies = false;
            complete = true;
            if( bindprops != null) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name + "Bind properties found");
                }
                res = ((RfcResponse)message.getResponse()).getResultCode().intValue();
                if(res == LDAPException.SASL_BIND_IN_PROGRESS) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name + "Sasl Bind in-progress status");
                    }
                } else {
                    // We either have success or failure on the bind
                    if(res == LDAPException.SUCCESS) {
                        // Set bind properties into connection object
                        conn.setBindProperties(bindprops);
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.messages, name + "Bind status success");
                        }
                    } else {
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.messages, name + "Bind status " + res);
                        }
                    }
                    // If not a sasl bind in-progress, release the bind
                    // semaphore and wake up all waiting threads
                    int id;
                    if( conn.isBindSemIdClear()) {
                        // Semaphore id for normal operations
                        id = msgId;
                    } else {
                        // Semaphore id for sasl bind
                        id = conn.getBindSemId();
                        conn.clearBindSemId();
                    }
                    conn.freeWriteSemaphore(id);
                }
            }
        }
        // wake up waiting threads
        sleepersAwake();
        return;
    }

    /**
     * Gets the next reply from the reply queue or waits until one is there
     *
     * @return the next reply message on the reply queue or null
     */
    /* package */
    Object waitForReply()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "waitForReply()");
        }
        if( replies == null) {
            return null;
        }
        // sync on message so don't confuse with timer thread
        synchronized( replies ) {
            Object msg = null;
            while( waitForReply ) {
                if( replies.isEmpty()) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "No replies queued, waitForReply=" + waitForReply);
                    }
                    try {
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.messages, name +
                                "Wait for a reply");
                        }
                        replies.wait();
                    } catch(InterruptedException ir) {
                        ; // do nothing
                    }
                    if( waitForReply) {
                        continue;
                    } else {
                        break;
                    }
                } else {
                    msg = replies.remove(0); // Atomic get and remove
                }
                if( (complete || ! acceptReplies) && replies.isEmpty()) {
                    // Remove msg from connection queue when last reply read
                    conn.removeMessage(this);
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "Last message removed, remove msg from Connection");
                    }
                }
                else {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "Got reply from queue(" +
                            replies.size() + " remaining in queue)");
                    }
                }
                return msg;
            }
            return null;
        }
    }

    /**
     * Gets the next reply from the reply queue if one exists
     *
     * @return the next reply message on the reply queue or null if none
     */
    /* package */
    Object getReply()
    {
            Object msg;
            if( replies == null) {
                return null;
            }
            synchronized( replies) {
                // Test and remove must be atomic
                if( replies.isEmpty()) {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "No replies queued for message");
                    }
                    return null;    // No data
                }
                msg = replies.remove(0); // Atomic get and remove
            }
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                        "Got reply from queue(" +
                        replies.size() + " remaining in queue)");
            }
            if( (conn != null) && (complete || ! acceptReplies) && replies.isEmpty()) {
                // Remove msg from connection queue when last reply read
                conn.removeMessage(this);
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
     * @param informUserEx true if user must be informed of operation
     */
    /* package */
    void abandon( LDAPConstraints cons, InterThreadException informUserEx)
    {
        if( ! waitForReply) {
            Debug.trace( Debug.messages, name + "Abandon request ignored");
            return;
        }
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "Abandon request, complete="
                + complete + ", bind=" + (bindprops != null) +
                ", informUser=" + (informUserEx != null) +
                ", waitForReply=" + waitForReply);
        }
        acceptReplies = false;  // don't listen to anyone
        waitForReply = false;   // don't let sleeping threads lie
        if( ! complete) {
            try {
                // If a bind, release bind semaphore & wake up waiting threads
                // Must do before writing abandon message, otherwise deadlock
                if( bindprops != null) {
                    int id;
                    if( conn.isBindSemIdClear()) {
                        // Semaphore id for normal operations
                        id = msgId;
                    } else {
                        // Semaphore id for sasl bind
                        id = conn.getBindSemId();
                        conn.clearBindSemId();
                    }
                    conn.freeWriteSemaphore(id);
                }

                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, name + "Sending abandon request");
                }
                // Create the abandon message, but don't track it.
                LDAPControl[] cont = null;
                if( cons != null) {
                    cont = cons.getControls();
                }
                LDAPMessage msg = new LDAPAbandonRequest( msgId, cont);
                // Send abandon message to server
                conn.writeMessage( msg);
            } catch (LDAPException ex) {
                ; // do nothing
            }
            // If not informing user, remove message from agent
            if( informUserEx == null) {
                agent.abandon( msgId, null);
            }
            conn.removeMessage( this);
        }
        // Get rid of all replies queued
        if( informUserEx != null) {
            replies.addElement( new LDAPResponse( informUserEx,
                        conn.getActiveReferral()));
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                        "Queued exception as LDAPResponse (" + replies.size() +
                        " in queue):" +
                        " following referral=" +
                        (conn.getActiveReferral() != null) + "\n\texception: " +
                        informUserEx.getLDAPErrorMessage());
            }
            stopTimer();
            // wake up waiting threads to receive exception
            sleepersAwake();
            // Message will get cleaned up when last response removed from queue
        } else {
            // Wake up any waiting threads, so they can terminate.
            // If informing the user, we wake sleepers after
            // caller queues dummy response with error status
            sleepersAwake();
            cleanup();
        }
        return;
    }

    /**
     * Release reply messages
     */
    private
    void cleanup()
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "cleanup");
        }
        stopTimer();        // Make sure timer stopped
        try {
            acceptReplies = false;
            if( conn != null) {
                conn.removeMessage( this );
            }
            // Empty out any accumuluated replies
            if( replies != null) {
                if( Debug.LDAP_DEBUG) {
                    if( ! replies.isEmpty()) {
                        Debug.trace( Debug.messages, name +
                            "cleanup: remove " + replies.size() + " replies");
                    }
                }
                while( ! replies.isEmpty()) {
                    replies.remove(0);
                }
            }
        } catch ( Throwable ex ) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages, name +
                    "cleanup exception:" + ex.toString());
            }
            ;// nothing
        }
        // Let GC clean up this stuff, leave name in case finalized is called
        conn = null;
        msg = null;
        // agent = null;  // leave this reference
        queue = null;
        //replies = null; //leave this since we use it as a semaphore
        bindprops = null;
        return;
    }

    /**
     * Returns true if this message is a bind request
     *
     * @return true if a bind request
     */
    /* package */
    final boolean isBindRequest()
    {
        return (bindprops != null);
    }

    /**
     * finalize
     */
    protected final void finalize() throws Throwable
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name + "finalize");
        }
        super.finalize();
        cleanup();
        return;
    }

    /**
     * Timer class to provide timing for messages.  Only called
     * if time to wait is non zero.
     */
    private final class Timeout extends Thread
    {
        private int timeToWait = 0;
        private Message message;

        /* package */
        Timeout( int interval, Message msg)
        {
            super();
            timeToWait = interval;
            message = msg;
            return;
        }

        /**
         * The timeout thread.  If it wakes from the sleep, future input
         * is stopped and the request is timed out.
        */
        public final void run()
        {
            try {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, message.name +
                       "client timer started, " + timeToWait + " milliseconds");
                }
                sleep(timeToWait);
                message.acceptReplies = false;
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, message.name + "client timed out");
                }
                // Note: Abandon clears the bind semaphore after failed bind.
                message.abandon( null,
                            new InterThreadException("Client request timed out",
                            null, LDAPException.LDAP_TIMEOUT, null, message));
            } catch ( InterruptedException ie ) {
                if( Debug.LDAP_DEBUG) {
                    Debug.trace( Debug.messages, message.name + "timer stopped");
                }
                // the timer was stopped, do nothing
            }
            return;
        }
    }
}

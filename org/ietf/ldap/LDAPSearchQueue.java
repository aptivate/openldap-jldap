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

package org.ietf.ldap;

/**
 *  A mechanism for queuing asynchronous search results
 *  and references received from a server.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html">
            com.novell.ldap.LDAPSearchQueue</a>
 */
public class LDAPSearchQueue implements LDAPMessageQueue
{
    private com.novell.ldap.LDAPSearchQueue queue;

    /**
     * Constructs a response queue from a com.novell.ldap.LDAPSearchQueue
     */
    /* package */
    LDAPSearchQueue(com.novell.ldap.LDAPSearchQueue queue)
    {
        this.queue = queue;
        return;
    }

    /**
     * Returns a com.novell.ldap.LDAPSearchQueue object
     */
    /* package */
    com.novell.ldap.LDAPSearchQueue getWrappedObject()
    {
        return queue;
    }

   /**
    * Returns the message IDs for all outstanding requests.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#getMessageIDs()">
            com.novell.ldap.LDAPSearchQueue.getMessageIDs()</a>
    */
    public int[] getMessageIDs()
    {
        return queue.getMessageIDs();
    }

   /**
    * Reports whether a response has been received from the server and
    * not yet retrieved with getResponse.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#isResponseReceived()">
            com.novell.ldap.LDAPSearchQueue.isResponseReceived()</a>
    */
    public boolean isResponseReceived()
    {
        return queue.isResponseReceived();
    }

   /**
    * Reports whether a response has been received from the server and
    * not yet retrieved with getResponse.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#isResponseReceived(int)">
            com.novell.ldap.LDAPSearchQueue.isResponseReceived(int)</a>
    */
    public boolean isResponseReceived(int msgid)
    {
        return queue.isResponseReceived(msgid);
    }

   /**
    * Merges two response queues by moving the contents from another
    * queue to this one.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#merge(com.novell.ldap.LDAPMessagequeue)">
            com.novell.ldap.LDAPSearchQueue.merge(
            LDAPResponsequeue)</a>
    */
    public void merge(LDAPMessageQueue queue2)
    {
        if( queue2 == null) {
            queue.merge( (com.novell.ldap.LDAPMessageQueue)null);
        }
        if( queue2 instanceof LDAPResponseQueue) {
            queue.merge( ((LDAPResponseQueue)queue2).getWrappedObject());
        } else {
            queue.merge( ((LDAPSearchQueue)queue2).getWrappedObject());
        }
        return;
    }

    /**
     * Reports true if all results have been received for a particular
     * message id.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#isComplete(int)">
            com.novell.ldap.LDAPSearchQueue.isComplete(int)</a>
     */
    public boolean isComplete( int msgid )
    {
        return queue.isComplete( msgid);
    }

    /**
     * Blocks until a response is available, or until all operations
     * associated with the object have completed or been canceled, and
     * returns the response.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#getResponse()">
            com.novell.ldap.LDAPSearchQueue.getResponse()</a>
     */
    public LDAPMessage getResponse()
            throws LDAPException
    {
        try {
            return new LDAPMessage(queue.getResponse());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                            (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Blocks until a response is available for a particular message id,
     * or until all operations
     * associated with the object have completed or been canceled, and
     * returns the response.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchQueue.html#getResponse(int)">
            com.novell.ldap.LDAPSearchQueue.getResponse(int)</a>
     */
    public LDAPMessage getResponse(int msgid)
            throws LDAPException
    {
        try {
            return new LDAPMessage(queue.getResponse( msgid));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }
}

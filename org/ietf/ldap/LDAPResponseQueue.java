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
 *  Encapsulates a low-level mechanism for processing asynchronous messages
 *  received from a server.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html">
            com.novell.ldap.LDAPResponseQueue</a>
 */
public class LDAPResponseQueue implements LDAPMessageQueue
{
    private com.novell.ldap.LDAPResponseQueue queue;

    /**
     * Constructs a response queue from com.novell.ldap.LDAPResponseQueue
     */
    /* package */
    LDAPResponseQueue(com.novell.ldap.LDAPResponseQueue queue)
    {
        this.queue = queue;
        return;
    }

    /**
     * Returns the com.novell.ldap.LDAPResponseQueue object
     */
    /* package */
    com.novell.ldap.LDAPResponseQueue getWrappedObject()
    {
        return queue;
    }

    /**
     * Returns the message IDs for all outstanding requests.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html#getMessageIDs()">
            com.novell.ldap.LDAPResponse.getMessageIDs()</a>
     */
     public int[] getMessageIDs()
     {
        return queue.getMessageIDs();
     }

    /**
     * Reports whether a response has been received from the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html#isResponseReceived()">
            com.novell.ldap.LDAPResponse.isResponseReceived()</a>
     */
     public boolean isResponseReceived()
     {
        return queue.isResponseReceived();
     }

    /**
     * Reports whether a response has been received from the server for a
     * particular message id.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html#isResponseReceived(int)">
            com.novell.ldap.LDAPResponse.isResponseReceived(int)</a>
     */
     public boolean isResponseReceived(int msgid)
     {
        return queue.isResponseReceived( msgid);
     }

    /**
     * Merges two response queues by moving the contents from another
     * queue to this one.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#merge(com.novell.ldap.LDAPResponseQueue)">
            com.novell.ldap.LDAPResponse.merge(LDAPMessageQueue)</a>
     */
     public void merge(LDAPMessageQueue queue2)
     {
        if( queue2 == null) {
            queue.merge( (com.novell.ldap.LDAPMessageQueue)null);
        }
        if( queue2 instanceof LDAPResponseQueue) {
            queue.merge( ((LDAPResponseQueue)queue2).getWrappedObject() );
        } else {
            queue.merge( ((LDAPSearchQueue)queue2).getWrappedObject() );
        }
        return;
     }

    /**
     * Returns the response.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html#getResponse()">
            com.novell.ldap.LDAPResponse.getResponse()</a>
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
     * Returns the response for a particular message id.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponseQueue.html#getResponse(int)">
            com.novell.ldap.LDAPResponse.getResponse(int)</a>
     */
    public LDAPMessage getResponse(int msgid)
        throws LDAPException
    {
        try {
            return new LDAPMessage(queue.getResponse(msgid));
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

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
import com.novell.ldap.client.Debug;

/**
 *  A mechanism for processing asynchronous messages received from a server.
 *  It represents the message queue associated with a particular asynchronous
 *  LDAP operation or operations.
 */
public class LDAPResponseQueue extends LDAPMessageQueue
{
    /**
     * Constructs a response queue using the specified message agent
     *
     *  @param agent The message agent to associate with this queue
     */
    /* package */
    LDAPResponseQueue(MessageAgent agent)
    {
        super( "LDAPResponseQueue", agent);
        return;
    }

    /**
     * Merges two message queues.  It appends the current and
     *                   future contents from another queue to this one.
     *
     *                  <p>After the operation, queue2.getMessageIDs()
     *                  returns an empty array, and its outstanding responses
     *                  have been removed and appended to this queue</p>.
     *
     * @param queue2    The queue that is merged from.  Following
     *                  the merge, this queue object will no
     *                  longer receive any data, and calls made
     *                  to its methods will fail with a RuntimeException.
     *                  The queue can be reactivated by using it in an 
     *                  LDAP request, after which it will receive responses
     *                  for that request..
     */
    public void merge(LDAPMessageQueue queue2)
    {
        LDAPResponseQueue q = (LDAPResponseQueue)queue2;
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                    "merge " + q.getDebugName());
            }
        agent.merge( q.getMessageAgent() );
        
        return;
    }
}

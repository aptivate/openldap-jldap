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

import com.novell.ldap.client.MessageAgent;

/**
 *  A mechanism for queuing asynchronous search results
 *  received from a server.
 *
 * @see LDAPConnection#search
 * @see LDAPResponseQueue
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/asynchronous/Searchas.java.html">Searchas.java</p>
 */
public class LDAPSearchQueue extends LDAPSearchListener
{
    /**
     * Constructs a response queue using a specific client queue
     *
     *  @param agent The message agent to associate with this queue
     */
    /* package */
    LDAPSearchQueue(MessageAgent agent)
    {
        super( agent);
        return;
    }
    
    /**
     * Returns the name used for debug
     *
     * @return name of object instance used for debug
     */
    /* package */
    String getDebugName()
    {
        return super.getDebugName();
    }

   /**
    * Returns message agent associated with this queue
    *
    * @return the message agent associated with this queue
    */
    /* package */
    MessageAgent getMessageAgent()
    {
        return super.getMessageAgent();
    }

    public int[] getMessageIDs()
    {
        return super.getMessageIDs();
    }

    public boolean isResponseReceived()
    {
        return super.isResponseReceived();
    }

    public boolean isResponseReceived(int msgid)
    {
        return super.isResponseReceived(msgid);
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
    */
    public void merge(LDAPMessageQueue queue2)
    {
        super.merge(queue2);
        return;
    }

    /**
     * Reports true if all results have been received for a particular
     * message id, i.e. a response has been received from the server for the
     * id.  There may still be messages waiting to be retrieved with
     * getResponse.
     */
    public boolean isComplete( int msgid )
    {
        return super.isComplete( msgid);
    }

    public LDAPMessage getResponse()
        throws LDAPException
    {
        return super.getResponse();
    }

    public LDAPMessage getResponse(int msgid)
        throws LDAPException
    {
        return super.getResponse(msgid);
    }
}

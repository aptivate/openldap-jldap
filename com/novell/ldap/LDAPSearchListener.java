/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

// When deprecated methods are removed, remove this class

package com.novell.ldap;

import com.novell.ldap.client.*;

/**
 *  <p>This class has been renamed to LDAPSearchQueue in IETF
 *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
 *  and will be removed from the LDAP Classes for Java API in the fall of 2003.</p>
 *
 *  <p>A mechanism for queuing asynchronous search results
 *  and references received from a server.</p>
 *
 * @see LDAPConnection#search
 *
 * @deprecated replaced by {@link LDAPSearchQueue}.
 */
public class LDAPSearchListener extends LDAPMessageQueue
{
    /**
     * Constructs a response queue using a specific message agent
     *
     *  @param agent The message agent to associate with this queue
     */
    /* package */
    LDAPSearchListener(MessageAgent agent)
    {
        super( "LDAPSearchListener", agent);
        return;
    }

    /* package */
    LDAPSearchListener(String name, MessageAgent agent)
    {
        super( name, agent);
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
     *
     * @deprecated replaced by {@link LDAPSearchQueue#merge(LDAPMessageQueue)}.
     */
    public void merge(LDAPListener queue2)
    {
        doMerge((LDAPMessageQueue)queue2);
        return;
    }
    
    /**
     * @see LDAPSearchQueue#merge
     */
    public void merge(LDAPMessageQueue queue2)
    {
        doMerge(queue2);
        return;
    }
}

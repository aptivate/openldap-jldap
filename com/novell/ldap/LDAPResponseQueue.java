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
 *  A mechanism for processing asynchronous messages received from a server.
 *  It represents the message queue associated with a particular asynchronous
 *  LDAP operation or operations.
 */
public class LDAPResponseQueue extends LDAPResponseListener
{
    /**
     * Constructs a response queue using the specified message agent
     *
     *  @param agent The message agent to associate with this queue
     */
    /* package */
    LDAPResponseQueue(MessageAgent agent)
    {
        super( agent);
        return;
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
        return super.isResponseReceived( msgid);
    }

    public void merge(LDAPListener queue2)
    {
        super.merge(queue2);
        return;
    }

   public LDAPMessage getResponse()
        throws LDAPException
   {
        return super.getResponse();
   }

   public LDAPMessage getResponse(int msgid)
        throws LDAPException
   {
        return super.getResponse( msgid);
   }
}

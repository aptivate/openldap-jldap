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

package com.novell.ldap;

import com.novell.ldap.client.*;
import com.novell.ldap.rfc2251.*;

/**
 *  <p>This class has been renamed to LDAPResponseQueue in IETF draft 17
 *  of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will
 *  be removed from the LDAP Classes for Java API in the fall of 2003.</p>
 *
 *  <p>Encapsulates a mechanism for processing asynchronous messages
 *  received from a server.  It represents the message queue associated
 *  with a particular asynchronous LDAP operation or operations.</p>
 *
 *  @deprecated replaced by {@link LDAPResponseQueue}.
 */
public class LDAPResponseListener implements LDAPMessageQueue
{
    private static Object nameLock = new Object(); // protect connNum
    private static int rQueueNum = 0;
    private String name="";

   /**
    * The message agent object associated with this queue
    */
    private MessageAgent agent;

    /**
     * Constructs a response queue using the specified message agent
     *
     *  @param agent The message agent to associate with this conneciton
     */
    /* package */
    LDAPResponseListener(MessageAgent agent)
    {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPResponseListener(" + ++rQueueNum + "): ";
            }
            Debug.trace( Debug.messages, name + "Created");
        }
        this.agent = agent;
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
        return name;
    }

   /**
    * Returns the message agent associated with this queue
    *
    * @return the message agent object
    */
    /* package */
    MessageAgent getMessageAgent()
    {
        return agent;
    }

    public int[] getMessageIDs()
    {
        return agent.getMessageIDs();
    }

    public boolean isResponseReceived()
    {
        return agent.isResponseReceived();
    }

    public boolean isResponseReceived(int msgid)
    {
        return agent.isResponseReceived( msgid);
    }

    public void merge(LDAPListener queue2)
    {
        merge( (LDAPMessageQueue)queue2);
        return;
    }

    public void merge(LDAPMessageQueue queue2)
    {
        if( queue2 instanceof LDAPResponseQueue) {
            LDAPResponseQueue q = (LDAPResponseQueue)queue2;
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                    "merge " + q.getDebugName());
            }
            agent.merge( q.getMessageAgent() );
        } else {
            LDAPSearchQueue q = (LDAPSearchQueue)queue2;
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                    "merge " + q.getDebugName());
            }
            agent.merge( q.getMessageAgent() );
        }
        return;
    }

   public LDAPMessage getResponse()
        throws LDAPException
   {
        return getresp( null );
   }

   public LDAPMessage getResponse(int msgid)
        throws LDAPException
   {
        return getresp( new Integer(msgid));
   }

   private LDAPMessage getresp( Integer msgid)
        throws LDAPException
   {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                "getResponse(" + msgid + ")");
        }
        Object resp;
        RfcLDAPMessage message;
        LDAPResponse response;
        if( (resp = agent.getLDAPMessage( msgid)) == null) {
            return null;
        }
        // Local error occurred
        if( resp instanceof LDAPResponse) {
            return (LDAPMessage)resp;
        }
        // Normal message handling
        message = (RfcLDAPMessage)resp;
        if(message.getType() == LDAPMessage.EXTENDED_RESPONSE) {
            ExtResponseFactory fac = new ExtResponseFactory();
            response = fac.convertToExtendedResponse(message);
        } else {
            response = new LDAPResponse(message);
        }
        return response;
   }
}

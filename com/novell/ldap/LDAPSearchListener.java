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
import java.util.Vector;

import com.novell.ldap.rfc2251.*;

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
public class LDAPSearchListener implements LDAPMessageQueue
{

    // Connection number & name used only for debug
    private static Object nameLock = new Object(); // protect connNum
    private static int sQueueNum = 0;
    private String name = "";

   /**
    * The client message agent
    */
    private MessageAgent agent;

    /**
     * Constructs a response queue using a specific message agent
     *
     *  @param agent The message agent to associate with this queue
     */
    /* package */
    LDAPSearchListener(MessageAgent agent)
    {
        if( Debug.LDAP_DEBUG) {
            synchronized(nameLock) {
                name = "LDAPSearchListener(" + ++sQueueNum + "): ";
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
    * Returns the internal client message agent
    *
    * @return The internal client message agent
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
        return agent.isResponseReceived(msgid);
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
            agent.merge( q.getMessageAgent());
        } else {
            LDAPSearchQueue q = (LDAPSearchQueue)queue2;
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.apiRequests, name +
                    "merge " + q.getDebugName());
            }
            agent.merge( q.getMessageAgent());
        }
        return;
    }

    /**
     * Reports true if all results have been received for a particular
     * message id.
     *
     * <p>If the search result done has been received from the server for the
     * message id, it reports true.  There may still be messages waiting to be
     * retrieved by the applcation with getResponse.<p>
     *
     * @throws IllegalArgumentException if there is no outstanding operation
     * for the message ID,
     */
     public boolean isComplete( int msgid )
     {
        return agent.isComplete( msgid);
     }

   public LDAPMessage getResponse()
      throws LDAPException
   {
      return getResp( null );
   }

   public LDAPMessage getResponse(int msgid)
      throws LDAPException
   {
      return getResp( new Integer(msgid));
   }

   private LDAPMessage getResp( Integer msgid)
   {
      LDAPMessage message;
      RfcLDAPMessage msg;
      Object resp;

      if( Debug.LDAP_DEBUG) {
          Debug.trace( Debug.apiRequests, name +
              "getResponse(" + msgid + ")");
      }

      if( (resp = agent.getLDAPMessage( msgid)) == null) { // blocks
          return null;  // no messages on this agent
      }

      // Local error occurred
      if( resp instanceof LDAPResponse) {
           return (LDAPMessage)resp;
      }
      // Normal message handling
      msg = (RfcLDAPMessage)resp;
      if(msg.getProtocolOp() instanceof RfcSearchResultEntry) {
         message = new LDAPSearchResult(msg);
      }
      else if(msg.getProtocolOp() instanceof RfcSearchResultReference) {
         message = new LDAPSearchResultReference(msg);
      }
      else { // must be SearchResultDone
         message = new LDAPResponse(msg);
      }

      return message;
   }
}

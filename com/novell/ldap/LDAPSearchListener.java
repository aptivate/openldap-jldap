/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchListener.java,v 1.25 2001/01/04 20:14:48 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import com.novell.ldap.client.*;
import java.util.Vector;

import com.novell.ldap.rfc2251.*;

/**
 *  A low-level mechanism for queuing asynchronous search results
 *  and references received from a server.
 */
public class LDAPSearchListener implements LDAPListener
{

    // Connection number & name used only for debug
    private static Object nameLock = new Object(); // protect connNum
    private static int sListenNum = 0;
    private String name = "";

   /**
    * The client listener object
    */
    private MessageAgent agent;

    /**
     * Constructs a response listener using a specific client listener
     *
     *  @param listen The client listener to associate with this listener
     */
    /* package */
    LDAPSearchListener(MessageAgent agent)
    {
        if( Debug.LDAP_DEBUG) {
            synchronized(nameLock) {
                name = "LDAPSearchListener(" + ++sListenNum + "): ";
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
   /**
    * Returns the internal client listener object
    *
    * @return The internal client listener object
    */
    /* package */
    MessageAgent getMessageAgent()
    {
        return agent;
    }

   /**
    * Returns the message IDs for all outstanding requests.
    *
    * <p>The last ID in the array is the messageID of the
    * last submitted request.</p>
    *
    * @return The message IDs for all outstanding requests.
    */
    public int[] getMessageIDs()
    {
        return agent.getMessageIDs();
    }

   /**
    * Reports whether a response has been received from the server.
    *
    * @return True if a response has been received from the server; false if
    *         a response has not been received.
    */
    public boolean isResponseReceived()
    {
        return agent.isResponseReceived();
    }

   /**
    * Reports whether a response has been received from the server.
    *
    * @return True if a response has been received from the server; false if
    *         a response has not been received.
    */
    public boolean isResponseReceived(int msgid)
    {
        return agent.isResponseReceived(msgid);
    }

   /**
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @param listener2 The listener that receives the contents from the
    *                  other listener.
    */
    public void merge(LDAPResponseListener listener2)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, name +
                "merge " + listener2.getDebugName());
        }
        agent.merge( listener2.getMessageAgent());
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
        return agent.isComplete( msgid);
     }

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response.
    *
    * <p>The response may be a search result, a search
    * reference, a search response, or null (if there are no more
    * outstanding requests). LDAPException is thrown on network errors.</p>
    *
    * <p>The only time this method should return a null is if there is no
    * response in the message queue and there are no message IDs pending.</p>
    *
    * @return The response (a search result, search reference, or search response)or
    *         null if there are no more outstanding requests.
    *
    * @exception LDAPException A general exception which includes an error
    *                          message and an LDAP error code.
    */
   public LDAPMessage getResponse()
      throws LDAPException
   {
      return getResp( null );
   }

   /**
    * Blocks until a response is available for a particular message id,
    * or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response.
    *
    * <p>The response may be a search result, a search
    * reference, a search response, or null (if there are no more
    * outstanding requests). LDAPException is thrown on network errors.</p>
    *
    * <p>The only time this method should return a null is if there is no
    * response in the message queue and there are no message IDs pending.</p>
    *
    * @return The response (a search result, search reference, or search response)or
    *         null if there are no more outstanding requests.
    *
    * @exception LDAPException A general exception which includes an error
    *                          message and an LDAP error code.
    */
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

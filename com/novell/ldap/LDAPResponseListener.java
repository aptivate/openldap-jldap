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

import java.util.Vector;

import com.novell.ldap.client.*;
import com.novell.ldap.rfc2251.*;

/**
 *  Encapsulates a low-level mechanism for processing asynchronous messages
 *  received from a server.  It
 *  represents the message queue associated with a particular asynchronous LDAP
 *  operation or operations.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/asynchronous/Searchas.java.html">Searchas.java</p>
 */
public class LDAPResponseListener implements LDAPListener
{
    private static Object nameLock = new Object(); // protect connNum
    private static int rListenNum = 0;
    private String name="";

   /**
    * The message agent object associated with this listener
    */
    private MessageAgent agent;

    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param listen The message agent to associate with this conneciton
     */
    /* package */
    LDAPResponseListener(MessageAgent agent)
    {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPResponseListener(" + ++rListenNum + "): ";
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
    * Returns the message agent associated with this listener
    *
    * @return the message agent object
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
    * Reports whether a response has been received from the server for a
    * particular message id.
    *
    * @return True if a response has been received from the server; false if
    *         a response has not been received.
    */
    public boolean isResponseReceived(int msgid)
    {
        return agent.isResponseReceived( msgid);
    }

   /**
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @param listener2 The listener that is merged into this listener.
    *
    */
    public void merge(LDAPResponseListener listener2)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                "merge " + listener2.getDebugName());
        }
        agent.merge( listener2.getMessageAgent() );
        return;
    }

   /**
    * Returns the response.
    *
    * <p>The getResponse method locks until a response is available, or until all
    * operations associated with the object have completed or been canceled, and
    * then returns the response. The client is responsible for processing
    * the responses returned from a listener.</p>
    *
    * @return The response or null if there are no requests..
    *
    * @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
   public LDAPMessage getResponse()
        throws LDAPException
   {
        return getresp( null );
   }

   /**
    * Returns the response for a particular message id.
    *
    * <p>The getResponse method locks until a response is available, or until all
    * operations associated with the object have completed or been canceled, and
    * then returns the response. The client is responsible for processing
    * the responses returned from a listener.</p>
    *
    * @return The response or null if there are no requests..
    *
    * @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
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
        if(message.getProtocolOp() instanceof RfcExtendedResponse) {
            ExtResponseFactory fac = new ExtResponseFactory();
            response = fac.convertToExtendedResponse(message);
        } else {
            response = new LDAPResponse(message);
        }
        return (LDAPMessage)response;
   }
}

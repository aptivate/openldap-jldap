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
import com.novell.ldap.client.IntermediateResponseFactory;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.client.ExtResponseFactory;

/**
 *  Represents a queue of incoming asynchronous messages from the server.
 *  It is the common interface for {@link LDAPResponseQueue} and
 *  {@link LDAPSearchQueue}.
 */
public abstract class LDAPMessageQueue 
{
    /**
     * The message agent object associated with this queue
     */
    /* package */
    MessageAgent agent;

    // Queue name used only for debug
    /* package */
    String name="";

     // nameLock used to protect queueNum during increment
     /* package */
     static Object nameLock = new Object();

    // Queue number used only for debug
    /* package */
     static int queueNum = 0;

    /**
     * Constructs a response queue using the specified message agent
     *
     *  @param agent The message agent to associate with this conneciton
     */
    /* package */
    LDAPMessageQueue(String myname, MessageAgent agent)
    {
        // Get a unique connection name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = myname + "(" + ++queueNum + "): ";
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

    /**
     * Returns the message IDs for all outstanding requests. These are requests
     * for which a response has not been received from the server or which
     * still have messages to be retrieved with getResponse.
     *
     * <p>The last ID in the array is the messageID of the last submitted
     * request.</p>
     *
     * @return The message IDs for all outstanding requests.
     */
    public int[] getMessageIDs()
    {
        return agent.getMessageIDs();
    }

    /**
     * Returns the response from an LDAP request.
     *
     * <p>The getResponse method blocks until a response is available, or until
     * all operations associated with the object have completed or been
     * canceled, and then returns the response.</p>
     *
     * <p>The application is responsible to determine the type of message
     * returned.</p>
     *
     * @return The response.
     *
     * @see LDAPResponse
     * @see LDAPSearchResult
     * @see LDAPSearchResultReference
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPMessage getResponse()
            throws LDAPException
    {
        return getResponse( null );
    }

    /**
     * Returns the response from an LDAP request for a particular message ID.
     *
     * <p>The getResponse method blocks until a response is available
     * for a particular message ID, or until all operations associated
     * with the object have completed or been canceled, and
     * then returns the response.  If there is no outstanding operation for
     * the message ID (or if it is zero or a negative number),
     * IllegalArgumentException is thrown.
     *
     * <p>The application is responsible to determine the type of message
     * returned.</p>
     *
     * @param msgid query for responses for a specific message request
     *
     * @return The response from the server.
     *
     * @see LDAPResponse
     * @see LDAPSearchResult
     * @see LDAPSearchResultReference
     *
     * @exception LDAPException A general exception which includes an error
     *  message and an LDAP error code.
     */
    public LDAPMessage getResponse(int msgid)
        throws LDAPException
    {
        return getResponse( new Integer(msgid));
    }

    /**
     * Private implementation of getResponse.
     * Has an Integer object as a parameter so we can distinguish
     * the null and the message number case
     */
    private LDAPMessage getResponse( Integer msgid)
        throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                "getResponse(" + msgid + ")");
        }
        Object resp;
        RfcLDAPMessage message;
        LDAPMessage response;

        if( (resp = agent.getLDAPMessage( msgid)) == null) { // blocks
            return null; // no messages from this agent
        }
        // Local error occurred, contains a LocalException
        if( resp instanceof LDAPResponse) {
            return (LDAPMessage)resp;
        }
        // Normal message handling
        message = (RfcLDAPMessage)resp;
        switch( message.getType()) {
            case LDAPMessage.SEARCH_RESPONSE: // Entry returned from a search
                response = new LDAPSearchResult(message);
                break;
            case LDAPMessage.SEARCH_RESULT_REFERENCE:
                response = new LDAPSearchResultReference(message);
                break;
            case LDAPMessage.INTERMEDIATE_RESPONSE:
            	
                response = IntermediateResponseFactory.convertToIntermediateResponse(message);
                break;
            case LDAPMessage.EXTENDED_RESPONSE:
                ExtResponseFactory fac = new ExtResponseFactory();
                response = fac.convertToExtendedResponse(message);
                break;
            default:    // This is the completion of a request
                response = new LDAPResponse(message);
                break;
        }
        return response;
    }

    /**
     * Reports true if any response has been received from the server and not
     * yet retrieved with getResponse.  If getResponse has been used to
     * retrieve all messages received to this point, then isResponseReceived
     * returns false.
     *
     * @return true if a response is available to be retrieved via getResponse,
     *         otherwise false.
     *
     * @see #getResponse()
     */
    public boolean isResponseReceived()
    {
        return agent.isResponseReceived();
    }

    /**
     * Reports true if a response has been received from the server for
     * a particular message ID but not yet retrieved with getResponse.  If
     * there is no outstanding operation for the message ID (or if it is
     * zero or a negative number), IllegalArgumentException is thrown.
     *
     * @param msgid    A particular message ID to query for available responses.
     *
     * @return true if a response is available to be retrieved via getResponse
     *         for the specified message ID, otherwise false.
     *
     * @see #getResponse(int)
     */
    public boolean isResponseReceived(int msgid)
    {
        return agent.isResponseReceived( msgid);
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
}

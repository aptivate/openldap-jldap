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

/**
 *  Represents a queue of incoming asynchronous messages from the server.
 *  It is the common interface for {@link LDAPResponseQueue} and
 *  {@link LDAPSearchQueue}.
 */
public interface LDAPMessageQueue extends LDAPListener
{
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
   public int[] getMessageIDs();

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
            throws LDAPException;

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
    * @return The response.
    *
    * @see LDAPResponse
    * @see LDAPSearchResult
    * @see LDAPSearchResultReference
    *
    * @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
   public LDAPMessage getResponse(int msgid)
            throws LDAPException;

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
   public boolean isResponseReceived();

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
   public boolean isResponseReceived(int msgid);

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
   public void merge(LDAPListener queue2);
}

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

package org.ietf.ldap;

/**
 *  The common interface for LDAPResponseQueue and LDAPSearchQueue.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html">
            com.novell.ldap.LDAPMessageQueue</a>
 */
public interface LDAPMessageQueue
{
   /**
    * Returns the message IDs for all outstanding requests.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#getMessageIDs()">
            com.novell.ldap.LDAPMessageQueue.getMessageIDs()</a>
    */
   public int[] getMessageIDs();

   /**
    * Returns the response from an LDAP request.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#getResponse()">
            com.novell.ldap.LDAPMessageQueue.getResponse()</a>
    */
   public LDAPMessage getResponse()
            throws LDAPException;

   /**
    * Returns the response from an LDAP request for a particular msg id.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#getResponse(int)">
            com.novell.ldap.LDAPMessageQueue.getResponse(int)</a>
    */
   public LDAPMessage getResponse(int msgid)
            throws LDAPException;

   /**
    * Reports whether a response has been received from the server.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#isResponseReceived()">
            com.novell.ldap.LDAPMessageQueue.isResponseReceived()</a>
    */
   public boolean isResponseReceived();

   /**
    * Reports whether a response has been received from the server for
    * a particular msg id.
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#isResponseReceived(int)">
            com.novell.ldap.LDAPMessageQueue.isResponseReceived(int)</a>
    */
   public boolean isResponseReceived(int msgid);

   /**
    * Merges two response queues by moving the current and
    *
    * @see <a href="../../../../api/com/novell/ldap/LDAPMessageQueue.html#merge(com.novell.ldap.LDAPMessageQueue)">
            com.novell.ldap.LDAPMessageQueue.merge(LDAPMessageQueue)</a>
    */
   public void merge(LDAPMessageQueue queue2);
}

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
 *  The common interface for LDAPResponseListener and LDAPSearchListener.
 *
 * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html">
            com.novell.ldap.LDAPListener</a>
 */
public interface LDAPListener
{
   /**
    * Returns the message IDs for all outstanding requests.
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #getMessageIDs()">
            com.novell.ldap.LDAPListener.getMessageIDs()</a>
    */
   public int[] getMessageIDs();

   /**
    * Returns the response from an LDAP request.
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #getResponse()">
            com.novell.ldap.LDAPListener.getResponse()</a>
    */
   public LDAPMessage getResponse()
            throws LDAPException;

   /**
    * Returns the response from an LDAP request for a particular msg id.
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #getResponse(int)">
            com.novell.ldap.LDAPListener.getResponse(int)</a>
    */
   public LDAPMessage getResponse(int msgid)
            throws LDAPException;

   /**
    * Reports whether a response has been received from the server.
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #isResponseReceived()">
            com.novell.ldap.LDAPListener.isResponseReceived()</a>
    */
   public boolean isResponseReceived();

   /**
    * Reports whether a response has been received from the server for
    * a particular msg id.
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #isResponseReceived(int)">
            com.novell.ldap.LDAPListener.isResponseReceived(int)</a>
    */
   public boolean isResponseReceived(int msgid);

   /**
    * Merges two response listeners by moving the current and
    *
    * @see <a href="../../../../doc/com/novell/ldap/LDAPListener.html
            #merge(com.novell.ldap.LDAPResponseListener)">
            com.novell.ldap.LDAPListener.merge(LDAPResponseListener)</a>
    */
   public void merge(LDAPResponseListener listener2);
}

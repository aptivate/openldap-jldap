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

/**
 *  This interface
 *  has been renamed to LDAPMessageQueue in IETF draft 17 of the Java LDAP API
 *  (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
 *  in the fall of 2003.
 *
 *  <p>The common interface for {@link LDAPResponseListener} and
 *  {@link LDAPSearchListener}.  It represents a queue of incoming
 *  asynchronous messages from the server.</p>
 *
 *  @deprecated replaced by {@link LDAPMessageQueue}.
 */
public interface LDAPListener
{
   public int[] getMessageIDs();

   public LDAPMessage getResponse() throws LDAPException;

   public LDAPMessage getResponse(int msgid) throws LDAPException;

   public boolean isResponseReceived();

   public boolean isResponseReceived(int msgid);

   public void merge(LDAPListener listener2);
}

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
 *  Encapsulates a low-level mechanism for processing asynchronous messages
 *  received from a server.
 *
 *  @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html">
            com.novell.ldap.LDAPResponseListener</a>
 */
public class LDAPResponseListener implements LDAPListener
{
    private com.novell.ldap.LDAPResponseListener listener;

    /**
     * Constructs a response listener from com.novell.ldap.LDAPResponseListener
     */
    /* package */
    LDAPResponseListener(com.novell.ldap.LDAPResponseListener listener)
    {
        this.listener = listener;
        return;
    }

    /**
     * Returns the com.novell.ldap.LDAPResponseListener object
     */
    /* package */
    com.novell.ldap.LDAPResponseListener getWrappedObject()
    {
        return listener;
    }

    /**
     * Returns the message IDs for all outstanding requests.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #getMessageIDs()">
            com.novell.ldap.LDAPResponse.getMessageIDs()</a>
     */
     public int[] getMessageIDs()
     {
        return listener.getMessageIDs();
     }

    /**
     * Reports whether a response has been received from the server.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #isResponseReceived()">
            com.novell.ldap.LDAPResponse.isResponseReceived()</a>
     */
     public boolean isResponseReceived()
     {
        return listener.isResponseReceived();
     }

    /**
     * Reports whether a response has been received from the server for a
     * particular message id.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #isResponseReceived(int)">
            com.novell.ldap.LDAPResponse.isResponseReceived(int)</a>
     */
     public boolean isResponseReceived(int msgid)
     {
        return listener.isResponseReceived( msgid);
     }

    /**
     * Merges two response listeners by moving the contents from another
     * listener to this one.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #merge(com.novell.ldap.LDAPResponseListener)">
            com.novell.ldap.LDAPResponse.merge(LDAPResponseListener)</a>
     */
     public void merge(LDAPResponseListener listener2)
     {
        if( listener2 == null) {
            listener.merge( (com.novell.ldap.LDAPResponseListener)null);
        }
        listener.merge( listener2.getWrappedObject() );
        return;
     }

    /**
     * Returns the response.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #getResponse()">
            com.novell.ldap.LDAPResponse.getResponse()</a>
     */
    public LDAPMessage getResponse()
        throws LDAPException
    {
        try {
            return new LDAPMessage(listener.getResponse());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                            (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Returns the response for a particular message id.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPResponseListener.html
            #getResponse(int)">
            com.novell.ldap.LDAPResponse.getResponse(int)</a>
     */
    public LDAPMessage getResponse(int msgid)
        throws LDAPException
    {
        try {
            return new LDAPMessage(listener.getResponse(msgid));
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                            (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchListener.java,v 1.29 2001/05/02 18:04:03 vtag Exp $
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

package org.ietf.ldap;

/**
 *  A low-level mechanism for queuing asynchronous search results
 *  and references received from a server.
 *
 * @see com.novell.ldap.LDAPSearchListener
 */
public class LDAPSearchListener implements LDAPListener
{
    private com.novell.ldap.LDAPSearchListener listener;

    /**
     * Constructs a response listener from a com.novell.ldap.LDAPSearchListener
     */
    /* package */
    LDAPSearchListener(com.novell.ldap.LDAPSearchListener listener)
    {
        this.listener = listener;
        return;
    }

    /**
     * Returns a com.novell.ldap.LDAPSearchListener object
     */
    com.novell.ldap.LDAPSearchListener getWrappedObject()
    {
        return listener;
    }

   /**
    * Returns the message IDs for all outstanding requests.
    *
    * @see com.novell.ldap.LDAPSearchListener#getMessageIDs()
    */
    public int[] getMessageIDs()
    {
        return listener.getMessageIDs();
    }

   /**
    * Reports whether a response has been received from the server and
    * not yet retrieved with getResponse.
    *
    * @see com.novell.ldap.LDAPSearchListener#isResponseReceived()
    */
    public boolean isResponseReceived()
    {
        return listener.isResponseReceived();
    }

   /**
    * Reports whether a response has been received from the server and
    * not yet retrieved with getResponse.
    *
    * @see com.novell.ldap.LDAPSearchListener#isResponseReceived(int)
    */
    public boolean isResponseReceived(int msgid)
    {
        return listener.isResponseReceived(msgid);
    }

   /**
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @see com.novell.ldap.LDAPSearchListener#merge(LDAPResponseListener)
    */
    public void merge(LDAPResponseListener listener2)
    {
        if( listener2 == null) {
            listener.merge( (com.novell.ldap.LDAPResponseListener)null);
        }
        listener.merge( listener2.getWrappedObject());
        return;
    }

    /**
     * Reports true if all results have been received for a particular
     * message id.
     *
     * @see com.novell.ldap.LDAPSearchListener#isComplete(int)
     */
    public boolean isComplete( int msgid )
    {
        return listener.isComplete( msgid);
    }

    /**
     * Blocks until a response is available, or until all operations
     * associated with the object have completed or been canceled, and
     * returns the response.
     *
     * @see com.novell.ldap.LDAPSearchListener#getResponse(int)
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
     * Blocks until a response is available for a particular message id,
     * or until all operations
     * associated with the object have completed or been canceled, and
     * returns the response.
     *
     * @see com.novell.ldap.LDAPSearchListener#getResponse(int)
     */
    public LDAPMessage getResponse(int msgid)
            throws LDAPException
    {
        try {
            return new LDAPMessage(listener.getResponse( msgid));
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

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPResponseListener.java,v 1.18 2000/11/08 22:41:32 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/

package com.novell.ldap;

import java.util.Vector;

import com.novell.ldap.client.*;
import com.novell.ldap.protocol.*;

/**
 *  Encapsulates a low-level mechanism for processing asynchronous messages 
 *  received from a server.  It
 *  represents the message queue associated with a particular asynchronous LDAP
 *  operation or operations.
 */
public class LDAPResponseListener implements LDAPListener
{

   /**
    * The client listener object
    */
    private ClientListener listen;
    
    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param listen The client listener to associate with this conneciton
     */
    /* package */
    LDAPResponseListener(ClientListener listen)
    {
        this.listen = listen;
        return;    
    }

   /**
    * Returns the internal client listener object
    *
    * @return The internal client listener object
    */
    /* package */
    ClientListener getClientListener()
    {
        return listen;
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
        return listen.getMessageIDs();
    }
    
   /**
    * Reports whether a response has been received from the server.
    *
    * @return True if a response has been received from the server; false if
    *         a response has not been received. 
    */
    public boolean isResponseReceived()
    {
        return listen.isResponseReceived();
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
        throw new RuntimeException("LDAPSearchListener.isResponseRecieved(msgid) is not implemented");
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
        listen.merge( listener2 );
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
    * @return The response.
    *
    * @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
   public LDAPMessage getResponse()
        throws LDAPException
    {
        LDAPResponse response;
        RfcLDAPMessage message = listen.getLDAPMessage();
        if(message.getProtocolOp() instanceof ExtendedResponse)
            response = new LDAPExtendedResponse(message);
        else
            response = new LDAPResponse(message);
        listen.removeMessageID(response.getMessageID());
        return (LDAPMessage)response;
   }

   /**
    * Returns the response for a particular message id.
    *
    * <p>The getResponse method locks until a response is available, or until all 
    * operations associated with the object have completed or been canceled, and 
    * then returns the response. The client is responsible for processing
    * the responses returned from a listener.</p>
    *
    * @return The response.
    *
    * @exception LDAPException A general exception which includes an error
    *  message and an LDAP error code.
    */
   public LDAPMessage getResponse(int msgid)
        throws LDAPException
   {
        throw new RuntimeException("LDAPSearchListener.getResponse(msgid) is not implemented");
   }

}


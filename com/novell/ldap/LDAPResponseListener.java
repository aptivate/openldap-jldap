/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPResponseListener.java,v 1.12 2000/10/02 19:46:52 judy Exp $
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

/*
 * 4.28 public class LDAPResponseListener
 */
 
/**
 *  Represents the message queue associated with a particular asynchronous LDAP
 *  operation or operations.
 */
public class LDAPResponseListener
{

   /**
    * The client listener object
    */
    private LDAPListener listen;
    
    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param conn The connection for the listener.
     */
    /* package */
    LDAPResponseListener(LDAPListener listen)
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
    LDAPListener getClientListener()
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
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @param listener2 The listener that receives the contents from the
    *                  other listener.
    */
    public void merge(LDAPResponseListener listener2)
    {
        listen.merge( listener2 );
        return;
    }
   /*
    * 4.28.2 getResponse
    */

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
   public LDAPResponse getResponse()
        throws LDAPException
    {
        LDAPResponse response;
        com.novell.ldap.protocol.LDAPMessage message = listen.getLDAPMessage();
        if(message.getProtocolOp() instanceof ExtendedResponse)
            response = new LDAPExtendedResponse(message);
        else
            response = new LDAPResponse(message);
        listen.removeMessageID(response.getMessageID());
        return response;
   }

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchListener.java,v 1.15 2000/10/03 21:44:00 vtag Exp $
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

import com.novell.ldap.client.*;
import java.util.Vector;

import com.novell.ldap.protocol.*;

/*
 * 4.32 public class LDAPSearchListener
 */
 
/**
 *
 *  Manages search results and references returned
 *  from one or more search requests.
 */
public class LDAPSearchListener implements LDAPListener
{
 
   /**
    * The client listener object
    */
    private ClientListener listen;
    
    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param conn The connection for the listener.
     */
    /* package */
    LDAPSearchListener(ClientListener listen)
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
    * Merges two response listeners by moving the contents from another
    * listener to this one.
    *
    * @param listener2 The listener that receives the contents from the
    *                  other listener.
    */
    public void merge(LDAPResponseListener listener2)
    {
        listen.merge( listener2);
        return;
    }
   /*
    * 4.32.2 getResponse
    */

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response. 
    *
    * <p>The response may be a search result, a search
    * reference, a search response, or null (if there are no more
    * outstanding requests). LDAPException is thrown on network errors.</p>
    *
    * <p>The only time this method should return a null is if there is no
    * response in the message queue and there are no message IDs pending.</p>
    *
    * @return The response (a search result, search reference, or search response)or
    *         null if there are no more outstanding requests.
    *
    * @exception LDAPException A general exception which includes an error 
    *                          message and an LDAP error code.
    */
   public LDAPMessage getResponse()
      throws LDAPException
   {
      LDAPMessage message;
      com.novell.ldap.protocol.LDAPMessage msg = listen.getLDAPMessage(); // blocks

      if(msg == null)
         return null;

      if(msg.getProtocolOp() instanceof SearchResultEntry) {
         message = new LDAPSearchResult(msg);
      }
      else if(msg.getProtocolOp() instanceof SearchResultReference) {
         message = new LDAPSearchResultReference(msg);
      }
      else { // must be SearchResultDone
         message = new LDAPResponse(msg);
         listen.removeMessageID(message.getMessageID());
      }

      // network error exceptions... (LDAP_TIMEOUT for example)
      LDAPException e = listen.getException();
      if( e != null )
         throw e;

      return message;
   }
}

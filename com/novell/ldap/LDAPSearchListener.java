/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchListener.java,v 1.12 2000/09/11 21:05:51 vtag Exp $
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
 *  on one or more search requests.
 */
public class LDAPSearchListener extends LDAPListener {
 
   /**
    * Constructs an LDAPSearchListener for the specified connection.
    *
    * @param conn The connection to the LDAP server.
    */
   public LDAPSearchListener(Connection conn)
   {
      this.conn = conn;
      this.queue = new LDAPMessageQueue();
      this.exceptions = new Vector(5);
      conn.addLDAPListener(this);
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
    * @return The response (a search result, search reference, or search response)
    *
    * @exception LDAPException A general exception which includes an error 
    *                          message and an LDAP error code.
    */
   public LDAPMessage getResponse()
      throws LDAPException
   {
      LDAPMessage message;
      com.novell.ldap.protocol.LDAPMessage msg = queue.getLDAPMessage(); // blocks

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
         queue.removeMessageID(message.getMessageID());
      }

      // network error exceptions... (LDAP_TIMEOUT for example)
      if(!exceptions.isEmpty()) {
         LDAPException e = (LDAPException)exceptions.firstElement();
         exceptions.removeElementAt(0);
         throw e;
      }

      return message;
   }

}


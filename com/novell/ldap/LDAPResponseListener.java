/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPResponseListener.java,v 1.10 2000/09/11 22:47:50 judy Exp $
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
 *  Represents the message queue associated with a particular LDAP
 *  operation or operations.
 */
public class LDAPResponseListener extends LDAPListener {

    /**
     * Constructs a response listener on the specific connection.
     *
     *  @param conn The connection for the listener.
     */
    public LDAPResponseListener(Connection conn)
    {
        this.conn = conn;
        this.queue = new LDAPMessageQueue();
        this.exceptions = new Vector(5);
        conn.addLDAPListener(this);
    }

   /*
    * 4.28.2 getResponse
    */

   /**
    * Returns the response.
    *
    * <p>Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and then
    * returns the response. It is the responsibility of the client to
    * process the responses returned from a listener.</p>
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
        com.novell.ldap.protocol.LDAPMessage message = queue.getLDAPMessage();
        if(message.getProtocolOp() instanceof ExtendedResponse)
            response = new LDAPExtendedResponse(message);
        else
            response = new LDAPResponse(message);
        queue.removeMessageID(response.getMessageID());
        return response;
   }

}


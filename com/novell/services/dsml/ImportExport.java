/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
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

package com.novell.services.dsml;

import com.novell.ldap.*;
import com.novell.ldap.connectionpool.*;
import com.novell.ldap.util.*;

import java.io.IOException;

/**
 * Class that provides utility functions for processing LDAP requests.
 */
public class ImportExport
{
    private ImportExport()
    {
        // Don't allow an instance of this class
        return;
    }

    /**
     * Processes LDAP requests from an LDAPReader class.
     *
     * <p>Processes requests from an LDAPReader class, submits the requests
     * to an LDAPConnection class (obtained from the connection pool),
     * and writes the responses to an LDAPWriter class.</p>
     *
     * @param reqAuth the class that encapsulates the authentication credentials
     * @param connPool a connection pool used to obtain an connection
     * @param reqDsmlRdr the LDAPReader class that supplies the LDAP requests
     * @param rspDsmlWtr the LDAPWriter class that accepts the LDAP responses
     */
    static public void process(Authorization reqAuth,
                               PoolManager connPool,
                               LDAPReader reqDsmlRdr,
                               LDAPWriter rspDsmlWtr)
            throws InterruptedException, IOException
    {
        LDAPConnection ldapConn = null;
        LDAPMessage reqLdapMsg, rspLdapMsg;

        try{
            ldapConn = connPool.getBoundConnection(
                    reqAuth.getDN(), reqAuth.getPasswordBytes());
            if(null == ldapConn){
                throw new LDAPException(
                        "DSML server busy, exceeded max connections",
                        LDAPException.BUSY, "");
            }
            // Convert DSML batch request into LDAPMessage request.
            reqLdapMsg = reqDsmlRdr.readMessage();
            // Process LDAP message.
            while( reqLdapMsg != null )
            {
                // Send LDAPMessage request to LDAP server.
                LDAPMessageQueue rspLdapMsgQ = ldapConn.sendRequest( reqLdapMsg, null, null);
                // Read the response from the LDAP server and convert and write
                // it to the response stream.
                while (( rspLdapMsg = rspLdapMsgQ.getResponse()) != null )
                {
                    rspDsmlWtr.writeMessage( rspLdapMsg );
                }
                reqLdapMsg = reqDsmlRdr.readMessage();
            }
        } catch (LDAPException le){
            /* LDAP errors are standard DSML errors.  All other errors are
               returned as SOAP faults */
            rspDsmlWtr.writeError(le);
        } finally {
            //Finish converting DSML batch response
            rspDsmlWtr.finish();
        }
        // Free Connection
        if (ldapConn != null){
            connPool.makeConnectionAvailable(ldapConn);
            ldapConn = null;
        }
        return;
    }
}

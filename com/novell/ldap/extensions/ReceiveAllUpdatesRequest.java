/* **************************************************************************
 * $Id: ReceiveAllUpdatesRequest.java,v 1.2 2000/07/27 18:08:13 javed Exp $
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
package com.novell.ldap.ext; 

import com.novell.ldap.*;
import com.novell.ldap.client.protocol.lber.*;
import java.io.IOException;
 
/**
 *  public class ReceiveAllUpdatesRequest
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to schedule a specified directory server to receive
 *  updates from another directory server for a specific partition.
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.21"
 *
 *  The RequestValue has the folling ASN:
 *
 *  requestValue ::=
 *          partitionRoot   LDAPDN
 *          toServerDN      LDAPDN
 *          fromServerDN    LDAPDN
 */
public class ReceiveAllUpdatesRequest extends LDAPExtendedOperation {
   
/**
 *  public ReceiveAllUpdatesRequest()
 *
 *      The constructor takes four parameters:
 *
 *      String partitionRoot:   Specify the distinguished name of the replica
 *                              that will be updated
 *
 *      String toServerDN:      The server holding the replica to be updated
 *      
 *      String fromServerDN:    The server from which updates are sent out.
 *
 */   
 public ReceiveAllUpdatesRequest(String partitionRoot, String toServerDN, String fromServerDN) 
                throws LDAPException {
        
        super(NamingContextConstants.RECEIVE_ALL_UPDATES_REQ, null);
        
        try {
            // ber encode the parameters and set the requestValue
            LberEncoder requestlber = new LberEncoder();
            
            if ( (partitionRoot == null) || (toServerDN == null) || (fromServerDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
				                        
            requestlber.encodeString(partitionRoot, true);            
            requestlber.encodeString(toServerDN, true);
            requestlber.encodeString(fromServerDN, true);
                    
            setValue(requestlber.getTrimmedBuf());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

/* **************************************************************************
 * $Id: SendAllUpdatesRequest.java,v 1.2 2000/07/27 18:08:13 javed Exp $
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
 *  public class SendAllUpdatesRequest
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to schedule an updated request to be sent to all
 *  directory servers in a partition ring.
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.23"
 *
 *  The RequestValue has the folling ASN:
 *
 *  requestValue ::=
 *          partitionRoot   LDAPDN
 *          origServerDN    LDAPDN
 */
public class SendAllUpdatesRequest extends LDAPExtendedOperation {
   
/**
 *  public SendAllUpdatesRequest()
 *
 *      The constructor takes four parameters:
 *
 *      String partitionRoot:   Specify the distinguished name of the replica
 *                              that will be updated
 *
 *      String origServerDN:      The server holding the replica to be updated
 *
 */   
 public SendAllUpdatesRequest(String partitionRoot, String origServerDN) 
                throws LDAPException {
        
        super(NamingContextConstants.SEND_ALL_UPDATES_REQ, null);
        
        try {
            // ber encode the parameters and set the requestValue
            LberEncoder requestlber = new LberEncoder();
            
            if ( (partitionRoot == null) || (origServerDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
				                        
            requestlber.encodeString(partitionRoot, true);            
            requestlber.encodeString(origServerDN, true);
                    
            setValue(requestlber.getTrimmedBuf());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

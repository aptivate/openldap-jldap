/* **************************************************************************
 * $Id: ReceiveAllUpdatesRequest.java,v 1.3 2000/08/04 15:32:31 javed Exp $
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

import org.ietf.ldap.*;
import org.ietf.asn1.*;
import java.io.*;
 
/**
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to schedule a specified directory server to receive
 *  updates from another directory server for a specific partition.
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.21"
 *
 *  The RequestValue has the folling ASN:
 *
 *  requestValue ::=<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;     partitionRoot   LDAPDN<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;     toServerDN      LDAPDN<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;     fromServerDN    LDAPDN<br>
 */
public class ReceiveAllUpdatesRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes four parameters:<br><br>
 *
 *      String partitionRoot:   Specify the distinguished name of the replica
 *                              that will be updated<br><br>
 *
 *      String toServerDN:      The server holding the replica to be updated<br><br>
 *      
 *      String fromServerDN:    The server from which updates are sent out.<br><br>
 *
 */   
 public ReceiveAllUpdatesRequest(String partitionRoot, String toServerDN, String fromServerDN) 
                throws LDAPException {
        
        super(NamingContextConstants.RECEIVE_ALL_UPDATES_REQ, null);
        
        try {
            
            if ( (partitionRoot == null) || (toServerDN == null) || (fromServerDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);	                        
            
            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                                                    
		    ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
		    ASN1OctetString asn1_toServerDN = new ASN1OctetString(toServerDN);
		    ASN1OctetString asn1_fromServerDN = new ASN1OctetString(fromServerDN);
            
            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_toServerDN.encode(encoder, encodedData);
            asn1_fromServerDN.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

/* **************************************************************************
 * $Id: ListReplicasRequest.java,v 1.6 2000/08/08 21:28:50 javed Exp $
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
 *
 *      This class is used to list all the replicas that reside on the
 *  the specified directory server.  To list replicas create an instance
 *  of this class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter<br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.19"<br><br>
 *
 *  The RequestValue has the folling ASN:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    serverName  LDAPDN<br>
 */
public class ListReplicasRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes four parameters:<br><br>
 *
 * @param serverName Points to the server on which the replica will be added<br><br>
 *      
 */   
 public ListReplicasRequest(String serverName) 
                throws LDAPException {
        
        super(NamingContextConstants.LIST_REPLICAS_REQ, null);
        
        try {
            
            if (serverName == null)
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
			
			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                                                 
		    ASN1OctetString asn1_serverName = new ASN1OctetString(serverName);
            
            asn1_serverName.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

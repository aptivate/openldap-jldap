/* **************************************************************************
 * $Id: GetReplicaInfoRequest.java,v 1.6 2000/08/08 21:28:50 javed Exp $
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
 *      This class is used to read other interesting information 
 *  about a replica.  To read other information about a replica 
 *  create an instance of this class and then call the 
 *  extendedOperation method with this object as the required 
 *  LDAPExtendedOperation parameter<br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.17"<br><br>
 *
 *  The RequestValue has the folling ASN:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    serverDN      LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    partitionDN   LDAPDN<br>    
 */
public class GetReplicaInfoRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes two parameters:<br><br>
 *
 * @param serverDN Points to the server on which the replica tobe read
 * resides.<br><br>
 *      
 * @param partitionDN The distinguished name of the replica to be read.<br><br>
 *
 */   
 public GetReplicaInfoRequest(String serverDN, String partitionDN) 
                throws LDAPException {
        
        super(NamingContextConstants.GET_REPLICA_INFO_REQ, null);
        
        try {
            
            if ( (serverDN == null) || (partitionDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
			
			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                                                 
		    ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
		    ASN1OctetString asn1_partitionDN = new ASN1OctetString(partitionDN);
            
            asn1_serverDN.encode(encoder, encodedData);
            asn1_partitionDN.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

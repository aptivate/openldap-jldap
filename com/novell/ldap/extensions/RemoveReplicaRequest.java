/* **************************************************************************
 * $Id: RemoveReplicaRequest.java,v 1.5 2000/08/08 21:28:52 javed Exp $
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
import com.novell.asn1.*;
import java.io.*;
 
/**
 *
 *      This class is used to remove a replica from the specified directory 
 *  server. To remove a replica from a particular server create an instance
 *  of this class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.11"<br><br>
 *
 *  The RequestValue has the folling ASN:<br><br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        flags       INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        serverName  LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        dn          LDAPDN<br>
 */
public class RemoveReplicaRequest extends LDAPExtendedOperation {
   
/**
 *      The constructor takes three parameters:<br><br>
 *
 * @param dn:          Specify the distinguished name of the replicas
 *                          partition root<br><br>
 *
 * @param serverDN:    Points to the server from which the replica
 * will be removed<br><br>
 *      
 * @param flags:       Specifies if all servers in the replica ring must 
 * be up before proceeding.  Set to LDAP_ENSURE_SERVERS_UP field defined in the 
 * NamingContextConstants class.<br><br>
 */   
 public RemoveReplicaRequest(String dn, String serverDN, int flags) 
                throws LDAPException {
        
        super(NamingContextConstants.DELETE_REPLICA_REQ, null);
        
        try {
            
            if ( (dn == null) || (serverDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
			
		    ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                                     
		    ASN1Integer asn1_flags = new ASN1Integer(flags);
		    ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
		    ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_flags.encode(encoder, encodedData);
            asn1_serverDN.encode(encoder, encodedData);
            asn1_dn.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

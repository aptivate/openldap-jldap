/* **************************************************************************
 * $Id: AbortNamingContextOperationRequest.java,v 1.7 2000/08/21 18:35:44 vtag Exp $
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
import com.novell.asn1.*;
import java.io.*;
 
/**
 *
 *      This class is used to abort the last naming context operation that 
 *  was requested on the specified naming context (assuming it is
 *  still pending). <br><br>
 *  
 *  To add a replica to a particular server create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.<br><br>
 *  
 *  The OID used for this extended operation is: "2.16.840.1.113719.1.27.100.7"<br><br>
 *  
 *  The RequestValue has the folling ASN:<br><br>
 *  
 *  requestValue ::= <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    flags          INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    partitionDN     LDAPDN
 */
public class AbortNamingContextOperationRequest extends LDAPExtendedOperation {
   
/**
 * The constructor takes two parameters:<br><br>
 *
 * @param partitionDN Specify the distinguished name of the replicas 
 * partition root.<br><br>
 *
 * @param flags Specifies if all servers in the replica ring must 
 * be up before proceeding.  Set to LDAP_ENSURE_SERVERS_UP field defined 
 * in the NamingContextConstants class.
 */   
 public AbortNamingContextOperationRequest(String partitionDN, int flags) 
                throws LDAPException {
        
        super(NamingContextConstants.ABORT_NAMING_CONTEXT_OP_REQ, null);
        
        try {
            
            if (partitionDN == null)
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
			
			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                               
                                   
		    ASN1Integer asn1_flags = new ASN1Integer(flags);
		    ASN1OctetString asn1_partitionDN = new ASN1OctetString(partitionDN);
            
            asn1_flags.encode(encoder, encodedData);
            asn1_partitionDN.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

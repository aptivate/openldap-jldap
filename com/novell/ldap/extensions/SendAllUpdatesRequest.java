/* **************************************************************************
 * $Id: SendAllUpdatesRequest.java,v 1.4 2000/08/08 16:58:43 javed Exp $
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
 *      This class is used to schedule an updated request to be sent to all
 *  directory servers in a partition ring.<br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.23"<br>
 *
 *  The RequestValue has the folling ASN:<br><br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;partitionRoot   LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;origServerDN    LDAPDN<br>
 */
public class SendAllUpdatesRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes four parameters:<br><br>
 *
 * @param partitionRoot Specify the distinguished name of the replica
 * that will be updated<br><br>
 *
 * @param origServerDN      The server holding the replica to be updated
 *
 */   
 public SendAllUpdatesRequest(String partitionRoot, String origServerDN) 
                throws LDAPException {
        
        super(NamingContextConstants.SEND_ALL_UPDATES_REQ, null);
        
        try {
            
            if ( (partitionRoot == null) || (origServerDN == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			BEREncoder encoder  = new BEREncoder();
                                                    
		    ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
		    ASN1OctetString asn1_origServerDN = new ASN1OctetString(origServerDN);

            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_origServerDN.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());

        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

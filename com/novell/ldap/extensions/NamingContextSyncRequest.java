/* **************************************************************************
 * $Id: NamingContextSyncRequest.java,v 1.9 2000/09/11 21:05:58 vtag Exp $
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
package com.novell.ldap.extensions; 

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import java.io.*;
 
/**
 *
 *      This class is used to synchronize all replicas in a partition ring<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.25"<br><br>
 *
 *  The RequestValue has the following ASN:<br><br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    serverName      LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    partitionRoot   LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    delay           INTEGER<br>
 */
public class NamingContextSyncRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes four parameters:<br><br>
 *
 * @param serverName     The server to sync from<br><br>
 *
 * @param partitionRoot   Specify the distinguished name of the replica
 *                              that will be synchronized<br><br>
 *
 * @param delay              The time in seconds after which the synchronization 
 *                              should start.
 *
 */   
 public NamingContextSyncRequest(String serverName, String partitionRoot, int delay) 
                throws LDAPException {
        
        super(NamingContextConstants.NAMING_CONTEXT_SYNC_REQ, null);
        
        try {
            
            if ( (serverName == null) || (partitionRoot == null) )
                throw new LDAPException("Invalid parameter",
                                    LDAPException.PARAM_ERROR);
         
         ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();
                                                    
          ASN1OctetString asn1_serverName = new ASN1OctetString(serverName);
          ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
          ASN1Integer asn1_delay = new ASN1Integer(delay);
            
            asn1_serverName.encode(encoder, encodedData);
            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_delay.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
             
        }
      catch(IOException ioe) {
         throw new LDAPException("Encoding Error",
                                 LDAPException.ENCODING_ERROR);
      }
   }

}

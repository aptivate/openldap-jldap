/* **************************************************************************
 * $Id: GetReplicaInfoRequest.java,v 1.6 2000/09/25 17:36:41 fzhao Exp $
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
 *  Reads information about a replica.
 *
 *  <p>To read other information about a replica, you must 
 *  create an instance of this class and then call the 
 *  extendedOperation method with this object as the required 
 *  LDAPExtendedOperation parameter</p>
 *
 *  <p>The GetReplicaInfoRequest operation uses the following OID:<br>
 *   &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.17</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; serverDN&nbsp;&nbsp;&nbsp;     LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; partitionDN&nbsp;&nbsp;&nbsp;  LDAPDN</p>    
 */
public class GetReplicaInfoRequest extends LDAPExtendedOperation {
   
/**
 *
 * Constructs an extended operations object for reading replica information.
 *
 * @param serverDN The server on which the replica resides.
 * <br><br>     
 * @param partitionDN The distinguished name of the replica to be read.
 *
 * @exception LDAPException A general exception which includes an error 
 *                          message and an LDAP error code.
 */   
 public GetReplicaInfoRequest(String serverDN, String partitionDN) 
                throws LDAPException {
        
        super(NamingContextConstants.GET_REPLICA_INFO_REQ, null);
        
        try {
            
            if ( (serverDN == null) || (partitionDN == null) )
                throw new LDAPException("Invalid parameter",
                                    LDAPException.PARAM_ERROR);
         
         ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();
                                                 
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

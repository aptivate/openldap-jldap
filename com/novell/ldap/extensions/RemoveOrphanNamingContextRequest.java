/* **************************************************************************
 * $Id: RemoveOrphanNamingContextRequest.java,v 1.9 2000/09/11 21:05:58 vtag Exp $
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
 *      This class is used to delete an orphan partition.
 *  To delete an orphan partition create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.41"<br><br>
 *
 *  The RequestValue has the following ASN:<br><br>
 *
 *  requestValue ::=
 * &nbsp;&nbsp;&nbsp;&nbsp;        serverDN    LDAPDN
 * &nbsp;&nbsp;&nbsp;&nbsp;        contextName LDAPDN
 */
public class RemoveOrphanNamingContextRequest extends LDAPExtendedOperation {
   
/**
 *      The constructor takes two parameters:<br><br>
 *
 * @param serverDN:    Specify the distinguished name of the 
 * server on which the orphan parition resides.<br><br>
 *
 * @param contextName: Specifies the distinguished name of the 
 * orphan partition to delete.<br><br>
 */   
 public RemoveOrphanNamingContextRequest(String serverDN, String contextName) 
                throws LDAPException {
        
        super(NamingContextConstants.REMOVE_ORPHAN_NAMING_CONTEXT_REQ, null);
        
        try {
            
            if ( (serverDN == null) || (contextName == null) )
                throw new LDAPException("Invalid parameter",
                                    LDAPException.PARAM_ERROR);
            
            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();
                               
          ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
          ASN1OctetString asn1_contextName = new ASN1OctetString(contextName);
            
            asn1_serverDN.encode(encoder, encodedData);
            asn1_contextName.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
      catch(IOException ioe) {
         throw new LDAPException("Encoding Error",
                                 LDAPException.ENCODING_ERROR);
      }
   }

}

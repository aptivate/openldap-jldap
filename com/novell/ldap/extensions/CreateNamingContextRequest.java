/* **************************************************************************
 * $Id: CreateNamingContextRequest.java,v 1.14 2000/09/25 17:33:03 fzhao Exp $
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
 *  Creates a new naming context (or in NDS terminology, a new partition).
 *
 *  <p>To create a new naming context, you must create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The create naming context request uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.3</p>
 *
 *  <p>The request value has the following ASN.1 format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    flags   INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn      LDAPDN</p>
 */
public class CreateNamingContextRequest extends LDAPExtendedOperation {
   
/**
 *
 *  Constructs an extended operation object for creating a naming context.
 *
 * @param dn      The distinguished name of the container where the new naming 
 *                context root should be located.
 *<br><br>
 * @param flags Specifies whether all servers in the replica ring must be up before 
 *              proceeding. When set to zero, the status of the servers is not 
 *              checked. When set to LDAP_ENSURE_SERVERS_UP, all servers must be up  
 *              for the operation to proceed.
 *
 * @exception LDAPException A general exception which includes an error message 
 *                          and an LDAP error code.
 */   
 public CreateNamingContextRequest(String dn, int flags) 
                throws LDAPException {
        
        super(NamingContextConstants.CREATE_NAMING_CONTEXT_REQ, null);
        
        try {
            
            if (dn == null)
                throw new LDAPException("Invalid parameter",
                                    LDAPException.PARAM_ERROR);
                                    
            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();
                                     
          ASN1Integer asn1_flags = new ASN1Integer(flags);
          ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_flags.encode(encoder, encodedData);
            asn1_dn.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
            
        }
      catch(IOException ioe) {
         throw new LDAPException("Encoding Error",
                                 LDAPException.ENCODING_ERROR);
      }
   }

}

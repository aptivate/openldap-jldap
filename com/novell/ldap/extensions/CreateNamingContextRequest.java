/* **************************************************************************
 * $Id: CreateNamingContextRequest.java,v 1.2 2000/07/26 21:33:18 javed Exp $
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
import com.novell.ldap.client.protocol.lber.*;
import java.io.IOException;
 
/**
 *  public class CreateNamingContextRequest
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to create a new naming context. (NDS partition).
 *  To create a new naming context create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.3"
 *
 *  The RequestValue has the folling ASN:
 *
 *  requestValue ::=
 *          flags   INTEGER
 *          dn      LDAPDN
 */
public class CreateNamingContextRequest extends LDAPExtendedOperation {
   
    private static final String requestOID  = "2.16.840.1.113719.1.27.100.3";
    private static final String respOID  = "2.16.840.1.113719.1.27.100.4";

/**
 *  public CreateNamingContextRequest()
 *
 *      The constructor takes two parameters:
 *
 *      String dn:  Specify the distinguished name of the container
 *                  where the new naming context root should be located.
 *
 *      int flags:  Specifies if all servers in the replica ring must be
 *                  up before proceeding.  Set to LDAP_ENSURE_SERVERS_UP 
 *                  field defined in the NamingContextConstants class .
 */   
 public CreateNamingContextRequest(String dn, int flags) 
                throws LDAPException {
        
        super(requestOID, null);
        
        try {
            // ber encode the parameters and set the requestValue
            LberEncoder requestlber = new LberEncoder();
        
            requestlber.encodeInt(flags);
            
            if (dn == null)
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
            
            requestlber.encodeString(dn, true);
                    
            setValue(requestlber.getTrimmedBuf());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

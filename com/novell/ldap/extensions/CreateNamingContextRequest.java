/* **************************************************************************
 * $Id: CreateNamingContextRequest.java,v 1.6 2000/08/01 01:03:31 javed Exp $
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
import java.io.IOException;
 
/**
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to create a new naming context. (NDS partition).
 *  To create a new naming context create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.3"<br><br>
 *
 *  The RequestValue has the folling ASN:<br><br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    flags   INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn      LDAPDN<br>
 */
public class CreateNamingContextRequest extends LDAPExtendedOperation {
   
/**
 *
 *      The constructor takes two parameters:<br><br>
 *
 *      String dn:  Specify the distinguished name of the container
 *                  where the new naming context root should be located.<br><br>
 *
 *      int flags:  Specifies if all servers in the replica ring must be
 *                  up before proceeding.  Set to LDAP_ENSURE_SERVERS_UP 
 *                  field defined in the NamingContextConstants class.<br><br>
 */   
 public CreateNamingContextRequest(String dn, int flags) 
                throws LDAPException {
        
        super(NamingContextConstants.CREATE_NAMING_CONTEXT_REQ, null);
        
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

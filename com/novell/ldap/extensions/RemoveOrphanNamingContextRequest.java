/* **************************************************************************
 * $Id: RemoveOrphanNamingContextRequest.java,v 1.5 2000/07/27 17:50:57 javed Exp $
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
 *  public class RemoveOrphanNamingContextRequest
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to delete an orphan partition.
 *  To delete an orphan partition create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.41"
 *
 *  The RequestValue has the folling ASN:
 *
 *  requestValue ::=
 *          serverDN    LDAPDN
 *          contextName LDAPDN
 */
public class RemoveOrphanNamingContextRequest extends LDAPExtendedOperation {
   
/**
 *  public RemoveOrphanNamingContextRequest()
 *
 *      The constructor takes two parameters:
 *
 *      String serverDN:    Specify the distinguished name of the 
 *                          server on which the orphan parition resides.
 *
 *      String contextName: Specifies the distinguished name of the 
 *                          orphan partition to delete.
 */   
 public RemoveOrphanNamingContextRequest(String serverDN, String contextName) 
                throws LDAPException {
        
        super(NamingContextConstants.REMOVE_ORPHAN_NAMING_CONTEXT_REQ, null);
        
        try {
            // ber encode the parameters and set the requestValue
            LberEncoder requestlber = new LberEncoder();
            
            if ( (serverDN == null) || (contextName == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
            
            requestlber.encodeString(serverDN, true);
            requestlber.encodeString(contextName, true);
                    
            setValue(requestlber.getTrimmedBuf());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
   }

}

/* **************************************************************************
 * $Id: CreateNamingContextRequest.java,v 1.2 2000/03/14 18:17:27 smerrill Exp $
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

import com.novell.ldap.client.protocol.lber.*;
 
/**
 * 4.11 public class CreateNamingContextRequest
 *
 */
public class CreateNamingContextRequest extends LDAPExtendedOperation {
   
    private static final String requestOID  = "";
    private static final String respOID  = "";
    
    protected LberEncoder lber;
    
    public CreateNamingContextRequest(String dn, int flags) 
                throws LDAPException {
        
        super(requestOID, null);
        
        // ber encode the parameters and set the requestValue
        requestlber = new LberEncoder();
        
        lber.encodeInt(flags);
        
        if (dn == null)
            throw new LDAPException("Invalid parameter",
				                     LDAPException.PARAM_ERROR;
        lber.encodeString(dn, true)
                
        setValue(requestlber.getBug());
   }

}

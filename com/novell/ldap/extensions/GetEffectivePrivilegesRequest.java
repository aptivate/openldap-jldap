/* **************************************************************************
 * $Id: GetEffectivePrivilegesRequest.java,v 1.1 2000/08/01 01:03:32 javed Exp $
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
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and returns the distingusihed name of the object your are 
 *  logged in as.<br><br>
 *
 *  This class is used to build the extended request for
 *  getting the effective rights of the specified entry
 *  on the attribute of another entry.  To use this class instantiate
 *  an object of this class and then call the extendedOperation method 
 *  with this object as the required LDAPExtendedOperation parameter.<br><br>
 *
 *  The returned LDAPExtendedResponse object can then be converted to
 *  a GetEffectivePrivilegesRequest object.  The 
 *  GetEffectivePrivilegesRequest object contains
 *  methods for retreiving the effective rights.<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.33"<br><br>
 *
 *  The RequestValue has the folling ASN:<br>
 *
 *  requestValue ::=<br><br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn          LDAPDN
 *  &nbsp;&nbsp;&nbsp;&nbsp;    trusteeDN   LDAPDN
 *  &nbsp;&nbsp;&nbsp;&nbsp;    attrName    LDAPDN     
 */
 public class GetEffectivePrivilegesRequest extends LDAPExtendedOperation {
 
    /**
    *      The constructor takes four parameters:<br><br>
    *
    *   This API takes three parameters.  
    * String dn: dn of the entry whose attribute follows
    * String trusteeDN: dn of the entry whose trustee rights are being returned
    * String attrName: LDAP attribute name.
    *
    */  

    public GetEffectivePrivilegesRequest(String dn, String trusteeDN, String attrName) 
                throws LDAPException {
        
        super(NamingContextConstants.GET_EFFECTIVE_PRIVILEGES_REQ, null);
        
        try {
            // ber encode the parameters and set the requestValue
            LberEncoder requestlber = new LberEncoder();
            
            if ( (dn == null) )
                throw new LDAPException("Invalid parameter",
				                        LDAPException.PARAM_ERROR);
				                        
		    requestlber.encodeString(dn, true);
		    requestlber.encodeString(trusteeDN, true);
		    requestlber.encodeString(attrName, true);		    
                    
            setValue(requestlber.getTrimmedBuf());
            
        }
		catch(IOException ioe) {
			throw new LDAPException("Encoding Error",
				                     LDAPException.ENCODING_ERROR);
		}
        
     }
}

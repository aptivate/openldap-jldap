/* **************************************************************************
 * $Id: GetContextIdentityNameRequest.java,v 1.6 2000/08/28 22:19:19 vtag Exp $
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
 *      This class returns the distingusihed name of the object your are 
 *  logged in as.<br><br>
 *
 *  To use this API create an instance of this 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.<br><br>
 *
 *  The returned LDAPExtendedResponse object can then be converted to
 *  a GetContextIdentityNameResponse object.  This object contains
 *  methods for retreiving the dn.<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.31"<br><br>
 *
 *  The RequestValue has a value of null<br>
 *
 */
 public class GetContextIdentityNameRequest extends LDAPExtendedOperation {
 
    /**
    *      The constructor takes four parameters:<br><br>
    *
    *   This API takes no parameters.  
    *
    */  

    public GetContextIdentityNameRequest() 
                throws LDAPException {
        
        super(NamingContextConstants.GET_IDENTITY_NAME_REQ, null);
        
     }
}

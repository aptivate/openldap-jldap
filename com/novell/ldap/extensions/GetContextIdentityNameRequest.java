/* **************************************************************************
 * $Id: GetContextIdentityNameRequest.java,v 1.10 2000/10/04 22:39:33 judy Exp $
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
 *  Returns the distingusihed name of the object your are 
 *  logged in as.
 *
 *  <p>To use this class, you must create an instance of the 
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The returned LDAPExtendedResponse object can then be converted to
 *  a GetContextIdentityNameResponse object with the ExtendedREsponseFactory 
 *  class. This object contains  methods for retrieving the distinguished
 *  name.</p>
 *
 *  <p>The getContextIdentityNameRequest extension uses the following OID:<br>
 *   &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.31</p>
 *
 *  <p>The request value has a value of null.</p>
 *
 */
 public class GetContextIdentityNameRequest extends LDAPExtendedOperation {
 
    /**
    *   Constructs an extended operation object for retrieving the context's 
    *   identity.
    *
    *  @exception LDAPException A general exception which includes an error message
    *                           and an LDAP error code. 
    *
    */  

    public GetContextIdentityNameRequest() 
                throws LDAPException {
        
        super(NamingContextConstants.GET_IDENTITY_NAME_REQ, null);
        
     }
}

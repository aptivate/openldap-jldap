/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.extensions;

import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedResponse;
/**
 *  Returns the distingusihed name of the object your are logged in as.
 *
 *  <p>To use this class, you must create an instance of the
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The returned LDAPExtendedResponse object can then be converted to
 *  a GetBindDNResponse object with the ExtendedREsponseFactory
 *  class. This object contains  methods for retrieving the distinguished
 *  name.</p>
 *
 *  <p>The GetBindDNRequest extension uses the following OID:<br>
 *   &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.31</p>
 *
 *  <p>The request value has a value of null.</p>
 *
 */
 public class GetBindDNRequest extends LDAPExtendedOperation {

    static
    {
		/*
         * Register the extendedresponse class which is returned by the
		 * server in response to a GetBindDNRequest
		 */
        try {
            LDAPExtendedResponse.register(
                  ReplicationConstants.GET_IDENTITY_NAME_RES,
                  Class.forName(
                  "com.novell.ldap.extensions.GetBindDNResponse"));
        }catch (ClassNotFoundException e) {
            System.err.println("Could not register Extended Response -" +
                               " Class not found");
        }catch(Exception e){
           e.printStackTrace();
        }
        
    }
    
    /**
    *   Constructs an extended operation object for retrieving the bind dn.
    *
    *  @exception LDAPException A general exception which includes an error 
    *             message and an LDAP error code.    
    */

    public GetBindDNRequest() throws LDAPException {

        super(ReplicationConstants.GET_IDENTITY_NAME_REQ, null);
     }
}

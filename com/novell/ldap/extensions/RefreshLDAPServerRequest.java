/* **************************************************************************
 * $Id: RefreshLDAPServerRequest.java,v 1.2 2000/07/27 18:08:13 javed Exp $
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
 *  public class RefreshLDAPServerRequest
 *
 *      This class inherits from the LDAPExtendedOperation class
 *  and is used to reload the ldap server.
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.7"
 *
 *  The RequestValue is set to null
 */
public class RefreshLDAPServerRequest extends LDAPExtendedOperation {
   
/**
 *  public RefreshLDAPServerRequest()
 *
 *      The constructor does not have any parametes.  We merely set the OID.
 *
 */   
 public RefreshLDAPServerRequest() 
                throws LDAPException {
        
        super(NamingContextConstants.REFRESH_SERVER_REQ, null);
        
   }

}

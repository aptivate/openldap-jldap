/* **************************************************************************
 * $Id: ExtendedResponseFactory.java,v 1.2 2000/07/27 16:35:23 javed Exp $
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
 
/**
 * public class ExtendedResponseFactory
 *
 *  This factory class takes an LDAPExtendedResponse and returns 
 *  a childExtendedResponse based on the OID.  You can then call
 *  methods defined in the child class to parse the contents of the
 *  reponse.  The methods available depend on the child class created.
 *  All child classes will inherit from the LDAPExtendedResponse class
 *  to allow access to result codes and OID parameters available in the
 *  parent class.
 *
 */
public class ExtendedResponseFactory {
    
    public LDAPExtendedResponse ExtendedResponseFactory(LDAPExtendedResponse inResponse) {
                
        // switch based on OID an instantiate appropriate reponse
        
        
        
        return null;
        
    }   
		

}

/* **************************************************************************
 * $Id: ExtendedResponseFactory.java,v 1.5 2000/08/04 15:32:30 javed Exp $
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
import java.io.IOException; 
/**
 *
 *  This factory class takes an LDAPExtendedResponse and returns 
 *  an object (that implements the base class ParsedExtendedResponse)
 *  based on the OID.  You can then call methods defined in the child
 *  class to parse the contents of the response.  The methods available
 *  depend on the child class. All child classes inherit from the 
 *  ParsedExtendedResponse.
 *
 */
public class ExtendedResponseFactory {
    
    /**
     * This method converts a LDAPExtendedResponse  object to the appropriate
     * PArsedExtendedREsponse object depending on the operation being performed.<br><br>
     *
     * @param inResponse pass in the LDAPExtendedReponse object as returned by
     * the extendedOperation method in the LDAPConnection object.<br><br>
     * @return An object of baseclass ParsedExtendedReponse.  The actual child class of 
     * this returnde object depends on the operation being performed.
     * 
     */
 
    static public ParsedExtendedResponse parseExtendedResponse(LDAPExtendedResponse inResponse) 
            throws LDAPException {
                
        // Get the oid stored in the Extended response
        String inOID = inResponse.getID();
        
        try {
            if (inOID.equals(NamingContextConstants.NAMING_CONTEXT_COUNT_RES)) {
                return new NamingContextEntryCountResponse(inResponse);
            }
            if (inOID.equals(NamingContextConstants.GET_IDENTITY_NAME_RES) ) {
                return new GetContextIdentityNameResponse(inResponse);
            }
            if (inOID.equals(NamingContextConstants.GET_EFFECTIVE_PRIVILEGES_RES) ) {
                return new GetEffectivePrivilegesResponse(inResponse);
            }
            
            else
                throw new LDAPException("Unsupported OID in LDAPResponse", 
                    LDAPException.DECODING_ERROR);
        }
        
        catch(IOException ioe) {
			throw new LDAPException("Error Decoding respnseValue", 
                    LDAPException.DECODING_ERROR);
		}
    }
}

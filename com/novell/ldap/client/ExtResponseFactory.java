/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.client;

import com.novell.ldap.*;
import com.novell.ldap.extensions.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.resources.*;
import java.io.IOException;
import com.novell.ldap.client.Debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

/**
 *
 *  Takes an LDAPExtendedResponse and returns an object
 *  (that implements the base class ParsedExtendedResponse)
 *  based on the OID.
 *
 *  <p>You can then call methods defined in the child
 *  class to parse the contents of the response.  The methods available
 *  depend on the child class. All child classes inherit from the
 *  ParsedExtendedResponse.
 *
 */
public class ExtResponseFactory {

    /**
     * Used to Convert an RfcLDAPMessage object to the appropriate
     * LDAPExtendedResponse object depending on the operation being performed.
     *
     * @param inResponse   The LDAPExtendedReponse object as returned by the
     *                     extendedOperation method in the LDAPConnection object.
     * <br><br>
     * @return An object of base class LDAPExtendedResponse.  The actual child
     *         class of this returned object depends on the operation being
     *         performed.
     *
     * @exception LDAPException A general exception which includes an error message
     *                          and an LDAP error code.
     */

    static public LDAPExtendedResponse convertToExtendedResponse(RfcLDAPMessage inResponse)
            throws LDAPException {
        
        LDAPExtendedResponse tempResponse = new LDAPExtendedResponse(inResponse);
        // Get the oid stored in the Extended response
        String inOID = tempResponse.getID();

        RespExtensionSet regExtResponses = 
                                LDAPExtendedResponse.getRegisteredResponses();
        try{
            Class extRespClass = regExtResponses.findResponseExtension(inOID);            
            if ( extRespClass == null ){
                return tempResponse;
            }
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages,
                 "For oid " + inOID + ", found class " + extRespClass.toString());
            }    
            
            Class[] argsClass = { RfcLDAPMessage.class };
            Object[] args = { inResponse };
            Exception ex;
            try{
                Constructor extConstructor = 
                                     extRespClass.getConstructor(argsClass);
                try{
                    Object resp = null;
                    resp = extConstructor.newInstance(args);
                    return (LDAPExtendedResponse) resp;
                }catch(InstantiationException e) {
                    // Could not create the ResponseControl object
                    // All possible exceptions are ignored. We fall through
                    // and create a default LDAPControl object
                    ex = e;
                } catch (IllegalAccessException e) {
                    ex = e;
                } catch (InvocationTargetException e) {
                    ex = e;
                }
            } catch (NoSuchMethodException e) {
                // bad class was specified, fall through and return a
                // default  LDAPExtendedResponse object
                ex = e;
            }
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages,
                "Unable to create new instance of child LDAPExtendedResponse");
                Debug.trace( Debug.messages,
                   ex.toString());
            }
        } catch (NoSuchFieldException e) {
            // No match with the OID
            // Do nothing. Fall through and construct a default LDAPControl object.
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.messages,
                      "Oid " + inOID + " not registered");
            }
        }
        // If we get here we did not have a registered extendedresponse
        // for this oid.  Return a default LDAPExtendedResponse object.
        return tempResponse;
    }

}

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

package com.novell.ldap;

import com.novell.ldap.rfc2251.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.RespExtensionSet;

/**
 *
 *  Encapsulates the response returned by an LDAP server on an
 *  asynchronous extended operation request.  It extends LDAPResponse.
 *
 *  The response can contain the OID of the extension, an octet string
 *  with the operation's data, both, or neither.
 */
public class LDAPIntermediateResponse extends LDAPResponse {

	private static RespExtensionSet registeredResponses =
													new RespExtensionSet();


	/**
	 * Registers a class to be instantiated on receipt of a extendedresponse
	 * with the given OID.
	 *
	 * <p>Any previous registration for the OID is overridden. The 
	 *  extendedResponseClass object MUST be an extension of 
	 *  LDAPIntermediateResponse. </p>
	 *
	 * @param oid            The object identifier of the control.
	 * <br><br>
	 * @param extendedResponseClass  A class which can instantiate an 
	 *                                LDAPIntermediateResponse.
	 */
	public static void register(String oid, Class extendedResponseClass) 
	{
		registeredResponses.registerResponseExtension(oid, extendedResponseClass);
		return;
	}
    

	/* package */
	public static RespExtensionSet getRegisteredResponses()
	{
		return registeredResponses;
	}


    /**
     * Creates an LDAPIntermediateResponse object which encapsulates
     * a server response to an asynchronous extended operation request.
     *
     * @param message  The RfcLDAPMessage to convert to an
     *                 LDAPIntermediateResponse object.
     */
    public LDAPIntermediateResponse(RfcLDAPMessage message)
    {
        super(message);
    }

    /**
     * Returns the message identifier of the response.
     *
     * @return OID of the response.
     */
    public String getID()
    {
        RfcLDAPOID respOID =
            ((RfcIntermediateResponse)message.getResponse()).getResponseName();
        if (respOID == null)
            return null;
        return respOID.stringValue();
    }

    /**
     * Returns the value part of the response in raw bytes.
     *
     * @return The value of the response.
     */
    public byte[] getValue()
    {
		ASN1OctetString tempString =
                ((RfcIntermediateResponse)message.getResponse()).getResponse();
		if (tempString == null)
			return null;
		else
			return(tempString.byteValue());
    }
	
}

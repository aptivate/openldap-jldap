/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2007 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;

public class LDAPDnsToX500DNRequest extends LDAPExtendedOperation {
	static {
		try
		{
			/*
			 * Register the extendedresponse class which is returned by the server
			 * in response to a LDAPDnsToX500DNRequest
			 */
			LDAPExtendedResponse.register(NamingContextConstants.LDAP_DNS_TO_X500_DN_EXTENDED_REPLY,
					Class.forName("com.novell.ldap.extensions.LDAPDnsToX500DNResponse"));
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Could not register Extended Response -"+ " Class not found");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LDAPDnsToX500DNRequest(String objectDN ) throws LDAPException {

		super(NamingContextConstants.LDAP_DNS_TO_X500_DN_EXTENDED_REQUEST, null);
		try {
			if (objectDN == null)
				throw new IllegalArgumentException(ExceptionMessages.PARAM_ERROR);
			else 
			{
				// if the objectdn is not null then I need to process the dn
				ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
				LBEREncoder encoder = new LBEREncoder();
				//Encode the data of objectDN 
				ASN1OctetString asn1_objectDN = new ASN1OctetString(objectDN);
				asn1_objectDN.encode(encoder,encodedData );
				// 	set the value of operation specific data
				setValue(encodedData.toByteArray());
			}
		}
		catch(IOException ioe)
		{
			throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
					LDAPException.ENCODING_ERROR,(String) null);	
		}
	}
}

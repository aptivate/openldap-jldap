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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.rfc2251.RfcLDAPMessage;

public class LDAPDnsToX500DNResponse extends LDAPExtendedResponse{
	private String namemappedDn;
	public LDAPDnsToX500DNResponse(RfcLDAPMessage rfcMessage) throws IOException {
		super(rfcMessage);
		//		Verify if returned ID is not proper
		if (getID() == null	|| !(getID().equals(NamingContextConstants.LDAP_DNS_TO_X500_DN_EXTENDED_REPLY)))
			throw new IOException("LDAP Extended Operation not supported");
		
		if (getResultCode() == LDAPException.SUCCESS) 
		{
			// Get the contents of the reply
			byte[] returnedValue = this.getValue();
			if (returnedValue == null)
				throw new IOException("LDAP Operations error. No returned value.");

			// Create a decoder object
			LBERDecoder decoder = new LBERDecoder();
			if (decoder == null)
				throw new IOException("Decoding error");

			// Parse the parameters in the order
			ByteArrayInputStream currentPtr = new ByteArrayInputStream(returnedValue);
			
			//decoding the string dn which comes back in response 
			ASN1OctetString asn1_dn = (ASN1OctetString) decoder.decode(currentPtr);
			
			if (asn1_dn == null)
				throw new IOException("Decoding error");
			
			namemappedDn = asn1_dn.stringValue();
			
			if(namemappedDn==null)
				throw new IOException("Decoding error");

		}
		else
			namemappedDn="";
	}
	
	public String getX500DN()
	{
		return namemappedDn;
	}
}

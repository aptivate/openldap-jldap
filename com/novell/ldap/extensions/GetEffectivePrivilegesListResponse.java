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

package com.novell.ldap.extensions;
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

import java.io.IOException;

/**
 *  Retrieves the effective rights from an GetEffectivePrivilegesListResponse object.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 *  <p>The getEffectivePrivilegesListResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.104</p>
 *
 */
public class GetEffectivePrivilegesListResponse extends LDAPExtendedResponse{
	//Identity returned by the server
	private int[] privileges= {0};
	private int no_privileges;
	
	/**
	    * Constructs an object from the responseValue which contains the effective
	    * privileges.
	    *
	    *   <p>The constructor parses the responseValue which has the following
	    *   format:<br>
	    *   responseValue ::=<br>
	    *   &nbsp;&nbsp;&nbsp;&nbsp;  sequence of number of privileges&nbsp;&nbsp;&nbsp;  INTEGER</p>
	    *   &nbsp;&nbsp;&nbsp;&nbsp;  set of sequence of privileges &nbsp;&nbsp;&nbsp;  INTEGER</p>
	    *
	    * @exception IOException The responseValue could not be decoded.
	    */
	public GetEffectivePrivilegesListResponse (RfcLDAPMessage rfcMessage) throws IOException 
	{
		super(rfcMessage);
		if (getResultCode() == LDAPException.SUCCESS)
		{
			// parse the contents of the reply
			byte [] returnedValue = this.getValue();
			if (returnedValue == null)
			   throw new IOException("No returned value");

			//Create a decoder object
			LBERDecoder decoder = new LBERDecoder();
			if (decoder == null)
				throw new IOException("Decoding error");
			
			ASN1Sequence asn1_seq1 = (ASN1Sequence)decoder.decode(returnedValue);
			if (asn1_seq1 == null)
				throw new IOException("Decoding error");
			ASN1Sequence asn1_seq2 = (ASN1Sequence)asn1_seq1.get(0);
			no_privileges = ((ASN1Integer)asn1_seq2.get(0)).intValue();
			
			/* 
			 * Chunks returned from server is encoded as shown below::
			 * 			SET of [
			 * 				SEQUENCE of {privileges        INTEGER}]
			 * 	       }
			 */
			
			ASN1Set set_privileg_response = null;
			ASN1Integer[] asn1_privileges = null;
			set_privileg_response = ((ASN1Set)asn1_seq1.get(1));
			
			ASN1Sequence seq2 = null;
			privileges = new int[no_privileges];
			for(int index=0; index < no_privileges; index++)
			{
				seq2 = (ASN1Sequence)set_privileg_response.get(index);
				privileges[index]=((ASN1Integer)seq2.get(0)).intValue();		
			}
		}
	}
	
	public int[] getPrivileges()
	{
		return privileges;
	}
}

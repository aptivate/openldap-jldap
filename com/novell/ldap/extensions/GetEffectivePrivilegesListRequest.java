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
import com.novell.ldap.resources.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
*
* Returns the effective rights of one object to a string of attributes of another object.
*
* <p>To use this class, you must instantiate an object of this class and then
* call the extendedOperation method with this object as the required
* LDAPExtendedOperation parameter.</p>
*
* <p>The returned LDAPExtendedResponse object can then be converted to
* a GetEffectivePrivilegesListResponse object with the ExtendedResponseFactory class.
* The GetEffectivePrivilegesListResponse class  contains methods for
* retrieving the effective rights.</p>
*
* <p>The getEffectivePrivilegesListRequest extension uses the following OID:<br>
*  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.103</p>
*
* <p>The requestValue has the following format:<br>
*
*  requestValue ::=<br>
*  &nbsp;&nbsp;&nbsp;&nbsp;  dn &nbsp;&nbsp;&nbsp;        LDAPDN<br>
*  &nbsp;&nbsp;&nbsp;&nbsp;  trusteeDN&nbsp;&nbsp;&nbsp;  LDAPDN<br>
*  &nbsp;&nbsp;&nbsp;&nbsp;  sequence of {"attr1","attr2",...,null} &nbsp;&nbsp;&nbsp;  LDAPDN </p>
*/

public class GetEffectivePrivilegesListRequest extends LDAPExtendedOperation {

	static
	{
		/*
	    * Register the extendedresponse class which is returned by the
		* server in response to a GetEffectivePrivilegesListRequest
		*/
	    try {
	       LDAPExtendedResponse.register(
	          ReplicationConstants.GET_EFFECTIVE_LIST_PRIVILEGES_RES,
	          Class.forName("com.novell.ldap.extensions.GetEffectivePrivilegesListResponse"));
	        }catch (ClassNotFoundException e) {
	            System.err.println("Could not register Extended Response -" +
	                               " Class not found");
	        }catch(Exception e){
	           e.printStackTrace();
	        }
	}	 
	 
	    /**
	    * Constructs an extended operation object for checking effective rights.
	    *
	    * @param dn        The distinguished name of the entry whose attribute is
	    *                  being checked.
	    *<br><br>
	    * @param trusteeDN The distinguished name of the entry whose trustee rights
	    *                  are being returned
	    *<br><br>
	    * @param sequence of {"attr1","attr2",...,null}  The list of LDAP attribute names.
	    *
	    * @exception LDAPException A general exception which includes an error
	    *                          message and an LDAP error code.
	    */

	public GetEffectivePrivilegesListRequest(String dn, String trusteeDN, String[] attrName)
	throws LDAPException 
	{

			super(ReplicationConstants.GET_EFFECTIVE_LIST_PRIVILEGES_REQ, null);

			try {
				
				if ( (dn == null) )
					throw new IllegalArgumentException(ExceptionMessages.PARAM_ERROR);

				ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
				LBEREncoder encoder  = new LBEREncoder();

				ASN1OctetString asn1_trusteeDN = new ASN1OctetString(trusteeDN);
				ASN1OctetString asn1_dn = new ASN1OctetString(dn);
				asn1_trusteeDN.encode(encoder, encodedData);
				asn1_dn.encode(encoder, encodedData);
				ASN1Sequence asn1_seqattr = new ASN1Sequence();
				for (int i = 0;attrName[i]!= null ; i++)
				{
					ASN1OctetString asn1_attrName = new ASN1OctetString(attrName[i]);
					asn1_seqattr.add(asn1_attrName);
				}
				asn1_seqattr.encode(encoder, encodedData);
				setValue(encodedData.toByteArray());

			}
			catch(IOException ioe) {
				throw new LDAPException(ExceptionMessages.ENCODING_ERROR,LDAPException.ENCODING_ERROR,(String)null);
			}
	}
}

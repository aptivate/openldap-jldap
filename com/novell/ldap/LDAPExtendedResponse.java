/* **************************************************************************
 * $Id: LDAPExtendedResponse.java,v 1.4 2000/07/26 16:48:25 javed Exp $
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
 
package com.novell.ldap;

import java.io.IOException;

import com.novell.ldap.LDAPResponse;
import com.novell.ldap.client.protocol.lber.*;


/**
 * 4.2 public class LDAPExtendedResponse extends LDAPResponse
 *
 *  An LDAPExtendedResponse object encapsulates a server response to an
 *  extended operation request.
 *
 *  Extended Response ::= [APPLICATION 24] SEQUENCE {
 *      COMPONENTS OF LDAPResult,
 *      reponseName [10]    LDAPOID OPTIONAL
 *      response    [11]    OCTET STRING OPTIONAL }
 */
public class LDAPExtendedResponse extends LDAPResponse {

    private String oid;
    private byte[] vals;

	public LDAPExtendedResponse(int messageID, LberDecoder lber,
		                         boolean isLdapv3)
		throws IOException
	{
		super(messageID, EXTENDED_RESPONSE, lber, isLdapv3);

		this.oid = lber.parseStringWithTag(Lber.ASN_EXOP_RESP_OID, true, null);
		this.vals = lber.parseOctetString(Lber.ASN_EXOP_RESP_VALUE, null);

	}

   /*
    * 4.2.1 getID
    */

   /**
    * Returns the OID of the response.
    */
   public String getID() {
      return oid;
   }

   /*
    * 4.2.2 getValue
    */

   /**
    * Returns the raw bytes of the value part of the response.
    */
   public byte[] getValue() {
      return vals;
   }

}


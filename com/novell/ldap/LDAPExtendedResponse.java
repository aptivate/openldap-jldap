/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/LDAPExtendedResponse.java,v 1.13 2000/09/08 23:43:50 judy Exp $
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
import com.novell.ldap.protocol.*;
//import com.novell.ldap.client.protocol.lber.*;


/*
 * 4.2 public class LDAPExtendedResponse extends LDAPResponse
 */
 
/**
 *
 *  Encapsulates a server response to an extended operation request.
 *  
 *  The repsonse can contain the OID of the extension, an octet string
 *  with the operation's data, both, or neither.
 *
 */
public class LDAPExtendedResponse extends LDAPResponse {

	 /**
	  * Creates a Java-API LDAPExtendedResponse (which is a Java-API
	  * LDAPMessage) when receiving an LDAPMessage from a server.
      *
      * @param message  The LDAPMessage to convert to a Java-API
      *                 LDAPExtendedResponse
	  */
	 public LDAPExtendedResponse(com.novell.ldap.protocol.LDAPMessage message)
	 {
		 super(message);
	 }

/*
	public LDAPExtendedResponse(int messageID, LberDecoder lber,
		                         boolean isLdapv3)
		throws IOException
	{
		super(messageID, EXTENDED_RESPONSE, lber, isLdapv3);
        
		if((lber.bytesLeft() > 0) && (lber.peekByte() == Lber.ASN_EXOP_RESP_OID) )
               this.oid = lber.parseStringWithTag(Lber.ASN_EXOP_RESP_OID, true, null);
        
		if((lber.bytesLeft() > 0) && (lber.peekByte() == Lber.ASN_EXOP_RESP_VALUE) )
		    this.vals = lber.parseOctetString(Lber.ASN_EXOP_RESP_VALUE, null);
	}
*/			 

   /*
    * 4.2.1 getID
    */

   /**
    * Returns the OID of the response.
    *
    * @return OID of the response.
    */
   public String getID() {
		return ((ExtendedResponse)message.getProtocolOp()).getResponseName().getString();
   }

   /*
    * 4.2.2 getValue
    */

   /**
    * Returns the raw bytes of the value part of the response.
    *
    * @return The value of the response.
    */
   public byte[] getValue() {
		return ((ExtendedResponse)message.getProtocolOp()).getResponse().getContent();
   }

}


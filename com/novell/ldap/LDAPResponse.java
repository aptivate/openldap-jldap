/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPResponse.java,v 1.10 2000/09/11 22:47:50 judy Exp $
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
import java.util.Vector;

import com.novell.ldap.asn1.*;
import com.novell.ldap.protocol.*;

/**
 *  Represents the response to an LDAP protocol operation.
 */
public class LDAPResponse extends LDAPMessage {

	/**
	 * Creates an LDAPMessage when receiving an RFC 2251 LDAPMessage from a
	 * server.
     *
     *  @param message  The LDAPMessage from a server.
	 */
	public LDAPResponse(com.novell.ldap.protocol.LDAPMessage message)
	{
		super(message);
	}

   /**
    * Returns any error message in the response.
    *
    * @return Any error message in the response.
    */
   public String getErrorMessage()
	{
		return
			((Response)message.getProtocolOp()).getErrorMessage().getString();
   }

   /**
    * Returns the partially matched DN field, if any, in a server response.
    *
    * @return The partially matched DN field, if the response contains one.
    *         
    */
   public String getMatchedDN()
	{
		return
			((Response)message.getProtocolOp()).getMatchedDN().getString();
   }

   /**
    * Returns all referrals, if any, in a server response.
    *
    * @return All the referrals in the server response.
    */
   public String[] getReferrals() {
		String[] referrals = null;
		Referral ref = ((Response)message.getProtocolOp()).getReferral();
		
		if(ref != null) {
			// convert RFC 2251 Referral to String[]
			int size = ref.size();
			referrals = new String[size];
			for(int i=0; i<size; i++) {
				referrals[i] = new String(((ASN1OctetString)ref.get(i)).getContent());
			}
		}

      return referrals;
   }

   /**
    * Returns the result code in a server response.
    *
    * <p> For a list of result codes, see the LDAPException class. </p>
    *
    * @return The result code.
    */
   public int getResultCode()
	{
		return ((Response)message.getProtocolOp()).getResultCode().getInt();
   }

   /**
    * Checks the resultCode and throws the appropriate exception.
    */
   /* package */ void chkResultCode() throws LDAPException {
      switch(getResultCode()) {
         case LDAPException.SUCCESS:
            break;
         case LDAPException.OPERATIONS_ERROR:
         case LDAPException.PROTOCOL_ERROR:
         case LDAPException.TIME_LIMIT_EXCEEDED:
			case LDAPException.SIZE_LIMIT_EXCEEDED:
			case LDAPException.AUTH_METHOD_NOT_SUPPORTED:
			case LDAPException.STRONG_AUTH_REQUIRED:
			case LDAPException.LDAP_PARTIAL_RESULTS:
			case LDAPException.ADMIN_LIMIT_EXCEEDED:
			case LDAPException.UNAVAILABLE_CRITICAL_EXTENSION:
			case LDAPException.CONFIDENTIALITY_REQUIRED:
			case LDAPException.SASL_BIND_IN_PROGRESS:
			case LDAPException.NO_SUCH_ATTRIBUTE:
			case LDAPException.UNDEFINED_ATTRIBUTE_TYPE:
			case LDAPException.INAPPROPRIATE_MATCHING:
			case LDAPException.CONSTRAINT_VIOLATION:
			case LDAPException.ATTRIBUTE_OR_VALUE_EXISTS:
			case LDAPException.INVALID_ATTRIBUTE_SYNTAX:
			case LDAPException.NO_SUCH_OBJECT:
			case LDAPException.ALIAS_PROBLEM:
			case LDAPException.INVALID_DN_SYNTAX:
			case LDAPException.IS_LEAF:
			case LDAPException.ALIAS_DEREFERENCING_PROBLEM:
			case LDAPException.INAPPROPRIATE_AUTHENTICATION:
			case LDAPException.INVALID_CREDENTIALS:
			case LDAPException.INSUFFICIENT_ACCESS_RIGHTS:
			case LDAPException.BUSY:
			case LDAPException.UNAVAILABLE:
			case LDAPException.UNWILLING_TO_PERFORM:
			case LDAPException.LOOP_DETECT:
			case LDAPException.NAMING_VIOLATION:
			case LDAPException.OBJECT_CLASS_VIOLATION:
			case LDAPException.NOT_ALLOWED_ON_NONLEAF:
			case LDAPException.NOT_ALLOWED_ON_RDN:
			case LDAPException.ENTRY_ALREADY_EXISTS:
			case LDAPException.OBJECT_CLASS_MODS_PROHIBITED:
			case LDAPException.AFFECTS_MULTIPLE_DSAS:
			case LDAPException.OTHER:
			case LDAPException.SERVER_DOWN:
			case LDAPException.LOCAL_ERROR:
			case LDAPException.ENCODING_ERROR:
			case LDAPException.DECODING_ERROR:
			case LDAPException.LDAP_TIMEOUT:
			case LDAPException.AUTH_UNKNOWN:
			case LDAPException.FILTER_ERROR:
			case LDAPException.USER_CANCELLED:
			case LDAPException.PARAM_ERROR:
			case LDAPException.NO_MEMORY:
			case LDAPException.CONNECT_ERROR:
			case LDAPException.LDAP_NOT_SUPPORTED:
			case LDAPException.CONTROL_NOT_FOUND:
			case LDAPException.NO_RESULTS_RETURNED:
			case LDAPException.MORE_RESULTS_TO_RETURN:
			case LDAPException.CLIENT_LOOP:
            throw new LDAPException(getErrorMessage(), getMatchedDN(),
					                     getResultCode());
			case LDAPException.REFERRAL_LIMIT_EXCEEDED:
         case LDAPException.REFERRAL:
            // only throw this if automatic referral handling has not been
            // enabled.
            throw new LDAPReferralException();
         default: // unknown
            throw new LDAPException(getErrorMessage(), getMatchedDN(), -1);
      }
   }

}


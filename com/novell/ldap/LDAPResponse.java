/* **************************************************************************
 * $Id$
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
import com.novell.ldap.client.protocol.lber.*;

/**
 * 4.4 public class LDAPResponse extends LDAPMessage
 *
 *  Represents the response to a particular LDAP operation.
 */
public class LDAPResponse extends LDAPMessage {

	private String errorMessage;     // RFC 2251
	private String matchedDN;        // RFC 2251
	private Vector referrals = null; // RFC 2251
	private int resultCode;          // RFC 2251

	private LberDecoder lber;

	/**
	 * Constructor called when an object with only a resultCode is needed.
	 */
	public LDAPResponse(int resultCode)
	{
		this.resultCode = resultCode;
	}

	/**
	 * Constructor called when parsing responses to messages as they are
	 * retrieved from the socket.
	 */
	public LDAPResponse(int messageID, int type, LberDecoder lber,
		                 boolean isLdapv3)
		throws IOException
	{
		super(messageID, type);
		this.lber = lber;

		try {
			// the following data items are defined in RFC2251 sec 4.1.10
			resultCode = lber.parseEnumeration();
			matchedDN = lber.parseString(isLdapv3);
			errorMessage = lber.parseString(isLdapv3);

			// parse any optional LDAPv3 referrals
			if(isLdapv3 &&
				(lber.bytesLeft() > 0) &&
				(lber.peekByte() == LDAP_REP_REFERRAL)) {

				Vector URLs = new Vector(5);
				int[] seqlen = new int[1];

				lber.parseSeq(seqlen);
				int endseq = lber.getParsePosition() + seqlen[0];
				while((lber.getParsePosition() < endseq) &&
						(lber.bytesLeft() > 0)) {

					URLs.addElement(lber.parseString(isLdapv3));
				}

				if(referrals == null) {
					referrals = new Vector(5);
				}
				referrals.addElement(URLs);
			}

			// parse any optional LDAPv3 controls
			if(isLdapv3) parseControls();

		}
		catch(IOException ioe) {
		}

	}

   private void parseControls()
		throws IOException
	{
      // handle LDAPv3 controls (if present)
      if((lber.bytesLeft() > 0) &&
			(lber.peekByte() == LDAP_CONTROLS)) {
         controls = new Vector(4);
         String controlOID;
         boolean criticality = false; // default
         byte[] controlValue = null;  // optional
         int[] seqlen = new int[1];

         lber.parseSeq(seqlen);
         int endseq = lber.getParsePosition() + seqlen[0];
         while((lber.getParsePosition() < endseq) &&
               (lber.bytesLeft() > 0)) {

            lber.parseSeq(null);
            controlOID = lber.parseString(true);

            if((lber.bytesLeft() > 0) &&
               (lber.peekByte() == Lber.ASN_BOOLEAN)) {
               criticality = lber.parseBoolean();
            }
            if((lber.bytesLeft() > 0) &&
               (lber.peekByte() == Lber.ASN_OCTET_STR)) {
               controlValue =
               lber.parseOctetString(Lber.ASN_OCTET_STR, null);
            }
            if(controlOID != null) {
               controls.addElement(
						new LDAPControl(controlOID, criticality, controlValue));
            }
         }
      }
   }

   /*
    * 4.4.1 getErrorMessage
    */

   /**
    * Returns any error message in the response.
    */
   public String getErrorMessage() {
      return errorMessage;
   }

   /*
    * 4.4.2 getMatchedDN
    */

   /**
    * Returns the partially matched DN field, if any, in a server response.
    */
   public String getMatchedDN() {
      return matchedDN;
   }

   /*
    * 4.4.3 getReferrals
    */

   /**
    * Returns all referrals, if any, in a server response.
    */
   public String[] getReferrals() {
		int size = referrals.size();
		String[] ref = new String[size];
		for(int i=0; i<size; i++) {
			ref[i] = (String)referrals.elementAt(i);
		}
      return ref;
   }

   /*
    * 4.4.4 getResultCode
    */

   /**
    * Returns the result code in a server response, as defined in [LDAPv3].
    */
   public int getResultCode() {
      return resultCode;
   }

   /**
    * Check the resultCode and throw the appropriate exception
    */
   public void chkResultCode() throws LDAPException {
      switch(resultCode) {
         case LDAPException.SUCCESS:
            break;
         case LDAPException.OPERATIONS_ERROR:
         case LDAPException.PROTOCOL_ERROR:
         case LDAPException.TIME_LIMIT_EXCEEDED:
			case LDAPException.SIZE_LIMIT_EXCEEDED:
			case LDAPException.AUTH_METHOD_NOT_SUPPORTED:
			case LDAPException.STRONG_AUTH_REQUIRED:
			case LDAPException.PARTIAL_RESULTS:
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
            throw new LDAPException(errorMessage, matchedDN, resultCode);
			case LDAPException.REFERRAL_LIMIT_EXCEEDED:
         case LDAPException.REFERRAL:
            // only throw this if automatic referral handling has not been
            // enabled.
            throw new LDAPReferralException();
         default:
            throw new LDAPException(errorMessage, matchedDN, -1); // unknown
      }
   }

   public static final int LBER_BOOLEAN = 0x01;
   public static final int LBER_INTEGER = 0x02;
   public static final int LBER_BITSTRING = 0x03;
   public static final int LBER_OCTETSTRING = 0x04;
   public static final int LBER_NULL = 0x05;
   public static final int LBER_SEQUENCE = 0x30;
   public static final int LBER_SET = 0x31;

   public static final int LDAP_REP_BIND = 0x61;   // app + constructed | 1
   public static final int LDAP_REP_SEARCH = 0x64; // app + constructed | 4
   public static final int LDAP_REP_SEARCH_REF = 0x73;// app + constructed    (LDAPv3)
   public static final int LDAP_REP_RESULT = 0x65; // app + constructed | 5
   public static final int LDAP_REP_MODIFY = 0x67; // app + constructed | 7
   public static final int LDAP_REP_ADD = 0x69; // app + constructed | 9
   public static final int LDAP_REP_DELETE = 0x6b; // app + primitive | b
   public static final int LDAP_REP_MODRDN = 0x6d; // app + primitive | d
   public static final int LDAP_REP_COMPARE = 0x6f;   // app + primitive | f
   public static final int LDAP_REP_EXTENSION = 0x78; // app + constructed    (LDAPv3)

   public static final int LDAP_REP_REFERRAL = 0xa3;  // ctx + constructed    (LDAPv3)
   public static final int LDAP_REP_EXT_OID = 0x8a;   // ctx + primitive      (LDAPv3)
   public static final int LDAP_REP_EXT_VAL = 0x8b;   // ctx + primitive      (LDAPv3)

}

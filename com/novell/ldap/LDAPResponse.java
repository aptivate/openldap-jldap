/**
 * 4.4 public abstract class LDAPResponse extends LDAPMessage
 *
 *  Represents the response to a particular LDAP operation.
 */
package com.novell.ldap;

import java.util.Vector;

public abstract class LDAPResponse extends LDAPMessage {

	public String errorMessage;     // RFC 2251
	public String matchedDN;        // RFC 2251
	public Vector referrals = null; // RFC 2251
	public int resultCode;          // RFC 2251

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

}

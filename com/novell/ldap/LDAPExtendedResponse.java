/**
 * 4.2 public class LDAPExtendedResponse extends LDAPResponse
 *
 *  An LDAPExtendedResponse object encapsulates a server response to an
 *  extended operation request.
 */
package com.novell.ldap;

public class LDAPExtendedResponse extends LDAPResponse {

   /*
    * 4.2.1 getID
    */

   /**
    * Returns the OID of the response.
    */
   public String getID() {
      return null;
   }

   /*
    * 4.2.2 getValue
    */

   /**
    * Returns the raw bytes of the value part of the response.
    */
   public byte[] getValue() {
      return null;
   }

}


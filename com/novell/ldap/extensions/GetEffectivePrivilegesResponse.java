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
 *  Retrieves the effective rights from an GetEffectivePrivilegesResponse object.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 *  <p>The getEffectivePrivilegesResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.34</p>
 *
 */
public class GetEffectivePrivilegesResponse extends LDAPExtendedResponse {

   // Identity returned by the server
   private int privileges;

   /**
    * Constructs an object from the responseValue which contains the effective
    * privileges.
    *
    *   <p>The constructor parses the responseValue which has the following
    *   format:<br>
    *   responseValue ::=<br>
    *   &nbsp;&nbsp;&nbsp;&nbsp;  privileges&nbsp;&nbsp;&nbsp;  INTEGER</p>
    *
    * @exception IOException The responseValue could not be decoded.
    */
   public GetEffectivePrivilegesResponse (RfcLDAPMessage rfcMessage)
         throws IOException {

        super(rfcMessage);

        if (getResultCode() == LDAPException.SUCCESS)
        {
            // parse the contents of the reply
            byte [] returnedValue = this.getValue();
            if (returnedValue == null)
                throw new IOException("No returned value");

            // Create a decoder object
            LBERDecoder decoder = new LBERDecoder();
            if (decoder == null)
                throw new IOException("Decoding error");

            ASN1Integer asn1_privileges = (ASN1Integer)decoder.decode(returnedValue);
            if (asn1_privileges == null)
                throw new IOException("Decoding error");

            privileges = asn1_privileges.intValue();
        }
        else
        {
            privileges = 0;
        }
   }

   /**
    * Returns the effective privileges.
    *
    * <p>See the ReplicationConstants class for the privilege flags.
    *
    * @return A flag which is a combination of zero or more privilege flags as
    * returned by the server.
    *
    * @see LDAPDSConstants#LDAP_DS_ATTR_COMPARE
    * @see LDAPDSConstants#LDAP_DS_ATTR_READ
    * @see LDAPDSConstants#LDAP_DS_ATTR_WRITE
    * @see LDAPDSConstants#LDAP_DS_ATTR_SELF
    * @see LDAPDSConstants#LDAP_DS_ATTR_SUPERVISOR
    * @see LDAPDSConstants#LDAP_DS_ATTR_INHERIT_CTL
    */
   public int getPrivileges() {
        return privileges;
   }

}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2001 Novell, Inc. All Rights Reserved.
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

import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPException;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import java.io.IOException;

/**
 *  Retrieves the identity from an GetBindDNResponse object.
 *
 *  <p>An object in this class is generated from an LDAPExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 * <p>The GetBindDNResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.32 </p>
 *
 */
public class GetBindDNResponse extends LDAPExtendedResponse {

   // Identity returned by the server
   private String identity;

   /**
    * Constructs an object from the responseValue which contains the bind dn.
    *
    *  <p>The constructor parses the responseValue which has the following
    *  format:<br>
    *  responseValue ::=<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp;identity &nbsp;&nbsp;&nbsp;  OCTET STRING</p>
    *
    *  @exception IOException The return value could not be decoded.
    */
   public GetBindDNResponse (RfcLDAPMessage rfcMessage)
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

            // The only parameter returned should be an octet string
            ASN1OctetString asn1_identity = (ASN1OctetString)decoder.decode(returnedValue);
            if (asn1_identity == null)
                throw new IOException("Decoding error");

            // Convert to normal string object
            identity = asn1_identity.stringValue();
            if (identity == null)
                throw new IOException("Decoding error");
        }
        else
        {
            identity = "";
        }
   }

   /**
    * Returns the identity of the object.
    *
    * @return A string value specifying the bind dn returned by the server.
    */
   public String getIdentity() {
        return identity;
   }

}

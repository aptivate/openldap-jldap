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
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import java.io.IOException;

/**
 * Returns the number of entries in the naming context.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 * <p>The namingContextEntryCountResponse extension uses the following
 * OID:<br>
 * &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.14</p>
 *
 */
public class NamingContextEntryCountResponse extends LDAPExtendedResponse {

   //The count of the objects returned by the server is saved here

   private int count;
   /**
    * Constructs an object from the responseValue which contains the
    * entry count.
    *
    * <p>The constructor parses the responseValue which has the following
    * format:<br>
    *  responseValue ::=<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp;  count &nbsp;&nbsp;&nbsp INTEGER</p>
    *
    * @exception IOException  The response value could not be decoded.
    */
   public NamingContextEntryCountResponse (RfcLDAPMessage rfcMessage)
         throws IOException {

        super(rfcMessage);

        // parse the contents of the reply
        byte [] returnedValue = this.getValue();
        if (returnedValue == null)
            throw new IOException("No returned value");

        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

        ASN1Integer asn1_count = (ASN1Integer)decoder.decode(returnedValue);
        if (asn1_count == null)
            throw new IOException("Decoding error");

        count = asn1_count.getInt();
   }

   /**
    * Returns the number of entries in the naming context.
    *
    * @return The count of the number of objects returned.
    */
   public int getCount() {
        return count;
   }

}

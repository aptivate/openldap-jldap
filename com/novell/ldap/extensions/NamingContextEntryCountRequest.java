/* **************************************************************************
 * $Id: NamingContextEntryCountRequest.java,v 1.12 2000/10/10 19:17:30 judy Exp $
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
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import java.io.*;

/**
 *
 *  Returns a count of the number of entries (objects) in the
 *  specified naming context.
 *
 *  <p>To obtain the count of entries, you must create an instance of this
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The returned LDAPExtendedResponse object can then be converted to
 *  a NamingContextEntryCountResponse object. This class contains
 *  methods for retrieving the returned count.</p>
 *
 *  <p>The namingContextEntryCountRequest extension uses the following
 *  OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.13</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br><br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn &nbsp;&nbsp;&nbsp;     LDAPDN
 */
 public class NamingContextEntryCountRequest extends LDAPExtendedOperation {

    /**
    *  Constructs an extended operation object for counting entries
    *  in a naming context.
    *
    * @param dn  The distinguished name of the naming context.
    *
    * @exception LDAPException A general exception which includes an
    *                          error message and an LDAP error code.
    */

    public NamingContextEntryCountRequest(String dn)
                throws LDAPException {

        super(NamingContextConstants.NAMING_CONTEXT_COUNT_REQ, null);

        try {

            if ( (dn == null) )
                throw new LDAPException(LDAPExceptionMessageResource.PARAM_ERROR,
                                    LDAPException.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_dn.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(LDAPExceptionMessageResource.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR);
      }
     }
}

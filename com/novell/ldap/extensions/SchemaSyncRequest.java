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
import com.novell.ldap.resources.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 *
 *  Synchronizes the schema.
 *
 *  <p>The requestSchemaSyncRequest extension uses the following OID: <br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.27</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;     serverName &nbsp;&nbsp;&nbsp;      LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;     delay &nbsp;&nbsp;&nbsp;           INTEGER</p>
 */
public class SchemaSyncRequest extends LDAPExtendedOperation {

/**
 *  Constructs an extended operation object for synchronizing the schema.
 *
 * @param serverName     The distinguished name of the server which will start
 *                       the synchronization.
 * <br><br>
 * @param delay          The time, in seconds, to delay before the synchronization
 *                       should start.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public SchemaSyncRequest(String serverName, int delay)
                throws LDAPException {

        super(ReplicationConstants.SCHEMA_SYNC_REQ, null);

        try {

            if (serverName == null)
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

          ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1OctetString asn1_serverName = new ASN1OctetString(serverName);
          ASN1Integer asn1_delay = new ASN1Integer(delay);

            asn1_serverName.encode(encoder, encodedData);
            asn1_delay.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
      }
   }

}

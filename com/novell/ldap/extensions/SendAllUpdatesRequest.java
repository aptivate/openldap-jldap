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
 *  Schedules an updated request to be sent to all directory servers in a
 *  replica ring.
 *
 *  <p>The sendAllUpdatesRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.23</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    partitionRoot&nbsp;&nbsp;&nbsp;   LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    origServerDN&nbsp;&nbsp;&nbsp;    LDAPDN</p>
 */
public class SendAllUpdatesRequest extends LDAPExtendedOperation {

/**
 *
 * Constructs an extended operation object for sending updates to a replica ring.
 *
 * @param partitionRoot The distinguished name of the replica
 *                      that will be updated.
 *<br><br>
 * @param origServerDN  The distinguished name of the server that sends the
 *                      updates to the replica ring.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public SendAllUpdatesRequest(String partitionRoot, String origServerDN)
                throws LDAPException {

        super(ReplicationConstants.SEND_ALL_UPDATES_REQ, null);

        try {

            if ( (partitionRoot == null) || (origServerDN == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);
         ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
          ASN1OctetString asn1_origServerDN = new ASN1OctetString(origServerDN);

            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_origServerDN.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
      }
   }

}

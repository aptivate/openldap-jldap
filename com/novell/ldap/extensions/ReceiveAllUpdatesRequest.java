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
 *  Schedules a specified directory server to receive updates from another
 *  directory server for a specific replica.
 *
 *  <p>The receiveAllUpdatesRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.21</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;   partitionRoot &nbsp;&nbsp;&nbsp;   LDAPDN<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;   toServerDN &nbsp;&nbsp;&nbsp;      LDAPDN<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;   fromServerDN &nbsp;&nbsp;&nbsp;    LDAPDN</p>
 */
public class ReceiveAllUpdatesRequest extends LDAPExtendedOperation {

/**
 *
 * Constructs an extended operation object for receiving all updates from
 * another directory server for a specific replica.
 *
 * @param partitionRoot   The distinguished name of the replica
 *                        that will be updated.
 *<br><br>
 * @param toServerDN      The distinguished name of the server holding the
 *                        replica to be updated.
 * <br><br>
 * @param fromServerDN    The distinguished name of the server from which
 *                        updates are sent.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public ReceiveAllUpdatesRequest(String partitionRoot, String toServerDN, String fromServerDN)
                throws LDAPException {

        super(ReplicationConstants.RECEIVE_ALL_UPDATES_REQ, null);

        try {

            if ( (partitionRoot == null) || (toServerDN == null) || (fromServerDN == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
          ASN1OctetString asn1_toServerDN = new ASN1OctetString(toServerDN);
          ASN1OctetString asn1_fromServerDN = new ASN1OctetString(fromServerDN);

            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_toServerDN.encode(encoder, encodedData);
            asn1_fromServerDN.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
      }
   }

}

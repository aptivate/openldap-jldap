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

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.resources.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 *
 *  Synchronizes all replicas of a naming context.
 *
 *  <p>The PartitionSyncRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.25</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; serverName&nbsp;&nbsp;&nbsp;      LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; partitionRoot&nbsp;&nbsp;&nbsp;   LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; delay&nbsp;&nbsp;&nbsp;           INTEGER</p>
 */
public class PartitionSyncRequest extends LDAPExtendedOperation {

/**
 *
 *   Constructs an extended operation object for synchronizing the replicas
 *   of a partition.
 *
 * @param serverName     The distinquished name of server containing the
 *                       naming context.
 * <br><br>
 * @param partitionRoot  The distinguished name of the naming context
 *                       to synchronize.
 *<br><br>
 * @param delay          The time, in seconds, to delay before the synchronization
 *                       should start.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public PartitionSyncRequest(String serverName, String partitionRoot, int delay)
                throws LDAPException {

        super(ReplicationConstants.NAMING_CONTEXT_SYNC_REQ, null);

        try {

            if ( (serverName == null) || (partitionRoot == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1OctetString asn1_serverName = new ASN1OctetString(serverName);
            ASN1OctetString asn1_partitionRoot = new ASN1OctetString(partitionRoot);
            ASN1Integer asn1_delay = new ASN1Integer(delay);

            asn1_serverName.encode(encoder, encodedData);
            asn1_partitionRoot.encode(encoder, encodedData);
            asn1_delay.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());
        }
        catch(IOException ioe) {
            throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                       LDAPException.ENCODING_ERROR,
                                       (String)null);
        }
    }
}

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
 *  Removes a replica from the specified directory server.
 *
 *  <p>To remove a replica from a particular server, you must create an instance
 *  of this class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The removeReplicaRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.11</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        flags &nbsp;&nbsp;&nbsp;       INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        serverName &nbsp;&nbsp;&nbsp;  LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;        dn &nbsp;&nbsp;&nbsp;          LDAPDN</p>
 */
public class RemoveReplicaRequest extends LDAPExtendedOperation {

/**
 * Constructs an extended operation object for removing a replica.
 *
 * @param dn          The distinguished name of the replica's
 *                    partition root.
 * <br><br>
 * @param serverDN    The distinguished name of server from which the replica
 *                    will be removed.
 * <br><br>
 * @param flags   Determines whether all servers in the replica ring must
 *                be up before proceeding. When set to zero, the status of the
 *                servers is not checked. When set to LDAP_ENSURE_SERVERS_UP,
 *                all servers must be up for the operation to proceed.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public RemoveReplicaRequest(String dn, String serverDN, int flags)
                throws LDAPException {

        super(ReplicationConstants.DELETE_REPLICA_REQ, null);

        try {

            if ( (dn == null) || (serverDN == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

          ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1Integer asn1_flags = new ASN1Integer(flags);
          ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
          ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_flags.encode(encoder, encodedData);
            asn1_serverDN.encode(encoder, encodedData);
            asn1_dn.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
      }
   }

}

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
 *  Changes the type of the replica that resides
 *  on the specified directory server.
 *
 *  <p>To change a replica's type, you must create an instance of this class and
 *  then call the extendedOperation method with this object as the required
 *  LDAPExtendedOperation parameter.</p>
 *
 *  <p>The changeReplicaTypeRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.15</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; flags &nbsp;&nbsp;&nbsp;&nbsp;       INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; replicaType&nbsp;&nbsp;&nbsp;&nbsp;  INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; serverName&nbsp;&nbsp;&nbsp;&nbsp;   LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; dn&nbsp;&nbsp;&nbsp;&nbsp;           LDAPDN</p>
 */
public class ChangeReplicaTypeRequest extends LDAPExtendedOperation {

/**
 *
 * Constructs a new extended operation object for changing a replica's type.
 *
 * @param dn          The distinguished name of the replica's
 *                    partition root.
 *<br><br>
 * @param serverDN    The server on which the replica resides.
 * <br><br>
 *
 * @param replicaType    The new replica type. The replica types are defined
 *                       in the ReplicationConstants class.
 *<br><br>
 * @param flags   Specifies whether all servers in the replica ring must be up
 *                before proceeding. When set to zero, the status of the servers is
 *                not checked. When set to LDAP_ENSURE_SERVERS_UP, all servers must be
 *                up for the operation to proceed.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 *
 * @see ReplicationConstants#LDAP_RT_MASTER
 * @see ReplicationConstants#LDAP_RT_SECONDARY
 * @see ReplicationConstants#LDAP_RT_READONLY
 * @see ReplicationConstants#LDAP_RT_SUBREF
 * @see ReplicationConstants#LDAP_RT_SPARSE_WRITE
 * @see ReplicationConstants#LDAP_RT_SPARSE_READ
 */
 public ChangeReplicaTypeRequest(String dn, String serverDN, int replicaType, int flags)
                throws LDAPException {

        super(ReplicationConstants.CHANGE_REPLICA_TYPE_REQ, null);

        try {

            if ( (dn == null) || (serverDN == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

         ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();

          ASN1Integer asn1_flags = new ASN1Integer(flags);
          ASN1Integer asn1_replicaType = new ASN1Integer(replicaType);
          ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
          ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_flags.encode(encoder, encodedData);
            asn1_replicaType.encode(encoder, encodedData);
            asn1_serverDN.encode(encoder, encodedData);
            asn1_dn.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String) null);
      }
   }

}

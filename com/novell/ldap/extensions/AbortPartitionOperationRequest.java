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
 *  Aborts the last partition operation that was requested on the 
 * specified partition if the operation is still pending.
 *
 *  <p>The AbortPartitionRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.29</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::= <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;  flags&nbsp;&nbsp;&nbsp;&nbsp;       INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;  partitionDN&nbsp;&nbsp;&nbsp;&nbsp; LDAPDN </p>
 */
public class AbortPartitionOperationRequest extends LDAPExtendedOperation {

/**
 * Constructs an extended operation object for aborting a partition operation.
 *
 * @param partitionDN The distinguished name of the replica's
 *                    partition root.<br><br>
 *
 * @param flags Determines whether all servers in the replica ring must
 *              be up before proceeding. When set to zero, the status of the
 *              servers is not checked. When set to LDAP_ENSURE_SERVERS_UP,
 *              all servers must be up for the operation to proceed.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public AbortPartitionOperationRequest(String partitionDN, int flags)
                throws LDAPException {

        super(ReplicationConstants.ABORT_NAMING_CONTEXT_OP_REQ, null);

        try {

            if (partitionDN == null)
                throw new IllegalArgumentException(
                                          ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();


            ASN1Integer asn1_flags = new ASN1Integer(flags);
            ASN1OctetString asn1_partitionDN = new ASN1OctetString(partitionDN);

            asn1_flags.encode(encoder, encodedData);
            asn1_partitionDN.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String) null);
      }
   }

}

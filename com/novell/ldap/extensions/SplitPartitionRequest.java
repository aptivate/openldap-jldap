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

import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPException;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 *  Creates a new partition.
 *
 *  <p>To split a new partition, you must create an instance of this
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The SplitPartitionRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.3</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; flags &nbsp;&nbsp;&nbsp;&nbsp; INTEGER<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; dn    &nbsp;&nbsp;&nbsp;&nbsp; LDAPDN</p>
 */
public class SplitPartitionRequest extends LDAPExtendedOperation {

/**
 *
 *  Constructs an extended operation object for splitting partition.
 *
 * @param dn      The distinguished name of the container where the new 
 *                partition  root should be located.
 *<br><br>
 * @param flags Specifies whether all servers in the replica ring must be up before
 *              proceeding. When set to zero, the status of the servers is not
 *              checked. When set to LDAP_ENSURE_SERVERS_UP, all servers must be up
 *              for the operation to proceed.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
    public SplitPartitionRequest(String dn, int flags) throws LDAPException {

        super(ReplicationConstants.CREATE_NAMING_CONTEXT_REQ, null);

        try {

            if (dn == null)
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1Integer asn1_flags = new ASN1Integer(flags);
            ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_flags.encode(encoder, encodedData);
            asn1_dn.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());
        }
        catch(IOException ioe) {
            throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String) null);
        }
    }
}

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
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Splits a new orphan partitiont.
 *
 * <p>To split a new orphan partition, you must create an instance of
 *  this class and then call the extendedOperation method with this object
 *  as the required LDAPExtendedOperation parameter.</p>
 *
 * <p>The SplitOrphanPartitionRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.39</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;  serverDN&nbsp;&nbsp;&nbsp;&nbsp;     LDAPDN<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;  contextName&nbsp;&nbsp;&nbsp;&nbsp;  LDAPDN</p>
 */
public class SplitOrphanPartitionRequest extends LDAPExtendedOperation {

/**
 *
 * Constructs an extended operation object for creating an orphan partition.
 *
 *
 * @param serverDN    The distinguished name of the server on which
 *                    the new orphan partition will reside.
 *<br><br>
 * @param contextName The distinguished name of the
 *                    new orphan partition.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
 public SplitOrphanPartitionRequest(String serverDN, String contextName)
                throws LDAPException {

        super(ReplicationConstants.CREATE_ORPHAN_NAMING_CONTEXT_REQ, null);

        try {

            if ( (serverDN == null) || (contextName == null) )
                throw new IllegalArgumentException(
                                          ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);
            ASN1OctetString asn1_contextName = new ASN1OctetString(contextName);

            asn1_serverDN.encode(encoder, encodedData);
            asn1_contextName.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());
        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String) null);
      }
   }
}

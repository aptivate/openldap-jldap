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
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPException;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.resources.ExceptionMessages;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 *  Returns a count of the number of entries (objects) in the
 *  specified partition.
 *
 *  <p>To obtain the count of entries, you must create an instance of this
 *  class and then call the extendedOperation method with this
 *  object as the required LDAPExtendedOperation parameter.</p>
 *
 *  <p>The returned LDAPExtendedResponse object can then be converted to
 *  a PartitionEntryCountResponse object. This class contains
 *  methods for retrieving the returned count.</p>
 *
 *  <p>The PartitionEntryCountRequest extension uses the following
 *  OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.13</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br><br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn &nbsp;&nbsp;&nbsp;     LDAPDN
 */
 public class PartitionEntryCountRequest extends LDAPExtendedOperation {

    
    static
    {
		/*
         * Register the extendedresponse class which is returned by the
		 * server in response to a PartitionEntryCountRequest
		 */
        try {
            LDAPExtendedResponse.register(
                  ReplicationConstants.NAMING_CONTEXT_COUNT_RES,
                  Class.forName(
                  "com.novell.ldap.extensions.PartitionEntryCountResponse"));
        }catch (ClassNotFoundException e) {
            System.err.println("Could not register Extended Response -" +
                               " Class not found");
        }catch(Exception e){
           e.printStackTrace();
        }
        
    }
    
    /**
    *  Constructs an extended operation object for counting entries
    *  in a naming context.
    *
    * @param dn  The distinguished name of the partition.
    *
    * @exception LDAPException A general exception which includes an
    *                          error message and an LDAP error code.
    */

    public PartitionEntryCountRequest(String dn) throws LDAPException {

        super(ReplicationConstants.NAMING_CONTEXT_COUNT_REQ, null);

        try {

            if ( (dn == null) )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1OctetString asn1_dn = new ASN1OctetString(dn);

            asn1_dn.encode(encoder, encodedData);

            setValue(encodedData.toByteArray());

        }
        catch(IOException ioe) {
            throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
        }
    }
}

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
 *  Sets the Replication filter for all replicas on the server.
 *
 * <p>The filter specified is a an array of classnames-attribute names pairs. </p>
 *
 *  <p>To set the filter for all replicas on the connected server, you must
 *  create an instance of this class and then call the
 *  extendedOperation method with this object as the required
 *  LDAPExtendedOperation parameter.</p>
 *
 *  <p>The SetReplicationFilterRequest extension uses the following OID:<br>
 *   &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.35</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp; serverName&nbsp;&nbsp;&nbsp;  LDAPDN</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of SEQUENCE {</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; classname&nbsp;&nbsp;&nbsp;  OCTET STRING</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of ATTRIBUTES</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp;}</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp;where</p>
 *  &nbsp;&nbsp;&nbsp;&nbsp;ATTRIBUTES:: OCTET STRING</p>
 */
public class SetReplicationFilterRequest extends LDAPExtendedOperation {

/**
 *
 * Constructs an extended operations object which contains the ber encoded
 * replication filter.
 *
 * @param serverDN The server on which the replication filter needs to be set
 * <br><br>
 * @param replicationFilter An array of String Arrays. Each array starting with
 * a class name followed by the attribute names for that class that should comprise
 * the replication filter.
 * <br><br>
 * @exception LDAPException A general exception which includes an error
 *                          message and an LDAP error code.
 */
 public SetReplicationFilterRequest(String serverDN,  String[][] replicationFilter)
                throws LDAPException {

        super(ReplicationConstants.SET_REPLICATION_FILTER_REQ, null);

        try {

            if (serverDN == null)
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);

            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1OctetString asn1_serverDN = new ASN1OctetString(serverDN);

            // Add the serverDN to encoded data
            asn1_serverDN.encode(encoder, encodedData);

            // The toplevel sequenceOF
            ASN1SequenceOf asn1_replicationFilter = new ASN1SequenceOf();

            if (replicationFilter == null) {
                asn1_replicationFilter.encode(encoder, encodedData);
                setValue(encodedData.toByteArray());
                return;
            }

            int i = 0;
            // for every element in the array
            while ( (i < replicationFilter.length) && (replicationFilter[i] != null) ) {


                // The following additional Sequence is not needed
                // as defined by the ASN1. But the server and the
                // C client are encoding it. Remove this when server
                // and C client are fixed to conform to the published ASN1.
                ASN1Sequence buginASN1Representation = new ASN1Sequence();

                // Add the classname to the sequence -
                buginASN1Representation.add(new ASN1OctetString(replicationFilter[i][0]));

                // Start a sequenceOF for attributes
                ASN1SequenceOf asn1_attributeList = new ASN1SequenceOf();

                // For every attribute in the array - remember attributes start after
                // the first element
                int j = 1;
                while ( ( j < replicationFilter[i].length) && (replicationFilter[i][j] != null) ) {

                    // Add the attribute name to the inner SequenceOf
                    asn1_attributeList.add(new ASN1OctetString(replicationFilter[i][j]));
                    j++;
                }


                // Add the attributeList to the sequence - extra add due to bug
                buginASN1Representation.add(asn1_attributeList);
                asn1_replicationFilter.add(buginASN1Representation);
                i++;
            }

            asn1_replicationFilter.encode(encoder, encodedData);
            setValue(encodedData.toByteArray());

        }
      catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
      }
   }

}

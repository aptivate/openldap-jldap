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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Set;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LDAPRestoreRequest extends LDAPExtendedOperation {

	public LDAPRestoreRequest(String objectDN, int bufferLength, String buffer)
			throws LDAPException, LDAPLocalException {

		super(BackupRestoreConstants.NLDAP_LDAP_RESTORE_REQUEST, null);

		try {
			if (objectDN == null || bufferLength == 0 || buffer == null)
				throw new IllegalArgumentException(
						ExceptionMessages.PARAM_ERROR);

			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			LBEREncoder encoder = new LBEREncoder();

			//rervers parse the buffer
			int index;
			int chunkSize;
			int chunks[] = null;
			index = buffer.indexOf(';');
			try {
				chunkSize = Integer.parseInt(buffer.substring(0, index));
			} catch (NumberFormatException e) {
				throw new LDAPLocalException(
						"Invalid data buffer send in the request",
						LDAPException.ENCODING_ERROR);
			}

			if (chunkSize == 0)
				throw new IllegalArgumentException(
						ExceptionMessages.PARAM_ERROR);

			buffer = buffer.substring(index + 1);

			int chunkIndex;
			chunks = new int[chunkSize];
			for (int i = 0; i < chunkSize; i++) {
				chunkIndex = buffer.indexOf(';');
				chunks[i] = Integer.parseInt(buffer.substring(0, chunkIndex));
				buffer = buffer.substring(chunkIndex + 1);
			}

			ASN1OctetString asn1_serverDN = new ASN1OctetString(objectDN);
			//Form the sequence to be passed to Server
			ASN1Sequence asn1_chunksSeq = new ASN1Sequence();
			asn1_chunksSeq.add(new ASN1Integer(chunkSize));
			ASN1Set asn1_chunksSet = new ASN1Set();

			for (int i = 0; i < chunkSize; i++) {
				ASN1Integer tmpChunk = new ASN1Integer(chunks[i]);
				ASN1Sequence tmpSeq = new ASN1Sequence();
				tmpSeq.add(tmpChunk);
				asn1_chunksSet.add(tmpSeq);

				//asn1_chunksSet.add((new ASN1Sequence()).add(new
				// ASN1Integer(chunks[i])));
			}
			asn1_chunksSeq.add(asn1_chunksSet);

			ASN1Integer asn1_bufferLength = new ASN1Integer(bufferLength);
			
			ASN1OctetString asn1_buffer = new ASN1OctetString(buffer);

			asn1_chunksSeq.encode(encoder, encodedData);
			asn1_serverDN.encode(encoder, encodedData);
			asn1_bufferLength.encode(encoder, encodedData);
			asn1_buffer.encode(encoder, encodedData);

			setValue(encodedData.toByteArray());

		} catch (IOException ioe) {
			throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
					LDAPException.ENCODING_ERROR, (String) null);
		}
	}
}
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Set;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.rfc2251.RfcLDAPMessage;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LDAPBackupResponse extends LDAPExtendedResponse {

	private int bufferLength;
	private String stateInfo;
	private String parsedString; 
	//parsedString = no_of_chunks;sizeOf(chunk1);sizeOf(chunk2)..sizeOf(chunkn);returnedBuffer
	
	public LDAPBackupResponse(RfcLDAPMessage rfcMessage) throws IOException {

		super(rfcMessage);
		
		int modificationTime = 0;
		int revision = 0;
		String returnedBuffer = null;
		int chunksSize = 0;
		int[] chunks = null; //This will hold size of each chunks returned from server

		if (getID() == null
				|| !(getID()
						.equals(BackupRestoreConstants.NLDAP_LDAP_BACKUP_RESPONSE)))
			throw new IOException("LDAP Extended Operation not supported");

		if (getResultCode() == LDAPException.SUCCESS) {
			// parse the contents of the reply
			byte[] returnedValue = this.getValue();
			if (returnedValue == null)
				throw new IOException(
						"LDAP Operations error. No returned value.");

			// Create a decoder object
			LBERDecoder decoder = new LBERDecoder();
			if (decoder == null)
				throw new IOException("Decoding error");

			// Parse the parameters in the order
			ByteArrayInputStream currentPtr = new ByteArrayInputStream(
					returnedValue);

			// Parse bufferLength
			ASN1Integer asn1_bufferLength = (ASN1Integer) decoder
					.decode(currentPtr);
			if (asn1_bufferLength == null)
				throw new IOException("Decoding error");

			bufferLength = asn1_bufferLength.intValue();
			System.out.println("sudhir buffer length =" + bufferLength);

			// Parse modificationTime
			ASN1Integer asn1_modificationTime = (ASN1Integer) decoder
					.decode(currentPtr);
			if (asn1_modificationTime == null)
				throw new IOException("Decoding error");

			modificationTime = asn1_modificationTime.intValue();

			// Parse revision
			ASN1Integer asn1_revision = (ASN1Integer) decoder
					.decode(currentPtr);
			if (asn1_revision == null)
				throw new IOException("Decoding error");

			revision = asn1_revision.intValue();
			
			//format stateInfo
			this.stateInfo = modificationTime + "+" + revision;

			// Parse returnedBuffer
			ASN1OctetString asn1_returnedBuffer = (ASN1OctetString) decoder
					.decode(currentPtr);
			if (asn1_returnedBuffer == null)
				throw new IOException("Decoding error");

			returnedBuffer = asn1_returnedBuffer.stringValue();
			System.out.println("Ravi buffer =" + returnedBuffer);
			System.out.println("sudhir buffer size =" + returnedBuffer.length());

			//Chunks are encoded as shown below
			//SEQUENCE{no_of_seq, SET{SEQUENCE1{size1}, SEQUENCE2{size2}....SEQUENCEn{sizen}} }
			
			ASN1Sequence asn1_chunksSeq = (ASN1Sequence) decoder
					.decode(currentPtr);
			if (asn1_chunksSeq == null)
				throw new IOException("Decoding error");
			
			chunksSize = ((ASN1Integer)asn1_chunksSeq.get(0)).intValue();

			chunks = new int[chunksSize];
			
			ASN1Set asn1_chunksSet =  (ASN1Set)asn1_chunksSeq.get(1);

			for (int index = 0; index < chunksSize; index++) {
				ASN1Sequence asn1_eachSeq = (ASN1Sequence)asn1_chunksSet.get(index);
				chunks[index] = ((ASN1Integer)asn1_eachSeq.get(0)).intValue();
			}
						
			StringBuffer tempBuffer = new StringBuffer();
			tempBuffer.append(chunksSize);
			tempBuffer.append(";");
			for (int i = 0; i < chunksSize; i++) {
				tempBuffer.append(chunks[i]);
				tempBuffer.append(";");
			}
			tempBuffer.append(returnedBuffer);

			this.parsedString = tempBuffer.toString();
		} else {
			this.bufferLength = 0;
			this.stateInfo = null;
			this.parsedString = null;
		}

	}

	public int getBufferLength() {
		return bufferLength;
	}

	public String getParsedString() {
				return parsedString;
	}
	
	public String getStatusInfo(){
		return stateInfo;
	}
}
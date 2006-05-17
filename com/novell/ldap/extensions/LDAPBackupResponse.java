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
 *  This object represent the data returned from a LDAPBackupRequest.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 *  <p>The LDAPBackupResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.97</p>
 *
 */
public class LDAPBackupResponse extends LDAPExtendedResponse {

	private int bufferLength; //Represents the length of backup data
	private String stateInfo; //Represent the state Information of data
	
	/*
	 * The String representing the number of chunks and each elements in chunk
	 * array as returned by server.
	 * Data from server is parsed as follows before sending to any Application::
	 * no_of_chunks;sizeOf(chunk1);sizeOf(chunk2);sizeOf(chunkn)
	 * where
	 * no_of_chunks => Represents the number of chunks of data returned from server
	 * sizeOf(chunkn) => Represents the size of data in chunkn
	 */	
	private String chunkSizesString;
	
	/*
	 * Actual data of returned eDirectoty Object in byte[]
	 */
	private byte[] returnedBuffer;
	
	/**
    * Constructs an object from the responseValue which contains the backup data.
    *  <p>The constructor parses the responseValue which has the following
    *  format:<br>
    *  responseValue ::=<br>
	*  <p>databufferLength ::= INTEGER <br>
	*  mts(modification time stamp) ::= INTEGER<br>
	*  revision ::= INTEGER<br>
	*  returnedBuffer ::= OCTET STRING<br>
	*  dataChunkSizes ::= <br>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp; 
	*  SEQUENCE{<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	*  noOfChunks INTEGER<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	*  SET of [<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	*  SEQUENCE of {eachChunksize INTEGER}]<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	*  }</p>
	* 
    * @exception IOException The responseValue could not be decoded.
    */
	public LDAPBackupResponse(RfcLDAPMessage rfcMessage) throws IOException {

		//Call the super constructor
		super(rfcMessage);
		
		int modificationTime = 0; // Modifaction timestamp of the Object
		int revision = 0; // Revision number of the Object
		int chunksSize = 0;
		int[] chunks = null; //Holds size of each chunks returned from server

		//Verify if returned ID is not proper
		if (getID() == null
				|| !(getID()
						.equals(BackupRestoreConstants.NLDAP_LDAP_BACKUP_RESPONSE)))
			throw new IOException("LDAP Extended Operation not supported");

		if (getResultCode() == LDAPException.SUCCESS) {
			// Get the contents of the reply
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
			
			//Format stateInfo to contain both modificationTime and revision
			this.stateInfo = modificationTime + "+" + revision;

			// Parse returnedBuffer
			ASN1OctetString asn1_returnedBuffer = (ASN1OctetString) decoder
					.decode(currentPtr);
			if (asn1_returnedBuffer == null)
				throw new IOException("Decoding error");
			returnedBuffer = asn1_returnedBuffer.byteValue();
		
			
			/* 
			 * Parse chunks array 
			 * Chunks returned from server is encoded as shown below::
			 * SEQUENCE{
			 * 			chunksSize	INTEGER
			 * 			SET of [
			 * 				SEQUENCE of {eacChunksize        INTEGER}]
			 * 	       }
			 */
			ASN1Sequence asn1_chunksSeq = (ASN1Sequence) decoder
					.decode(currentPtr);
			if (asn1_chunksSeq == null)
				throw new IOException("Decoding error");
			
			//Get number of chunks returned from server
			chunksSize = ((ASN1Integer)asn1_chunksSeq.get(0)).intValue();
			
			//Construct chunks array
			chunks = new int[chunksSize];
			
			ASN1Set asn1_chunksSet =  (ASN1Set)asn1_chunksSeq.get(1);
			//Iterate through asn1_chunksSet and put each size into chunks array
			for (int index = 0; index < chunksSize; index++) {
				ASN1Sequence asn1_eachSeq = (ASN1Sequence)asn1_chunksSet.get(index);
				chunks[index] = ((ASN1Integer)asn1_eachSeq.get(0)).intValue();
			}
						
			//Construct a temporary StringBuffer and append chunksSize, each size
			//element in chunks array and actual data of eDirectoty Object
			StringBuffer tempBuffer = new StringBuffer();
			tempBuffer.append(chunksSize);
			tempBuffer.append(";");
			int i = 0;
			for (; i < (chunksSize - 1); i++) {
				tempBuffer.append(chunks[i]);
				tempBuffer.append(";");
			}
			tempBuffer.append(chunks[i]);

			//Assign tempBuffer to parsedString to be returned to Application
			this.chunkSizesString = tempBuffer.toString();
		} else {
			//Intialize all these if getResultCode() != LDAPException.SUCCESS
			this.bufferLength = 0;
			this.stateInfo = null;
			this.chunkSizesString = null;
			this.returnedBuffer = null;
		}

	}
	
	/**
     * Returns the data buffer length
     *
     * @return bufferLength as integer.
     */
	public int getBufferLength() {
		return bufferLength;
	}
	
	/**
     * Returns the stateInfo of returned eDirectory Object.
     * This is combination of MT (Modification Timestamp) and
     * Revision value with char '+' as separator between two.<br>
     * Client application if want to use both MT and Revision need to break
     * this string to get both these data.
     *
     * @return stateInfo as String.
     */
	public String getStatusInfo(){
		return stateInfo;
	}

	/**
     * Returns the data in String as::<br>
     * no_of_chunks;sizeOf(chunk1);sizeOf(chunk2);sizeOf(chunkn)<br>
     * where<br>
     * no_of_chunks => Represents the number of chunks of data returned from server<br>
	 * sizeOf(chunkn) => Represents the size of data in chunkn<br>
	 * 
     * @return chunkSizesString as String.
     */
	public String getChunkSizesString() {
				return chunkSizesString;
	}
	
	/**
     * Returns the data buffer as byte[]
     *
     * @return returnedBuffer as byte[].
     */
	public byte[] getReturnedBuffer() {
		return returnedBuffer;
	}
	
}

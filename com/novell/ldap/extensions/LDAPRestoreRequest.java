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
*
* This class provides an LDAP interface for object based  
* restore of eDirectory objects.
*
* <p>The information need for restore includes such items as  object DN,
* data buffer length, string containing the number of chunks and each chunk
* elements representing the size of each chunk, data blob in byte[]. The API
* support restoring of both non-encrypted and encrypted objects.
* </p>
* 
* <p>To send this request to eDirectory, you must
* create an instance of this class and then call the
* extendedOperation method with this object as the required
* LDAPExtendedOperation parameter.</p><br>
*
* <p>The getLDAPRestoreRequest extension uses the following OID:<br>
* &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.98</p><br>
*
* <p>The requestValue has the following format:<br>
*
* <p>requestValue ::=<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; objectDN ::= LDAPDN<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; passwd	  ::= OCTET STRING<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; bufferLength ::= INTEGER<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; retunedBuffer::= OCTET STRING<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dataChunkSizes ::=<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE {<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
* noOfChunks INTEGER<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp; SET of [<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
* SEQUENCE of {eacChunksize INTEGER}]<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
* &nbsp;&nbsp;&nbsp; 
* }<br> </p>
*/
public class LDAPRestoreRequest extends LDAPExtendedOperation {

	
	/**
	 *
	 * Constructs an extended operations object which contains the ber encoded
	 * restore data.
	 *
	 * @param objectDN The object DN to restore
	 * <br>
	 * @param passwd 		The encrypted password required for the object to
	 * be backed up
	 * <br>
	 * @param bufferLength The length of backed up data
	 * <br>
	 * @param chunkSizesString The String containing number of chunks and 
	 * each chunk elements representing chunk sizes
	 * <br>
	 * @param returnedBuffer The actual data in byte[]
	 * <br><br>
	 * @exception LDAPException A general exception which includes an error
	 *                          message and an LDAP error code.
	 */
	public LDAPRestoreRequest(String objectDN, byte[] passwd, 
			int bufferLength, String chunkSizesString, byte[] returnedBuffer)
			throws LDAPException {

		super(BackupRestoreConstants.NLDAP_LDAP_RESTORE_REQUEST, null);

		try {
			//Verify the validity of arguments
			if (objectDN == null || bufferLength == 0 || 
				chunkSizesString == null || returnedBuffer == null)
					throw new IllegalArgumentException(
							ExceptionMessages.PARAM_ERROR);
			
			//If encrypted password has null reference make it null String
			if(passwd == null)
				passwd = "".getBytes("UTF8");
			
			/*
			 * From the input argument chunkSizesString get::
			 * chunkSize => Represents the number of chunks of data returned from server
			 * sizeOf each chunk => int represents the size of each chunk
			 */
			int index;
			int chunkSize;
			int chunks[] = null;
			index = chunkSizesString.indexOf(';');
			try {
				chunkSize = Integer.parseInt(chunkSizesString.substring(0, index));
			} catch (NumberFormatException e) {
				throw new LDAPLocalException(
						"Invalid data buffer send in the request",
						LDAPException.ENCODING_ERROR);
			}
			//Return exception if chunkSize == 0
			if (chunkSize == 0)
				throw new IllegalArgumentException(
						ExceptionMessages.PARAM_ERROR);

			chunkSizesString = chunkSizesString.substring(index + 1);

			int chunkIndex;
			//Construct chunks array
			chunks = new int[chunkSize];
			/*
			 * Iterate through each member in buffer and
			 * assign to chunks array elements
			 */
			for (int i = 0; i < chunkSize; i++) {
				chunkIndex = chunkSizesString.indexOf(';');
				if(chunkIndex == -1){
					chunks[i] = Integer.parseInt(chunkSizesString);
					break;
				}
				chunks[i] = Integer.parseInt(chunkSizesString.substring(0,
															chunkIndex));
				chunkSizesString = chunkSizesString.substring(chunkIndex + 1);
			}
			
			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			LBEREncoder encoder = new LBEREncoder();

			//Form objectDN, passwd, bufferLength, data byte[] as ASN1 Objects
			ASN1OctetString asn1_objectDN = new ASN1OctetString(objectDN);
			ASN1OctetString asn1_passwd = new ASN1OctetString(passwd);
			ASN1Integer asn1_bufferLength = new ASN1Integer(bufferLength);
			ASN1OctetString asn1_buffer = new ASN1OctetString(returnedBuffer);
			
			//Form the chunks sequence to be passed to Server
			ASN1Sequence asn1_chunksSeq = new ASN1Sequence();
			asn1_chunksSeq.add(new ASN1Integer(chunkSize));
			ASN1Set asn1_chunksSet = new ASN1Set();
			for (int i = 0; i < chunkSize; i++) {
				ASN1Integer tmpChunk = new ASN1Integer(chunks[i]);
				ASN1Sequence tmpSeq = new ASN1Sequence();
				tmpSeq.add(tmpChunk);
				asn1_chunksSet.add(tmpSeq);
			}
			asn1_chunksSeq.add(asn1_chunksSet);

			//Encode data to send to server
			asn1_objectDN.encode(encoder, encodedData);
			asn1_passwd.encode(encoder, encodedData);
			asn1_bufferLength.encode(encoder, encodedData);
			asn1_buffer.encode(encoder, encodedData);
			asn1_chunksSeq.encode(encoder, encodedData);
			
			// set the value of operation specific data
			setValue(encodedData.toByteArray());

		} catch (IOException ioe) {
			throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
					LDAPException.ENCODING_ERROR, (String) null);
		}
	}
}
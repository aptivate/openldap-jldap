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
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBEREncoder;
import com.novell.ldap.resources.ExceptionMessages;
import com.novell.ldap.asn1.ASN1Integer;

/**
*
* This class provides an LDAP interface for object based backup 
* of eDirectory objects. The backup API not only get the objects
* but all the DS level attributes associated with the objects.
*
* <p>The information available includes such items as  modification timestamp,
* revision,data blob consisting of backup data of any eDirectory Object. The API
* support backing of both non-encrypted and encrypted objects
* </p>
*
* <p>To get information about any eDirectory Object, you must
* create an instance of this class and then call the
* extendedOperation method with this object as the required
* LDAPExtendedOperation parameter.</p>
*
* <p>The getLDAPBackupRequest extension uses the following OID:<br>
* &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.96</p><br>
*
* <p>The requestValue has the following format:<br>
*
* requestValue ::=<br>
* &nbsp;&nbsp;&nbsp;&nbsp; objectDN&nbsp;&nbsp;&nbsp; 			LDAPDN<br>
* &nbsp;&nbsp;&nbsp;&nbsp; mts(modification timestamp)         INTEGER<br>
* &nbsp;&nbsp;&nbsp;&nbsp; revision&nbsp;&nbsp;&nbsp;			INTEGER<br>
* &nbsp;&nbsp;&nbsp;&nbsp; passwd&nbsp;&nbsp;&nbsp;			OCTET STRING</p>
*/
public class LDAPBackupRequest extends LDAPExtendedOperation {

	static {
		/*
		 * Register the extendedresponse class which is returned by the server
		 * in response to a LDAPBackupRequest
		 */
		try {
			LDAPExtendedResponse.register(
				BackupRestoreConstants.NLDAP_LDAP_BACKUP_RESPONSE,
				Class.forName("com.novell.ldap.extensions.LDAPBackupResponse"));
		} catch (ClassNotFoundException e) {
			System.err.println("Could not register Extended Response -"
					+ " Class not found");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Constructs an extended operations object for getting data about any Object.
	 *
	 * @param objectDN 		The DN of the object to be backed up
	 * <br>
	 * @param passwd 		The encrypted password required for the object to
	 * be backed up
	 * <br>
	 * @param stateInfo     The state information of the object to backup. 
	 * This parameter is a String which contains combination of modification 
	 * timestamp and revision number of object being backed up. The format 
	 * of both modification time stamp and revision should pertain to eDirectoty
	 * standard format of taking modification timestamp and revision.
	 * Separator being used between these two is a '+' character.<br> 
	 *
	 *
	 * @exception LDAPException A general exception which includes an error
	 *                          message and an LDAP error code.
	 */
	public LDAPBackupRequest(String objectDN, byte[] passwd, String stateInfo)
			throws LDAPException {

		super(BackupRestoreConstants.NLDAP_LDAP_BACKUP_REQUEST, null);

		int mts;		// Modifaction time stamp of the Object
		int revision;   // Revision number of the Object
		String mtsStr, revisionStr;
		
		try {
			if (objectDN == null)
				throw new IllegalArgumentException(
						ExceptionMessages.PARAM_ERROR);
			
			//If encrypted password has null reference make it null String
			if(passwd == null)
				passwd = "".getBytes("UTF8");
			
			if (stateInfo == null) {
				// If null reference is passed in stateInfo initialize both
				// mts and revision
				mts = 0;
				revision = 0; 
			} else {
				// Parse the passed stateInfo to obtain mts and revision
				stateInfo = stateInfo.trim();
				int index = stateInfo.indexOf('+');
				if(index == -1)
					throw new IllegalArgumentException(
							ExceptionMessages.PARAM_ERROR);
				mtsStr = stateInfo.substring(0, index);
				revisionStr = stateInfo.substring(index + 1);
				try {
					mts = Integer.parseInt(mtsStr);
				} catch (NumberFormatException e) {
					throw new LDAPLocalException(
							"Invalid Modification Timestamp send in the request",
							LDAPException.ENCODING_ERROR);
				}
				try {
					revision = Integer.parseInt(revisionStr);
				} catch (NumberFormatException e) {
					throw new LDAPLocalException(
							"Invalid Revision send in the request",
							LDAPException.ENCODING_ERROR);
				}
			}

			ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
			LBEREncoder encoder = new LBEREncoder();
				
			// Encode data of objectDN, mts and revision
			ASN1OctetString asn1_objectDN = new ASN1OctetString(objectDN);
			ASN1Integer asn1_mts = new ASN1Integer(mts);
			ASN1Integer asn1_revision = new ASN1Integer(revision);
			ASN1OctetString asn1_passwd = new ASN1OctetString(passwd);

			asn1_objectDN.encode(encoder, encodedData);
			asn1_mts.encode(encoder, encodedData);
			asn1_revision.encode(encoder, encodedData);
			asn1_passwd.encode(encoder, encodedData);
			
			// set the value of operation specific data
			setValue(encodedData.toByteArray());

		} catch (IOException ioe) {
			throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
					LDAPException.ENCODING_ERROR, (String) null);
		}
	}
}
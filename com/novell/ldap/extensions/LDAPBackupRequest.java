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
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
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

	public LDAPBackupRequest(String objectDN, String stateInfo)
			throws LDAPException, LDAPLocalException {

		super(BackupRestoreConstants.NLDAP_LDAP_BACKUP_REQUEST, null);

		int mts, revision; //mts = modifaction time stamp
		String mtsStr, revisionStr;
		try {
			if (objectDN == null)
				throw new IllegalArgumentException(
						ExceptionMessages.PARAM_ERROR);

			if (stateInfo == null) {
				mts = 0;
				revision = 0; //initialize these
			} else {
				//Parse this. stateInfo is should contain mts+revision
				stateInfo = stateInfo.trim();
				int index = stateInfo.indexOf('+');
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

			ASN1OctetString asn1_serverDN = new ASN1OctetString(objectDN);
			ASN1Integer asn1_mts = new ASN1Integer(mts);
			ASN1Integer asn1_revision = new ASN1Integer(revision);

			asn1_serverDN.encode(encoder, encodedData);
			asn1_mts.encode(encoder, encodedData);
			asn1_revision.encode(encoder, encodedData);

			setValue(encodedData.toByteArray());

		} catch (IOException ioe) {
			throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
					LDAPException.ENCODING_ERROR, (String) null);
		}
	}
}
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

/**
 * Contains a collection of constants used by the Novell LDAP Backup 
 * and Restore extensions.
 */
public class BackupRestoreConstants {

	
	/**
     * A constant for eDirectory LDAP Based Backup Request OID. 
     */
    public static final String NLDAP_LDAP_BACKUP_REQUEST
							= "2.16.840.1.113719.1.27.100.96";
    
    /**
     * A constant for eDirectory LDAP Based Backup Response OID. 
     */
    public static final String NLDAP_LDAP_BACKUP_RESPONSE
							= "2.16.840.1.113719.1.27.100.97";
    
    /**
     * A constant for eDirectory LDAP Based Restore Request OID. 
     */
    public static final String NLDAP_LDAP_RESTORE_REQUEST
							= "2.16.840.1.113719.1.27.100.98";
    
    
    /**
     * A constant for eDirectory LDAP Based Restore Response OID. 
     */
    public static final String NLDAP_LDAP_RESTORE_RESPONSE
							= "2.16.840.1.113719.1.27.100.99";
		
	/**
	 * Default constructor
	 */
	public BackupRestoreConstants() {
		super();
	}

}

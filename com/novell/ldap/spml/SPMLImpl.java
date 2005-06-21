/* **************************************************************************
*
* Copyright (C) 2005 Marc Boorshtein, Inc. All Rights Reserved.
*
* THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
* TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
* TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
* AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
* IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
* OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
* PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM OCTET STRING, INC., 
* COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
******************************************************************************/
package com.novell.ldap.spml;

import org.openspml.client.SpmlClient;

import com.novell.ldap.LDAPException;

/**
 * @author Marc Boorshtein
 * 
 * Base interface for accessing vendor specific SPML
 * implementation classes
 *
 */
public interface SPMLImpl {

	/**
	 * Returns the implementation of the SpmlClient
	 * @return
	 */
	public SpmlClient getSpmlClient();
	
	/**
	 * Performs a login
	 * @param username The user's name
	 * @param password The user's password
	 */
	public void login(String username,String password) throws LDAPException;
	
	public void logout() throws LDAPException;
}

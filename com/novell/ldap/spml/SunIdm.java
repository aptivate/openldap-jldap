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

import org.openspml.client.LighthouseClient;
import org.openspml.client.SpmlClient;
import org.openspml.util.SpmlException;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;

/**
 * @author mboorshtei002
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SunIdm implements SPMLImpl{
	LighthouseClient con;
	
	/* (non-Javadoc)
	 * @see com.novell.ldap.spml.SPMLImpl#getSpmlClient()
	 */
	public SpmlClient getSpmlClient() {
		if (con == null) {
			this.con = new LighthouseClient();
		}
		
		return con;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.spml.SPMLImpl#login(java.lang.String, java.lang.String)
	 */
	public void login(String username, String password) throws LDAPException {
		try {
			con.setUser(username);
			con.setPassword(password);
			con.login();
	
		} catch (SpmlException e) {
			throw new LDAPLocalException("Error logging in", LDAPException.INVALID_CREDENTIALS, e);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.spml.SPMLImpl#logout()
	 */
	public void logout() throws LDAPException {
		try {
			con.logout();
		} catch (SpmlException e) {
			throw new LDAPLocalException("Error logging out", 53, e);
		}
		
	}

	
}

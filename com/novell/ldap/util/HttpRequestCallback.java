/* **************************************************************************
 *
 * Copyright (C) 2004 Octet String, Inc. All Rights Reserved.
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
package com.novell.ldap.util;

import org.apache.commons.httpclient.methods.*;
import com.novell.ldap.*;

/**
 * 
 * @author Marc Boorshtein
 *
 * This interfact exposes methods that allow for the manipulation of the HTTP Request before it
 * is sent to the server.  This is usefaul for adjusting HTTP headers
 */
public interface HttpRequestCallback {
	/**
	 * Used for manipulating the post method before the DSMLv2 is sent
	 * to the server
	 * @param post The POST method 
	 * @param con The current connection
	 */
	public void manipulationPost(PostMethod post, DsmlConnection con);
}

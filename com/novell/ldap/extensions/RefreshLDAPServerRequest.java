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

import com.novell.ldap.*;

/**
 *
 *  Reloads the LDAP server.
 *
 *  <p>The refreshLDAPServerRequest extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.9</p>
 *
 *  <p>The requestValue is set to null.</p>
 */
public class RefreshLDAPServerRequest extends LDAPExtendedOperation {

/**
 *
 *  Constructs an extended operation object for reloading the LDAP server.
 *
 *  <p>The constructor sets the OID.</p>
 *
 *  @exception LDAPException A general exception which includes an error
 *                           message and an LDAP error code.
 */
 public RefreshLDAPServerRequest()
                throws LDAPException {

        super(ReplicationConstants.REFRESH_SERVER_REQ, null);

   }

}

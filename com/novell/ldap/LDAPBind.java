/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPBind.java,v 1.4 2000/08/25 17:38:45 judy Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
package com.novell.ldap;

/*
* 4.4 public interface LDAPBind
*/
 
/**
 *
 *  Used to do explicit bind processing on a referral. 
 *
 *  <p>A client can specify an instance of this class to be used
 *  on a single operation (through the LDAPConstraints object) 
 *  or for all operations (through LDAPConnection.setOption method).</p>
 *  
 */
public interface LDAPBind {

   /*
    * 4.4.1 bind
    */

   /**
    * Called by LDAPConnection when authenticating.
    *
    * <p> An implementation can access the host, port, credentials, and other
    * information in the LDAPConnection to decide on an appropriate
    * authentication mechanism, and/or interact with a user or external
    * module. An LDAPException is thrown on failure, as in
    * LDAPConnection.bind method. </p>
    *
    * @param conn    An established connection to an LDAP server.
    *
    * @exception  LDAPException A general exception which includes an error
    * message and an LDAP error code.
    */
   public void bind (LDAPConnection conn) throws LDAPException;

}

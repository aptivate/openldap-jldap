/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPRebind.java,v 1.3 2000/08/03 22:06:16 smerrill Exp $
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
 
/**
 * 4.19 public interface LDAPRebind
 *
 *  Used to provide credentials for reauthentication on processing a
 *  referral.
 */
public interface LDAPRebind {

   /*
    * 4.19.1 getRebindAuthentication
    */

   /**
    * Returns an object which can provide credentials for authenticating to
    * a server at the provided host name and port number.
    *
    * Parameters are:
    *
    *  host           Contains a hostname or dotted string representing
    *                  the IP address of a host running an LDAP server.
    *
    *  port           Contains the TCP or UDP port number to connect
    *                  to.
    */
   public LDAPRebindAuth getRebindAuthentication (String host, int port);

}



/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPRebind.java,v 1.5 2000/09/11 22:47:49 judy Exp $
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
 * 4.19 public interface LDAPRebind
 */
 
/**
 *
 *  Used to provide credentials for reauthentication when processing a
 *  referral.
 */
public interface LDAPRebind {

   /*
    * 4.19.1 getRebindAuthentication
    */

   /**
    * Returns an object which can provide credentials for authenticating to
    * a server at the specified host and port.
    *
    *  @param host    Contains a host name or the IP address (in dotted string 
    *                 format) of a host running an LDAP server.
    *<br><br>
    *  @param port    Contains the TCP or UDP port number of the host.
    *
    *  @return An object with authentication credentials to the specified 
    *          host and port.                  
    */
   public LDAPRebindAuth getRebindAuthentication (String host, int port);

}



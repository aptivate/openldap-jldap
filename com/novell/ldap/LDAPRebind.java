/**
 * 4.19 public interface LDAPRebind
 *
 *  Used to provide credentials for reauthentication on processing a
 *  referral.
 */
package com.novell.ldap; 
 
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



/**
 * 4.25 public interface LDAPSocketFactory
 *
 *  Used to construct a socket connection for use in an LDAPConnection.
 *  An implementation of this interface may, for example, provide a
 *  TLSSocket connected to a secure server.
 */
package com.novell.ldap; 

import java.net.*;
import java.io.IOException;

public interface LDAPSocketFactory {

   /*
    * 4.25.1 makeSocket
    */

   /**
    * Returns a socket connected using the provided host name and port
    * number.
    *
    * There may be additional makeSocket methods defined when interfaces to
    * establish TLS and SASL authentication in the java environment have
    * been standardized.
    * Parameters are:
    *  host           Contains a hostname or dotted string representing
    *                  the IP address of a host running an LDAP server
    *                  to connect to.
    *
    *  port           Contains the TCP or UDP port number to connect to
    *                  or contact. The default LDAP port is 389.
    */
   public Socket makeSocket(String host, int port)
      throws IOException, UnknownHostException;

}

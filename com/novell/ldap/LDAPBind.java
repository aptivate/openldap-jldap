/**
 * 4.4 public interface LDAPBind
 *
 *  Used to do explicit bind processing on a referral. A client may
 *  specify an instance of this class to be used on a single operation
 *  (through the LDAPConstraints object) or for all operations (through
 *  LDAPConnection.setOption()).
 */
package com.novell.ldap; 
 
public interface LDAPBind {

   /*
    * 4.4.1 bind
    */

   /**
    * This method is called by LDAPConnection when authenticating. An
    * implementation may access the host, port, credentials, and other
    * information in the LDAPConnection to decide on an appropriate
    * authentication mechanism, and/or may interact with a user or external
    * module. An LDAPException is thrown on failure, as in
    * LDAPConnection.bind().
    *
    * conn           An established connection to an LDAP server.
    */
   public void bind (LDAPConnection conn) throws LDAPException;

}

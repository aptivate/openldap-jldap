/**
 * 4.8 public class LDAPControl
 *                implements Cloneable
 *
 *  An LDAPControl encapsulates optional additional parameters or
 *  constraints to be applied to LDAP operations. If set as a Server
 *  Control, it is sent to the server along with operation requests. If
 *  set as a Client Control, it is not sent to the server, but rather
 *  interpreted locally by the client. LDAPControl is an LDAPv3
 *  extension, and is not supported in an LDAPv2 environment.
 */
package com.novell.ldap; 
 
public class LDAPControl implements Cloneable {

   private String id;
   private boolean critical;
   private byte vals[];

   /*
    * 4.8.1 Constructors
    */

   /**
    * Parameters are:
    *
    *  id             The type of the Control, as a string.
    *
    *  critical       True if the LDAP operation should be discarded if
    *                  the server does not support this Control.
    *
    *  vals           Control-specific data.
    */
   public LDAPControl(String id, boolean critical, byte vals[]) {
      this.id = id;
      this.critical = critical;
      this.vals = vals;
   }

   /*
    * 4.8.2 clone
    */

   /**
    * Returns a deep copy of the object.
    */
   public Object clone() {
      return null;
   }

   /*
    * 4.8.3 getID
    */

   /**
    * Returns the identifier of the control.
    */
   public String getID() {
      return id;
   }

   /*
    * 4.8.4 getValue
    */

   /**
    * Returns the control-specific data of the object.
    */
   public byte[] getValue() {
      return vals;
   }

   /*
    * 4.8.5 isCritical
    */

   /**
    * Returns true if the control must be supported for an associated
    * operation to be executed.
    */
   public boolean isCritical() {
      return critical;
   }

   /*
    * 4.8.6 newInstance
    */

   /**
    * Instantiates a control, given the raw data representing it in an LDAP
    * message.
    */
   public static LDAPControl newInstance(byte[] data) {
      return null;
   }

   /*
    * 4.8.7 register
    */

   /**
    * Registers a class to be instantiated on receipt of a control with the
    * given oid. Any previous registration for the oid is overridden. The
    * controlClass must be an extension of LDAPControl.
    *
    * Parameters are:
    *
    *  oid            The Object Identifier of the Control.
    *
    *  controlClass   A class which can instantiate an LDAPControl.
    */
   public static void register(String oid, Class controlClass) {
   }

}


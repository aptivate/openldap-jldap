/**
 * 4.11 public class LDAPExtendedOperation
 *
 *  An LDAPExtendedOperation encapsulates an ID which uniquely identifies
 *  a particular extended operation, known to a particular server, and
 *  the data associated with the operation.
 */
package com.novell.ldap; 
 
public class LDAPExtendedOperation {

   private String oid;
   private byte[] vals;

   /*
    * 4.11.1 Constructors
    */

   /**
    * Constructs a new object with the specified object ID and data.
    *
    * Parameters are:
    *
    *  oid            The unique identifier of the operation.
    *
    *  vals           The operation-specific data of the operation.
    */
   public LDAPExtendedOperation(String oid, byte[] vals) {
      this.oid = oid;
      this.vals = vals;
   }

   /*
    * 4.11.2 getID
    */

   /**
    * Returns the unique identifier of the operation.
    */
   public String getID() {
      return oid;
   }

   /*
    * 4.11.3 getValue
    */

   /**
    * Returns the operation-specific data (not a copy, a reference).
    */
   public byte[] getValue() {
      return vals;
   }

}

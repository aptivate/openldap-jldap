/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPExtendedOperation.java,v 1.5 2000/08/03 22:06:15 smerrill Exp $
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
 * 4.11 public class LDAPExtendedOperation
 *
 *  An LDAPExtendedOperation encapsulates an ID which uniquely identifies
 *  a particular extended operation, known to a particular server, and
 *  the data associated with the operation.
 */
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
   
   /**
    * Not defined in the spec but used by extension code
    */
   protected void setValue(byte[] newVals) {
        this.vals = newVals;
   }

}

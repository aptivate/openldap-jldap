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

package com.novell.ldap;

/**
 * Encapsulates an ID which uniquely identifies a particular extended
 * operation, known to a particular server, and the data associated
 * with that extended operation.
 *
 * @see LDAPConnection#extendedOperation
 */
public class LDAPExtendedOperation implements Cloneable
{

    private String oid;
    private byte[] vals;

    /**
     * Constructs a new object with the specified object ID and data.
     *
     *  @param oid     The unique identifier of the operation.
     *
     *  @param vals    The operation-specific data of the operation.
     */
    public LDAPExtendedOperation(String oid, byte[] vals) {
       this.oid = oid;
       this.vals = vals;
    }

    /**
     * Returns a clone of this object.
     *
     * @return clone of this object.
     */
    public Object clone()
    {
        try {
            LDAPExtendedOperation newOp = (LDAPExtendedOperation)super.clone();
            System.arraycopy( this.vals, 0, newOp.vals, 0, this.vals.length);
            return newOp;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Returns the unique identifier of the operation.
     *
     * @return The OID (object ID) of the operation.
     */
    public String getID() {
       return oid;
    }

    /**
     * Returns a reference to the operation-specific data.
     *
     * @return The operation-specific data.
     */
    public byte[] getValue() {
       return vals;
    }

    /**
     *  Sets the value for the operation-specific data.
     *
     *  @param newVals  The byte array of operation-specific data.
     */
    protected void setValue(byte[] newVals) {
         this.vals = newVals;
         return;
    }

    /**
     *  Resets the OID for the operation to a new value
     *
     *  @deprecated For internal use only
     *
     *  @param newoid  The new OID for the operation
     */
    protected void setID(String newoid) {
         this.oid = newoid;
         return;
    }
}

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

package org.ietf.ldap;

/**
 *  Encapsulates an ID which uniquely identifies a particular extended
 *  operation.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html">
            com.novell.ldap.LDAPExtendedOperation</a>
 */
public class LDAPExtendedOperation implements Cloneable
{
	private ExOp exop;

	/**
	 * Constructs an object from com.novell.ldap.LDAPExtendedOperation
	 */
	/* package */
	com.novell.ldap.LDAPExtendedOperation getWrappedObject()
	{
		return exop;
	}

    /**
     * Constructs a new object with the specified object ID and data.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html#LDAPExtendedOperation(java.lang.String, byte[])">
            com.novell.ldap.LDAPExtendedOperation.LDAPExtendedOperation(String,
            byte[])</a>
     */
    public LDAPExtendedOperation(String oid, byte[] vals)
	{
        exop = new ExOp( oid, vals);
        return;
    }

    /**
     * Creates a clone of this object
     *
     * @return a clone of this object
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html#clone()">com.novell.ldap.LDAPExtendedOperation.clone()</a>
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            ((LDAPExtendedOperation)newObj).exop = new ExOp( getID(), getValue());
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Returns the unique identifier of the operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html#getID()">
            com.novell.ldap.LDAPExtendedOperation.getID()</a>
     */
    public String getID() {
        return exop.getID();
    }

    /**
     * Returns a reference to the operation-specific data.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html#getValue()">
            com.novell.ldap.LDAPExtendedOperation.getValue()</a>
     */
    public byte[] getValue() {
        return exop.getValue();
    }

    /**
     *  Sets the value for the operation-specific data.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPExtendedOperation.html#setValue(byte[])">
            com.novell.ldap.LDAPExtendedOperation.setValue(byte[])</a>
     */
    protected void setValue(byte[] newVals) {
        exop.mySetValue( newVals);
        return;
    }

    private class ExOp extends com.novell.ldap.LDAPExtendedOperation
    {
        /**
         * Constructs a new object with the specified object ID and data.
         *
         * @see com.novell.ldap.LDAPExtendedOperation
         */
        private ExOp(String oid, byte[] vals)
	    {
            super( oid, vals);
            return;
        }

        /**
         * Change name so we can get past the protected access of setValue
         */
        private void mySetValue(byte[] newVals)
        {
            super.setValue( newVals);
            return;
        }
    }
}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.connectionpool;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPSocketFactory;

/**
 * Extends LDAPConnection to add information needed by pool management.
 *
 * @see com.novell.ldap.LDAPConnection
 * @see PoolManager
 */

/*package*/ class Connection extends LDAPConnection implements Cloneable
{
    // inUse flag used by connection pool to mark which connections are
    // in use. 
    boolean inUse = false;
    
    /**
     * Connection
     *
     * <p>Set up the socket factory.</p>
     * @param factory LDAPSocketFactory.
     * @see com.novell.ldap.LDAPSocketFactory               
     */
    public Connection(LDAPSocketFactory factory)
    {
        super(factory);
    }
    
    /**
     * Returns true if in use false otherwise.
     *
     * @return inUse flag.
     */
    public boolean inUse()
    {
        return inUse;
    }

    /**
     * Set inUse to true.
     *
     * @return inUse flag.
     */
    public boolean setInUse()
    {
        inUse = true;
        return inUse;
    }
    
    /**
     * Clear inUse to true
     *
     * @return inUse flag.
     */
    public boolean clearInUse()
    {
        inUse = false;
        return inUse;
    }
    
    /**
     * Call the super clone so that this will clone properly.
     *
     * @return Connection a clone of this object.
     */
    public Object clone()
    {
        return super.clone();
    }
}


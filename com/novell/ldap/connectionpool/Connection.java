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
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPResponseQueue;
import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;

import com.novell.ldap.resources.ExceptionMessages;

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
    private boolean inUse = false;

    // This flag allows bind to be called from the package but not outside
    // of the package.
    private boolean allowPoolBind = false;
    
    /**
     * Establish an LDAPConnection supplying an appropriate socket factory.
     *
     * @param factory LDAPSocketFactory or null if none
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
    boolean inUse()
    {
        return inUse;
    }

    /**
     * Set inUse to true.
     *
     * @return inUse flag.
     */
    boolean setInUse()
    {
        inUse = true;
        return inUse;
    }
    
    /**
     * Clear the inUse flag, marking the connection no long in use.
     *
     * @return inUse flag.
     */
    boolean clearInUse()
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

    /**
     * Provide the PoolManager a way to do a bind.
     */
    void poolBind(int version,
                     String dn,
                     byte[] passwd)
        throws LDAPException
    {
        try
        {
            allowPoolBind = true;
            super.bind(version, dn, passwd);
        }
        catch (LDAPException e)
        {
            throw e;
        }
        finally
        {    
            allowPoolBind = false;
        }
    }
    
    //*************************************************************************
    // Make all of the bind methods throw an LDAPLocalException to force
    // users to use the PoolManager for binding. Do this by overwriting the
    // mother of all binds.
    //*************************************************************************
    /**
     * Overrides LDAPConnection.bind().  All users of the connection pools
     * must pass bind credentials when the pool is established.
     */
    public LDAPResponseQueue bind(int version,
                                  String dn,
                                  byte[] passwd,
                                  LDAPResponseQueue queue,
                                  LDAPConstraints cons)
        throws LDAPException
    {
        // If this was called from our poolBind allow the bind to happen.
        if(allowPoolBind)
        {
            return super.bind(version, dn, passwd, queue, cons);
        }
        else
        {
            throw new LDAPLocalException(
                ExceptionMessages.CANNOT_BIND,LDAPException.LOCAL_ERROR);
        }
    }
}


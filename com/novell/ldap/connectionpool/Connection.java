/* **************************************************************************
 * $Novell: CPConn.java,v 1.1 2003/01/14 02:59:11 $
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
package com.novell.services.dsml;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPResponseQueue;

/**
 * CPConn
 *
 * <p> Extend LDAPConnection to save and retrieve the user password. The
 * password and DN are used by the ConnectionPool to keep track of who has
 * already connected. </p> 
 *
 * @see com.novell.ldap.LDAPConnection
 * @see ConnectionPool
 */

/*package*/ class CPConn extends LDAPConnection implements Cloneable
{
    // inUse flag used by connection pool to mark which connections are
    // in use. 
    boolean inUse = false;
    
    /**
     * CPConn
     *
     * <p>Set up the socket factory.</p>
     * @param factory LDAPSocketFactory.
     * @see com.novell.ldap.LDAPSocketFactory               
     */
    public CPConn(LDAPSocketFactory factory)
    {
        super(factory);
    }
    
    /**
     * inUse
     *
     * <p>Returns true if in use false otherwise</p>
     * @return inUse flag.
     */
    public boolean inUse()
    {
        return inUse;
    }

    /**
     * setInUse
     *
     * <p>Set inUse to true</p>
     * @return inUse flag.
     */
    public boolean setInUse()
    {
        inUse = true;
        return inUse;
    }
    
    /**
     * clearInUse
     *
     * <p>Clear inUse to true</p>
     * @return inUse flag.
     */
    public boolean clearInUse()
    {
        inUse = false;
        return inUse;
    }
    
    /**
     * clone
     *
     * <p>Call the super clone so that this will clone properly</p>
     * @return CPConn a clone of this object.
     */
    public Object clone()
    {
        return super.clone();
    }
}


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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A list of connections sharing one physical connection.
 *
 * <p> This helper class mannages connections that
 * share the same physical connection. This class extends ArrayList.
 * The password and DN for the physical connection are saved and available
 * from this class. Connection retrieval methods are available.</p>  
 *
 * @see PoolManager
 */
/* package */
class SharedConnections extends ArrayList
{
    // Save password and DN
    byte[] PW = null;
    String DN = null;
    
    
    public SharedConnections(int initialCapacity)
    {
        super(initialCapacity);
        return;
    }
        
    /**
     * Set the password.
     *
     * @param PW the login password.
     */
    public void setPW(byte[] PW)
    {
        this.PW = PW;
        return;
    }
    
    /**
     * Set the DN.
     *
     * @param DN the login DN.
     */
    public void setDN(String DN)
    {
        this.DN = DN;
        return;
    }
    
    /**
     * Get an available connection from the pool.
     *
     * @return the Connection or null if none.
     */
    public Connection getAvailableConnection()
    {
        for (int i = 0; i < super.size(); i++)
        {
            Connection conn = (Connection)super.get(i);
            if(!conn.inUse())
                return conn;
        }
        return null;
    }
        
    /**
     *  Check if a connection belongs to this pool.
     *
     * @param thisConn a Connection object to check.
     *
     * @return true if the Connection belongs to this pool, false otherwise.
     */
    public boolean isConnInHere(Connection thisConn)
    {
        for (int i = 0; i < super.size(); i++)
        {
            Connection conn = (Connection)super.get(i);
            if(thisConn.equals(conn)) return true;
        }
        return false;
    }
    
    /**
     * Compare DN and PW with this DN PW
     */
    public boolean DNPWequals(String DN, byte[] PW)
    {
        return (equals(this.DN, DN) && equals(this.PW, PW));
    }
    

    /**
     * Checks if all connections are available.  If no connections
     * are in use, the function returns true.
     *
     * @return true if all available, false otherwise.
     */
    public boolean allConnectionsAvailable()
    {
        for (int j = 0; j < super.size(); j++)
        {
            if(((Connection)super.get(j)).inUse())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compare two strings values for equality.
     *
     * <p>Performs a case insensitive compare of tow strings. Note: null == null</p>
     *
     * @return true if the string values are equal, false otherwise.
     */  
    private boolean equals(String s1, String s2)
    {        
            if(null == s1)
            {
                if(null == s2)
                    return true;
                else
                    return false;
            }
            else
            {
                if(null == s2)
                    return false;
            }
        return s1.equalsIgnoreCase(s2);
    }
    
    /**
     * Compare two byte arrays for equality.
     * Note: null == null
     *
     * @return true if the byte array values are equal, false otherwise.
     */
    private boolean equals(byte[] ba1, byte[] ba2)
    {        
            if(null == ba1)
            {
                if(null == ba2)
                    return true;
                else
                    return false;
            }
            else
            {
                if(null == ba2)
                    return false;
            }
        return Arrays.equals(ba1 ,ba2);
    }
}

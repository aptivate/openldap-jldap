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
    }
        
    /**
     * Set the password
     */
    public void setPW(byte[] PW)
    {
        this.PW = PW;
    }
    
    /**
     * Set the DN
     */
    public void setDN(String DN)
    {
        this.DN = DN;
    }
    
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
        return (equals(this.DN, DN) & equals(this.PW, PW));
    }
    

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
     * equals
     *
     * <p>String compare. Ingnore case. null == null</p>
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
     * Byte array compare. null == null
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


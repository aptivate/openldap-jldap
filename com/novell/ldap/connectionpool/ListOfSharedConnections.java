/* **************************************************************************
 * $Novell: ListOfSharedConnections.java,v 1.1 2003/01/14 21:36:23 $
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
import java.util.LinkedList;

/**
 * List of Shared Connections
 *
 * <p></p> 
 *
 * @see ConnectionPool
 */
class ListOfSharedConnections extends LinkedList
{
    public CPSharedConns getSharedConns(String DN, byte[] PW)
    {        
        for (int i = 0; i < super.size(); i++)
        {
            CPSharedConns sharedConns = (CPSharedConns)super.get(i);
    
            if(sharedConns.DNPWequals(DN,PW))
                return sharedConns;
        }
    
        return null;
    }
        
    public CPSharedConns getSharedConns(Connection conn)
    {        
        for (int i = 0; i < super.size(); i++)
        {
            CPSharedConns sharedConns = (CPSharedConns)super.get(i);
            if(sharedConns.isConnInHere(conn))
                return sharedConns;
        }
    
        return null;
    }
        
    public Connection getAvailableConnection(String DN, byte[] PW)
    {
        for (int i = 0; i < super.size(); i++)
        {
            CPSharedConns sharedConns = (CPSharedConns)super.get(i);
            Connection conn = (Connection)sharedConns.getAvailableConnection();
            if(null != conn) return conn;
        }
        return null;
    }
}


/* **************************************************************************
 * $Novell: ListOfSharedConnections.java,v 1.3 2003/01/14 21:50:51 $
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
package connectionpool;
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
    public SharedConnection getSharedConns(String DN, byte[] PW)
    {        
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnection sharedConns = (SharedConnection)super.get(i);
    
            if(sharedConns.DNPWequals(DN,PW))
                return sharedConns;
        }
    
        return null;
    }
        
    public SharedConnection getSharedConns(Connection conn)
    {        
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnection sharedConns = (SharedConnection)super.get(i);
            if(sharedConns.isConnInHere(conn))
                return sharedConns;
        }
    
        return null;
    }
        
    public Connection getAvailableConnection(String DN, byte[] PW)
    {
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnection sharedConns = (SharedConnection)super.get(i);
            Connection conn = (Connection)sharedConns.getAvailableConnection();
            if(null != conn) return conn;
        }
        return null;
    }
}


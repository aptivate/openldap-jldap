/* **************************************************************************
 * $Novell: ListOfSharedConnections.java,v 1.4 2003/01/23 00:47:54 $
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
 * <p>This is a helper class to manage SharedConnection. A SharedConnection
 * is an ArrayList of connections (original and clones) bound to the same
 * DN and password.</p>  
 *
 * @see SharedConnection.
 */
class ListOfSharedConnections extends LinkedList
{
    
    /**
     * getSharedConns
     *
     * <p>Find a SharedConnection that has the proper DN/PW</p>
     */
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
        
    /**
     * getSharedConns
     *
     * <p>Find a SharedConnection that a connection in it.</p>
     */
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
        
    /**
     * getAvailableConnection
     *
     * <p>Search all of the SharedConnection that are bound to a DN/PW
     * and return a connection that is not inuse.</p>
     */
    public Connection getAvailableConnection(String DN, byte[] PW)
    {
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnection sharedConns = (SharedConnection)super.get(i);
            if(sharedConns.DNPWequals(DN,PW))
            {
                Connection conn = (Connection)sharedConns.getAvailableConnection();
                if(null != conn) return conn;
            }
        }
        return null;
    }
}


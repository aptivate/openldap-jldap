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
import java.util.LinkedList;

/**
 * This helper class manages SharedConnections.
 *
 * @see SharedConnections
 */
class ListOfSharedConnections extends LinkedList
{

    /**
     * Find a SharedConnections object that shares the physical connection
     * to the proper DN and Password
     */
    public SharedConnections getSharedConns(String DN, byte[] PW)
    {
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnections sharedConns = (SharedConnections)super.get(i);

            if(sharedConns.DNPWequals(DN,PW))
                return sharedConns;
        }

        return null;
    }

    /**
     * Find a SharedConnections object that contain the desired connection.
     */
    public SharedConnections getSharedConns(Connection conn)
    {
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnections sharedConns = (SharedConnections)super.get(i);
            if(sharedConns.isConnInHere(conn))
                return sharedConns;
        }

        return null;
    }

    /**
     * Search the list of SharedConnections for a SharedConnections object 
     * who's physical connection is bound to the desired DN and Password.
     * Return a connection from the SharedConnections object that is available.
     */
    public Connection getAvailableConnection(String DN, byte[] PW)
    {
        for (int i = 0; i < super.size(); i++)
        {
            SharedConnections sharedConns = (SharedConnections)super.get(i);
            if(sharedConns.DNPWequals(DN,PW))
            {
                Connection conn = sharedConns.getAvailableConnection();
                if(null != conn) return conn;
            }
        }
        return null;
    }
}

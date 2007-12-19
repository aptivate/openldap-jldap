/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 - 2003 Novell, Inc. All Rights Reserved.
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
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPTLSSocketFactory;

/**
 * Manages connections to an LDAP Server.
 *
 * <p><code>PoolManager</code> manages connections to a single LDAP server.
 * The pool consists of a finite number of physical connections to the LDAP
 * server (parameter <code>maxConns</code>) and a finite number of
 * LDAPConnection objects sharing a physical connection
 * (parameter <code>maxSharedConns</code>),
 * see the {@link #PoolManager(String, int, int, int, com.novell.ldap.LDAPSocketFactory) PoolManager}
 * constructor.</p> 
 * <p>A physical connection, and its shared LDAPConnection objects are associated
 * with an LDAP DN and password (DN/PW).
 * {@link #getBoundConnection(String, byte[]) getBoundConnection}
 * searches for a physical connection associated with a given DN/PW
 * and returns an available LDAPConnection object associated with
 * that DN/PW.
 * If none are available it searches for
 * an unused physical connection, binds using the given DN/PW,
 * and returns an LDAPConnection object.
 * If no physical connection is available then it waits.
 * Once an LDAPConnection object is no longer needed the 
 * {@link #makeConnectionAvailable(LDAPConnection) makeConnectionAvailable}
 * function must be called to make the LDAPConnection available to other threads.</p>
 */
public class PoolManager
{
    /** Contains all of the sharedConns that are in use */
    private ListOfSharedConnections inUseListOfSharedConnections;
    /** Contains all of the available sharedConns */
    private ListOfSharedConnections availableListOfSharedConnections;
    /** Set by finalize. This tells any waiting thread to shutdown.*/
    private boolean shuttingDown;

    /**
     * Initialize the connection pool.
     *
     * @param host - Host name associated with this connection pool 
     * (see {@link com.novell.ldap.LDAPConnection#connect(String, int) LDAPConnection.connect()}).
     * @param port - Port number for the host associated with this connection
     *   pool.
     * (see {@link com.novell.ldap.LDAPConnection#connect(String, int) LDAPConnection.connect()}).
     * @param maxConns - Maximum number of physical connections allowed for
     *             this host.
     * @param maxSharedConns - Maximum number of shared connections per physical
     *             connection.
     * @param factory - A socket factory used to set an encrypted connection,
     *           or null if none.  If the factory is an instance of
     * {@link com.novell.ldap.LDAPTLSSocketFactory} then a startTLS is
     * performed after the connection to the server is established.
     *           <code>LDAPTLSSocketFactory</code>
     * (see {@link com.novell.ldap.LDAPConnection#setSocketFactory(com.novell.ldap.LDAPSocketFactory) LDAPConnection.setSocketFactory()}).
     */
    public PoolManager(String host,
                          int port,
                          int maxConns,
                          int maxSharedConns,
                          LDAPSocketFactory factory)
        throws LDAPException
    {
        // Use the keystore file if it is there.

        inUseListOfSharedConnections = new ListOfSharedConnections();
        availableListOfSharedConnections = new ListOfSharedConnections();
        // Set up the max connections and max shared connections
        // ( original + clones) in availableConnection.
        for (int i = 0; i < maxConns; i++)
        {
            SharedConnections sharedConns = new SharedConnections(maxSharedConns);
            // Create connection. Initialy anonymous
            Connection conn = new Connection(factory);
            // At this point all of the connections anonymous
            try
            {
            conn.connect(host, port);
            }
            catch(LDAPException e)
            {
            	System.out.println("Error :  " + e.getResultCode());
            }
            if( factory instanceof LDAPTLSSocketFactory) {
                conn.startTLS();
            }
            sharedConns.add(conn);
            // Clone the connections to make all of the sharedConns.
            for (int j = 1; j < maxSharedConns; j++)
            {
                Connection cloneConn = (Connection)conn.clone();
                sharedConns.add(cloneConn);
            }
            availableListOfSharedConnections.add(i, sharedConns);
        }
        shuttingDown = false;
    }

    /**
     * Get a bound connection.
     * <p>This returns a bound (bind) connection for the desired DN and
     * password.</p>
     * @param DN  Authentication DN used for bind and key.
     * @param PW  Authentication password used for bind and key.
     * @throws LDAPException if an LDAPConnection could not be bound.
     */
    public LDAPConnection getBoundConnection(String DN, byte[] PW)
            throws LDAPException, InterruptedException
    {

        Connection        conn        = null;
        SharedConnections sharedConns = null;
        boolean           needToBind  = false;

        synchronized (inUseListOfSharedConnections)
        {
            // See if there is a connection available in the in use list of
            // sharedConns, that are in use bound to DN,PW.
            conn = inUseListOfSharedConnections.getAvailableConnection(DN, PW);
            if(null != conn)
            {
                // Set this connection inuse.
                conn.setInUse();
                return conn;
            }
        }

        synchronized (availableListOfSharedConnections)
        {
            // See if there are shared connections that are available
            // bound to DN,PW.
            sharedConns = availableListOfSharedConnections.getSharedConns(DN, PW);
            if(null == sharedConns) // No we need to rebind an available
            {
                // If there are no available sharedConns wait for one.
                while(0 == availableListOfSharedConnections.size())
                {
                    // Wait for available Instances
                    availableListOfSharedConnections.wait();
                    // If we are shutting down return null
                    if(shuttingDown) return null;
                }
                // Get connection from first available sharedConns
                sharedConns = (SharedConnections)availableListOfSharedConnections.get(0);
                needToBind = true;
            }

            // Remove sharedConns from available.
            availableListOfSharedConnections.remove(sharedConns);
            // Get the first connection and mark it inuse
            conn = (Connection)sharedConns.get(0);
            // Set this connection inuse.
            conn.setInUse();
        }
        // Do we need to rebind? Bind will do a connect if needed
        if(needToBind || !conn.isConnectionAlive())
        {
            try
            {
                conn.poolBind(LDAPConnection.LDAP_V3, DN, PW);
                sharedConns.setDN(DN);
                sharedConns.setPW(PW);

            }catch (LDAPException e)
            {
                // If we get and exception make the shared connection available
                conn.clearInUse();
                sharedConns.setDN(null);
                sharedConns.setPW(null);
                synchronized (availableListOfSharedConnections)
                {
                    availableListOfSharedConnections.add(sharedConns);
                }
                throw e;
            }

        }

        synchronized (inUseListOfSharedConnections)
        {
            // Move into inuse.
            inUseListOfSharedConnections.add(sharedConns);
        }
        return conn;
    }

    /**
     * Make this connection available.
     * @param conn LDAPConnection to be made available.
     */
    public void makeConnectionAvailable(LDAPConnection conn)
    {
        SharedConnections sharedConns = null;

        synchronized(inUseListOfSharedConnections)
        {
            // Mark this connection available.
            ((Connection)conn).clearInUse();

            sharedConns = inUseListOfSharedConnections.getSharedConns((Connection)conn);
            if(sharedConns==null)
            {
            	sharedConns = availableListOfSharedConnections.getSharedConns((Connection)conn);
            }		
            // If all connections in this instance are available move to
            // from in use to available.
            if(sharedConns.allConnectionsAvailable())
            {
                inUseListOfSharedConnections.remove(sharedConns);
            }
            else
            {
                // Do not add to available connection list.
                sharedConns = null;
            }
        }

        if(null != sharedConns)
        {
            synchronized(availableListOfSharedConnections)
            {
                availableListOfSharedConnections.add(sharedConns);
                // Notify anyone that might be waiting on this connection.
                availableListOfSharedConnections.notify();
            }
        }
        return;
    }

    /**
     * Free connections.
     * <p> Tell all waiting threads that we are shutting down.
     * Clean up the in use and available connections.</p>
     *
     * @throws Throwable when disconnect fails.
     */
    protected void finalize()
            throws Throwable

    {
        synchronized (availableListOfSharedConnections)
        {
            // Notify all waiting threads.
            shuttingDown = true;
            availableListOfSharedConnections.notifyAll();
        }
    }
}


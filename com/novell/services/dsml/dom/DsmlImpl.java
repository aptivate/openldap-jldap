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
 ***************************************************************************/

package com.novell.services.dsml.dom;

import java.rmi.RemoteException;
import javax.servlet.*;
import org.w3c.dom.Element;
import com.novell.ldap.*;
import com.novell.ldap.connectionpool.PoolManager;
import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.util.*;
import com.novell.services.dsml.ImportExport;
import com.novell.services.dsml.Authorization;

public class DsmlImpl extends Dsml_ServiceSkeleton
{
    // Connection Pool stays intact for the life of the servlet.
    private PoolManager connPool = null;

    /**
     * This method defines the entry and exit point for the DSML service.
     *
     * @param request DOM element representing a DSMLv2 request
     * @return a DOM element representing the results of a DSMLv2 request
     * @throws RemoteException Any exception is converted into a SOAPFault
     */
    public Element batchRequest(Element request) throws RemoteException{

        Element response;
        try{
            DOMReader dsmlInput = new DOMReader(request);
            DOMWriter dsmlOutput = new DOMWriter();

            //Handle all DSML processing
            ImportExport.process(
                    new Authorization(), connPool, dsmlInput, dsmlOutput);

            //retreive DOM element from writer
            response = dsmlOutput.getRootElement();
        }
        catch(Exception e)
        {
            //A remoteException becomes a SOAP fault.
            throw new RemoteException("DSMLService - Error: ", e);
        }
        return response;
    }

    /**
     * Initialize servlet by creating Connection Pool.
     *
     * <p>Initialize this servlet by reading parameters from the web.xml
     * file (contained in servletConfig) and using those parameters
     * to create a PoolManager</p>
     * <p> This servlet is using stream input and output for efficiency.
     *
     * @param servletConfig Contains context and parameter.
     * @throws ServletException When error occurs creating PoolManager
     *
     * @see PoolManager
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        try
        {
            String            tmp               = null;
            String            host              = "localhost";
            int               port              = LDAPConnection.DEFAULT_PORT;
            int               maxConns          = 3;
            int               maxInsts2SharConn = 3;
            LDAPSocketFactory factory           = null;
            
            super.init( servletConfig );
            ServletContext ctx = servletConfig.getServletContext();

            if(ctx.getInitParameter("ldapHost") != null){
                host = ctx.getInitParameter("ldapHost");
            }

            // If port number is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapPort");
            if(null != tmp && 0 < Integer.parseInt(tmp)){
                port = Integer.parseInt(tmp);
            }
            // If max connections is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapMaxConnections");
            if(null != tmp && 0 < Integer.parseInt(tmp)){
                maxConns = Integer.parseInt(tmp);
            }
            // If maxInsts2SharConn is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapMaxInstancesToShareConnection");
            if(null != tmp && 0 < Integer.parseInt(tmp)) {
                maxInsts2SharConn = Integer.parseInt(tmp);
            }

            tmp = ctx.getInitParameter("ldapKeystore");
            if(null != tmp && 0 < tmp.length()){

                // Dynamically set the property that JSSE uses to identify
                // the keystore that holds trusted root certificates
                System.setProperty("javax.net.ssl.trustStore", tmp);
                // Setup the JSSE socket factory
                factory = new LDAPJSSESecureSocketFactory();
            }

            // This set up connections to the desired ldap host.
            connPool = new PoolManager(host,
                                       port,
                                       maxConns,
                                       maxInsts2SharConn,
                                       factory);
        }
        catch (Exception e)
        {
            throw new ServletException("DSMLService init Failed e: " + e);
        }
        return;
    }

    /**
     * This method is overridden to indicate to the JBroker webserver where
     * the WSDL file is located.
     * @throws ServletException
     */
    public void init() throws ServletException {
        super.init();
        _setProperty("xmlrpc.wsdl", "Dsml.wsdl");
        return;
    }
}

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
package com.novell.services.dsml.stream;

import com.novell.ldap.*;

import com.novell.ldap.connectionpool.PoolManager;
import com.novell.ldap.util.DSMLReader;
import com.novell.ldap.util.DSMLWriter;
import com.novell.ldap.util.LDAPReader;
import com.novell.ldap.util.LDAPWriter;

import com.novell.services.dsml.Authorization;
import com.novell.services.dsml.ImportExport;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * DSML Service
 *
 * <p> Process DSML Batch Request by: a)Converting the request into an
 * LDAPMessage (DSMLReader), b)Send the LDAPMessage to LDAP Server
 * (sendRequest), c) Convert the response LDAPMessage from the LDAP Server
 * to a DSML Batch Response (DSMLWriter), and d) Return the response to the
 * client. </p>
 *
 * @see com.novell.ldap.util.DSMLReader
 * @see com.novell.ldap.util.DSMLWriter
 * @see com.novell.ldap.LDAPMessage
 * @see com.novell.ldap.LDAPConnection#sendRequest
 */
public class DsmlService extends HttpServlet
{
    // Connection Pool stays intact for the life of the servlet.
    private PoolManager connPool = null;

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

            tmp = ctx.getInitParameter("ldapHost");
            if(null != tmp && 0 < tmp.length()){
                host = tmp;
            }

            // If port number is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapPort");
            if(null != tmp && 0 < tmp.length() && 0 < Integer.parseInt(tmp)){
                port = Integer.parseInt(tmp);
            }
            // If max connections is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapMaxConnections");
            if(null != tmp && 0 < tmp.length() && 0 < Integer.parseInt(tmp)){
                maxConns = Integer.parseInt(tmp);
            }
            // If maxInsts2SharConn is not null and greater than 0 use it otherwise use default
            tmp = ctx.getInitParameter("ldapMaxInstancesToShareConnection");
            if(null != tmp && 0 < tmp.length() && 0 < Integer.parseInt(tmp)) {
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
            throw new ServletException("DsmlService init Failed e: " + e);
        }
        return;
    }

    /**
     * Process batch requests.
     *
     * <p>Process DSML batch request from clients by: converting it
     * into an LDAPMessage, sending the LDAPMessage to the LDAP server,
     * converter the response LDAPMessage that came from the LDAP server
     * into a DSML batch response, and sent it back to the client.<p>
     * <p> This is done using stream input and output.
     *
     * @param req DSML batch request.
     * @param rsp DSML batch response.
     */
    public void doPost(HttpServletRequest req,
                       HttpServletResponse rsp)
                       throws ServletException,
                              IOException
    {
        PrintWriter      rspPrtWtr   = null;
        DSMLWriter       rspDsmlWtr  = null;
        DSMLReader       reqDsmlRdr  = null;
        Authorization    reqAuth     = null;

        try
        {
            // Get the request as a stream.
            reqDsmlRdr = new DSMLReader(req.getInputStream());
            // Get the Authorization out of the HTTP header.
            // This Authorization contains the DN and password that is
            // used to login to the LDAP server.
            reqAuth = new  Authorization(req);
            // Set response content type to text/xml
            rsp.setContentType("text/xml; charset=utf-8");
            // Get response PrintWriter object from HttpServletResponse so we
            // can write response.
            rspPrtWtr = rsp.getWriter();
            // Write XML version to response.
            rspPrtWtr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            // Write start of soap envelope.
            rspPrtWtr.println("<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            // Write start of soap body.
            rspPrtWtr.println("<soap-env:Body>");
            // Set response PrintWriter object into DSLMLWriter.
            rspDsmlWtr = new DSMLWriter(rspPrtWtr);

            //Handle all DSML processing
            ImportExport.process(reqAuth, connPool, reqDsmlRdr, rspDsmlWtr);

            // Write end soap body.
            rspPrtWtr.println("</soap-env:Body>");
            // Write end soap envelope
            rspPrtWtr.println("</soap-env:Envelope>");
            // Set HttpServletResponse status to OK
            rsp.setStatus(rsp.SC_OK);
            // Flush response to the client.
            rspPrtWtr.flush();
        }
        catch(Exception e)
        {
            rsp.setStatus(rsp.SC_INTERNAL_SERVER_ERROR);
            if(null != rspPrtWtr)rspPrtWtr.flush();
            throw new ServletException("DsmlService - Error: " + e);
        }
        return;
    }
}

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

import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPLocalException;
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
import java.lang.StringBuffer;
import java.util.Map;

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
    private        PoolManager connPool = null;
    private static String      status   = "OK";

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
        status = "Initalized properly";
        }
        catch (Exception e)
        {
            status = "Init Failed e: " + e;
            throw new ServletException(status);
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
        PrintWriter      respPrtWtr   = null;
        DSMLWriter       respDsmlWtr  = null;
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
            respPrtWtr = rsp.getWriter();
            // Write XML version to response.
            respPrtWtr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            // Write start of soap envelope.
            respPrtWtr.println("<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            // Write start of soap body.
            respPrtWtr.println("<soap-env:Body>");
            // Set response PrintWriter object into DSLMLWriter.
            respDsmlWtr = new DSMLWriter(respPrtWtr);

            //Handle all DSML processing
            ImportExport.process(reqAuth, connPool, reqDsmlRdr, respDsmlWtr);

            // Write end soap body.
            respPrtWtr.println("</soap-env:Body>");
            // Write end soap envelope
            respPrtWtr.println("</soap-env:Envelope>");
            // Set HttpServletResponse status to OK
            rsp.setStatus(rsp.SC_OK);
            // Flush response to the client.
            respPrtWtr.flush();
            
            status = "Last doPost successful";
        }catch(LDAPLocalException e){
                        rsp.setContentType("text/xml; charset=utf-8");
            // Get response PrintWriter object from HttpServletResponse so we
            // can write response.
            respPrtWtr = rsp.getWriter();
            // Write XML version to response.
            respPrtWtr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            // Write start of soap envelope.
            respPrtWtr.println("<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            // Write start of soap body.
            respPrtWtr.println("<soap-env:Body>");
            // Set response PrintWriter object into DSLMLWriter.
            respDsmlWtr = new DSMLWriter(respPrtWtr);
            
            respDsmlWtr.writeError(e);
            
			respDsmlWtr.finish();
            
			// Write end soap body.
			respPrtWtr.println("</soap-env:Body>");
			// Write end soap envelope
			respPrtWtr.println("</soap-env:Envelope>");
			// Set HttpServletResponse status to OK
			rsp.setStatus(rsp.SC_BAD_REQUEST);
			// Flush response to the client.
			respPrtWtr.flush();
            
        }catch(Exception e)
        {
            rsp.setStatus(rsp.SC_INTERNAL_SERVER_ERROR);
            if(null != respPrtWtr)respPrtWtr.flush();
            status = "DsmlService - Error: " + e;

            throw new ServletException(status);
        }
        return;
    }


    public void doGet (HttpServletRequest req,
                       HttpServletResponse rsp)
                       throws ServletException,
                              IOException
    {  
        PrintWriter rspPrtWtr = rsp.getWriter();
        Map params =  req.getParameterMap();
        
        if (params==null || 0 == params.size()){ 
            showStatus(rspPrtWtr);
        }
        else if(params.containsKey("wsdl") || params.containsKey("WSDL")){
            //if the specified userAction is "wsdl".
            doWSDL(rspPrtWtr,req.getRequestURL().toString());  
        }   
        else{
            // user Action is not specified
            errorAction(rspPrtWtr);
        }
        
        // Set HttpServletResponse status to OK
        rsp.setStatus(rsp.SC_OK);
        // Flush response to the client.
        rspPrtWtr.flush();
        
        return;
    }  // end doGet


    //when action is null
    private  void showStatus(PrintWriter prtWtr){
        prtWtr.println("<?xml version=\"1.0\"?>");
        prtWtr.println("<return_message>");
        prtWtr.println("<status>" + status + "</status>");
        prtWtr.println("</return_message>");
    }

    //when the action is doWSDL
    private void doWSDL(PrintWriter prtWtr, String location){
        prtWtr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        prtWtr.println("<definitions name=\"DsmlService\"");
        prtWtr.println("targetNamespace=\"http://www.stream.dsml.services.novell.com\"");
        prtWtr.println("xmlns=\"http://schemas.xmlsoap.org/wsdl/\"");
        prtWtr.println("xmlns:ns0=\"http://schemas.novell.com\"");
        prtWtr.println("xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"");
        prtWtr.println("xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"");
        prtWtr.println("xmlns:tns=\"http://www.dsml.services.novell.com\"");
        prtWtr.println("xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:xsd=\"http://www.oasis-open.org/committees/dsml/docs/DSMLv2.xsd\">");
        prtWtr.println("<types>");
        prtWtr.println("<schema");
        prtWtr.println("targetNamespace=\"http://schemas.novell.com\"");
        prtWtr.println("xmlns=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        prtWtr.println("<annotation>");
        prtWtr.println("<documentation xml:lang=\"en\">    XML type for Element");
        prtWtr.println("type.   </documentation>");
        prtWtr.println("</annotation>");
        prtWtr.println("<complexType name=\"element\">");
        prtWtr.println("<element name=\"element\" type=\"xsd:anyType\"/>");
        prtWtr.println("</complexType>");
        prtWtr.println("</schema>");
        prtWtr.println("</types>");
        prtWtr.println("<message name=\"batchRequestRequest\">");
        prtWtr.println("<part element=\"ns0:element\" name=\"arg0\"/>");
        prtWtr.println("</message>");
        prtWtr.println("<message name=\"batchRequestResponse\">");
        prtWtr.println("<part element=\"ns0:element\" name=\"result\"/>");
        prtWtr.println("</message>");
        prtWtr.println("<portType name=\"Dsml\">");
        prtWtr.println("<operation name=\"batchRequest\" parameterOrder=\"arg0\">");
        prtWtr.println("<input message=\"tns:batchRequestRequest\"/>");
        prtWtr.println("<output message=\"tns:batchRequestResponse\"/>");
        prtWtr.println("</operation>");
        prtWtr.println("</portType>");
        prtWtr.println("<binding name=\"DsmlBinding\" type=\"tns:Dsml\">");
        prtWtr.println("<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>");
        prtWtr.println("<operation name=\"batchRequest\">");
        prtWtr.println("<soap:operation soapAction=\"#batchRequest\"/>");
        prtWtr.println("<input>");
        prtWtr.println("<soap:body use=\"literal\"/>");
        prtWtr.println("</input>");
        prtWtr.println("<output>");
        prtWtr.println("<soap:body use=\"literal\"/>");
        prtWtr.println("</output>");
        prtWtr.println("</operation>");
        prtWtr.println("</binding>");
        prtWtr.println("<service name=\"DsmlService\">");
        prtWtr.println("<port binding=\"tns:DsmlBinding\" name=\"DsmlPort\">");
        prtWtr.print("<soap:address location=\"");
        prtWtr.print(location);        
        prtWtr.println("\"/>");
        prtWtr.println("</port>");
        prtWtr.println("</service>");
        prtWtr.println("</definitions>");
    }//end doWSDL
    
    //when action is not specified
    private  void errorAction(PrintWriter prtWtr){
        prtWtr.println("<?xml version=\"1.0\"?>");
        prtWtr.println("<return_message>");
        prtWtr.println("<action>Invalid Action!</action>");
        prtWtr.println("</return_message>");
    }
}

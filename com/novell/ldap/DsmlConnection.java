/* **************************************************************************
 *
 * Copyright (C) 2004 Octet String, Inc. All Rights Reserved.
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
package com.novell.ldap;

import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import java.io.*;

import com.novell.ldap.Connection;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedOperation;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPMessageQueue;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPResponseQueue;
import com.novell.ldap.LDAPSchema;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPUnsolicitedNotificationListener;
import com.novell.ldap.util.*;

/**
 * @author Marc Boorshtein
 *
 * This class is meant to be a drop-in replacement for an LDAPConnection 
 * when working sith synchronous LDAP calls
 */
public class DsmlConnection extends LDAPConnection {

	/** The Connection To The DSMLv2 Server */
	HttpClient con;
	
	/** The URL of the server */
	String serverString;
	
	/** The User's Name */
	String binddn;
	
	/** The User's Password */
	String pass;
	
	/** Determine if the connection is bound */
	boolean isBound;
	
	/** determine if we are "connected" */
	boolean isConnected;
	
	

	/** The host extracted from the url */
	private String host;
	
	/** Allow for the adjustment of the HTTP Post */
	HttpRequestCallback callback;
	
	
	/**
	 * Short hand for executing a modification operation (add/modify/delete/rename)
	 * @param message The message
	 * @return The response 
	 * @throws LDAPException
	 */
	private LDAPMessage sendMessage(LDAPMessage message) throws LDAPException {
		return (LDAPMessage) sendMessage(message,false);
	}
	
	/**
	 * Short hand for retrieving results from a query
	 * @param message
	 * @return The search results
	 * @throws LDAPException
	 */
	private DSMLSearchResults execQuery(LDAPMessage message) throws LDAPException {
		return (DSMLSearchResults) sendMessage(message,true);
	}
	
	/**
	 * Used to send requests to the server and retrieve responses
	 * @param message The Message To Send
	 * @param isSearch true if returning a DSMLSearchResult, false if LDAPMessage
	 * @return The Server's Response
	 */
	private Object sendMessage(LDAPMessage message,boolean isSearch) throws LDAPException {
		try {
			PostMethod post = new PostMethod(serverString);
			//post.setDoAuthentication(true);
			
			//First load up the content headers
			post.setRequestHeader("Content-Type","text/xml; charset=utf8");
			post.setRequestHeader("SOAPAction","#batchRequest");
			
			
			
			if (this.callback != null) {
				this.callback.manipulationPost(post,this);
			}
			
			
			
			
			StringWriter out = new StringWriter();
			
			DSMLWriter writer = new DSMLWriter(out);
			
			
			PrintWriter pout = new PrintWriter(out);
			
			//First print the SOAP Envelope 
			pout.println("<?xml version=\"1.0\" encoding=\"UTF8\"?>");
			pout.println("<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			pout.println("<soap-env:Body>");
			
			//write out the message
			writer.writeMessage(message);
			writer.finish();
			
			//Complete the SOAP Envelope
			pout.println("</soap-env:Body>");
			pout.println("</soap-env:Envelope>");
			
			
			
			ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes("UTF-8"));
			//Set the input stream
			
			post.setRequestBody(in);
			
			//POST the request
			con.executeMethod(post);
			
			if (post.getStatusCode() != 200) {
				//we have an error, if it's an authorization error throw an invalid credentials exception.  otherwise throw an unwilling to perform.
				if (post.getStatusCode() == 401 || post.getStatusCode() == 403) {
					throw new LDAPException(LDAPException.resultCodeToString(LDAPException.INVALID_CREDENTIALS),LDAPException.INVALID_CREDENTIALS,LDAPException.resultCodeToString(LDAPException.INVALID_CREDENTIALS));
				} else {
					throw new LDAPException(LDAPException.resultCodeToString(LDAPException.UNAVAILABLE),LDAPException.UNAVAILABLE,post.getStatusText());
				}
			}
			
			DSMLReader reader = new DSMLReader(post.getResponseBodyAsStream());
			
			post.releaseConnection();
			
			//Make sure it was successfull
			ArrayList errors = reader.getErrors();
			if (errors.size() > 0) {
				throw ((LDAPException) errors.get(0));
			}
			
			if (isSearch) {
				return new DSMLSearchResults(reader);
			}
			else {
				//return the message
				return reader.readMessage();
			}
		} catch (HttpException e) {
			throw new LDAPLocalException("Http Error",LDAPException.CONNECT_ERROR,e);
		} catch (IOException e) {
			throw new LDAPLocalException("Communications Error",LDAPException.CONNECT_ERROR,e);
		}
	}
	
	/**
	 *Allows for a pre-build DSMLv2 Document to be sent over
	 *the wire.  Usefull when doing batch requests
	 * @param DSML The String version of the DSMLv2
	 * @return List of results
	 * @throws LDAPException
	 */
	public ArrayList sendDoc(String DSML) throws LDAPException {
		ArrayList results = new ArrayList();
		try {
			PostMethod post = new PostMethod(serverString);
			//post.setDoAuthentication(true);
			
			//First load up the content headers
			post.setRequestHeader("Content-Type","text/xml; charset=utf8");
			post.setRequestHeader("SOAPAction","#batchRequest");
			
			
			
			if (this.callback != null) {
				this.callback.manipulationPost(post,this);
			}
			
			
			StringWriter out = new StringWriter();
			
			
			
			
			PrintWriter pout = new PrintWriter(out);
			
			//First print the SOAP Envelope 
			pout.println("<?xml version=\"1.0\" encoding=\"UTF8\"?>");
			pout.println("<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			pout.println("<soap-env:Body>");
			
			//write out the message
			//writer.writeMessage(message);
			//writer.finish();
			pout.write(DSML);
			
			//Complete the SOAP Envelope
			pout.println("</soap-env:Body>");
			pout.println("</soap-env:Envelope>");
			
			StringBufferInputStream in = new StringBufferInputStream(out.toString());
			//Set the input stream
			
			post.setRequestBody(in);
			
			//POST the request
			con.executeMethod(post);
			
			
			
			DSMLReader reader = new DSMLReader(post.getResponseBodyAsStream());
			
			post.releaseConnection();
			
			//Make sure it was successfull
			/*LDAPException e = reader.
			if (e != null) throw e;*/
			
			ArrayList errors = reader.getErrors();
			if (errors.size() > 0) {
				throw ((LDAPException) errors.get(0));
			}
			
			LDAPMessage msg;
			while ((msg = reader.readMessage()) != null) {
				results.add(msg);
			}
			
		} catch (HttpException e) {
			throw new LDAPLocalException("Http Error",LDAPException.CONNECT_ERROR,e);
		} catch (IOException e) {
			throw new LDAPLocalException("Communications Error",LDAPException.CONNECT_ERROR,e);
		}
		
		return results;
	}
	
	/**
	 * Default Contructor, initilizes the http client
	 *
	 */
	public DsmlConnection(){
		this.con = new HttpClient();
		this.con.getState().setAuthenticationPreemptive(true);
		
	}
	
	/**
	 * Creates an instace of DsmlConnection ignoring the socket factory
	 * @param factory
	 */
	public DsmlConnection(LDAPSocketFactory factory) {
		this();
	}
	

	
	/**
	 * Sets the host as the serverUrl, port is ignored
	 * @param serverUrl The Server location and context
	 * @param port The port (ignored)
	 */
	public void connect(String serverUrl, int port) throws LDAPException {
		this.serverString = serverUrl;
		host = serverUrl.substring(serverUrl.indexOf("//") + 2,serverUrl.indexOf("/",serverUrl.indexOf("//") + 2));
		
		this.isConnected = true;
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, byte[], com.novell.ldap.LDAPConstraints)
	 */
	public void bind(int arg0, String binddn, byte[] pass, LDAPConstraints arg3)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, byte[], com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue bind(
		int arg0,
		String arg1,
		byte[] arg2,
		LDAPResponseQueue arg3,
		LDAPConstraints arg4)
		throws LDAPException {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, byte[], com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue bind(
		int arg0,
		String arg1,
		byte[] arg2,
		LDAPResponseQueue arg3)
		throws LDAPException {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, byte[])
	 */
	public void bind(int arg0, String binddn, byte[] pass) throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, java.lang.String, com.novell.ldap.LDAPConstraints)
	 */
	public void bind(int arg0, String binddn, String pass, LDAPConstraints arg3)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(int, java.lang.String, java.lang.String)
	 */
	public void bind(int arg0, String binddn, String pass) throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String, com.novell.ldap.LDAPConstraints)
	 */
	public void bind(String binddn, String pass, LDAPConstraints arg2)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String, java.util.Map, java.lang.Object, com.novell.ldap.LDAPConstraints)
	 */
	public void bind(
		String binddn,
		String pass,
		Map arg2,
		Object arg3,
		LDAPConstraints arg4)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String, java.util.Map, java.lang.Object)
	 */
	public void bind(String binddn, String pass, Map arg2, Object arg3)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String, java.lang.String[], java.util.Map, java.lang.Object, com.novell.ldap.LDAPConstraints)
	 */
	public void bind(
		String binddn,
		String pass,
		String[] arg2,
		Map arg3,
		Object arg4,
		LDAPConstraints arg5)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String, java.lang.String[], java.util.Map, java.lang.Object)
	 */
	public void bind(
		String binddn,
		String arg1,
		String[] arg2,
		Map arg3,
		Object arg4)
		throws LDAPException {
		this.bind(binddn,new String(pass));
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#bind(java.lang.String, java.lang.String)
	 */
	public void bind(String binddn, String password) throws LDAPException {
		if (isBound) {
			//first clear old credentials on server
			GetMethod get = new GetMethod(this.serverString + "?clearbind");
			try {
				con.executeMethod(get);
				
			} catch (HttpException e) {
				
			} catch (IOException e) {
				
			}
		}
		//set the credentials globaly
		this.isBound = false;
		con.getState().setCredentials(null,null,new UsernamePasswordCredentials(binddn,password));
		//try's to connect in order to bind...
		this.search("",LDAPConnection.SCOPE_BASE,"(objectClass=*)", new String[] {"1.1"},false);
		this.isBound = true;
	}

	
	
	public void add(LDAPEntry entry, LDAPConstraints cont)
		throws LDAPException {
		LDAPControl[] conts = cont != null ? cont.getControls() : null;
		LDAPAddRequest add = new LDAPAddRequest(entry,conts);
		this.sendMessage(add);
	}

	public LDAPResponseQueue add(
		LDAPEntry arg0,
		LDAPResponseQueue arg1,
		LDAPConstraints arg2)
		throws LDAPException {
		return null;
	}

	public LDAPResponseQueue add(LDAPEntry arg0, LDAPResponseQueue arg1)
		throws LDAPException {
		return null;
	}

	public void add(LDAPEntry entry) throws LDAPException {
		this.add(entry,(LDAPConstraints) null);
	}
	
	

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification, com.novell.ldap.LDAPConstraints)
	 */
	public void modify(
		String dn,
		LDAPModification mod,
		LDAPConstraints consts)
		throws LDAPException {
		this.modify(dn,new LDAPModification[] {mod},consts);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification, com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue modify(
		String arg0,
		LDAPModification arg1,
		LDAPResponseQueue arg2,
		LDAPConstraints arg3)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification, com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue modify(
		String arg0,
		LDAPModification arg1,
		LDAPResponseQueue arg2)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification)
	 */
	public void modify(String dn, LDAPModification mod)
		throws LDAPException {
			this.modify(dn,new LDAPModification[] {mod},(LDAPConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification[], com.novell.ldap.LDAPConstraints)
	 */
	public void modify(
		String dn,
		LDAPModification[] mods,
		LDAPConstraints consts)
		throws LDAPException {
		LDAPControl[] controls = consts != null ? consts.getControls() : null;
		LDAPModifyRequest msg = new LDAPModifyRequest(dn,mods,controls);
		this.sendMessage(msg);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification[], com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue modify(
		String arg0,
		LDAPModification[] arg1,
		LDAPResponseQueue arg2,
		LDAPConstraints arg3)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification[], com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue modify(
		String arg0,
		LDAPModification[] arg1,
		LDAPResponseQueue arg2)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#modify(java.lang.String, com.novell.ldap.LDAPModification[])
	 */
	public void modify(String dn, LDAPModification[] mods)
		throws LDAPException {
			this.modify(dn,mods,(LDAPConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPConstraints)
	 */
	public void rename(
		String dn,
		String newDn,
		boolean delOld,
		LDAPConstraints consts)
		throws LDAPException {
		this.rename(dn,newDn,"",delOld,(LDAPConstraints) consts);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue rename(
		String arg0,
		String arg1,
		boolean arg2,
		LDAPResponseQueue arg3,
		LDAPConstraints arg4)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue rename(
		String arg0,
		String arg1,
		boolean arg2,
		LDAPResponseQueue arg3)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, boolean)
	 */
	public void rename(String dn, String newDn, boolean delOld)
		throws LDAPException {
		
		this.rename(dn,newDn,"",delOld,(LDAPConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPConstraints)
	 */
	public void rename(
		String dn,
		String newRdn,
		String newParentDN,
		boolean delOld,
		LDAPConstraints constr)
		throws LDAPException {
		LDAPControl[] controls = constr != null ? constr.getControls() : null;
												
		LDAPModifyDNRequest msg = new LDAPModifyDNRequest(dn,newRdn,newParentDN,delOld,controls);
		this.sendMessage(msg);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue rename(
		String arg0,
		String arg1,
		String arg2,
		boolean arg3,
		LDAPResponseQueue arg4,
		LDAPConstraints arg5)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, java.lang.String, boolean, com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue rename(
		String arg0,
		String arg1,
		String arg2,
		boolean arg3,
		LDAPResponseQueue arg4)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#rename(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void rename(String dn, String newRdn, String newParentDN, boolean delOld)
		throws LDAPException {
		
			this.rename(dn,newRdn,newParentDN,delOld,(LDAPConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#delete(java.lang.String, com.novell.ldap.LDAPConstraints)
	 */
	public void delete(String dn, LDAPConstraints consts)
		throws LDAPException {
		LDAPControl[] controls = consts != null ? consts.getControls() : null;
		LDAPDeleteRequest msg = new LDAPDeleteRequest(dn,controls);
		this.sendMessage(msg);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#delete(java.lang.String, com.novell.ldap.LDAPResponseQueue, com.novell.ldap.LDAPConstraints)
	 */
	public LDAPResponseQueue delete(
		String arg0,
		LDAPResponseQueue arg1,
		LDAPConstraints arg2)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#delete(java.lang.String, com.novell.ldap.LDAPResponseQueue)
	 */
	public LDAPResponseQueue delete(String arg0, LDAPResponseQueue arg1)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#delete(java.lang.String)
	 */
	public void delete(String dn) throws LDAPException {
		this.delete(dn,(LDAPConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, com.novell.ldap.LDAPSearchConstraints)
	 */
	public LDAPSearchResults search(
		String base,
		int scope,
		String filter,
		String[] attrs,
		boolean typesOnly,
		LDAPSearchConstraints cons)
		throws LDAPException {
		LDAPControl[] controls = cons != null ? cons.getControls() : null;
		LDAPSearchRequest msg = new LDAPSearchRequest(base,scope,filter,attrs,0,0,0,typesOnly,controls);
		
		
		DSMLSearchResults res = this.execQuery(msg);
		
		
		if (res == null) res = new DSMLSearchResults(null);
		return res;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, com.novell.ldap.LDAPSearchQueue, com.novell.ldap.LDAPSearchConstraints)
	 */
	public LDAPSearchQueue search(
		String base,
		int scope,
		String filter,
		String[] attrs,
		boolean typesOnly,
		LDAPSearchQueue queue,
		LDAPSearchConstraints cons)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, com.novell.ldap.LDAPSearchQueue)
	 */
	public LDAPSearchQueue search(
		String base,
		int scope,
		String filter,
		String[] attrs,
		boolean typesOnly,
		LDAPSearchQueue queue)
		throws LDAPException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean)
	 */
	public LDAPSearchResults search(
		String base,
		int scope,
		String filter,
		String[] attrs,
		boolean typesOnly)
		throws LDAPException {
			return this.search(base,scope,filter,attrs,typesOnly,(LDAPSearchConstraints) null);
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#isConnectionAlive()
	 */
	public boolean isConnectionAlive() {
		GetMethod get = new GetMethod(this.serverString + "?wsdl");
		try {
			con.executeMethod(get);
			return true;
		} catch (HttpException e) {
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#isBound()
	 */
	public boolean isBound() {
		return this.isBound;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#isConnected()
	 */
	public boolean isConnected() {
		return this.isConnected;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#isTLS()
	 */
	public boolean isTLS() {
		return this.serverString.toLowerCase().startsWith("https");
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#disconnect()
	 */
	public void disconnect() throws LDAPException {
		this.serverString = null;
		this.isConnected  = false;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#disconnect(com.novell.ldap.LDAPConstraints)
	 */
	public void disconnect(LDAPConstraints cons) throws LDAPException {
		this.disconnect();
	}

	

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPConnection#sendRequest(com.novell.ldap.LDAPMessage, com.novell.ldap.LDAPMessageQueue)
	 */
	public LDAPMessageQueue sendRequest(LDAPMessage request,
			LDAPMessageQueue queue) throws LDAPException {
		this.sendMessage(request);
		return null;
	}
	
	
	
	/**
	 * @return Returns the call.
	 */
	public HttpRequestCallback getCallback() {
		return callback;
	}
	/**
	 * @param call The call to set.
	 */
	public void setCallback(HttpRequestCallback call) {
		this.callback = call;
	}
	/**
	 * @return Returns the binddn.
	 */
	public String getBinddn() {
		return binddn;
	}
	/**
	 * @return Returns the con.
	 */
	public HttpClient getCon() {
		return con;
	}
	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @return Returns the pass.
	 */
	public String getPass() {
		return pass;
	}
	/**
	 * @return Returns the serverString.
	 */
	public String getServerString() {
		return serverString;
	}
}

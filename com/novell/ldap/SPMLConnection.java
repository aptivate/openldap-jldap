/* **************************************************************************
 *
 * Copyright (C) 2005 Marc Boorshtein All Rights Reserved.
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.openspml.client.LighthouseClient;
import org.openspml.client.SpmlClient;
import org.openspml.message.AddRequest;
import org.openspml.message.AddResponse;
import org.openspml.message.Attribute;
import org.openspml.message.Filter;
import org.openspml.message.FilterTerm;
import org.openspml.message.Identifier;
import org.openspml.message.Modification;
import org.openspml.message.ModifyRequest;
import org.openspml.message.ModifyResponse;
import org.openspml.message.SearchRequest;
import org.openspml.message.SearchResponse;
import org.openspml.message.SearchResult;
import org.openspml.message.SpmlResponse;
import org.openspml.util.SpmlException;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;

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
import com.novell.ldap.rfc2251.RfcFilter;
import com.novell.ldap.spml.SPMLImpl;
import com.novell.ldap.util.*;

/**
 * @author Marc Boorshtein
 *
 * This class is meant to be a drop-in replacement for an LDAPConnection 
 * when working sith synchronous LDAP calls
 */
public class SPMLConnection extends LDAPConnection {

	/** Default spml implementation */
	public static final String DEF_IMPL = "com.novell.ldap.spml.SunIdm";
	
	/** The Connection To The DSMLv2 Server */
	SpmlClient con;
	
	/** The vendor specific version of the SPML implementation */
	SPMLImpl vendorImpl;
	
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
		return null;//return (LDAPMessage) sendMessage(message,false);
	}
	
	/**
	 * Short hand for retrieving results from a query
	 * @param message
	 * @return The search results
	 * @throws LDAPException
	 */
	private DSMLSearchResults execQuery(LDAPMessage message) throws LDAPException {
		return null;//return (DSMLSearchResults) sendMessage(message,true);
	}
	
	
	
	
	
	/**
	 * Default Contructor, initilizes the http client
	 *
	 */
	public SPMLConnection(){
		this.loadImpl(SPMLConnection.DEF_IMPL,null);
		
		
	}
	
	public SPMLConnection(String className){
		this.loadImpl(className,null);
		
		
	}
	
	public SPMLConnection(String className,ClassLoader loader){
		this.loadImpl(className,loader);
		
		
	}
	
	/**
	 * Creates an instace of DsmlConnection ignoring the socket factory
	 * @param factory
	 */
	public SPMLConnection(LDAPSocketFactory factory) {
		this();
	}
	

	private void loadImpl(String className,ClassLoader loader) {
		SPMLImpl impl = null;
		
		if (loader == null) {
			try {
				impl = (SPMLImpl) Class.forName(className).newInstance();
			} catch (Exception e) {
				return;
			}
		} else {
			try {
				impl = (SPMLImpl) loader.loadClass(className).newInstance();
			} catch (Exception e) {
				return;
			}
				
		}
		
		this.vendorImpl = impl;
		this.con = impl.getSpmlClient();
		
	}
	
	
	/**
	 * Sets the host as the serverUrl, port is ignored
	 * @param serverUrl The Server location and context
	 * @param port The port (ignored)
	 */
	public void connect(String serverUrl, int port) throws LDAPException {
		this.serverString = serverUrl;
		host = serverUrl.substring(serverUrl.indexOf("//") + 2,serverUrl.indexOf("/",serverUrl.indexOf("//") + 2));
		try {
			this.con.setUrl(serverUrl);
		} catch (MalformedURLException e) {
			throw new LDAPLocalException(e.toString(), 53, e);
		}
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
			
				this.vendorImpl.logout();
			
		}
		//set the credentials globaly
		this.isBound = false;
		
		
		this.vendorImpl.login(binddn,password);
		
		this.isBound = true;
	}

	
	
	public void add(LDAPEntry entry, LDAPConstraints cont)
		throws LDAPException {
		
		AddRequest add = new AddRequest();
		
		DN dn = new DN(entry.getDN());
		
		RDN rdn = (RDN) dn.getRDNs().get(0);
		Identifier id = new Identifier();
		
		try {
			id.setType(this.getIdentifierType(rdn.getType()));
		} catch (IllegalArgumentException e1) {
			throw new LDAPException("Could not determine type",53, e1.toString(), e1);
		} catch (IllegalAccessException e1) {
			throw new LDAPException("Could not determine type",53, e1.toString(), e1);
		}
		
		id.setId(rdn.getValue());
		
		String objectClass = entry.getAttribute("objectClass").getStringValue();
		add.setObjectClass(objectClass);
		
		Iterator it = entry.getAttributeSet().iterator();
		
		HashMap map = new HashMap();
		
		while (it.hasNext()) {
			LDAPAttribute attrib = (LDAPAttribute) it.next();
			if (attrib.getName().toLowerCase().equals("objectclass")) {
				continue;
			}
			
			String[] vals = attrib.getStringValueArray();
			ArrayList list = new ArrayList();
			for (int i=0,m=vals.length;i<m;i++) {
				list.add(vals[i]);
			}
			
			
			map.put(attrib.getName(),list);
		}
		
		add.setAttributes(map);
		add.setIdentifier(id);
		
		try {
			
			SpmlResponse resp =  con.request(add);
			if (resp.getResult().equals("urn:oasis:names:tc:SPML:1:0#pending")) {
				String res = "";
				List attrs = resp.getOperationalAttributes();
				it = attrs.iterator();
				while (it.hasNext()) {
					Attribute attr = (Attribute) it.next();
					res += "[" + attr.getName() + "=" + attr.getValue() + "] ";
				}
				
				throw new LDAPLocalException(res,0);
				
			} else if (! resp.getResult().equals("urn:oasis:names:tc:SPML:1:0#success")) {
				System.out.println("Response : " + resp.getResult());
				throw new LDAPLocalException(resp.getErrorMessage(), 53);
			}
		} catch (SpmlException e) {
			throw new LDAPException(e.toString(),53,e.toString());
		}
		
		
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
		
		
		
		ModifyRequest modreq = new ModifyRequest();
		
		DN dnVal = new DN(dn);
		
		RDN rdn = (RDN) dnVal.getRDNs().get(0);
		Identifier id = new Identifier();
		
		try {
			id.setType(this.getIdentifierType(rdn.getType()));
		} catch (IllegalArgumentException e1) {
			throw new LDAPException("Could not determine type",53, e1.toString(), e1);
		} catch (IllegalAccessException e1) {
			throw new LDAPException("Could not determine type",53, e1.toString(), e1);
		}
		
		modreq.setIdentifier(id);
		
		id.setId(rdn.getValue());
		
		Modification mod = null;
		
		for (int i=0,m=mods.length;i<m;i++) {
			mod = new Modification();
			mod.setName(mods[i].getAttribute().getName());
			ArrayList list = new ArrayList();
			Enumeration evals = mods[i].getAttribute().getStringValues();
			while (evals.hasMoreElements()) {
				list.add(evals.nextElement());
			}
			mod.setValue(list);
			
			int op = mods[i].getOp();
			switch (op) {
				case LDAPModification.ADD : mod.setOperation("add"); break;
				case LDAPModification.REPLACE : mod.setOperation("replace"); break;
				case LDAPModification.DELETE : mod.setOperation("delete"); break;
			}
			
			modreq.addModification(mod);
			
		}
		
		try {
			ModifyResponse resp = (ModifyResponse) con.request(modreq);
			
			if (! resp.getResult().equals("urn:oasis:names:tc:SPML:1:0#success")) {
				throw new LDAPLocalException(resp.getErrorMessage(), 53);
			}
		} catch (SpmlException e) {
			throw new LDAPException(e.toString(),53,e.toString());
		}
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
		
		
		SearchRequest req = new SearchRequest();
		if (base != null && base.trim().length() != 0) {
			//req.setSearchBase(base);
			
			DN dn = new DN(base);
			
			
			if (! base.toLowerCase().startsWith("ou") && ! base.toLowerCase().startsWith("dc") && ! base.toLowerCase().startsWith("o")) {
				if (scope == 0) {
					Identifier id = new Identifier();
					try {
						id.setType(this.getIdentifierType(((RDN)dn.getRDNs().get(0)).getType()));
					} catch (IllegalArgumentException e1) {
						throw new LDAPException("Could not determine type",53, e1.toString(), e1);
					} catch (IllegalAccessException e1) {
						throw new LDAPException("Could not determine type",53, e1.toString(), e1);
					}
					id.setId(dn.explodeDN(true)[0]);
					
					req.setSearchBase(id);
				} else {
					req.setSearchBase(base);
				}
			} else if (scope == 0) {
				
				return new SPMLSearchResults(new ArrayList());
			}
		}
		
		if (filter != null && ! filter.trim().equalsIgnoreCase("objectClass=*") && ! filter.trim().equalsIgnoreCase("(objectClass=*)")) {
			RfcFilter rfcFilter = new RfcFilter(filter.trim());
			FilterTerm filterPart = new FilterTerm();
			System.out.println("part : " + filterPart.getOperation());
			this.stringFilter(rfcFilter.getFilterIterator(),filterPart);
			req.addFilterTerm(filterPart);
		}
		
		for (int i=0,m=attrs.length;i<m;i++) {
			req.addAttribute(attrs[i]);
		}
		
		SearchResponse res;
		try {
			res = con.searchRequest(req);
		} catch (SpmlException e) {
			throw new LDAPException("Could not search",53, e.toString());
		}
		
		
		
		return new SPMLSearchResults(res.getResults() != null ? res.getResults() : new ArrayList());
		
		
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
		//GetMethod get = new GetMethod(this.serverString + "?wsdl");
		/*try {
			//con.executeMethod(get);
			return true;
		} catch (HttpException e) {
			return true;
		} catch (IOException e) {
			return false;
		}*/
		return true;
		
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
		return null;
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
	
	private String getIdentifierType(String rdnAttrib) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = Identifier.class.getFields();
		for (int i=0,m=fields.length;i<m;i++) {
			if (fields[i].getName().startsWith("TYPE") && fields[i].get(null).toString().endsWith(rdnAttrib)) {
				return fields[i].get(null).toString();
			}
		}
		
		return "";
		
		
	}
	
	private static String byteString(byte[] value) {
        String toReturn = null;
        if (com.novell.ldap.util.Base64.isValidUTF8(value, true)) {
            try {
                toReturn = new String(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "Default JVM does not support UTF-8 encoding" + e);
            }
        } else {
            StringBuffer binary = new StringBuffer();
            for (int i=0; i<value.length; i++){
                //TODO repair binary output
                //Every octet needs to be escaped
                if (value[i] >=0) {
                    //one character hex string
                    binary.append("\\0");
                    binary.append(Integer.toHexString(value[i]));
                } else {
                    //negative (eight character) hex string
                    binary.append("\\"+
                            Integer.toHexString(value[i]).substring(6));
                }
            }
            toReturn = binary.toString();
        }
        return toReturn;
    }
	
	private void stringFilter(Iterator itr, FilterTerm filter) {
        int op=-1;
        //filter.append('(');
        String comp = null;
        byte[] value;
        boolean doAdd = false;
        boolean isFirst = true;
        FilterTerm part;
        while (itr.hasNext()){
            Object filterpart = itr.next();
            if (filterpart instanceof Integer){
                op = ((Integer)filterpart).intValue();
                switch (op){
                    case LDAPSearchRequest.AND:
                        if (filter.getOperation() != null) {
	                        FilterTerm andFilter = new FilterTerm();
	                        andFilter.setOperation(FilterTerm.OP_AND);
	                        filter.addOperand(andFilter);
	                        filter = andFilter;
                        } else {
                        	filter.setOperation(FilterTerm.OP_AND);
                        }
                    	break;
                    case LDAPSearchRequest.OR:
                    	if (filter.getOperation() != null) {
	                    	FilterTerm orFilter = new FilterTerm();
		                    orFilter.setOperation(FilterTerm.OP_OR);
		                    filter.addOperand(orFilter);
		                    filter = orFilter;
                    	} else {
                    		if (filter.getOperation() == null) {
                    			filter.setOperation(FilterTerm.OP_OR);
                    		}
                    	}
                        break;
                    case LDAPSearchRequest.NOT:
                    	if (filter.getOperation() != null) {
	                    	FilterTerm notFilter = new FilterTerm();
	                    	notFilter.setOperation(FilterTerm.OP_NOT);
	                    	filter.addOperand(notFilter);
	                    	filter = notFilter;
                    	} else {
                    		filter.setOperation(FilterTerm.OP_NOT);
                    	}
                        break;
                    case LDAPSearchRequest.EQUALITY_MATCH:{
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
                        part.setName((String)itr.next());
                        part.setOperation(FilterTerm.OP_EQUAL);
                        value = (byte[])itr.next();
                        part.setValue(byteString(value));
                        
                        if (doAdd) {
                        	filter.addOperand(part);
                        }
                        
                        break;
                    }
                    case LDAPSearchRequest.GREATER_OR_EQUAL:{
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
                        part.setName((String)itr.next());
                        part.setOperation(FilterTerm.OP_GTE);
                        value = (byte[])itr.next();
                        part.setValue(byteString(value));
                        if (doAdd) {
                        	filter.addOperand(part);
                        }
                        break;
                    }
                    case LDAPSearchRequest.LESS_OR_EQUAL:{
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
                        part.setName((String)itr.next());
                        part.setOperation(FilterTerm.OP_LTE);
                        value = (byte[])itr.next();
                        part.setValue(byteString(value));
                        if (doAdd) {
                        	filter.addOperand(part);
                        }
                        break;
                    }
                    case LDAPSearchRequest.PRESENT:
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
	                    part.setName((String)itr.next());
	                    part.setOperation(FilterTerm.OP_PRESENT);
	                    if (doAdd) {
                        	filter.addOperand(part);
                        }
                        
                        break;
                    case LDAPSearchRequest.APPROX_MATCH:
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
	                    part.setName((String)itr.next());
	                    part.setOperation(FilterTerm.OP_APPROX);
	                    value = (byte[])itr.next();
	                    part.setValue(byteString(value));
	                    if (doAdd) {
                        	filter.addOperand(part);
                        }
                        break;
                    case LDAPSearchRequest.EXTENSIBLE_MATCH:
                        String oid = (String)itr.next();

	                    if (filter.getOperation() == null) {
	                		part = filter;
	                	} else {
	                		part = new FilterTerm();
	                		doAdd = true;
	                	}
	                    part.setName((String)itr.next() + ":" + oid);
	                    part.setOperation(FilterTerm.OP_EXTENSIBLE_MATCH);
	                    value = (byte[])itr.next();
	                    part.setValue(byteString(value));
	                    if (doAdd) {
                        	filter.addOperand(part);
                        }
                        
                        
                        break;
                    case LDAPSearchRequest.SUBSTRINGS:{
                    	if (filter.getOperation() == null) {
                    		part = filter;
                    	} else {
                    		part = new FilterTerm();
                    		doAdd = true;
                    	}
	                    part.setName((String)itr.next());
	                    part.setOperation(FilterTerm.OP_SUBSTRINGS);
                        boolean noStarLast = false;
                        while (itr.hasNext()){
                            op = ((Integer)itr.next()).intValue();
                            switch(op){
                                case LDAPSearchRequest.INITIAL:
                                    filter.addSubstring((String)itr.next());
                                    
                                    noStarLast = false;
                                    break;
                                case LDAPSearchRequest.ANY:
                                    
                                   filter.addSubstring((String)itr.next());
                                    
                                    noStarLast = false;
                                    break;
                                case LDAPSearchRequest.FINAL:
                                    
                                    
                                	filter.addSubstring((String)itr.next());
                                    break;
                            }
                            
                            
                            
                            
                        }
                        
                        if (doAdd) {
                        	filter.addOperand(part);
                        }
                        
                        break;
                    }
                }
            } else if (filterpart instanceof Iterator){
                stringFilter((Iterator)filterpart, filter);
            }
            
            
        }
        
        
        
        
    }
}

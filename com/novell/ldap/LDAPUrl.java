/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/LDAPUrl.java,v 1.6 2000/09/13 15:29:05 judy Exp $
*
* Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.util.*;
import java.net.*;
import com.novell.ldap.client.Debug;

/*
* 4.38 public class LDAPUrl
*/

/**
*
*  Encapsulates parameters of an LDAP URL query.  
*
*  An LDAPUrl object can be passed to LDAPConnection.search to retrieve
*  search results.
*/
public class LDAPUrl {

	// Broken out parts of the URL
	static private boolean	enclosed = false;			// URL is enclosed by < & >
	static private String	scheme = null;				// URL scheme
	static private String	host = null;				// Host
	static private int		port;						// Port
	static private String	dn = "";					// Base DN
	static private String	attrs = null;				// Attributes
	static private String	filter = "(objectClass=*)";	// Filter
	static private int		scope  = LDAPv2.SCOPE_BASE;	// Scope
	static private String	extensions = null;			// Extensions

	/*
	* 4.38.1 Constructors
	*/

	/**
	* Constructs a URL object with the specified string as the URL.
	*
	* @param url       An explicit LDAP URL string, for example
	*                  "ldap://ldap.acme.com:80/o=Ace%20Industry,c=us?cn
	*                  ,sn?sub?(objectclass=inetOrgPerson)".
	*
	* @exception MalformedURLException The specified URL cannot be parsed.
	*/
	public LDAPUrl(String url) throws MalformedURLException {
		throw new RuntimeException("LDAPUrl: LDAPUrl() not implemented");
	}


	/**
	* Constructs a URL object with the specified host, port, and DN. 
	*
	* <p>This form is used to create URL references to a particular object 
	* in the directory.</p>
	*
	*  @param host     The host name of LDAP server, or null for "nearest
	*                  X.500/LDAP".
	*<br><br>
	*  @param port     The port number for LDAP server (use
	*                  LDAPConnection.DEFAULT_PORT for default port).
	*<br><br>
	*  @param dn      The distinguished name of the object to fetch.
	*
	*/
	public LDAPUrl(String host, int port, String dn) {
		throw new RuntimeException("LDAPUrl: LDAPUrl() not implemented");
	}

	/**
	* Constructs a full-blown LDAP URL to specify an LDAP search operation.
	*
	*
	*  @param host     The host name of LDAP server, or null for "nearest
	*                  X.500/LDAP".
	*<br><br>
	*  @param port     The port number for LDAP server (use
	*                  LDAPConnection.DEFAULT_PORT for default port).
	*<br><br>
	*  @param dn       The distinguished name of object to fetch.
	*<br><br>
	*  attrNames       The names of attributes to retrieve (use null for all
	*                  attributes).
	*<br><br>
	*  @param scope    The depth of search and uses one of the following 
	*                  from LDAPv2: SCOPE_BASE, SCOPE_ONE, SCOPE_SUB.
	*<br><br>
	*  @param filter   The search filter specifying the search criteria.
	*/
	public LDAPUrl(String host,
	              int port,
	              String dn,
	              String attrNames[],
	              int scope,
	              String filter) {
		throw new RuntimeException("LDAPUrl: LDAPUrl() not implemented");
	}

	/*
	* 4.38.2 decode
	*/

	/**
	* Decodes a URL-encoded string. 
	*
	* <p>Any occurences of %HH are decoded to the hex value represented. 
	* However, this method does NOT decode "+" into " ".
	* 
	*  @param URLEncoded     String to decode.
	*
	*  @return The decoded string.
	*
	*  @exception MalformedURLException The URL could not be parsed.
	*/
	public static String decode(String URLEncoded) throws
	MalformedURLException {
		throw new RuntimeException("LDAPUrl: decode() not implemented");
	}

	/*
	* 4.38.3 encode
	*/

	/**
	* Encodes an arbitrary string using the URL encoding rules. 
	*
	* <p> Any illegal characters are encoded as %HH.  
	* However, this method does NOT encode " " into "+".</p>
	*
	*
	*  @param toEncode     The string to encode.
	*
	* @return The URL-encoded string.
	*/
	public static String encode(String toEncode) {
		throw new RuntimeException("LDAPUrl: encode() not implemented");
	}

	/*
	* 4.38.4 getAttributeArray
	*/

	/**
	* Returns an array of attribute names specified in the URL.
	*
	* @return An array of attribute names in the URL.
	*/
	public String[] getAttributeArray() {
		throw new RuntimeException("LDAPUrl: getAtributeArray() not implemented");
	}

	/*
	* 4.38.5 getAttributes
	*/

	/**
	* Returns an enumerator for the attribute names specified in the URL.
	*
	* @return An enumeration of attribute names.
	*/
	public Enumeration getAttributes() {
		throw new RuntimeException("LDAPUrl: getAtributes() not implemented");
	}

	/*
	* 4.38.6 getDN
	*/

	/**
	* Returns the distinguished name encapsulated in the URL.
	*
	* @return The base distinguished name specified in the URL.
	*/
	public String getDN() {
		throw new RuntimeException("LDAPUrl: getDN() not implemented");
	}

	/*
	* 4.38.8 getFilter
	*/

	/**
	* Returns the search filter or the default filter 
	* (objectclass=*) if none was specified.
	*
	* @return The search filter.
	*/
	public String getFilter() {
		throw new RuntimeException("LDAPUrl: getFilter() not implemented");
	}

	/*
	* 4.38.9 getHost
	*/

	/**
	* Returns the name of the LDAP server in the URL.
	*
	* @return The host name specified in the URL.
	*/
	public String getHost() {
		throw new RuntimeException("LDAPUrl: getHost() not implemented");
	}

	/*
	* 4.38.10 getPort
	*/

	/**
	* Returns the port number of the LDAP server in the URL.
	*
	* @return The port number in the URL.
	*/
	public int getPort() {
		throw new RuntimeException("LDAPUrl: getPort() not implemented");
	}

	/*
	* 4.38.12 getUrl
	*/

	/**
	* Returns a valid string representation of this LDAP URL.
	*
	* @return The string representation of the LDAP URL.
	*/
	public String getUrl() {
		throw new RuntimeException("LDAPUrl: getUrl() not implemented");
	}

	private void parseUrl( String url) throws MalformedURLException
	{
		int scanStart = 0;
		int scanEnd = url.length();

		Debug.trace( "Referrals", "parseURL(%s" + url + ")");
		if( url == null)
			throw new MalformedURLException("LDAPURL: URL cannot be null");

		// Check if URL is enclosed by < & >
		if( url.charAt(scanStart) == '<') {
			if( url.charAt(scanEnd - 1) != '>')
				throw new MalformedURLException("LDAPURL: URL bad enclosure");
			enclosed = true;
			scanStart += 1;
			scanEnd -= 1;
			Debug.trace( "Referrals", "LDAPURL: parseURL: Url is enclosed");
		}

		// Determine the URL scheme and set appropriate default port
		if( url.substring(scanStart, scanStart + 4).equalsIgnoreCase( "URL:")) {
			scanStart += 4;		
		}
		if( url.substring(scanStart, scanStart + 7).equalsIgnoreCase( "ldap://")) {
			scheme = url.substring(scanStart, scanStart + 4);
			scanStart += 7;
			port = LDAPConnection.DEFAULT_PORT;
		} else
		if( url.substring(scanStart, scanStart + 8).equalsIgnoreCase( "ldaps://")) {
			scheme = url.substring(scanStart, scanStart + 5);
			scanStart += 8;
			port = LDAPConnection.DEFAULT_SSL_PORT;
		} else {
			throw new MalformedURLException("LDAPURL: URL scheme is not ldap");
		}
		Debug.trace( "Referrals", "parseURL: scheme is " + scheme);

		// Find where host:port ends and dn begins
		int dnStart = url.indexOf("/", scanStart);
		int hostPortEnd;
		if( dnStart < 0) {
			hostPortEnd = scanEnd;
		} else {
			hostPortEnd = dnStart;
		}
		// Check for IPV6 "[ipaddress]:port"
		int portStart;
		int hostEnd = hostPortEnd;
		if( url.charAt(scanStart) == '[') {
			hostEnd = url.indexOf(']', scanStart + 1);
			if ( (hostEnd >= hostPortEnd) || (hostEnd == -1)) {
				throw new MalformedURLException("LDAPURL: \"]\" is missing on IPV6 host name");
			}
			// Get host w/o the [ & ]
			host = url.substring( scanStart +1, hostEnd);
			portStart = url.indexOf(":", hostEnd);
			if ( (portStart < hostPortEnd) && (portStart != -1)) {
				// port is specified
				port = Integer.decode( url.substring(portStart+1, hostPortEnd) ).intValue();
				Debug.trace( "Referrals", "parseURL: IPV6 host " + host + " port " + port);
			} else {
				Debug.trace( "Referrals", "parseURL: IPV6 host " + host + " default port " + port);
			}
		} else {
			portStart = url.indexOf(":", scanStart);
			// Isolate the host and port
			if( (portStart < 0) || (portStart > hostPortEnd)) {
				// no port is specified, we keep the default
				host = url.substring(scanStart, hostPortEnd);
				Debug.trace( "Referrals", "parseURL: host " + host + " default port " + port);
			} else {
				// port specified in URL
				host = url.substring(scanStart, portStart);
				port = Integer.decode( url.substring(portStart+1, hostPortEnd) ).intValue();
				Debug.trace( "Referrals", "parseURL: host " + host + " port " + port);
			}
		}

		scanStart = hostPortEnd + 1;
		if( scanStart > scanEnd )
			return;

	}
}

/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/LDAPUrl.java,v 1.7 2000/09/22 17:23:57 vtag Exp $
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
	static private String[] attrs = null;				// Attributes
	static private String	filter = "(objectClass=*)";	// Filter
	static private int		scope  = LDAPv2.SCOPE_BASE;	// Scope
	static private String[]	extensions = null;			// Extensions

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
		parseURL( url );
		return;
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

	private String[] parseList(	String listStr,	// input String
								char delimiter,	// list item delimiter
								int listStart,	// start of list
								int listEnd)	// end of list + 1
	{
		String[] list;
		// First count how many items are specified
		int itemStart = listStart;
		int itemEnd;
		int itemCount = 0;
		while( itemStart > 0 ) {
			itemCount += 1;
			itemEnd = listStr.indexOf(delimiter, itemStart);				
			if( (itemEnd > 0) && (itemEnd < listEnd) ) {
				itemStart = itemEnd + 1;
			} else {
				break;
			}
		}
		// Now fill in the array with the attributes
		itemStart = listStart;
		list = new String[itemCount];
		itemCount = 0;
		while( itemStart > 0 ) {
			itemEnd = listStr.indexOf(delimiter, itemStart);				
			if( (itemEnd > 0) && (itemEnd <= itemStart) ) {
				list[itemCount] = listStr.substring( itemStart, itemEnd);
				itemStart = itemEnd + 1;
			} else {
				break;
			}
		}
		return list;
	}


	private void parseURL( String url) throws MalformedURLException
	{
		int scanStart = 0;
		int scanEnd = url.length();

		if( Debug.LDAP_DEBUG)
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
			if( Debug.LDAP_DEBUG)
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
		if( Debug.LDAP_DEBUG)
			Debug.trace( "Referrals", "parseURL: scheme is " + scheme);

		// Find where host:port ends and dn begins
		int dnStart = url.indexOf("/", scanStart);
		int hostPortEnd = scanEnd;
		if( dnStart < 0) {
			/*
			 * Kludge. check for ldap://111.222.333.444:389??cn=abc,o=company
			 *
			 * Check for broken Novell referral format.  The dn is in
			 * the scope position, but the required slash is missing.
			 * This is illegal syntax but we need to account for it.
			 * Fortunately it can't be confused with anything real.
			 */
			dnStart = url.indexOf("?", scanStart);
			if( dnStart > 0) {
				if( url.charAt( dnStart+1) == '?') {
					hostPortEnd = dnStart;
					dnStart += 2;
					if( Debug.LDAP_DEBUG)
						Debug.trace( "Referrals", "parseURL: Novell bad syntax found");
				} else {
					dnStart = -1;
				}
			}
		} else {
			hostPortEnd = dnStart;
		}
		// Check for IPV6 "[ipaddress]:port"
		int portStart;
		int hostEnd = hostPortEnd;
		if( url.charAt(scanStart) == '[') {
			hostEnd = url.indexOf(']', scanStart + 1);
			if( (hostEnd >= hostPortEnd) || (hostEnd == -1)) {
				throw new MalformedURLException("LDAPURL: \"]\" is missing on IPV6 host name");
			}
			// Get host w/o the [ & ]
			host = url.substring( scanStart +1, hostEnd);
			portStart = url.indexOf(":", hostEnd);
			if( (portStart < hostPortEnd) && (portStart != -1)) {
				// port is specified
				port = Integer.decode( url.substring(portStart+1, hostPortEnd) ).intValue();
				if( Debug.LDAP_DEBUG)
					Debug.trace( "Referrals", "parseURL: IPV6 host " + host + " port " + port);
			} else {
				if( Debug.LDAP_DEBUG)
					Debug.trace( "Referrals", "parseURL: IPV6 host " + host + " default port " + port);
			}
		} else {
			portStart = url.indexOf(":", scanStart);
			// Isolate the host and port
			if( (portStart < 0) || (portStart > hostPortEnd)) {
				// no port is specified, we keep the default
				host = url.substring(scanStart, hostPortEnd);
				if( Debug.LDAP_DEBUG)
					Debug.trace( "Referrals", "parseURL: host " + host + " default port " + port);
			} else {
				// port specified in URL
				host = url.substring(scanStart, portStart);
				port = Integer.decode( url.substring(portStart+1, hostPortEnd) ).intValue();
				if( Debug.LDAP_DEBUG)
					Debug.trace( "Referrals", "parseURL: host " + host + " port " + port);
			}
		}

		scanStart = hostPortEnd + 1;
		if( (scanStart >= scanEnd) || (dnStart < 0) )
			return;

		// Parse out the base dn
		scanStart = dnStart + 1;					

		int attrsStart = url.indexOf('?', scanStart);
		if( attrsStart < 0 ) {
			dn = url.substring( scanStart, scanEnd);
		} else {
			dn = url.substring( scanStart, attrsStart);
		}

		if( Debug.LDAP_DEBUG)
			Debug.trace( "Referrals", "parseURL: dn " + dn);
		scanStart = attrsStart + 1;					
		if( (scanStart >= scanEnd) || (attrsStart < 0) )
			return;

		// Parse out the attributes
		int scopeStart = url.indexOf('?', scanStart);
		if( scopeStart < 0)
			scopeStart = scanEnd - 1;
		attrs = parseList( url, ',', attrsStart + 1, scopeStart);
		if( Debug.LDAP_DEBUG) {
			Debug.trace( "Referrals", "parseURL: " + attrs.length + " attributes" );
			for( int i = 0; i < attrs.length; i++) {
				Debug.trace( "Referrals", "\t" + attrs[i] );
			}
		}

		scanStart = scopeStart + 1;					
		if( scanStart >= scanEnd)
			return;

		// Parse out the scope
		int filterStart = url.indexOf('?',scanStart);
		String scopeStr;
		if( filterStart < 0 ) {
			 scopeStr = url.substring( scanStart, scanEnd);
		} else {
			 scopeStr = url.substring( scanStart, filterStart);
		}
		if( scopeStr.equalsIgnoreCase("")) {
			scope = LDAPv2.SCOPE_BASE;
		} else
		if( scopeStr.equalsIgnoreCase("base")) {
			scope = LDAPv2.SCOPE_BASE;
		} else
		if( scopeStr.equalsIgnoreCase("one")) {
			scope = LDAPv2.SCOPE_ONE;
		} else
		if( scopeStr.equalsIgnoreCase("sub")) {
			scope = LDAPv2.SCOPE_SUB;
		} else {
			throw new MalformedURLException("LDAPURL: URL invalid scope");
		}

		if( Debug.LDAP_DEBUG)
			Debug.trace( "Referrals", "parseURL: scope(" + scope + ") " + scopeStr);

		scanStart = filterStart + 1;
		if( (scanStart >= scanEnd) || (filterStart < 0) )
			return;

		// Parse out the filter
		scanStart = filterStart + 1;					

		String filterStr;
		int extStart = url.indexOf('?', scanStart);
		if( extStart < 0 ) {
			filterStr = url.substring( scanStart, scanEnd);
		} else {
			filterStr = url.substring( scanStart, extStart);
		}

		if( ! filterStr.equals("") ) {
			filter = filterStr;	// Only modify if not the default filter
		}
		if( Debug.LDAP_DEBUG)
			Debug.trace( "Referrals", "parseURL: filter " + filter);

		scanStart = extStart + 1;					
		if( (scanStart >= scanEnd) || (extStart < 0) )
			return;
		
		// Parse out the extensions
		int end = url.indexOf('?', scanStart);
		if( end > 0)
			throw new MalformedURLException("LDAPURL: URL has too many ? fields");
		extensions = parseList( url, ',', scanStart, scanEnd);
		if( Debug.LDAP_DEBUG) {
			Debug.trace( "Referrals", "parseURL: " + extensions.length + " extensions" );
			for( int i = 0; i < extensions.length; i++) {
				Debug.trace( "Referrals", "\t" + extensions[i] );
			}
		}

		return;
	}
}

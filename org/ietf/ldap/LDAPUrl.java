/* **************************************************************************
* $OpenLDAP$
*
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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

package org.ietf.ldap;

import java.net.MalformedURLException;
import java.util.Enumeration;

/**
 *
 * Encapsulates parameters of an LDAP URL query as defined in RFC2255.
 *
 * @see com.novell.ldap.LDAPUrl
 */
public class LDAPUrl {

    private com.novell.ldap.LDAPUrl url;

    /**
     * Constructs a URL object with the specified string as the URL.
     *
     * @see com.novell.ldap.LDAPUrl#LDAPUrl(String)
     */
    public LDAPUrl(String url) throws MalformedURLException
    {
        this.url = new com.novell.ldap.LDAPUrl( url);
        return;
    }


    /**
     * Constructs a URL object with the specified host, port, and DN.
     *
     * @see com.novell.ldap.LDAPUrl#LDAPUrl(String,int,String)
     */
    public LDAPUrl(String host, int port, String dn)
    {
		url = new com.novell.ldap.LDAPUrl( host, port, dn);
		return;
    }

    /**
     * Constructs an LDAP URL with all fields explicitly assigned, to
     * specify an LDAP search operation.
     *
     * @see com.novell.ldap.LDAPUrl#LDAPUrl(String,int,String)
     */
    public LDAPUrl(String host,
                   int port,
                   String dn,
                   String attrNames[],
                   int scope,
                   String filter)
    {
		url = new com.novell.ldap.LDAPUrl(
		                        host, port, dn, attrNames, scope, filter);
		return;
    }

    /**
     * Returns the com.novell.ldap.LDAPUrl object
     */
    /* package */
    com.novell.ldap.LDAPUrl getWrappedObject()
    {
        return url;
    }

    /**
     * Decodes a URL-encoded string.
     *
     * @see com.novell.ldap.LDAPUrl#decode(String)
     */
    public static String decode(String URLEncoded)
                        throws MalformedURLException
    {
        return com.novell.ldap.LDAPUrl.decode( URLEncoded);
    }

    /**
     * Encodes an arbitrary string using the URL encoding rules.
     *
     * @see com.novell.ldap.LDAPUrl#encode(String)
     */
    public static String encode(String toEncode)
    {
        return com.novell.ldap.LDAPUrl.encode( toEncode);
    }

    /**
     * Returns an array of attribute names specified in the URL.
     *
     * @see com.novell.ldap.LDAPUrl#getAttributeArray()
     */
    public String[] getAttributeArray() {
		return url.getAttributeArray();
    }

    /**
     * Returns an enumerator for the attribute names specified in the URL.
     *
     * @see com.novell.ldap.LDAPUrl#getAttributes()
     */
    public Enumeration getAttributes() {
		return url.getAttributes();
    }

    /**
     * Returns the distinguished name encapsulated in the URL.
     *
     * @see com.novell.ldap.LDAPUrl#getDN()
     */
    public String getDN() {
		return url.getDN();
    }

    /**
     * Returns any LDAP URL extensions specified, or null if none are
     * specified.
     *
     * @see com.novell.ldap.LDAPUrl#getExtensions()
     */
    public String[] getExtensions() {
		return url.getExtensions();
    }

    /**
     * Returns the search filter or the default filter
     * (objectclass=*) if none was specified.
     *
     * @see com.novell.ldap.LDAPUrl#getFilter()
     */
    public String getFilter() {
		return url.getFilter();
    }

    /**
     * Returns the name of the LDAP server in the URL.
     *
     * @see com.novell.ldap.LDAPUrl#getHost()
     */
    public String getHost() {
		return url.getHost();
    }

    /**
     * Returns the port number of the LDAP server in the URL.
     *
     * @see com.novell.ldap.LDAPUrl#getPort()
     */
    public int getPort()
    {
		return url.getPort();
    }

    /**
     * Returns the depth of search.
     *
     * @see com.novell.ldap.LDAPUrl#getScope()
     */
    public int getScope()
    {
		return url.getScope();
    }

    /**
     * Returns a valid string representation of this LDAP URL.
     *
     * @see com.novell.ldap.LDAPUrl#toString()
     */
    public String toString()
    {
        return "org.ietf.ldap.LDAPUrl:" + url.getUrl();
    }
}

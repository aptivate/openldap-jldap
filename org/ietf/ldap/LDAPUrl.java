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
 *  @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html">
            com.novell.ldap.LDAPUrl</a>
 */
public class LDAPUrl implements Cloneable {

    private com.novell.ldap.LDAPUrl url;

    /**
     * Constructs a URL object with the specified string as the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#LDAPUrl(java.lang.String)">
            com.novell.ldap.LDAPUrl.LDAPUrl(String)</a>
     */
    public LDAPUrl(String url) throws MalformedURLException
    {
        this.url = new com.novell.ldap.LDAPUrl( url);
        return;
    }


    /**
     * Constructs a URL object with the specified host, port, and DN.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#LDAPUrl(java.lang.String, int, java.lang.String)">
            com.novell.ldap.LDAPUrl.LDAPUrl(String, int, String)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#LDAPUrl(java.lang.String, int, java.lang.String, java.lang.String[], int, java.lang.String, java.lang.String[])">
            com.novell.ldap.LDAPUrl.LDAPUrl(String, int, String,
            String[], int, String, String[])</a>
     */
    public LDAPUrl(String host,
                   int port,
                   String dn,
                   String[] attrNames,
                   int scope,
                   String filter,
                   String extensions[])
    {
		url = new com.novell.ldap.LDAPUrl(
                host, port, dn, attrNames, scope, filter, extensions);
		return;
    }

    /**
     * Returns a clone of this URL object.
     *
     * @return clone of this URL object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#clone()">com.novell.ldap.LDAPUrl.clone()</a>
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            ((LDAPUrl)newObj).url = (com.novell.ldap.LDAPUrl)this.url.clone();
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * This constructor is used by clone and sets wrapped novell URL
     */
    /*package*/
    LDAPUrl( com.novell.ldap.LDAPUrl novellURL ){
        this.url = novellURL;
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#decode(java.lang.String)">
            com.novell.ldap.LDAPUrl.decode(String)</a>
     */
    public static String decode(String URLEncoded)
                        throws MalformedURLException
    {
        return com.novell.ldap.LDAPUrl.decode( URLEncoded);
    }

    /**
     * Encodes an arbitrary string using the URL encoding rules.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#encode(java.lang.String)">
            com.novell.ldap.LDAPUrl.encode(String)</a>
     */
    public static String encode(String toEncode)
    {
        return com.novell.ldap.LDAPUrl.encode( toEncode);
    }

    /**
     * Returns an array of attribute names specified in the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getAttributeArray()">
            com.novell.ldap.LDAPUrl.getAttributeArray()</a>
     */
    public String[] getAttributeArray() {
		return url.getAttributeArray();
    }

    /**
     * Returns an enumerator for the attribute names specified in the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getAttributes()">
            com.novell.ldap.LDAPUrl.getAttributes()</a>
     */
    public Enumeration getAttributes() {
		return url.getAttributes();
    }

    /**
     * Returns the distinguished name encapsulated in the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getDN()">
            com.novell.ldap.LDAPUrl.getDN()</a>
     */
    public String getDN() {
		return url.getDN();
    }

    /**
     * Returns any LDAP URL extensions specified, or null if none are
     * specified.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getExtensions()">
            com.novell.ldap.LDAPUrl.getExtensions()</a>
     */
    public String[] getExtensions() {
		return url.getExtensions();
    }

    /**
     * Returns the search filter or the default filter
     * (objectclass=*) if none was specified.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getFilter()">
            com.novell.ldap.LDAPUrl.getFilter()</a>
     */
    public String getFilter() {
		return url.getFilter();
    }

    /**
     * Returns the name of the LDAP server in the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getHost()">
            com.novell.ldap.LDAPUrl.getHost()</a>
     */
    public String getHost() {
		return url.getHost();
    }

    /**
     * Returns the port number of the LDAP server in the URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getPort()">
            com.novell.ldap.LDAPUrl.getPort()</a>
     */
    public int getPort()
    {
		return url.getPort();
    }

    /**
     * Returns the depth of search.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#getScope()">
            com.novell.ldap.LDAPUrl.getScope()</a>
     */
    public int getScope()
    {
		return url.getScope();
    }

    /**
     * Returns a valid string representation of this LDAP URL.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPUrl.html#toString()">
            com.novell.ldap.LDAPUrl.toString()</a>
     */
    public String toString()
    {
        return url.toString();
    }
}

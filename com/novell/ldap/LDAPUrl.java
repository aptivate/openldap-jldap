/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPUrl.java,v 1.4 2000/08/28 22:18:59 vtag Exp $
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
   }

   /*
    * 4.27.2 decode
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
      return null;
   }

   /*
    * 4.27.3 encode
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
      return null;
   }

   /*
    * 4.27.4 getAttributeArray
    */

   /**
    * Returns an array of attribute names specified in the URL.
    *
    * @return An array of attribute names in the URL.
    */
   public String[] getAttributeArray() {
      return null;
   }

   /*
    * 4.27.5 getAttributes
    */

   /**
    * Returns an enumerator for the attribute names specified in the URL.
    *
    * @return An enumeration of attribute names.
    */
   public Enumeration getAttributes() {
      return null;
   }

   /*
    * 4.27.6 getDN
    */

   /**
    * Returns the distinguished name encapsulated in the URL.
    *
    * @return The base distinguished name specified in the URL.
    */
   public String getDN() {
      return null;
   }

   /*
    * 4.27.7 getFilter
    */

   /**
    * Returns the search filter or the default filter 
    * (objectclass=*) if none was specified.
    *
    * @return The search filter.
    */
   public String getFilter() {
      return null;
   }

   /*
    * 4.27.8 getHost
    */

   /**
    * Returns the host name of the LDAP server in the URL.
    *
    * @return The host name specified in the URL.
    */
   public String getHost() {
      return null;
   }

   /*
    * 4.27.9 getPort
    */

   /**
    * Returns the port number of the LDAP server to connect to.
    */
   public int getPort() {
      return 0;
   }

   /*
    * 4.27.10 getUrl
    */

   /**
    * Returns a valid string representation of this LDAP URL.
    */
   public String getUrl() {
      return null;
   }

}

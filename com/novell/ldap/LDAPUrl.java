/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPUrl.java,v 1.3 2000/08/03 22:06:19 smerrill Exp $
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

/**
 * 4.27 public class LDAPUrl
 *
 *  Encapsulates parameters of an LDAP Url query, as defined in [7].  An
 *  LDAPUrl object can be passed to LDAPConnection.search to retrieve
 *  search results.
 */
public class LDAPUrl {

   /*
    * 4.27.1 Constructors
    */

   /**
    * Constructs a URL object with the specified string as URL.
    */
   public LDAPUrl(String url) throws MalformedURLException {
   }


   /**
    * Constructs a URL object with the specified host, port, and DN. This
    * form is used
    * to create URL references to a particular object in the directory.
    */
   public LDAPUrl(String host, int port, String dn) {
   }

   /**
    * Constructs a full-blown LDAP URL to specify an LDAP search operation.
    *
    * Parameters are:
    *
    *  url            An explicit URL string, e.g.
    *                  "ldap://ldap.acme.com:80/o=Ace%20Industry,c=us?cn
    *                  ,sn?sub?(objectclass=inetOrgPerson)".
    *
    *  host           Host name of LDAP server, or null for "nearest
    *                  X.500/LDAP".
    *
    *  port           Port number for LDAP server (use
    *                  LDAPConnection.DEFAULT_PORT for default port).
    *
    *  dn             Distinguished name of object to fetch.
    *
    *  attrNames      Names of attributes to retrieve. null for all
    *                  attributes.
    *
    *  scope          Depth of search (in DN namespace). Use one of
    *                  SCOPE_BASE, SCOPE_ONE, SCOPE_SUB from LDAPv2.
    *
    *  filter         Search filter specifying the search criteria, as
    *                  defined in [3].
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
    * Decodes a URL-encoded string. Any occurences of %HH are decoded to
    * the hex value represented. However, this method does NOT decode "+"
    * into " ". See [9] for details on URL encoding/decoding.
    *
    * Parameters are:
    *
    *  URLEncoded     String to decode.
    */
   public static String decode(String URLEncoded) throws
   MalformedURLException {
      return null;
   }

   /*
    * 4.27.3 encode
    */

   /**
    * Encodes an arbitrary string. Any illegal characters are encoded as
    * %HH.  However, this method does NOT encode " " into "+".
    *
    * Parameters are:
    *
    *  toEncode       String to encode.
    */
   public static String encode(String toEncode) {
      return null;
   }

   /*
    * 4.27.4 getAttributes
    */

   /**
    * Returns an array of attribute names specified in the URL.
    */
   public String[] getAttributeArray() {
      return null;
   }

   /*
    * 4.27.5 getAttributes
    */

   /**
    * Returns an Enumerator for the attribute names specified in the URL.
    */
   public Enumeration getAttributes() {
      return null;
   }

   /*
    * 4.27.6 getDN
    */

   /**
    * Returns the distinguished name encapsulated in the URL.
    */
   public String getDN() {
      return null;
   }

   /*
    * 4.27.7 getFilter
    */

   /**
    * Returns the search filter [7], or the default filter -
    * (objectclass=*) - if none was specified.
    */
   public String getFilter() {
      return null;
   }

   /*
    * 4.27.8 getHost
    */

   /**
    * Returns the host name of the LDAP server to connect to.
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

/* **************************************************************************
 * $Novell: LDAPDN.java,v 1.2 2000/03/14 18:17:26 smerrill Exp $
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
 
package org.ietf.ldap;
 
/**
 * 4.9 public class LDAPDN
 *
 *  A utility class representing a distinguished name (DN).
 */
public class LDAPDN {

   /*
    * 4.9.1 equals
    */

   /**
    * Returns true if the two strings correspond to the same DN, after
    * appropriate normalization.
    *
    * Parameters are:
    *
    *  dn1            String form of first DN to compare.
    *
    *  dn2            String form of second DN to compare.
    */
   public static boolean equals (String dn1, String dn2) {
      return false;
   }

   /*
    * 4.9.2 escapeRDN
    */

   /**
    * Returns the RDN after escaping the characters requiring escaping [6].
    * For example, for the rdn ôcn=Acme, Incö, ôcn=Acme\, Incö is returned.
    *
    * Parameters are:
    *
    *  rdn            The RDN to escape.
    */
   public static String escapeRDN (String rdn) {
      return null;
   }

   /*
    * 4.9.3 unescapeRDN
    */

   /**
    * Returns the RDN after unescaping the characters requiring escaping
    * [6]. For example, for the rdn ôcn=Acme\, Incö, ôcn=Acme, Incö is
    * returned.
    *
    * Parameters are:
    *
    *  rdn            The RDN to unescape.
    */
   public static String unescapeRDN (String rdn) {
      return null;
   }

   /*
    * 4.9.4 explodeDN
    */

   /**
    * Returns the individual components of a distinguished name (DN).
    *
    * Parameters are:
    *
    *  dn             Distinguished name, e.g. "cn=Babs
    *                  Jensen,ou=Accounting,o=Acme,c=us"
    *
    *  noTypes        If true, returns only the values of the
    *                  components, and not the names, e.g. "Babs
    *                  Jensen", "Accounting", "Acme", "us" - instead of
    *                  "cn=Babs Jensen", "ou=Accounting", "o=Acme", and
    *                  "c=us".
    */
   public static String[] explodeDN(String dn, boolean noTypes) {
      return null;
   }

   /*
    * 4.9.5 explodeRDN
    */

   /**
    * Returns the individual components of a relative distinguished name
    * (RDN).
    *
    * Parameters are:
    *
    *  rdn            Relative distinguished name, i.e. the left-most
    *                  component of a distinguished name.
    *
    *  noTypes        If true, returns only the values of the
    *                  components, and not the names.
    */
   public static String[] explodeRDN(String rdn, boolean noTypes) {
      return null;
   }

}

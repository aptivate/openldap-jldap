/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPDN.java,v 1.7 2000/10/09 19:11:24 vtag Exp $
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
 
/*
 * 4.11 public class LDAPDN
 */
 
/**
 *  A utility class representing a distinguished name (DN).
 *
 *  Specifies methods for manipulating a distinguished name (DN)
 *  and a relative distinguished name (RDN).
 */
public class LDAPDN {

   /*
    * 4.11.1 equals
    */

   /**
    * Normalizes the names (if appropriate) and then determines whether the 
    * two distinguished names are the same.
    *
    *  @param dn1            String form of the first DN to compare.
    *<br><br>
    *  @param dn2            String form of the second DN to compare.
    *
    * @return Returns true if the two strings correspond to the same DN; false
    *         if the DNs are different. 
    */
   public static boolean equals (String dn1, String dn2) {
      throw new RuntimeException("Method LDAPDN.equals not implemented");
   }

   /*
    * 4.11.2 escapeRDN
    */

   /**
    * Returns the RDN after escaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "ôcn=Acme, Incö", the escapeRDN method 
    * returns "ôcn=Acme\, Incö".</p>
    *
    *  @param rdn            The RDN to escape.
    *
    *@return The RDN with escaping characters.
    */
   public static String escapeRDN (String rdn) {
      throw new RuntimeException("Method LDAPDN.escapeRDN not implemented");
   }

   /*
    * 4.11.3 unescapeRDN
    */

   /**
    * Returns the RDN after unescaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "ôcn=Acme\, Incö", the unescapeRDN method 
    * returns "ôcn=Acme, Incö".</p>
    *
    *  @param rdn            The RDN to unescape.
    *
    * @return The RDN with the escaping characters removed.
    */
   public static String unescapeRDN (String rdn) {
      throw new RuntimeException("Method LDAPDN.unescapeRDN not implemented");
   }

   /*
    * 4.11.4 explodeDN
    */

   /**
    * Returns the individual components of a distinguished name (DN).
    *
    * @param dn        The distinguished name, for example, "cn=Babs
    *                  Jensen,ou=Accounting,o=Acme,c=US"
    *<br><br>
    * @param noTypes   If true, returns only the values of the
    *                  components, and not the names, for example, "Babs
    *                  Jensen", "Accounting", "Acme", "US" instead of
    *                  "cn=Babs Jensen", "ou=Accounting", "o=Acme", and
    *                  "c=US".
    *
    * @return An array of strings representing the individual components 
    * of a DN, or null if the DN is not a valid DN.
    */
   public static String[] explodeDN(String dn, boolean noTypes) {
      throw new RuntimeException("Method LDAPDN.explodeDN not implemented");
   }

   /*
    * 4.11.5 explodeRDN
    */

   /**
    * Returns the individual components of a relative distinguished name
    * (RDN).
    *
    *  @param rdn     The relative distinguished name, or in other words,
    *                 the left-most component of a distinguished name.
    *<br><br>
    *  @param noTypes   If true, returns only the values of the
    *                  components, and not the names of the component, for
    *                  example "Babs Jensen" instead of "cn=Babs Jensen".
    *
    * @return An array of strings representing the individual components 
    * of an RDN, or null if the RDN is not a valid RDN.

    */
   public static String[] explodeRDN(String rdn, boolean noTypes) {
      throw new RuntimeException("Method LDAPRDN.explodeRDN not implemented");
   }

}

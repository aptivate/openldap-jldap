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

/**
 *  A utility class to facilitate composition and deomposition
 *  of distinguished names (DNs).
 *
 * @see com.novell.ldap.LDAPDN
 */
public class LDAPDN
{

   /**
    * Compares the two strings per the distinguishedNameMatch equality matching
    * (using case-ignore matching).
    *
    * @see com.novell.ldap.LDAPDN#equals(String, String)
    */

    public static boolean equals (String dn1, String dn2)
    {
        return com.novell.ldap.LDAPDN.equals(dn1, dn2);
    }

    /**
     * Returns the RDN after escaping the characters requiring escaping.
     *
     * @see com.novell.ldap.LDAPDN#escapeRDN(String)
     */
    public static String escapeRDN (String rdn)
    {
        return com.novell.ldap.LDAPDN.escapeRDN(rdn);
    }

    /**
     * Returns the individual components of a distinguished name (DN).
     *
     * @see com.novell.ldap.LDAPDN#explodeDN(String,boolean)
     */
    public static String[] explodeDN(String dn, boolean noTypes)
    {
        return com.novell.ldap.LDAPDN.explodeDN(dn,noTypes);
    }

    /**
     * Returns the individual components of a relative distinguished name
     *
     * @see com.novell.ldap.LDAPDN#explodeRDN(String,boolean)
     */
    public static String[] explodeRDN(String rdn, boolean noTypes) {
        return com.novell.ldap.LDAPDN.explodeRDN(rdn,noTypes);
    }

    /**
     * Returns true if the string conforms to distinguished name syntax.
     *
     * @see com.novell.ldap.LDAPDN#isValid(String)
     */
    public static boolean isValid(String dn){
        return com.novell.ldap.LDAPDN.isValid(dn);
    }

    /**
     * Returns the DN normalized by removal of non-significant space characters.
     *
     * @see com.novell.ldap.LDAPDN#normalize(String)
     */
    public static String normalize(String dn){
        return com.novell.ldap.LDAPDN.normalize(dn);
    }


    /**
     * Returns the RDN after unescaping the characters requiring escaping.
     *
     * @see com.novell.ldap.LDAPDN#unescapeRDN(String)
     */
    public static String unescapeRDN (String rdn) {
        return com.novell.ldap.LDAPDN.unescapeRDN(rdn);
    }
}

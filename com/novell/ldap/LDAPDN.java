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

package com.novell.ldap;
import com.novell.ldap.util.DN;
import com.novell.ldap.util.RDN;

/**
 *  A utility class to facilitate composition and deomposition
 *  of distinguished names (DNs).
 *
 *  <p>Specifies methods for manipulating a distinguished name (DN)
 *  and a relative distinguished name (RDN).</p>
 */
public class LDAPDN {
   /**
    * Compares the two strings per the distinguishedNameMatch equality matching
    * (using case-ignore matching).  IllegalArgumentException is thrown if one
    * or both DNs are invalid.  UnsupportedOpersationException is thrown if the
    * API implementation is not able to detemine if the DNs match or not.
    *
    *  @param dn1            String form of the first DN to compare.
    *<br><br>
    *  @param dn2            String form of the second DN to compare.
    *
    * @return Returns true if the two strings correspond to the same DN; false
    *         if the DNs are different.
    */

    public static boolean equals (String dn1, String dn2) {
        DN dnA = new DN(dn1);
        DN dnB = new DN(dn2);
        return dnA.equals(dnB);
    }

   /**
    * Returns the RDN after escaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "cn=Acme, Inc", the escapeRDN method
    * returns "cn=Acme\, Inc".</p>
    *
    * <p>escapeRDN escapes the AttributeValue by inserting '\' before the
    * following chars: * ',' '+' '"' '\' '<' '>' ';' <BR>
    * '#' if it comes at the beginning of the string, and <BR>
    * ' ' (space) if it comes at the beginning or the end of a string.
    * Note that single-valued attributes can be used because of ambiguity. See
    * RFC 2253 </p>
    *
    *  @param rdn            The RDN to escape.
    *
    *@return The RDN with escaping characters.
    */
   public static String escapeRDN (String rdn) {
      StringBuffer escapedS = new StringBuffer(rdn);
      int i = 0;

      while (i < escapedS.length() && escapedS.charAt(i) != '='){
         i++;  //advance until we find the separator =
      }
      if ( i == escapedS.length()){
         throw new IllegalArgumentException("Could not parse RDN: Attribute " +
            "type and name must be separated by an equal symbol, '='");
      }

      i++;
        //check for a space or # at the beginning of a string.
      if ((escapedS.charAt(i) == ' ') || (escapedS.charAt(i) == '#')){
         escapedS.insert(i++, '\\');
      }

        //loop from second char to the second to last
      for ( ; i < escapedS.length(); i++){
         if((escapedS.charAt(i) == ',') || (escapedS.charAt(i) == '+') ||
            (escapedS.charAt(i) == '"') || (escapedS.charAt(i) == '\\') ||
            (escapedS.charAt(i) == '<') || (escapedS.charAt(i) == '>') ||
            (escapedS.charAt(i) == ';')) {
                escapedS.insert( i++,'\\');
         }
      }

        //check last char for a space
      if (escapedS.charAt(escapedS.length()-1) == ' ') {
         escapedS.insert(escapedS.length()-1, '\\');
      }
      return escapedS.toString();
   }



   /**
    * Returns the individual components of a distinguished name (DN).
    *
    * @param dn        The distinguished name, for example, "cn=Babs
    *                  Jensen,ou=Accounting,o=Acme,c=US"
    *<br><br>
    * @param noTypes   If true, returns only the values of the
    *                  components and not the names.  For example, "Babs
    *                  Jensen", "Accounting", "Acme", "US" instead of
    *                  "cn=Babs Jensen", "ou=Accounting", "o=Acme", and
    *                  "c=US".
    *
    * @return An array of strings representing the individual components
    * of a DN, or null if the DN is not valid.
    */
    public static String[] explodeDN(String dn, boolean noTypes) {
        DN dnToExplode = new DN(dn);
        return dnToExplode.explodeDN(noTypes);
    }

    /**
     * Returns the individual components of a relative distinguished name
     * (RDN), normalized.
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
        RDN rdnToExplode = new RDN(rdn);
        return rdnToExplode.explodeRDN(noTypes);
    }

    /**
     * Returns true if the string conforms to distinguished name syntax.
     * @param dn    String to evaluate fo distinguished name syntax.
     * @return      true if the dn is valid.
     */
    public static boolean isValid(String dn){
        try {
            new DN(dn);
        } catch (IllegalArgumentException iae){
            return false;
        }
        return true;
    }

    /**
     * Returns the DN normalized by removal of non-significant space characters
     * as per RFC 2253, section4.
     *
     * @return      a normalized string
     */
    public static String normalize(String dn){
        DN testDN = new DN(dn);
        return testDN.toString();
    }


    /**
     * Returns the RDN after unescaping the characters requiring escaping.
     *
     * <p>For example, for the rdn "cn=Acme\, Inc", the unescapeRDN method
     * returns "cn=Acme, Inc".</p>
     * <p><b>Note:</b>This function doesn't check for the
     * validity of the RDN string use isValid(String) function to check the 
     * validity. The results is undefined for invalid RDN's.
     * </p>
     * unescapeRDN unescapes the AttributeValue by
     * removing the '\' when the next character fits the following:<BR>
     * ',' '+' '"' '\' '<' '>' ';'<BR>
     * '#' if it comes at the beginning of the Attribute Name
     * (without the '\').<BR>
     * ' ' (space) if it comes at the beginning or the end of the Attribute Name
     * </p>
     *  @param rdn            The RDN to unescape.
     *
     * @return The RDN with the escaping characters removed.
     * @see LDAPDN#isValid(String)
     */
    public static String unescapeRDN (String rdn) {
        StringBuffer unescaped = new StringBuffer();
        int i = 0;

        while (i < rdn.length() && rdn.charAt(i) != '='){
			unescaped.append(rdn.charAt(i));
            i++;  //advance until we find the separator =
        }
        //add character '='
		unescaped.append(rdn.charAt(i));
        if ( i == rdn.length()){
            throw new IllegalArgumentException("Could not parse rdn: Attribute "
                + "type and name must be separated by an equal symbol, '='");
        }
        i++;
            //check if the first two chars are "\ " (slash space) or "\#"
        if (( rdn.charAt(i) == '\\') && (i+1 < rdn.length()-1) //bounds checking
        && ((rdn.charAt(i+1) == ' ') || (rdn.charAt(i+1) == '#'))){
             i++;
        }
        for ( ; i < rdn.length(); i++){
            //if the current char is a slash, not the end char, and is followed
            // by a special char then...
            if ((rdn.charAt(i)== '\\') && (i != rdn.length()-1)){
                if((rdn.charAt(i+1) == ',') || (rdn.charAt(i+1) == '+') ||
                   (rdn.charAt(i+1) == '"') || (rdn.charAt(i+1) == '\\') ||
                   (rdn.charAt(i+1) == '<') || (rdn.charAt(i+1) == '>') ||
                   (rdn.charAt(i+1) == ';'))
                {   //I'm not sure if I have to check for these special chars
					unescaped.append(rdn.charAt(i+1));
                	i++;
                    continue;
                }
                //check if the last two chars are "\ "
                else if ((rdn.charAt(i+1) == ' ') && (i+2 == rdn.length())){
                    //if the last char is a space
                    continue;
                }
            }
            unescaped.append(rdn.charAt(i));
        }
        return unescaped.toString();
    }


}//end class LDAPDN

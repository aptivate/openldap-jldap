/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPDN.java,v 1.10 2000/11/02 20:23:48 cmorris Exp $
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
import java.lang.RuntimeException;                 

/**
 *  A utility class to facilitate composition and deomposition
 *  of distinguished names (DNs).
 *
 *  <p>Specifies methods for manipulating a distinguished name (DN)
 *  and a relative distinguished name (RDN).</p>
 */
public class LDAPDN {

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
      //throw new RuntimeException("Method LDAPDN.equals not implemented");
      if (dn1.equals(dn2)){ //short-cut, otherwise look up OID's
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Returns the RDN after escaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "ôcn=Acme, Incö", the escapeRDN method
    * returns "ôcn=Acme\, Incö".</p>
    *
    *  @param rdn            The RDN to escape.
    *
    *@return The RDN with escaping characters.
    *
    * Comments:
    * RDN syntax: AttributeType = AttributeValue
    * escapeRDN Escapes the AttributeValue by inserting '\' before the following chars:
    * ',' '+' '"' '\' '<' '>' ';'
    * '#' if it comes at the beginning of the string
    * ' ' (space) if it comes at the beginning or the end of a string.
    *
    * Note: Current grammer does not allow multivalued RDN's for this method.  See RFC 2253 2.4
    */
   public static String escapeRDN (String rdn) {
      //throw new RuntimeException("Method LDAPDN.escapeRDN not implemented");
      StringBuffer escapedS = new StringBuffer(rdn);
      int i = 0;
      
      while (i < escapedS.length() && escapedS.charAt(i) != '='){
         i++;  //advance until we find the separator =
      }
      if ( i == escapedS.length()){
         throw new RuntimeException("Attribute type and name must be separated by an equal symbol, '='");
      }
      i++;
      //check for a space or # at the beginning of a string.
      if ((escapedS.charAt(i) == ' ') || (escapedS.charAt(i) == '#')){
         escapedS.insert(i++, '\\');
      }
      
      for ( ; i < escapedS.length(); i++){ //loop from second char to the second to last
         char test = escapedS.charAt(i);
         if((escapedS.charAt(i) == ',') || (escapedS.charAt(i) == '+') || (escapedS.charAt(i) == '"') || (escapedS.charAt(i) == '\\')
         || (escapedS.charAt(i) == '<') || (escapedS.charAt(i) == '>') || (escapedS.charAt(i) == ';')) {
            escapedS.insert( i++,'\\');
         }
      }
      if (escapedS.charAt(escapedS.length()-1) == ' ') {//check last char for a space
         escapedS.insert(escapedS.length()-1, '\\');
      }
      return escapedS.toString();
   }

   /**
    * Returns the RDN after unescaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "ôcn=Acme\, Incö", the unescapeRDN method
    * returns "ôcn=Acme, Incö".</p>
    *
    *  @param rdn            The RDN to unescape.
    *
    * @return The RDN with the escaping characters removed.
    *
    * Comments:
    * RDN syntax: AttributeType = AttributeValue
    * unescapeRDN unescapes the AttributeValue by
    * removing the '\' when the next character fits the following:
    * ',' '+' '"' '\' '<' '>' ';'
    * '#' if it comes at the beginning of the string (without the '\').
    * ' ' (space) if it comes at the beginning or the end of a string (without the '\').
    */
   public static String unescapeRDN (String rdn) {
      //throw new RuntimeException("Method LDAPDN.unescapeRDN not implemented");
      StringBuffer unescaped = new StringBuffer();
      int i = 0;

      while (i < rdn.length() && rdn.charAt(i) != '='){
         i++;  //advance until we find the separator =
      }   
      if ( i == rdn.length()){
         throw new RuntimeException("Attribute type and name must be separated by an equal symbol, '='");
      }
      i++;
      //check if the first two chars are "\ " (slash space) or "\#"
      if (( rdn.charAt(i) == '\\') && (i+1 < rdn.length()-1) //is there a next char?
      && ((rdn.charAt(i+1) == ' ') || (rdn.charAt(i+1) == '#'))){
         i++;
      }
      for ( ; i < rdn.length(); i++){
         //if the current char is a slash, not the end char, and is followed by a special char then...
         if ((rdn.charAt(i)== '\\') && (i != rdn.length()-1)){
            if((rdn.charAt(i+1) == ',') || (rdn.charAt(i+1) == '+') || (rdn.charAt(i+1) == '"') || (rdn.charAt(i+1) == '\\') ||
               (rdn.charAt(i+1) == '<') || (rdn.charAt(i+1) == '>') || (rdn.charAt(i+1) == ';'))
            {  //I'm not sure if I have to check for these special chars
               continue;
            } 
            //check if the last two chars are "\ " 
            else if ((rdn.charAt(i+1) == ' ') && (i+2 == rdn.length())){//if the last char is a space
               continue;
            }
         }
         unescaped.append(rdn.charAt(i));
      }
      return unescaped.toString();
   }

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

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

package com.novell.ldap.rfc2251;

import java.util.StringTokenizer;

import com.novell.ldap.asn1.*;
import com.novell.ldap.LDAPException;
import com.novell.ldap.resources.*;

/* 
 *       Filter ::= CHOICE {
 *               and             [0] SET OF Filter,
 *               or              [1] SET OF Filter,
 *               not             [2] Filter,
 *               equalityMatch   [3] AttributeValueAssertion,
 *               substrings      [4] SubstringFilter,
 *               greaterOrEqual  [5] AttributeValueAssertion,
 *               lessOrEqual     [6] AttributeValueAssertion,
 *               present         [7] AttributeDescription,
 *               approxMatch     [8] AttributeValueAssertion,
 *               extensibleMatch [9] MatchingRuleAssertion }
 */
public class RfcFilter extends ASN1Choice {

   //*************************************************************************
   // Public variables for Filter
   //*************************************************************************

   /**
    * Context-specific TAG for AND component.
    */
   public final static int AND = 0;
   /**
    * Context-specific TAG for OR component.
    */
   public final static int OR = 1;
   /**
    * Context-specific TAG for NOT component.
    */
   public final static int NOT = 2;
   /**
    * Context-specific TAG for EQUALITY_MATCH component.
    */
   public final static int EQUALITY_MATCH = 3;
   /**
    * Context-specific TAG for SUBSTRINGS component.
    */
   public final static int SUBSTRINGS = 4;
   /**
    * Context-specific TAG for GREATER_OR_EQUAL component.
    */
   public final static int GREATER_OR_EQUAL = 5;
   /**
    * Context-specific TAG for LESS_OR_EQUAL component.
    */
   public final static int LESS_OR_EQUAL = 6;
   /**
    * Context-specific TAG for PRESENT component.
    */
   public final static int PRESENT = 7;
   /**
    * Context-specific TAG for APPROX_MATCH component.
    */
   public final static int APPROX_MATCH = 8;
   /**
    * Context-specific TAG for EXTENSIBLE_MATCH component.
    */
   public final static int EXTENSIBLE_MATCH = 9;

   /**
    * Context-specific TAG for INITIAL component.
    */
   public final static int INITIAL = 0;
   /**
    * Context-specific TAG for ANY component.
    */
   public final static int ANY = 1;
   /**
    * Context-specific TAG for FINAL component.
    */
   public final static int FINAL = 2;

   //*************************************************************************
   // Private variables for Filter
   //*************************************************************************

   private FilterTokenizer ft;

   //*************************************************************************
   // Constructor for Filter
   //*************************************************************************

   /**
    * Constructs a Filter object by parsing an RFC 2254 Search Filter String.
    */
   public RfcFilter(String filter)
      throws LDAPException
   {
      setContent(parse(filter));
   }

   //*************************************************************************
   // Helper methods for RFC 2254 Search Filter parsing.
   //*************************************************************************

   /**
    * Parses an RFC 2251 filter string into an ASN.1 LDAP Filter object.
    */
   private ASN1Tagged parse(String filterExpr)
      throws LDAPException
   {
      if(filterExpr == null || filterExpr.equals("")) {
		 filterExpr = new String("(objectclass=*)");
      }

      if(filterExpr.charAt(0) != '(')
        filterExpr = "(" + filterExpr + ")";

      ft = new FilterTokenizer(filterExpr);

      return parseFilter();
   }

   /**
    * Will parse an RFC 2254 filter
    */
   private ASN1Tagged parseFilter()
      throws LDAPException
   {
      ft.getLeftParen();

      ASN1Tagged filter = parseFilterComp();

      ft.getRightParen();

      return filter;
   }

   /**
    * RFC 2254 filter helper method. Will Parse a filter component.
    */
   private ASN1Tagged parseFilterComp()
      throws LDAPException
   {
      ASN1Tagged tag = null;
      int filterComp = ft.getOpOrAttr();

      switch(filterComp) {
         case AND:
         case OR:
            tag = new ASN1Tagged(
               new ASN1Identifier(ASN1Identifier.CONTEXT, true, filterComp),
               parseFilterList(),
               false);
            break;
         case NOT:
            tag = new ASN1Tagged(
               new ASN1Identifier(ASN1Identifier.CONTEXT, true, filterComp),
               parseFilter(),
               true);
            break;
         default:
            int filterType = ft.getFilterType();
            String value = ft.getValue();

            switch(filterType) {
               case GREATER_OR_EQUAL:
               case LESS_OR_EQUAL:
               case APPROX_MATCH:
                  tag = new ASN1Tagged(
                     new ASN1Identifier(ASN1Identifier.CONTEXT, true,
                                        filterType),
                     new RfcAttributeValueAssertion(
                        new RfcAttributeDescription(ft.getAttr()),
                        new RfcAssertionValue(unescapeString(value))),
                     false);
                  break;
               case EQUALITY_MATCH: // may be PRESENT or SUBSTRINGS also
                  if(value.equals("*")) { // present
                     tag = new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, false,
                                           PRESENT),
                        new RfcAttributeDescription(ft.getAttr()),
                        false);
                  }
                  else if(value.indexOf('*') != -1) { // substrings
                     // parse: [initial], *any*, [final] into an
                     // ASN1SequenceOf
                     StringTokenizer sub =
                        new StringTokenizer(value, "*", true);
                     ASN1SequenceOf seq = new ASN1SequenceOf(5);
                     int tokCnt = sub.countTokens();
                     int cnt = 0;

                     while(sub.hasMoreTokens()) {
                        String subTok = sub.nextToken();
                        cnt++;
                        if(subTok.equals("*")) { // delimiter
                        }
                        else { // value (RfcLDAPString)
                           if(cnt == 1) { // initial
                              seq.add(
                                 new ASN1Tagged(
                                    new ASN1Identifier(ASN1Identifier.CONTEXT,
                                                       false, INITIAL),
                                    new RfcLDAPString(unescapeString(subTok)),
                                    false));
                           }
                           else if(cnt < tokCnt) { // any
                              seq.add(
                                 new ASN1Tagged(
                                    new ASN1Identifier(ASN1Identifier.CONTEXT,
                                                       false, ANY),
                                    new RfcLDAPString(unescapeString(subTok)),
                                    false));
                           }
                           else { // final
                              seq.add(
                                 new ASN1Tagged(
                                    new ASN1Identifier(ASN1Identifier.CONTEXT,
                                                       false, FINAL),
                                    new RfcLDAPString(unescapeString(subTok)),
                                    false));
                           }
                        }
                     }

                     tag = new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, true,
                                           SUBSTRINGS),
                        new RfcSubstringFilter(
                           new RfcAttributeDescription(ft.getAttr()),
                           seq),
                        false);
                  }
                  else { // simple
                     tag = new ASN1Tagged(
                        new ASN1Identifier(ASN1Identifier.CONTEXT, true,
                                           EQUALITY_MATCH),
                        new RfcAttributeValueAssertion(
                           new RfcAttributeDescription(ft.getAttr()),
                           new RfcAssertionValue(unescapeString(value))),
                        false);
                  }
                  break;
               case EXTENSIBLE_MATCH:
                  String type = null, matchingRule = null;
                  boolean dnAttributes = false;
                  StringTokenizer st =
                     new StringTokenizer(ft.getAttr(), ":", true);

                  boolean first = true;
                  while(st.hasMoreTokens()) {
                     String s = st.nextToken().trim();
                     if(first && !s.equals(":")) {
                        type = s;
                     }
                     else if(s.equalsIgnoreCase("dn")) {
                        dnAttributes = true;
                     }
                     else if(!s.equals(":")) {
                        matchingRule = s;
                     }
                     first = false;
                  }

                  tag = new ASN1Tagged(
                     new ASN1Identifier(ASN1Identifier.CONTEXT, true,
                                        EXTENSIBLE_MATCH),
                     new RfcMatchingRuleAssertion(
                        (matchingRule == null) ? null :
                            new RfcMatchingRuleId(matchingRule),
                        (type == null) ? null :
                            new RfcAttributeDescription(type),
                        new RfcAssertionValue(unescapeString(value)),
                        (dnAttributes == false) ? null :
                            new ASN1Boolean(true)),
                     false);
            }
      }
      return tag;

   }

   /**
    * Must have 1 or more Filters
    */
   private ASN1SetOf parseFilterList()
      throws LDAPException
   {
      ASN1SetOf set = new ASN1SetOf();

      set.add(parseFilter()); // must have at least 1 filter

      while(ft.peekChar() == '(') { // check for more filters
         set.add(parseFilter());
      }

      return set;
   }

   /**
    * Convert hex character to an integer. Return -1 if char is something
    * other than a hex char.
    */
   private int hex2int(char c)
   {
      return
         (c >= '0' && c <= '9') ? c - '0'      :
         (c >= 'A' && c <= 'F') ? c - 'A' + 10 :
         (c >= 'a' && c <= 'f') ? c - 'a' + 10 :
            -1;
   }

   /**
    * Replace escaped hex digits with the equivalent binary representation.
    * Assume either V2 or V3 escape mechanisms:
    * V2: \*,  \(,  \),  \\.
    * V3: \2A, \28, \29, \5C, \00.
    *
    * @param string    A part of the input filter string to be converted.
    *
    * @return octet-string encoding of the specified string.
    */
   private byte[] unescapeString(String string)
      throws LDAPException
   {
      byte octets[] = new byte[string.length()];
      // index for string and octets
      int iString, iOctets;
      // escape==true means we are in an escape sequence.
      boolean escape = false;
      // escStart==true means we are reading the first character of an escape.
      boolean escStart = false;

      int ival, length = string.length();
      char ch, temp = 0;

      /* loop through each character of the string and copy them into octets
         converting escaped sequences when needed */
      for(iString = 0, iOctets = 0; iString < length; iString++) {
         ch = string.charAt(iString);
         if(escape) {
            if((ival = hex2int(ch)) < 0) {
               // V2 escaped "*()" chars differently: \*, \(, \)
               if(escStart) {

                  escStart = escape = false;
                  octets[iOctets++] = (byte) ch;
               }
               else { //"Invalid escape value",
                  throw new LDAPException(ExceptionMessages.INVALID_ESCAPE,
                                          LDAPException.FILTER_ERROR);
               }
            }
            else {
               //V3 escaped: \\**
               if(escStart) {
                  temp = (char)(ival<<4);
                  escStart = false;
               }
               else {
                  temp |= (char)(ival);
                  octets[iOctets++] = (byte) temp;
                  escStart = escape = false;
               }
            }
         }
         else if(ch == '\\') {
             escStart = escape = true;
         }
         else { //place the character into octets.
             byte b = (byte) ch;
             if (( b >= 0x01 && b <= 0x27 ) ||
                 ( b >= 0x2B && b <= 0x5B ) ||
                 ( b >= 0x5D && b <= 0x7F ))
             {
                 //found valid character = %x01-27 / %x2b-5b / %x5d-7f
                 octets[iOctets++] = (byte)ch;
                 escape = false;
             }
             else{
                 //found invalid character
                 throw new com.novell.ldap.LDAPLocalException(
                         ExceptionMessages.INVALID_CHAR_IN_FILTER,
                         LDAPException.FILTER_ERROR);
             }

         }
      }

      //Verify that any escape sequence completed
      if (escStart || escape){
          throw new LDAPException(ExceptionMessages.INVALID_ESCAPE,
                                  LDAPException.FILTER_ERROR);
      }

      byte toReturn[] = new byte[iOctets];
      System.arraycopy(octets, 0, toReturn, 0, iOctets);
      octets = null;
      return toReturn;
   }

}

/**
 * This class will tokenize the components of an RFC 2254 search filter.
 */
class FilterTokenizer {

   //*************************************************************************
   // Private variables
   //*************************************************************************

   private String filter; // The filter string to parse
   private String attr;   // Name of the attribute just parsed
   private int i;         // Offset pointer into the filter string
   private int len;       // Length of the filter string to parse

   //*************************************************************************
   // Constructor
   //*************************************************************************

   /**
    * Constructs a FilterTokenizer for a filter.
    */
   public FilterTokenizer(String filter) {
      this.filter = filter;
      this.i = 0;
      this.len = filter.length();
   }

   //*************************************************************************
   // Tokenizer methods
   //*************************************************************************

   /**
    * Reads the current char and throws an Exception if it is not a left
    * parenthesis.
    */
   public void getLeftParen()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      if(filter.charAt(i++) != '(')
         throw new LDAPException(ExceptionMessages.MISSING_LEFT_PAREN, //"Missing left paren",
                                 LDAPException.FILTER_ERROR);
   }

   /**
    * Reads the current char and throws an Exception if it is not a right
    * parenthesis.
    */
   public void getRightParen()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      if(filter.charAt(i++) != ')')
         throw new LDAPException(ExceptionMessages.MISSING_RIGHT_PAREN, //"Missing right paren",
                                 LDAPException.FILTER_ERROR);
   }

   /**
    * Reads either an operator, or an attribute, whichever is
    * next in the filter string.
    *
    * <p>Operators are &, |, or !.<p>
    *
    * <p>If the next component is an attribute, it is read and stored in the
    * attr field of this class which may be retrieved with getAttr()
    * and a -1 is returned. Otherwise, the int value of the operator read is
    * returned.</p>
    */
   public int getOpOrAttr()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      if(filter.charAt(i) == '&') {
         i++;
         return RfcFilter.AND;
      }
      if(filter.charAt(i) == '|') {
         i++;
         return RfcFilter.OR;
      }
      if(filter.charAt(i) == '!') {
         i++;
         return RfcFilter.NOT;
      }

      // get first component of 'item' (attr or :dn or :matchingrule)
      String delims = "=~<>()";
      StringBuffer sb = new StringBuffer();
      while(delims.indexOf(filter.charAt(i)) == -1 &&
            filter.startsWith(":=", i) == false) {
         sb.append(filter.charAt(i++));
      }

      attr = sb.toString().trim();
      return -1;
   }

   /**
    * Reads an RFC 2251 filter type from the filter string and returns its
    * int value.
    */
   public int getFilterType()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      if(filter.startsWith(">=", i)) {
         i+=2;
         return RfcFilter.GREATER_OR_EQUAL;
      }
      if(filter.startsWith("<=", i)) {
         i+=2;
         return RfcFilter.LESS_OR_EQUAL;
      }
      if(filter.startsWith("~=", i)) {
         i+=2;
         return RfcFilter.APPROX_MATCH;
      }
      if(filter.startsWith(":=", i)) {
         i+=2;
         return RfcFilter.EXTENSIBLE_MATCH;
      }
      if(filter.charAt(i) == '=') {
         i++;
         return RfcFilter.EQUALITY_MATCH;
      }
      throw new LDAPException(ExceptionMessages.INVALID_FILTER, //"Invalid filter type",
                              LDAPException.FILTER_ERROR);
   }

   /**
    * Reads a value from a filter string and returns it after trimming any
    * superfluous spaces from the beginning or end of the value.
    */
   public String getValue()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      StringBuffer sb = new StringBuffer();
      while(i < len && filter.charAt(i) != ')') {
         sb.append(filter.charAt(i++));
      }

      return sb.toString().trim();
   }

   /**
    * Returns the current attribute identifier.
    */
   public String getAttr()
   {
      return attr;
   }

   /**
    * Return the current char without advancing the offset pointer. This is
    * used by ParseFilterList when determining if there are any more
    * Filters in the list.
    */
   public char peekChar()
      throws LDAPException
   {
      if(i >= len)
         throw new LDAPException(ExceptionMessages.UNEXPECTED_END, //"Unexpected end of filter",
                                 LDAPException.FILTER_ERROR);

      return filter.charAt(i);
   }

}


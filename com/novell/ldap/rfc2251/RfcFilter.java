/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1.ldap;

import java.util.*;
import java.io.*;

import com.novell.asn1.*;
import org.ietf.ldap.LDAPException;

/**
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
public class Filter extends ASN1Choice {

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

	private StringTokenizer st;

	//*************************************************************************
	// Constructor for Filter
	//*************************************************************************

	/**
	 * Constructs a Filter object by parsing an RFC 2254 Search Filter String.
	 */
	public Filter(String filter)
		throws LDAPException
	{
		setContent(parse(filter));
	}

	//*************************************************************************
	// Helper methods for RFC 2254 Search Filter parsing.
	//*************************************************************************

	private ASN1Tagged parse(String filterExpr)
		throws LDAPException
	{
		if(filterExpr == null || filterExpr.equals("")) {
         throw new LDAPException("Invalid filter",
                                 LDAPException.FILTER_ERROR);
		}

		if(filterExpr.charAt(0) != '(')
		  filterExpr = "(" + filterExpr + ")";

		st = new StringTokenizer(filterExpr, "()=", true);

		return parseFilter();
	}

	/**
	 * 
	 */
   private ASN1Tagged parseFilter()
		throws LDAPException
   {
	   if(!st.nextToken().equals("(")) {
		   throw new LDAPException("Missing opening paren",
			                        LDAPException.FILTER_ERROR);
	   }

      ASN1Tagged filter = parseFilterComp();

		if(!st.nextToken().equals(")")) {
			throw new LDAPException("Missing closing paren",
											LDAPException.FILTER_ERROR);
		}

		return filter;
   }

   /**
    * RFC 2254 filter helper method. Will Parse a filter component.
    */
   private ASN1Tagged parseFilterComp()
		throws LDAPException
   {
		String tok = st.nextToken().trim(); // get operator or attr name

		if(tok.equals("&")) {
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, AND),
				parseFilterList(),
				false);
		}
		else if(tok.equals("|")) {
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, OR),
				parseFilterList(),
				false);
		}
		else if(tok.equals("!")) {
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, NOT),
				parseFilter(),
				false); // maybe change this to true per RFC 2251 4.5.1
		}
		else {
			// get item (may be: simple / present / substring / extensible)
			String filtertype = st.nextToken("><~=()").trim(); // get rel op

			if(filtertype.equals(">")) {
				if(!st.nextToken().equals("=")) {
					throw new LDAPException("Invalid operator",
													LDAPException.FILTER_ERROR);
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 GREATER_OR_EQUAL),
					new AttributeValueAssertion(
						new AttributeDescription(tok),
						new AssertionValue(escaped2unicode(value))),
					false);
			}

			else if(filtertype.equals("<")) {
				if(!st.nextToken().equals("=")) {
					throw new LDAPException("Invalid operator",
													LDAPException.FILTER_ERROR);
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 LESS_OR_EQUAL),
					new AttributeValueAssertion(
						new AttributeDescription(tok),
						new AssertionValue(escaped2unicode(value))),
					false);
			}

			else if(filtertype.equals("~")) {
				if(!st.nextToken().equals("=")) {
					throw new LDAPException("Invalid operator",
													LDAPException.FILTER_ERROR);
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 APPROX_MATCH),
					new AttributeValueAssertion(
						new AttributeDescription(tok),
						new AssertionValue(escaped2unicode(value))),
					false);
			}

			else if(filtertype.equals("=")) {
				// look for: simple / present / substring
				String value = st.nextToken().trim();

				if(value.equals("*")) { // present
					return new ASN1Tagged(
						new ASN1Identifier(ASN1Identifier.CONTEXT, false, PRESENT),
						new AttributeDescription(tok),
						false);
				}
				else if(value.indexOf('*') != -1) { // substring
					// parse: [initial], any, [final] into an ASN1SequenceOf
					int begin=0, end;
					ASN1SequenceOf seq = new ASN1SequenceOf(3); // max 3 elements
					if(value.charAt(0) != '*') { // initial
						end = value.indexOf('*');
						seq.add(
							new ASN1Tagged(
								new ASN1Identifier(ASN1Identifier.CONTEXT, false,
									                INITIAL),
								new LDAPString(
									escaped2unicode(value.substring(begin, end))),
								false));

						begin = end;
					}

					end = value.lastIndexOf('*'); // any
					seq.add(
						new ASN1Tagged(
							new ASN1Identifier(ASN1Identifier.CONTEXT, false, ANY),
							new LDAPString(
								escaped2unicode(value.substring(begin, end))),
							false));

					if((value.length() - end) > 0) { // final
						seq.add(
							new ASN1Tagged(
								new ASN1Identifier(ASN1Identifier.CONTEXT, false,
									                FINAL),
								new LDAPString(
									escaped2unicode(value.substring(end))),
								false));
					}

					return new ASN1Tagged(
						new ASN1Identifier(ASN1Identifier.CONTEXT, true,
							                SUBSTRINGS),
						new SubstringFilter(
							new AttributeDescription(tok),
							seq),
						false);
				}
				else { // simple
					return new ASN1Tagged(
						new ASN1Identifier(ASN1Identifier.CONTEXT, true,
							                EQUALITY_MATCH),
						new AttributeValueAssertion(
						   new AttributeDescription(tok),
							new AssertionValue(escaped2unicode(value))),
						false);
				}
			}
			else
			{
				throw new LDAPException("Invalid operator",
												LDAPException.FILTER_ERROR);
			}

		}
   }

   /**
    * Helper method for parsing an rfc1960 filter. This method will parse
    * a list of components and distribute the operator for that list of
    * components between each component as it is placed in the NetFilter.
    */
   private ASN1SetOf parseFilterList()
		throws LDAPException
   {
		ASN1SetOf set = new ASN1SetOf();

		while(st.nextToken().trim().equals("("))
		{
			set.add(parseFilterComp());
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
	 * Replace escaped hex digits with the equivalent unicode representation.
	 * Assume either V2 or V3 escape mechanisms:
	 * V2: \*,  \(,  \),  \\.
	 * V3: \2A, \28, \29, \5C, \00.
	 */
   private String escaped2unicode(String value)
		throws LDAPException
   {
      StringBuffer sb = new StringBuffer();
      boolean escape = false, escStart = false;
      int ival;
      char ch, temp = 0;

      for(int i = 0; i < value.length(); i++) {
         ch = value.charAt(i);
         if(escape) {
            // Try LDAP V3 escape (\\xx)
            if((ival = hex2int(ch)) < 0) {
               if(escStart) {
                  // V2 escaped "*()" chars differently: \*, \(, \)
                  escape = false;
                  sb.append(ch);
               }
					else {
						throw new LDAPException("Invalid escape value",
														LDAPException.FILTER_ERROR);
               }
            }
				else {
               if(escStart) {
                  temp = (char)(ival<<4);
                  escStart = false;
               }
					else {
                  temp |= (char)(ival);
                  sb.append(temp);
                  escape = false;
               }
            }
         }
         else if(ch != '\\') {
            sb.append(ch);
            escape = false;
         }
			else {
            escStart = escape = true;
         }
      }

      return sb.toString();
   }

}


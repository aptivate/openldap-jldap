
package com.novell.asn1.ldap;

import java.util.*;
import java.io.*;

import com.novell.asn1.*;

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

	//*************************************************************************
	// Private variables for Filter
	//*************************************************************************

	private StringTokenizer st;

	//*************************************************************************
	// Constructors for Filter
	//*************************************************************************

	/**
	 *
	 */
	public Filter(ASN1Tagged choice)
	{
		super(choice);
	}

	/**
	 * Constructs a Filter object by parsing an RFC 2254 Search Filter String.
	 */
	public Filter(String filter)
	{
		setContent(parse(filter));
	}

	//*************************************************************************
	// Helper methods for RFC 2254 Search Filter parsing.
	//*************************************************************************

	private ASN1Tagged parse(String filterExpr)
	{
		if(filterExpr == null || filterExpr.equals("")) {
//			throw new LDAPException("Filter: invalid filter");
		}

		if(filterExpr.charAt(0) != '(')
		  filterExpr = "(" + filterExpr + ")";

		try {
			filterExpr = new String(filterExpr.getBytes("UTF8"));
		}
		catch(UnsupportedEncodingException uee) {
		}

		st = new StringTokenizer(filterExpr, "()=", true);

		return parseFilter();
	}

	/**
	 * 
	 */
   private ASN1Tagged parseFilter()
   {

	  if(!st.nextToken().equals("(")) {
//		 throw new InvalidSearchFilterException (); // missing '('
	  }

      ASN1Tagged filter = parseFilterComp();

		if(!st.nextToken().equals(")")) {
//		  throw new InvalidSearchFilterException (); // missing ')'
		}

		return filter;
   }

   /**
    * RFC 2254 filter helper method. Will Parse a filter component.
    */
   private ASN1Tagged parseFilterComp()
   {
		String tok = st.nextToken().trim(); // get operator or attr name

		if(tok.equals("&"))
		{
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, AND),
				parseFilterList(),
				false);
		}
		else if(tok.equals("|"))
		{
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, OR),
				parseFilterList(),
				false);
		}
		else if(tok.equals("!"))
		{
			return new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, true, NOT),
				parseFilter(),
				false);
		}
		else
		{
			// get item
			String filtertype = st.nextToken("><~=()").trim(); // get relational operator

			if(filtertype.equals(">"))
			{
				if(!st.nextToken().equals("=")) {
//					throw new InvalidSearchFilterException ();
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 GREATER_OR_EQUAL),
					 new AttributeValueAssertion(
						  new AttributeDescription(tok),
						  new AssertionValue(unescapeFilterValue(value))),
					false);
			}

			else if(filtertype.equals("<"))
			{
				if(!st.nextToken().equals("=")) {
//					throw new InvalidSearchFilterException ();
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 LESS_OR_EQUAL),
					 new AttributeValueAssertion(
						  new AttributeDescription(tok),
						  new AssertionValue(unescapeFilterValue(value))),
					false);
			}

			else if(filtertype.equals("~"))
			{
				if(!st.nextToken().equals("=")) {
//					throw new InvalidSearchFilterException ();
				}

				String value = st.nextToken().trim();

				return new ASN1Tagged(
					new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											 APPROX_MATCH),
					 new AttributeValueAssertion(
						  new AttributeDescription(tok),
						  new AssertionValue(unescapeFilterValue(value))),
					false);
			}

			else if(filtertype.equals("="))
			{
				String value = st.nextToken().trim();

				if(value.equals("*"))
				{
					return new ASN1Tagged(
						new ASN1Identifier(ASN1Identifier.CONTEXT, false,
												 PRESENT),
							  new AttributeDescription(tok), false);
				}
				else
				{
					return new ASN1Tagged(
						new ASN1Identifier(ASN1Identifier.CONTEXT, true,
												 EQUALITY_MATCH),
						 new AttributeValueAssertion(
							  new AttributeDescription(tok),
							  new AssertionValue(unescapeFilterValue(value))),
						false);
				}
			}
			else
			{
//				throw new InvalidSearchFilterException (); // invalid operator
				return null;
			}

		}
   }

   /**
    * Helper method for parsing an rfc1960 filter. This method will parse
    * a list of components and distribute the operator for that list of
    * components between each component as it is placed in the NetFilter.
    */
   private ASN1SetOf parseFilterList()
   {
		ASN1SetOf set = new ASN1SetOf();

		while(st.nextToken().trim().equals("("))
		{
			set.add(parseFilterComp());
		}

		return set;
   }

   /**
    * convert character 'c' that represents a hexadecimal digit to an integer.
    * if 'c' is not a hexidecimal digit [0-9A-Fa-f], -1 is returned.
    * otherwise the converted value is returned.
    */
   private static int hexchar2int( char c )
   {
      if ( c >= '0' && c <= '9' )
	  {
         return( c - '0' );
      }
      if ( c >= 'A' && c <= 'F' )
	  {
         return( c - 'A' + 10 );
      }
      if ( c >= 'a' && c <= 'f' )
	  {
         return( c - 'a' + 10 );
      }
      return( -1 );
   }

   /**
    * Replace escaped hex digits with the equivalent unicode representation.
	* Assume either V2 or V3 escape mechanisms:
	* V2: \*,  \(,  \),  \\.
	* V3: \2A, \28, \29, \5C, \00.
	*/
   private String unescapeFilterValue(String value)
   {
	  StringBuffer sb = new StringBuffer();
      boolean escape = false, escStart = false;
      int ival;
      char ch, temp = 0;

      for(int i = 0; i < value.length(); i++)
	  {
         ch = value.charAt(i);
         if(escape)
		 {
            // Try LDAP V3 escape (\\xx)
            if((ival = hexchar2int(ch)) < 0)
			{
               if(escStart)
			   {
                  // V2 escaped "*()" chars differently: \*, \(, \)
                  escape = false;
				  sb.append(ch);
               }
			   else
			   {
                  // escaping already started but we can't find 2nd hex
//                  throw new InvalidSearchFilterException("invalid escape sequence: " + value);
               }
            }
			else
			{
               if(escStart)
			   {
				  temp = (char)(ival<<4);
                  escStart = false;
               }
			   else
			   {
				  temp |= (char)(ival);
				  sb.append(temp);
                  escape = false;
               }
            }
         }
		 else if(ch != '\\')
		 {
			sb.append(ch);
            escape = false;
         }
		 else
		 {
            escStart = escape = true;
         }
      }

	  return sb.toString();
   }

}


/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

import java.io.IOException;
import java.util.Iterator;

import com.novell.ldap.asn1.ASN1Boolean;
import com.novell.ldap.asn1.ASN1Enumerated;
import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.rfc2251.RfcAttributeDescription;
import com.novell.ldap.rfc2251.RfcAttributeDescriptionList;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcFilter;
import com.novell.ldap.rfc2251.RfcLDAPDN;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcRequest;
import com.novell.ldap.rfc2251.RfcSearchRequest;

/**
 * Represents an LDAP Search request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       SearchRequest ::= [APPLICATION 3] SEQUENCE {
 *               baseObject      LDAPDN,
 *               scope           ENUMERATED {
 *                       baseObject              (0),
 *                       singleLevel             (1),
 *                       wholeSubtree            (2) },
 *               derefAliases    ENUMERATED {
 *                       neverDerefAliases       (0),
 *                       derefInSearching        (1),
 *                       derefFindingBaseObj     (2),
 *                       derefAlways             (3) },
 *               sizeLimit       INTEGER (0 .. maxInt),
 *               timeLimit       INTEGER (0 .. maxInt),
 *               typesOnly       BOOLEAN,
 *               filter          Filter,
 *               attributes      AttributeDescriptionList }
 */
public class LDAPSearchRequest extends LDAPMessage {
    //*************************************************************************
    // Public variables for Filter
    //*************************************************************************

    /**
     * Search Filter Identifier for an AND component.
     */
    public final static int AND = 0;
    /**
     * Search Filter Identifier for an OR component.
     */
    public final static int OR = 1;
    /**
     * Search Filter Identifier for a NOT component.
     */
    public final static int NOT = 2;
    /**
     * Search Filter Identifier for an EQUALITY_MATCH component.
     */
    public final static int EQUALITY_MATCH = 3;
    /**
     * Search Filter Identifier for a SUBSTRINGS component.
     */
    public final static int SUBSTRINGS = 4;
    /**
     * Search Filter Identifier for a GREATER_OR_EQUAL component.
     */
    public final static int GREATER_OR_EQUAL = 5;
    /**
     * Search Filter Identifier for a LESS_OR_EQUAL component.
     */
    public final static int LESS_OR_EQUAL = 6;
    /**
     * Search Filter Identifier for a PRESENT component.
     */
    public final static int PRESENT = 7;
    /**
     * Search Filter Identifier for an APPROX_MATCH component.
     */
    public final static int APPROX_MATCH = 8;
    /**
     * Search Filter Identifier for an EXTENSIBLE_MATCH component.
     */
    public final static int EXTENSIBLE_MATCH = 9;

    /**
     * Search Filter Identifier for an INITIAL component of a SUBSTRING.
     * Note: An initial SUBSTRING is represented as "<value>*".
     */
    public final static int INITIAL = 0;
    /**
     * Search Filter Identifier for an ANY component of a SUBSTRING.
     * Note: An ANY SUBSTRING is represented as "*<value>*".
     */
    public final static int ANY = 1;
    /**
     * Search Filter Identifier for a FINAL component of a SUBSTRING.
     * Note: A FINAL SUBSTRING is represented as "*<value>".
     */
    public final static int FINAL = 2;
    
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
    public LDAPSearchRequest(){
    	super(LDAPMessage.SEARCH_REQUEST);
    }
    
    /**
     * Constructs an LDAP Search Request.
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                          within its subtree
     *</ul><br><br>
     *  @param filter         The search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          The names of attributes to retrieve.
     *                  operation exceeds the time limit.
     *<br><br>
     *  @param dereference Specifies when aliases should be dereferenced.
     *                  Must be one of the constants defined in
     *                  LDAPConstraints, which are DEREF_NEVER,
     *                  DEREF_FINDING, DEREF_SEARCHING, or DEREF_ALWAYS.
     *<br><br>
     *  @param maxResults The maximum number of search results to return
     *                  for a search request.
     *                  The search operation will be terminated by the server
     *                  with an LDAPException.SIZE_LIMIT_EXCEEDED if the
     *                  number of results exceed the maximum.
     *<br><br>
     *  @param serverTimeLimit The maximum time in seconds that the server
     *                  should spend returning search results. This is a
     *                  server-enforced limit.  A value of 0 means
     *                  no time limit.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found.  If false, returns the
     *                        names and values for attributes found.
     *<br><br>
     * @param cont            Any controls that apply to the search request.
     *                        or null if none.
     *
     * @see com.novell.ldap.LDAPConnection#search
     * @see com.novell.ldap.LDAPSearchConstraints
     */
    public LDAPSearchRequest( String base,
                              int scope,
                              String filter,
                              String[] attrs,
                              int dereference,
                              int maxResults,
                              int serverTimeLimit,
                              boolean typesOnly,
                              LDAPControl[] cont)
        throws LDAPException
    {
        super(  LDAPMessage.SEARCH_REQUEST,
                new RfcSearchRequest (
                    new RfcLDAPDN(base),
                    new ASN1Enumerated(scope),
                    new ASN1Enumerated(dereference),
                    new ASN1Integer(maxResults),
                    new ASN1Integer(serverTimeLimit),
                    new ASN1Boolean(typesOnly),
                    new RfcFilter(filter),
                    new RfcAttributeDescriptionList(attrs)),
               cont);
        return;
    }

    /**
     * Constructs an LDAP Search Request with a filter in ASN1 format.
     *
     *  @param base           The base distinguished name to search from.
     *<br><br>
     *  @param scope          The scope of the entries to search. The following
     *                        are the valid options:
     *<ul>
     *   <li>SCOPE_BASE - searches only the base DN
     *
     *   <li>SCOPE_ONE - searches only entries under the base DN
     *
     *   <li>SCOPE_SUB - searches the base DN and all entries
     *                          within its subtree
     *</ul><br><br>
     *  @param filter         The search filter specifying the search criteria.
     *<br><br>
     *  @param attrs          The names of attributes to retrieve.
     *                  operation exceeds the time limit.
     *<br><br>
     *  @param dereference Specifies when aliases should be dereferenced.
     *                  Must be either one of the constants defined in
     *                  LDAPConstraints, which are DEREF_NEVER,
     *                  DEREF_FINDING, DEREF_SEARCHING, or DEREF_ALWAYS.
     *<br><br>
     *  @param maxResults The maximum number of search results to return
     *                  for a search request.
     *                  The search operation will be terminated by the server
     *                  with an LDAPException.SIZE_LIMIT_EXCEEDED if the
     *                  number of results exceed the maximum.
     *<br><br>
     *  @param serverTimeLimit The maximum time in seconds that the server
     *                  should spend returning search results. This is a
     *                  server-enforced limit.  A value of 0 means
     *                  no time limit.
     *<br><br>
     *  @param typesOnly      If true, returns the names but not the values of
     *                        the attributes found.  If false, returns the
     *                        names and values for attributes found.
     *<br><br>
     * @param cont            Any controls that apply to the search request.
     *                        or null if none.
     *
     * @see com.novell.ldap.LDAPConnection#search
     * @see com.novell.ldap.LDAPSearchConstraints
     */
    public LDAPSearchRequest(String base,
                             int scope,
                             RfcFilter filter,
                             String[] attrs,
                             int dereference,
                             int maxResults,
                             int serverTimeLimit,
                             boolean typesOnly,
                             LDAPControl[] cont)
    {
        super(  LDAPMessage.SEARCH_REQUEST,
                new RfcSearchRequest (
                    new RfcLDAPDN(base),
                    new ASN1Enumerated(scope),
                    new ASN1Enumerated(dereference),
                    new ASN1Integer(maxResults),
                    new ASN1Integer(serverTimeLimit),
                    new ASN1Boolean(typesOnly),
                    filter,
                    new RfcAttributeDescriptionList(attrs)),
                cont);
        return;
    }


    /**
     * Retrieves the Base DN for a search request.
     *
     * @return the base DN for a search request
     */
    public String getDN()
    {
        return getASN1Object().getRequestDN();
    }

    /**
     * Retrieves the scope of a search request.
     * @return scope of a search request
     *
     * @see com.novell.ldap.LDAPConnection#SCOPE_BASE
     * @see com.novell.ldap.LDAPConnection#SCOPE_ONE
     * @see com.novell.ldap.LDAPConnection#SCOPE_SUB
     * * @see com.novell.ldap.LDAPConnection#SCOPE_SUBORDINATESUBTREE
     */
    public int getScope()
    {
        //element number one stores the scope
        return ( (ASN1Enumerated)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(1)).intValue();
    }

    /**
     * Retrieves the behaviour of dereferencing aliases on a search request.
     * @return integer representing how to dereference aliases
     *
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_ALWAYS
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_FINDING
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_NEVER
     * @see com.novell.ldap.LDAPSearchConstraints#DEREF_SEARCHING
     */
    public int getDereference()
    {
        //element number two stores the dereference
        return ( (ASN1Enumerated)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(2)).intValue();
    }

    /**
     * Retrieves the maximum number of entries to be returned on a search.
     *
     * @return Maximum number of search entries.
     */
    public int getMaxResults()
    {
        //element number three stores the max results
        return ( (ASN1Integer)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(3)).intValue();
    }

    /**
     * Retrieves the server time limit for a search request.
     *
     * @return server time limit in nanoseconds.
     */
    public int getServerTimeLimit()
    {
        //element number four stores the server time limit
        return ( (ASN1Integer)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(4)).intValue();
    }

    /**
     * Retrieves whether attribute values or only attribute types(names) should
     * be returned in a search request.
     * @return true if only attribute types (names) are returned, false if
     * attributes types and values are to be returned.
     */
    public boolean isTypesOnly()
    {
        //element number five stores types value
        return ( (ASN1Boolean)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(5)).booleanValue();
    }

    /**
     * Retrieves an array of attribute names to request for in a search.
     * @return Attribute names to be searched
     */
    public String[] getAttributes()
    {
        RfcAttributeDescriptionList attrs = (RfcAttributeDescriptionList)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(7);

        String rAttrs[] = new String[attrs.size()];
        for(int i=0; i< rAttrs.length; i++){
            rAttrs[i]=((RfcAttributeDescription)attrs.get(i)).stringValue();
        }
        return rAttrs;
    }

    /**
     * Creates a string representation of the filter in this search request.
     * @return filter string for this search request
     */
    public String getStringFilter()
    {
        return this.getRfcFilter().filterToString();
    }

    /**
     * Retrieves an SearchFilter object representing a filter for a search request
     * @return filter object for a search request.
     */
    private RfcFilter getRfcFilter()
    {
        return (RfcFilter)
                ( (RfcSearchRequest)
                    ( this.getASN1Object() ).get(1)
                ).get(6);
    }

    /**
     * Retrieves an Iterator object representing the parsed filter for
     * this search request.
     *
     * <p>The first object returned from the Iterator is an Integer indicating
     * the type of filter component. One or more values follow the component
     * type as subsequent items in the Iterator. The pattern of Integer 
     * component type followed by values continues until the end of the
     * filter.</p>
     * 
     * <p>Values returned as a byte array may represent UTF-8 characters or may
     * be binary values. The possible Integer components of a search filter
     * and the associated values that follow are:
     *<ul>
     * <li>AND - followed by an Iterator value</li>
     * <li>OR - followed by an Iterator value</li>
     * <li>NOT - followed by an Iterator value</li>
     * <li>EQUALITY_MATCH - followed by the attribute name represented as a
     *     String, and by the attribute value represented as a byte array</li>
     * <li>GREATER_OR_EQUAL - followed by the attribute name represented as a
     *     String, and by the attribute value represented as a byte array</li>
     * <li>LESS_OR_EQUAL - followed by the attribute name represented as a
     *     String, and by the attribute value represented as a byte array</li>
     * <li>APPROX_MATCH - followed by the attribute name represented as a
     *     String, and by the attribute value represented as a byte array</li>
     * <li>PRESENT - followed by a attribute name respresented as a String</li>
     * <li>EXTENSIBLE_MATCH - followed by the name of the matching rule
     *     represented as a String, by the attribute name represented
     *     as a String, and by the attribute value represented as a 
     *     byte array.
     * <li>SUBSTRINGS - followed by the attribute name represented as a
     *     String, by one or more SUBSTRING components (INITIAL, ANY,
     *     or FINAL) followed by the SUBSTRING value.
     * </ul></p>
     *
     * @return Iterator representing filter components
     */
    public Iterator getSearchFilter()
    {
        return getRfcFilter().getFilterIterator();
    }
    
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
	   throws IOException, ClassNotFoundException {
//		Check if it is the correct message type
	  if(!(readObject instanceof LDAPSearchRequest))
	  	throw new ClassNotFoundException("Error occured while deserializing " +	  		"LDAPSearchRequest object");
	  try{
		   LDAPSearchRequest tmpObject = (LDAPSearchRequest)readObject;	
		   String base = tmpObject.getDN();
		   int scope = tmpObject.getScope();
		   String filter = tmpObject.getStringFilter();
		   String[] attrs = tmpObject.getAttributes();
		   int dereference = tmpObject.getDereference();
		   int maxResults = tmpObject.getMaxResults();
		   int serverTimeLimit = tmpObject.getServerTimeLimit();
		   boolean typesOnly = tmpObject.isTypesOnly();
		  tmpObject = null; //remove reference after getting properties
	
		  RfcRequest operation = null;
		  operation =  new RfcSearchRequest (
					  new RfcLDAPDN(base),
					  new ASN1Enumerated(scope),
					  new ASN1Enumerated(dereference),
					  new ASN1Integer(maxResults),
					  new ASN1Integer(serverTimeLimit),
					  new ASN1Boolean(typesOnly),
					  new RfcFilter(filter),
					  new RfcAttributeDescriptionList(attrs)); 	 
		    	
			message = new RfcLDAPMessage(operation, asn1Ctrls); 
	  }
	  catch(LDAPException le){
	 	  throw new IOException("LDAPException occured while de-serializing the stored " +
		  "object. There is a corruption in stored object. Restore it before" +
		  "using this feature." + le);
	  }
//		Garbage collect the readObject from readDSML()..	
		readObject = null;
   }       
}

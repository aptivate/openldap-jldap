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

package com.novell.ldap.message;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/*
 * Represents an LDAP Search request.
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
}

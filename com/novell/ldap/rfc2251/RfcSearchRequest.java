
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
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
public class SearchRequest extends ASN1Sequence implements Request {

	//*************************************************************************
	// Constructors for SearchRequest
	//*************************************************************************

	/**
	 *
	 */
	public SearchRequest(LDAPDN baseObject, ASN1Enumerated scope,
		                  ASN1Enumerated derefAliases, ASN1Integer sizeLimit,
		                  ASN1Integer timeLimit, ASN1Boolean typesOnly,
		                  Filter filter, AttributeDescriptionList attributes)
	{
		super(8);
		add(baseObject);
		add(scope);
		add(derefAliases);
		add(sizeLimit);
		add(timeLimit);
		add(typesOnly);
		add(filter);
		add(attributes);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 3. (0x63)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       ProtocolOp.SEARCH_REQUEST);
	}

}


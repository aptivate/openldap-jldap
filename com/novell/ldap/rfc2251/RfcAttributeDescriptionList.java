
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 * The AttributeDescriptionList is used to list attributes to be returned in
 * a search request.
 *
 *       AttributeDescriptionList ::= SEQUENCE OF
 *               AttributeDescription
 *
 * @see AttributeDescription
 * @see ASN1SequenceOf
 * @see SearchRequest
 */
public class AttributeDescriptionList extends ASN1SequenceOf {

	/**
	 *
	 */
	public AttributeDescriptionList()
	{
		super();
	}

	/**
	 *
	 */
	public AttributeDescriptionList(int size)
	{
		super(size);
	}

	/**
	 * Override add() to only accept types of AttributeDescription
	 *
	 * @exception ASN1InvalidTypeException
	 */

}



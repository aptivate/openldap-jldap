
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
    * Convenience constructor. This constructor will construct an
    * AttributeDescriptionList using the supplied array of Strings.
    */
   public AttributeDescriptionList(String[] attrs)
   {
      super(attrs == null ? 0 : attrs.length);

		if(attrs != null) {
			for(int i=0; i<attrs.length; i++) {
				add(new AttributeDescription(attrs[i]));
			}
		}
   }

   /**
    * Override add() to only accept types of AttributeDescription
    *
    * @exception ASN1InvalidTypeException
    */

}


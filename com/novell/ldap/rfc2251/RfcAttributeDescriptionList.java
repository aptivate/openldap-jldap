
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 * The AttributeDescriptionList is used to list attributes to be returned in
 * a search request.
 *
 *       AttributeDescriptionList ::= SEQUENCE OF
 *               AttributeDescription
 *
 * @see RfcAttributeDescription
 * @see ASN1SequenceOf
 * @see RfcSearchRequest
 */
public class RfcAttributeDescriptionList extends ASN1SequenceOf {

   /**
    *
    */
   public RfcAttributeDescriptionList()
   {
      super();
   }

   /**
    *
    */
   public RfcAttributeDescriptionList(int size)
   {
      super(size);
   }

   /**
    * Convenience constructor. This constructor will construct an
    * AttributeDescriptionList using the supplied array of Strings.
    */
   public RfcAttributeDescriptionList(String[] attrs)
   {
      super(attrs == null ? 0 : attrs.length);

		if(attrs != null) {
			for(int i=0; i<attrs.length; i++) {
				add(new RfcAttributeDescription(attrs[i]));
			}
		}
   }

   /**
    * Override add() to only accept types of AttributeDescription
    *
    * @exception ASN1InvalidTypeException
    */

}


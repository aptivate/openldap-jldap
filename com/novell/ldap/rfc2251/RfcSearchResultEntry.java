
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
 *               objectName      LDAPDN,
 *               attributes      PartialAttributeList }
 */
public class SearchResultEntry extends ASN1Sequence {

   //*************************************************************************
   // Constructors for SearchResultEntry
   //*************************************************************************

   /**
    * The only time a client will create a SearchResultEntry is when it is
    * decoding it from an InputStream
    */
   public SearchResultEntry(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);

      // Decode objectName
//      set(0, new LDAPDN(((ASN1OctetString)get(0)).getContent()));

      // Create PartitalAttributeList. This does not need to be decoded, only
      // typecast.
//      set(1, new PartitalAttributeList());


   }

   //*************************************************************************
   // Accessors
   //*************************************************************************

   /**
    *
    */
   public ASN1OctetString getObjectName()
   {
      return (ASN1OctetString)get(0);
   }

   /**
    *
    */
   public ASN1Sequence getAttributes()
   {
      return (ASN1Sequence)get(1);
   }

   /**
    * Override getIdentifier to return an application-wide id.
    */
   public ASN1Identifier getIdentifier()
   {
      return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                ProtocolOp.SEARCH_RESULT_ENTRY);
   }

}



package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *       SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
 *               objectName      LDAPDN,
 *               attributes      PartialAttributeList }
 */
public class RfcSearchResultEntry extends ASN1Sequence {

   //*************************************************************************
   // Constructors for SearchResultEntry
   //*************************************************************************

   /**
    * The only time a client will create a SearchResultEntry is when it is
    * decoding it from an InputStream
    */
   public RfcSearchResultEntry(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);

      // Decode objectName
//      set(0, new RfcLDAPDN(((ASN1OctetString)get(0)).getContent()));

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
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, "RfcSearchResultEntry - Object name: " +
                ((ASN1OctetString)get(0)).toString());
        }
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
                                RfcProtocolOp.SEARCH_RESULT_ENTRY);
   }

}



package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *       SearchResultReference ::= [APPLICATION 19] SEQUENCE OF LDAPURL
 */
public class SearchResultReference extends ASN1SequenceOf {

   //*************************************************************************
   // Constructors for SearchResultReference
   //*************************************************************************

   /**
    * The only time a client will create a SearchResultReference is when it is
    * decoding it from an InputStream
    */
   public SearchResultReference(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);
   }

   //*************************************************************************
   // Accessors
   //*************************************************************************

   /**
    * Override getIdentifier to return an application-wide id.
    */
   public ASN1Identifier getIdentifier()
   {
      return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                ProtocolOp.SEARCH_RESULT_REFERENCE);
   }

}


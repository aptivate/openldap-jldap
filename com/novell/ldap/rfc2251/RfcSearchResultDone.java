
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       SearchResultDone ::= [APPLICATION 5] LDAPResult
 */
public class RfcSearchResultDone extends RfcLDAPResult {

   //*************************************************************************
   // Constructors for SearchResultDone
   //*************************************************************************

   /**
    * The only time a client will create a SearchResultDone is when it is
    * decoding it from an InputStream
    */
   public RfcSearchResultDone(ASN1Decoder dec, InputStream in, int len)
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
                                RfcProtocolOp.SEARCH_RESULT_DONE);
   }

}


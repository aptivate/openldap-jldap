
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 *                protocolOp      CHOICE {
 *                       bindRequest     BindRequest,
 *                       bindResponse    BindResponse,
 *                       unbindRequest   UnbindRequest,
 *                       searchRequest   SearchRequest,
 *                       searchResEntry  SearchResultEntry,
 *                       searchResDone   SearchResultDone,
 *                       searchResRef    SearchResultReference,
 *                       modifyRequest   ModifyRequest,
 *                       modifyResponse  ModifyResponse,
 *                       addRequest      AddRequest,
 *                       addResponse     AddResponse,
 *                       delRequest      DelRequest,
 *                       delResponse     DelResponse,
 *                       modDNRequest    ModifyDNRequest,
 *                       modDNResponse   ModifyDNResponse,
 *                       compareRequest  CompareRequest,
 *                       compareResponse CompareResponse,
 *                       abandonRequest  AbandonRequest,
 *                       extendedReq     ExtendedRequest,
 *                       extendedResp    ExtendedResponse },
 */
public class RfcProtocolOp extends ASN1Choice {

   /*
    * Protocol Operation application tags
    */
   public final static int BIND_REQUEST            = 0;
   public final static int BIND_RESPONSE           = 1;
   public final static int UNBIND_REQUEST          = 2;
   public final static int SEARCH_REQUEST          = 3;
   public final static int SEARCH_RESULT_ENTRY     = 4;
   public final static int SEARCH_RESULT_DONE      = 5;
   public final static int MODIFY_REQUEST          = 6;
   public final static int MODIFY_RESPONSE         = 7;
   public final static int ADD_REQUEST             = 8;
   public final static int ADD_RESPONSE            = 9;
   public final static int DEL_REQUEST             = 10;
   public final static int DEL_RESPONSE            = 11;
   public final static int MODIFY_DN_REQUEST       = 12;
   public final static int MODIFY_DN_RESPONSE      = 13;
   public final static int COMPARE_REQUEST         = 14;
   public final static int COMPARE_RESPONSE        = 15;
   public final static int ABANDON_REQUEST         = 16;
   public final static int SEARCH_RESULT_REFERENCE = 19;
   public final static int EXTENDED_REQUEST        = 23;
   public final static int EXTENDED_RESPONSE       = 24;

   //*************************************************************************
   // Constructor for ProtocolOp
   //*************************************************************************

   /**
    * Typically, a class which extends ASN1Choice would accept an ASN1Tagged
    * type for its constructor. Since the LDAP protocol employs the notion of
    * IMPLICIT TAGS, all of the application specific classes can extend some
    * ASN1Object and override the getIdentification method. (This could not
    * be done if EXPLICIT tags were used, since EXPLICIT tags encode both the
    * application specific tag, and the tag of the base type.) By extending
    * a base type, we can set the Identifier of the new type, while it
    * maintains its base type.
    */
   public RfcProtocolOp(ASN1Object choice)
   {
      super(choice);
   }

}


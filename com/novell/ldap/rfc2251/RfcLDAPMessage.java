
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *       LDAPMessage ::= SEQUENCE {
 *               messageID       MessageID,
 *               protocolOp      CHOICE {
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
 *                controls       [0] Controls OPTIONAL }
 */
public class LDAPMessage extends ASN1Sequence {

   /**
    * Create an LDAPMessage from input parameters.
    */
   public LDAPMessage(MessageID mid, Request op)
   {
      this(mid, op, null);
   }

   /**
    * Create an LDAPMessage from input parameters.
    */
   public LDAPMessage(MessageID mid, Request op, Controls controls)
   {
      super(3);

      add(mid);
      add((ASN1Object)op);
      if(controls != null)
         add(controls);
   }

   /**
    * Will decode an LDAPMessage directly from an InputStream.
    */
   public LDAPMessage(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);

//      set(0, new MessageID(((ASN1Integer)get(0)).getInt()));

      // Decode implicitly tagged protocol operation from an ASN1Tagged type
      // to its appropriate application type.
      ASN1Tagged protocolOp = (ASN1Tagged)get(1);
      ASN1Identifier protocolOpId = protocolOp.getIdentifier();
      byte[] content =
         ((ASN1OctetString)protocolOp.getContent()).getContent();
      ByteArrayInputStream bais =
          new ByteArrayInputStream(content);

      switch(protocolOpId.getTag()) {
         case ProtocolOp.BIND_RESPONSE:
            set(1, new BindResponse(dec, bais, content.length));
            break;
         case ProtocolOp.ADD_RESPONSE:
            set(1, new AddResponse(dec, bais, content.length));
            break;
         case ProtocolOp.DEL_RESPONSE:
            set(1, new DelResponse(dec, bais, content.length));
            break;
         case ProtocolOp.EXTENDED_RESPONSE:
            set(1, new ExtendedResponse(dec, bais, content.length));
            break;
//       case ProtocolOp.SEARCH_RESULT_ENTRY:
//          set(1, new SearchResultEntry(dec, bais, content.length));
//          break;
      }

      // decode implicitly tagged optional controls

   }

   //*************************************************************************
   // Accessors
   //*************************************************************************

   /**
    *
    */
   public MessageID getMessageID()
   {
      return (MessageID)get(0);
   }

   /**
    *
    */
   public ASN1Object getProtocolOp()
   {
      return get(1);
   }

   /**
    *
    */
   public Controls getControls()
   {
      return (Controls)get(2);
   }

}


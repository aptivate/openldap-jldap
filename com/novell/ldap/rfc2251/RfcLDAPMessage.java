/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcLDAPMessage.java,v 1.13 2000/11/10 16:50:08 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *       LDAPMessage ::= SEQUENCE {
 *               messageID       MessageID,
 *               protocolOp      CHOICE {
 *                   bindRequest     BindRequest,
 *                   bindResponse    BindResponse,
 *                   unbindRequest   UnbindRequest,
 *                   searchRequest   SearchRequest,
 *                   searchResEntry  SearchResultEntry,
 *                   searchResDone   SearchResultDone,
 *                   searchResRef    SearchResultReference,
 *                   modifyRequest   ModifyRequest,
 *                   modifyResponse  ModifyResponse,
 *                   addRequest      AddRequest,
 *                   addResponse     AddResponse,
 *                   delRequest      DelRequest,
 *                   delResponse     DelResponse,
 *                   modDNRequest    ModifyDNRequest,
 *                   modDNResponse   ModifyDNResponse,
 *                   compareRequest  CompareRequest,
 *                   compareResponse CompareResponse,
 *                   abandonRequest  AbandonRequest,
 *                   extendedReq     ExtendedRequest,
 *                   extendedResp    ExtendedResponse },
 *                controls       [0] Controls OPTIONAL }
 *
 * Note: The creation of a MessageID should be hidden within the creation of
 *       an RfcLDAPMessage. The MessageID needs to be in sequence, and has an
 *       upper and lower limit. There is never a case when a user should be
 *       able to specify the MessageID for an RfcLDAPMessage. The MessageID()
 *       constructor should be package protected. (So the MessageID value
 *       isn't arbitrarily run up.)
 */
public class RfcLDAPMessage extends ASN1Sequence {

   /**
    * Create an RfcLDAPMessage using the specified LDAP Request Protocol Op.
    */
   public RfcLDAPMessage(RfcRequest op)
   {
      this(op, null);
   }

   /**
    * Create an RfcLDAPMessage from input parameters.
    */
   public RfcLDAPMessage(RfcRequest op, RfcControls controls)
   {
      super(3);

      add(new RfcMessageID()); // MessageID has static counter
      add((ASN1Object)op);
      if(controls != null)
         add(controls);
   }

   /**
    * Will decode an RfcLDAPMessage directly from an InputStream.
    */
   public RfcLDAPMessage(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(dec, in, len);

        byte[] content;
        ByteArrayInputStream bais;

      //      set(0, new MessageID(((ASN1Integer)get(0)).getInt()));

      // Decode implicitly tagged protocol operation from an ASN1Tagged type
      // to its appropriate application type.
      ASN1Tagged protocolOp = (ASN1Tagged)get(1);
      ASN1Identifier protocolOpId = protocolOp.getIdentifier();
      content = ((ASN1OctetString)protocolOp.getContent()).getContent();
      bais = new ByteArrayInputStream(content);

      if( Debug.LDAP_DEBUG ) {
          Debug.trace( Debug.messages, "RfcLDAPMessage: input message w/tag " +
            protocolOpId.getTag());
      }
      switch(protocolOpId.getTag()) {
         case RfcProtocolOp.SEARCH_RESULT_ENTRY:
            set(1, new RfcSearchResultEntry(dec, bais, content.length));
            break;
         case RfcProtocolOp.SEARCH_RESULT_DONE:
            set(1, new RfcSearchResultDone(dec, bais, content.length));
            break;
         case RfcProtocolOp.SEARCH_RESULT_REFERENCE:
            set(1, new RfcSearchResultReference(dec, bais, content.length));
            break;
         case RfcProtocolOp.ADD_RESPONSE:
            set(1, new RfcAddResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.BIND_RESPONSE:
            set(1, new RfcBindResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.COMPARE_RESPONSE:
            set(1, new RfcCompareResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.DEL_RESPONSE:
            set(1, new RfcDelResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.EXTENDED_RESPONSE:
            set(1, new RfcExtendedResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.MODIFY_RESPONSE:
            set(1, new RfcModifyResponse(dec, bais, content.length));
            break;
         case RfcProtocolOp.MODIFY_DN_RESPONSE:
            set(1, new RfcModifyDNResponse(dec, bais, content.length));
            break;
      }

      // decode optional implicitly tagged controls from ASN1Tagged type to
      // to RFC 2251 types.
      if(size() > 2) {
         ASN1Tagged controls = (ASN1Tagged)get(2);
         //   ASN1Identifier controlsId = protocolOp.getIdentifier();
         // we could check to make sure we have controls here....

         content = ((ASN1OctetString)controls.getContent()).getContent();
         bais = new ByteArrayInputStream(content);
         set(2, new RfcControls(dec, bais, content.length));
      }
      return;
   }

   //*************************************************************************
   // Accessors
   //*************************************************************************

   /**
    * Returns this RfcLDAPMessage's messageID as an int.
    */
   public int getMessageID()
   {
      return ((ASN1Integer)get(0)).getInt();
   }

   /**
    * Returns the Protocol Operation for this RfcLDAPMessage.
    */
   public ASN1Object getProtocolOp()
   {
      return get(1);
   }

   /**
    * Returns the optional Controls for this RfcLDAPMessage.
    */
   public RfcControls getControls()
   {
        if(size() > 2)
            return (RfcControls)get(2);
        return null;
   }

}


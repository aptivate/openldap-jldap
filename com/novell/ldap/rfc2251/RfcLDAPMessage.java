/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

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
 *
 * Note: The creation of a MessageID should be hidden within the creation of
 *       an LDAPMessage. The MessageID needs to be in sequence, and has an
 *       upper and lower limit. There is never a case when a user should be
 *       able to specify the MessageID for an LDAPMessage. The MessageID()
 *       constructor should be package protected. (So the MessageID value
 *       isn't arbitrarily run up.)
 */
public class LDAPMessage extends ASN1Sequence {

   /**
    * Create an LDAPMessage using the specified LDAP Request Protocol Op.
    */
   public LDAPMessage(Request op)
   {
      this(op, null);
   }

   /**
    * Create an LDAPMessage from input parameters.
    */
   public LDAPMessage(Request op, Controls controls)
   {
      super(3);

      add(new MessageID()); // MessageID has static counter
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

		byte[] content;
		ByteArrayInputStream bais;

//      set(0, new MessageID(((ASN1Integer)get(0)).getInt()));

      // Decode implicitly tagged protocol operation from an ASN1Tagged type
      // to its appropriate application type.
      ASN1Tagged protocolOp = (ASN1Tagged)get(1);
      ASN1Identifier protocolOpId = protocolOp.getIdentifier();
      content = ((ASN1OctetString)protocolOp.getContent()).getContent();
      bais = new ByteArrayInputStream(content);

      switch(protocolOpId.getTag()) {
			case ProtocolOp.SEARCH_RESULT_ENTRY:
				set(1, new SearchResultEntry(dec, bais, content.length));
				break;
			case ProtocolOp.SEARCH_RESULT_DONE:
				set(1, new SearchResultDone(dec, bais, content.length));
				break;
			case ProtocolOp.SEARCH_RESULT_REFERENCE:
				set(1, new SearchResultReference(dec, bais, content.length));
				break;
         case ProtocolOp.ADD_RESPONSE:
            set(1, new AddResponse(dec, bais, content.length));
            break;
         case ProtocolOp.BIND_RESPONSE:
            set(1, new BindResponse(dec, bais, content.length));
            break;
         case ProtocolOp.COMPARE_RESPONSE:
            set(1, new CompareResponse(dec, bais, content.length));
            break;
         case ProtocolOp.DEL_RESPONSE:
            set(1, new DelResponse(dec, bais, content.length));
            break;
         case ProtocolOp.EXTENDED_RESPONSE:
            set(1, new ExtendedResponse(dec, bais, content.length));
            break;
         case ProtocolOp.MODIFY_RESPONSE:
            set(1, new ModifyResponse(dec, bais, content.length));
            break;
         case ProtocolOp.MODIFY_DN_RESPONSE:
            set(1, new ModifyDNResponse(dec, bais, content.length));
            break;
      }

      // decode optional implicitly tagged controls from ASN1Tagged type to
		// to RFC 2251 types.
		if(size() > 2) {
			ASN1Tagged controls = (ASN1Tagged)get(2);
//			ASN1Identifier controlsId = protocolOp.getIdentifier();
			// we could check to make sure we have controls here....

			content = ((ASN1OctetString)controls.getContent()).getContent();
			bais = new ByteArrayInputStream(content);
			set(2, new Controls(dec, bais, content.length));
		}

   }

   //*************************************************************************
   // Accessors
   //*************************************************************************

   /**
    * Returns this LDAPMessage's messageID as an int.
    */
   public int getMessageID()
   {
      return ((ASN1Integer)get(0)).getInt();
   }

   /**
    * Returns the Protocol Operation for this LDAPMessage.
    */
   public ASN1Object getProtocolOp()
   {
      return get(1);
   }

   /**
    * Returns the optional Controls for this LDAPMessage.
    */
   public Controls getControls()
   {
		if(size() > 2)
			return (Controls)get(2);
		return null;
   }

}


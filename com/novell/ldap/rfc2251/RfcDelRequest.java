/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/protocol/RfcDelRequest.java,v 1.5 2000/11/09 18:27:19 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       DelRequest ::= [APPLICATION 10] LDAPDN
 */
public class RfcDelRequest extends RfcLDAPDN implements RfcRequest {

   //*************************************************************************
   // Constructor for DelRequest
   //*************************************************************************

   /**
    * Constructs an LDAPv3 delete request protocol operation.
    *
    * @param dn The Distinguished Name of the entry to delete.
    */
   public RfcDelRequest(String dn)
   {
      super(dn);
   }

   /**
    * Override getIdentifier() to return the appropriate application-wide id
    * representing this delete request. The getIdentifier() method is called
    * when this object is encoded.
    *
    * Identifier = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 10
    */
   public ASN1Identifier getIdentifier()
   {
      return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
                                RfcProtocolOp.DEL_REQUEST);
   }

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/protocol/DelRequest.java,v 1.4 2000/09/11 21:06:00 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       DelRequest ::= [APPLICATION 10] LDAPDN
 */
public class DelRequest extends RfcLDAPDN implements Request {

   //*************************************************************************
   // Constructor for DelRequest
   //*************************************************************************

   /**
    * Constructs an LDAPv3 delete request protocol operation.
    *
    * @param dn The Distinguished Name of the entry to delete.
    */
   public DelRequest(String dn)
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
                                ProtocolOp.DEL_REQUEST);
   }

}


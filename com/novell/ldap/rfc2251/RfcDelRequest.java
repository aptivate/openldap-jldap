/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcDelRequest.java,v 1.9 2001/02/28 21:23:53 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

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

    public RfcRequest dupRequest(String base, String filter, Integer scope)
            throws LDAPException
    {
        throw new LDAPException(
                    LDAPExceptionMessageResource.NO_DUP_REQUEST,
                    new Object[] { "delete" },
                    LDAPException.LDAP_NOT_SUPPORTED);
    }
}

/* **************************************************************************
 * $Id: GetEffectivePrivilegesRequest.java,v 1.6 2000/08/28 22:19:19 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
package com.novell.ldap.ext; 

import com.novell.ldap.*;
import com.novell.asn1.*;
import java.io.*;
 
/**
 *
 * This class is used to get the effective rights of one object on an
 * specific attribute of another object<br><br>
 *
 * To use this class instantiate an object of this class and then call the 
 * extendedOperation method with this object as the required 
 * LDAPExtendedOperation parameter.<br><br>
 *
 * The returned LDAPExtendedResponse object can then be converted to
 * a GetEffectivePrivilegesRequest object.  The GetEffectivePrivilegesRequest object contains
 * methods for retreiving the effective rights.<br><br>
 *
 *  The OID used for this extended operation is:
 *      "2.16.840.1.113719.1.27.100.33"<br><br>
 *
 *  The RequestValue has the folling ASN:<br>
 *
 *  requestValue ::=<br><br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;    dn          LDAPDN
 *  &nbsp;&nbsp;&nbsp;&nbsp;    trusteeDN   LDAPDN
 *  &nbsp;&nbsp;&nbsp;&nbsp;    attrName    LDAPDN     
 */
 public class GetEffectivePrivilegesRequest extends LDAPExtendedOperation {
 
    /**
    *      The constructor takes three parameters:<br><br>
    *
    * @param dn dn of the entry whose attribute follows
    * @param trusteeDN dn of the entry whose trustee rights are being returned
    * @param attrName LDAP attribute name.
    *
    */  

    public GetEffectivePrivilegesRequest(String dn, String trusteeDN, String attrName) 
                throws LDAPException {
        
        super(NamingContextConstants.GET_EFFECTIVE_PRIVILEGES_REQ, null);
        
        try {
            
            if ( (dn == null) )
                throw new LDAPException("Invalid parameter",
                                    LDAPException.PARAM_ERROR);
         
         ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
         LBEREncoder encoder  = new LBEREncoder();
                               
          ASN1OctetString asn1_dn = new ASN1OctetString(dn);
          ASN1OctetString asn1_trusteeDN = new ASN1OctetString(trusteeDN);
          ASN1OctetString asn1_attrName = new ASN1OctetString(attrName);
            
            asn1_dn.encode(encoder, encodedData);
            asn1_trusteeDN.encode(encoder, encodedData);
            asn1_attrName.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
                   
        }
      catch(IOException ioe) {
         throw new LDAPException("Encoding Error",
                                 LDAPException.ENCODING_ERROR);
      }
        
     }
}

/* **************************************************************************
 * $Id: GetContextIdentityNameResponse.java,v 1.10 2000/10/04 17:00:48 judy Exp $
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
package com.novell.ldap.extensions; 

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import java.io.*;
 
/**
 *  Retrieves the identity from an GetContextIdentityNameResponse object.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object 
 *  using the ExtendedResponseFactory class.</p>
 *
 * <p> GetContextIdentityNameResponse objects have the following OID:<br> 
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.32 </p>
 *
 */
public class GetContextIdentityNameResponse implements ParsedExtendedResponse {
   
   // Identity returned by the server
   private String identity;
   
   /**
    * Constructs an object from the responseValue which contains the identity.
    *
    *  <p>The constructor parses the responseValue which has the following 
    *  format:<br>
    *  responseValue ::=<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp;identity &nbsp;&nbsp;&nbsp;  OCTET STRING</p>
    *
    *  @exception IOException The return value could not be decoded.
    */   
   public GetContextIdentityNameResponse (LDAPExtendedResponse r) 
         throws IOException {
        
        // parse the contents of the reply
        byte [] returnedValue = r.getValue();
        if (returnedValue == null)
            throw new IOException("No returned value");
        
        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");
           
        // The only parameter returned should be an octet string
        ASN1OctetString asn1_identity = (ASN1OctetString)decoder.decode(returnedValue);        
        if (asn1_identity == null)
            throw new IOException("Decoding error");
        
        // Convert to normal string object
        identity = new String(asn1_identity.getContent());
        if (identity == null)
            throw new IOException("Decoding error");
   }
   
   /**
    * Returns the identity of the object.
    * 
    * @return String value specifying the identity returned by the server
    */
   public String getIdentity() {
        return identity;
   }
    
}

/* **************************************************************************
 * $Id: GetReplicationFilterResponse.java,v 1.11 2000/10/10 16:39:15 judy Exp $
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
 *  This object represent the filter returned fom a GetReplicationFilterRequest.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 *  <p>The GetReplicationFilterResponse extension uses the following OID:<br> 
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.38</p>
 *
 */
public class GetReplicationFilterResponse implements ParsedExtendedResponse {
   
   
   /**
    * Constructs an object from the responseValue which contains the replication
    * filter
    *
    *  <p>The constructor parses the responseValue which has the following 
    *  format:<br>
    *  responseValue ::=<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of SEQUENCE {</p>      
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; classname&nbsp;&nbsp;&nbsp;  OCTET STRING</p> 
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of ATTRIBUTES</p> 
	*  &nbsp;&nbsp;&nbsp;&nbsp;}</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;where</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;ATTRIBUTES:: OCTET STRING</p> 
    *
    * @exception IOException The responseValue could not be decoded.
    */   
   public GetReplicationFilterResponse (LDAPExtendedResponse r) 
         throws IOException {
        
        // parse the contents of the reply
        byte [] returnedValue = r.getValue();
        if (returnedValue == null)
            throw new IOException("No returned value");
        
        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

       
   }
   
    
}

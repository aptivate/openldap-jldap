/* **************************************************************************
 * $Id: ListReplicasResponse.java,v 1.6 2000/10/04 22:39:34 judy Exp $
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
 * Retrieves the list of replicas from the specified server.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object 
 *  using the ExtendedResponseFactory class.</p>
 *
 * <p>The listReplicaResponse extension uses the following OID:<br> 
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.20</p>
 *
 */
public class ListReplicasResponse implements ParsedExtendedResponse {
   
   // Identity returned by the server
   private String[] replicaList;
   
   /**
    * Constructs an object from the responseValue which contains the list 
    * of replicas.
    *
    * <p>The constructor parses the responseValue which has the following 
    * format:<br>
    *  responseValue ::=<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp;  replicaList&nbsp;&nbsp;&nbsp;
    *                                    SEQUENCE OF OCTET STRINGS
    *
    * @exception IOException  The responseValue could not be decoded.
    */   
   public ListReplicasResponse (LDAPExtendedResponse r) 
         throws IOException {
        
        // parse the contents of the reply
        byte [] returnedValue = r.getValue();
        if (returnedValue == null)
            throw new IOException("No returned value");
        
        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");
           
        // We should get back a sequence
        ASN1Sequence returnedSequence = (ASN1Sequence)decoder.decode(returnedValue);        
        if (returnedSequence == null)
            throw new IOException("Decoding error");
        
        // How many replicas were returned
        int len = returnedSequence.size();
        replicaList = new String[len];
        
        // Copy each one into our String array
      for(int i=0; i < len; i++) {
          // Get the next ASN1Octet String in the sequence
          ASN1OctetString asn1_nextReplica = (ASN1OctetString)returnedSequence.get(i);
          if (asn1_nextReplica == null)
                throw new IOException("Decoding error");
            
            // Convert to a string
         replicaList[i] = new String(asn1_nextReplica.getContent());
         if (replicaList[i] == null)
                throw new IOException("Decoding error");
      }

   }
   
   /** 
    * Returns a list of distinguished names for the replicas on the server.
    *
    * @return String value specifying the identity returned by the server
    */
   public String[] getReplicaList() {
        return replicaList;
   }
    
}

/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.resources.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


public class LburpEndRequest extends LDAPExtendedOperation {

    static
    {
		/*
         * Register the extendedresponse class which is returned by the
		 * server in response to a LburpEndRequest
		 */
        try {
            LDAPExtendedResponse.register(
                  LburpConstants.LBURPEndReplResOID,
                  Class.forName(
                  "com.novell.ldap.extensions.LburpEndResponse"));
        }catch (ClassNotFoundException e) {
            System.err.println("Could not register Extended Response -" +
                               " Class not found");
        }catch(Exception e){
           e.printStackTrace();
        }
        
    }

                                         
    public LburpEndRequest(int lastSeq)
                     throws LDAPException {

        super(LburpConstants.LBURPEndReplReqOID, null);

        try {


            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1Sequence asn1_lpSeq = new ASN1Sequence();
            ASN1Integer asn1_lastSeq = new ASN1Integer(lastSeq);
            asn1_lpSeq.add(asn1_lastSeq);
            asn1_lpSeq.encode(encoder, encodedData);
            setValue(encodedData.toByteArray());

        }catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
        }
    }

}

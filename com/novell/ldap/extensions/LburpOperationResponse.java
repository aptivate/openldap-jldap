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
import com.novell.ldap.rfc2251.*;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.util.HashMap;

public class LburpOperationResponse extends LDAPExtendedResponse {
    
    private HashMap response; 
    
    public LburpOperationResponse(RfcLDAPMessage rfcMessage)
                                  throws IOException
    {

        super(rfcMessage);
        int record;
        String resp;
        
        response = new HashMap(5);
        byte [] returnedValue = this.getValue();
        if (returnedValue == null)
                throw new IOException("No returned value");

        ASN1Identifier   asn1ID  = new ASN1Identifier();
        ASN1Length       asn1Len = new ASN1Length();        
        LBERDecoder dec = new LBERDecoder();

        ASN1Object asn1 = null;
        ByteArrayInputStream in = new ByteArrayInputStream(returnedValue);
        try {
        
            int[] len = new int[1];
            while(true){     
                asn1ID.reset(in);
                asn1Len.reset(in);

                int length = asn1Len.getLength();
                len[0] = asn1ID.getEncodedLength() +
                asn1Len.getEncodedLength() +
                length;

                int TAG = 0x10;
                ASN1Identifier ID =
                new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
                     
                ASN1Object[] content;      
                int contentIndex = 0;
                content = new ASN1Object[10];
                int[] componentLen = new int[1]; // collects length of component

                while(length > 0) {
                    content[contentIndex++] = dec.decode(in, componentLen);
                    length -= componentLen[0];
                }

                ASN1Object obj=content[0];
                record = ((ASN1Integer)obj).intValue();

                ASN1Tagged tval= (ASN1Tagged)content[1];
                ASN1OctetString astr=(ASN1OctetString)tval.taggedValue();
                byte[] bval=astr.byteValue();

                ByteArrayInputStream ins = new ByteArrayInputStream(bval);
                int[] compLen = new int[1];
                ASN1Object asb=dec.decode(ins, compLen);

                asn1ID.reset(ins);
                asn1Len.reset(ins);  
         

                ASN1Object asn2=dec.decode(ins,len);
                resp=((ASN1OctetString)asn2).stringValue();
                if(resp.length() != 0){
                    response.put((Object)new Integer(record),(Object)new String(resp));
                }
            }      
      }catch(IOException ioe){

      }
      catch(Exception e){
            e.printStackTrace();
    }
    
}

    public HashMap getResponse()
    {
        return response;
    }

}


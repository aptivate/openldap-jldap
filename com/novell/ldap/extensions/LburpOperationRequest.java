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
import com.novell.ldap.resources.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


public class LburpOperationRequest extends LDAPExtendedOperation {

    int currSize;      
    
    static
    {
		/*
         * Register the extendedresponse class which is returned by the
		 * server in response to a LburpOperationResponse
		 */
        try {
            LDAPExtendedResponse.register(
                  LburpConstants.LBURPOperationResOID,
                  Class.forName(
                  "com.novell.ldap.extensions.LburpOperationResponse"));
        }catch (ClassNotFoundException e) {
            System.err.println("Could not register Extended Response -" +
                               " Class not found");
        }catch(Exception e){
           e.printStackTrace();
        }
        
    }

   
    public LburpOperationRequest(LDAPLburpRequest[] op, 
                                 int seqNumber)
                                 throws LDAPException
    {

        super(LburpConstants.LBURPOperationReqOID, null);
        try {

/*            if ( (seqNumber < 1 )  )
                throw new IllegalArgumentException(
                                         ExceptionMessages.PARAM_ERROR);
*/
            ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
            LBEREncoder encoder  = new LBEREncoder();

            ASN1Sequence asn1_lburpPack = new ASN1Sequence();
            
            ASN1Integer asn1_seqNumber = new ASN1Integer(seqNumber);
            asn1_lburpPack.add(asn1_seqNumber);

            currSize=0;
//            ASN1Sequence asn1_ldapOp = new ASN1Sequence();
            ASN1Sequence asn1_ops;
            ASN1OctetString asn1_dops;
            ASN1SequenceOf asn1_ldapControl;
            ASN1Choice asn1_ch;
            ASN1Identifier asn1_tag;
            ASN1Object iobj;
//            while(true){        
//			System.out.println("op length is:" + op.length);    
			while((currSize < op.length) && (op[currSize]!=null) ){
            ASN1Sequence asn1_ldapOp = new ASN1Sequence();
//                System.out.println("\n" + currSize + "opType:" +  op[currSize].opType);
                switch(op[currSize].opType)  {
                
                    case LDAPMessage.ADD_REQUEST:
                    case LDAPMessage.MODIFY_REQUEST:
                    case LDAPMessage.MODIFY_RDN_REQUEST:
                        asn1_ops = op[currSize].getRequestASN1Sequence();
                        asn1_ldapOp.add(asn1_ops);
                        break;

                    case LDAPMessage.DEL_REQUEST:
                        asn1_dops = op[currSize].getRequestASN1OcString();
                        asn1_tag = new ASN1Identifier(0,true,LDAPMessage.DEL_REQUEST);
                        asn1_ldapOp.add(asn1_dops);
                        break;

                    default:
                        break;
                }
                asn1_ldapControl=op[currSize].getControlsASN1Object();
                if(asn1_ldapControl != null){
                    asn1_ldapOp.add(asn1_ldapControl);
                }    
                ++currSize;   
                asn1_lburpPack.add(asn1_ldapOp);                
            }
//            System.out.println("\n currSize is :" + currSize);
//            asn1_lburpPack.add(asn1_ldapOp);
//            System.out.println("\n Now encoding..");
            asn1_lburpPack.encode(encoder, encodedData);
            
            setValue(encodedData.toByteArray());
        }catch(IOException ioe) {
         throw new LDAPException(ExceptionMessages.ENCODING_ERROR,
                                 LDAPException.ENCODING_ERROR,(String)null);
        }
    }

}

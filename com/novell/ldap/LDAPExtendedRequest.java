/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.io.IOException;

import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Tagged;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcExtendedRequest;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcLDAPOID;
import com.novell.ldap.rfc2251.RfcRequest;

/**
 * Represents an LDAP Extended Request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *               requestName      [0] LDAPOID,
 *               requestValue     [1] OCTET STRING OPTIONAL }
 */
public class LDAPExtendedRequest extends LDAPMessage
{
    
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPExtendedRequest(){
		super(LDAPMessage.EXTENDED_REQUEST);
	}
    
    /**
     * Constructs an LDAPExtendedRequest.
     *<br><br>
     * @param op  The object which contains (1) an identifier of an extended
     *            operation which should be recognized by the particular LDAP
     *            server this client is connected to, and (2) an operation-
     *            specific sequence of octet strings or BER-encoded values.
     *<br><br>
     * @param cont Any controls that apply to the extended request
     * or null if none.
     */
	public LDAPExtendedRequest(LDAPExtendedOperation op, LDAPControl[] cont)
    {
        super( LDAPMessage.EXTENDED_REQUEST,
               new RfcExtendedRequest(
                   new RfcLDAPOID(op.getID()),
                   (op.getValue() != null) ?
                       new ASN1OctetString(op.getValue()) :
                       null),
               cont);
        return;
    }

    /**
     * Retrieves an extended operation from this request
     * @return extended operation represented in this request.
     */
    public LDAPExtendedOperation getExtendedOperation(){
        RfcExtendedRequest xreq = (RfcExtendedRequest )
                this.getASN1Object().get(1);

        //Zeroth element is the OID, element one is the value
        ASN1Tagged tag = (ASN1Tagged) xreq.get(0);
        RfcLDAPOID oid = (RfcLDAPOID)tag.taggedValue();
        String requestID = oid.stringValue();

        byte requestValue[] = null;
        if (xreq.size() >= 2){
            tag = (ASN1Tagged) xreq.get(1);
            ASN1OctetString value = (ASN1OctetString)tag.taggedValue();
            requestValue = value.byteValue();
        }
        return new LDAPExtendedOperation(requestID, requestValue);
    }
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
		   throws IOException, ClassNotFoundException {
//			Check if it is the correct message type
		  if(!(readObject instanceof LDAPExtendedRequest))
			throw new ClassNotFoundException("Error occured while deserializing " +
				"LDAPExtendedRequest object");

			LDAPExtendedRequest tmp = (LDAPExtendedRequest)readObject;
			LDAPExtendedOperation extendedOp = tmp.getExtendedOperation();
			tmp = null; //remove reference after getting properties
			
			RfcRequest operation =  new RfcExtendedRequest(
				   new RfcLDAPOID(extendedOp.getID()),
				   (extendedOp.getValue() != null) ?
					   new ASN1OctetString(extendedOp.getValue()) :
					   null); 	 	
			message = new RfcLDAPMessage(operation, asn1Ctrls); 
//			Garbage collect the readObject from readDSML()..	
			readObject = null;
	   }       
    
    
}

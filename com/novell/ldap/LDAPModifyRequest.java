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
import java.util.Enumeration;

import com.novell.ldap.asn1.ASN1Enumerated;
import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1SequenceOf;
import com.novell.ldap.asn1.ASN1SetOf;
import com.novell.ldap.rfc2251.RfcAttributeDescription;
import com.novell.ldap.rfc2251.RfcAttributeTypeAndValues;
import com.novell.ldap.rfc2251.RfcAttributeValue;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPDN;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcModifyRequest;
import com.novell.ldap.rfc2251.RfcRequest;

/**
 * Modification Request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *               object          LDAPDN,
 *               modification    SEQUENCE OF SEQUENCE {
 *                       operation       ENUMERATED {
 *                                               add     (0),
 *                                               delete  (1),
 *                                               replace (2) },
 *                       modification    AttributeTypeAndValues } }
 */
public class LDAPModifyRequest extends LDAPMessage
{
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPModifyRequest()
	{
		super(LDAPMessage.MODIFY_REQUEST);
	}
	
    /**
     * Constructs an LDAP Modify request.
     *
     *  @param dn         The distinguished name of the entry to modify.
     *<br><br>
     *  @param mods       The changes to be made to the entry.
     *<br><br>
     * @param cont        Any controls that apply to the modify request,
     *                    or null if none.
     */
    public LDAPModifyRequest( String dn,
                              LDAPModification[] mods,
                              LDAPControl[] cont)
        throws LDAPException
    {

        super( LDAPMessage.MODIFY_REQUEST,
               new RfcModifyRequest(
                   new RfcLDAPDN(dn),
                   encodeModifications(mods)),
               cont);
        return;
    }
        
    /**
     * Encode an array of LDAPModifications to ASN.1.
     *
     * @param mods an array of LDAPModification objects
     *
     * @return an ASN1SequenceOf object containing the modifications.
     */
    final static
	private ASN1SequenceOf encodeModifications( LDAPModification[] mods)
    {
        // Convert Java-API LDAPModification[] to RFC2251 SEQUENCE OF SEQUENCE
        ASN1SequenceOf rfcMods = new ASN1SequenceOf(mods.length);
        for(int i=0; i<mods.length; i++) {
            LDAPAttribute attr = mods[i].getAttribute();

            // place modification attribute values in ASN1SetOf
            ASN1SetOf vals = new ASN1SetOf(attr.size());
            if( attr.size() > 0) {
                Enumeration attrEnum = attr.getByteValues();
                while(attrEnum.hasMoreElements()) {
                    vals.add(new RfcAttributeValue((byte[])attrEnum.nextElement()));
                }
            }

            // create SEQUENCE containing mod operation and attr type and vals
            ASN1Sequence rfcMod = new ASN1Sequence(2);
            rfcMod.add(new ASN1Enumerated(mods[i].getOp()));
            rfcMod.add(new RfcAttributeTypeAndValues(
                new RfcAttributeDescription(attr.getName()), vals));

            // place SEQUENCE into SEQUENCE OF
            rfcMods.add(rfcMod);
        }
        return rfcMods;    
    }
    
    /**
     * Returns of the dn of the entry to modify in the directory
     *
     * @return the dn of the entry to modify
     */
    public String getDN()
    {
        return getASN1Object().getRequestDN();
    }        

    /**
     * Constructs the modifications associated with this request
     *
     * @return an array of LDAPModification objects
     */
    public LDAPModification[] getModifications()
    {
        // Get the RFC request object for this request
        RfcModifyRequest req = (RfcModifyRequest)getASN1Object().getRequest();
        // get beginning sequenceOf modifications
        ASN1SequenceOf seqof = req.getModifications();
        ASN1Object[] mods = seqof.toArray();
        LDAPModification[] modifications = new LDAPModification[mods.length];
        // Process each modification
        for( int m=0; m < mods.length; m++) {
            // Each modification consists of a mod type and a sequence
            // containing the attr name and a set of values
            ASN1Sequence opSeq = (ASN1Sequence)mods[m];
            if( opSeq.size() != 2) {
                throw new RuntimeException(
                    "LDAPModifyRequest: modification " + m +
                    " is wrong size: " + opSeq.size());
            }
            // Contains operation and sequence for the attribute
            ASN1Object[] opArray = opSeq.toArray();
            ASN1Enumerated asn1op = (ASN1Enumerated)opArray[0];
            // get the operation
            int op = asn1op.intValue();
            ASN1Sequence attrSeq = (ASN1Sequence)opArray[1];
            ASN1Object[] attrArray = attrSeq.toArray();
            RfcAttributeDescription aname = (RfcAttributeDescription)attrArray[0];
            String name = aname.stringValue();
            ASN1SetOf avalue = (ASN1SetOf)attrArray[1];
            ASN1Object[] valueArray = avalue.toArray();
            LDAPAttribute attr = new LDAPAttribute( name);
            
            for( int v=0; v<valueArray.length; v++) {
                RfcAttributeValue rfcV = (RfcAttributeValue)valueArray[v];
                attr.addValue( rfcV.byteValue());
            }
            
            modifications[m] = new LDAPModification( op, attr);
        }
        return modifications;
    }
    
    /**
     * Return an ASN1 representation of this modify request
     *
     * #return an ASN1 representation of this object
     */
    public String toString()
    {
        return getASN1Object().toString();
    }
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
	   throws IOException, ClassNotFoundException {
//		Check if it is the correct message type
	  if(!(readObject instanceof LDAPModifyRequest))
		throw new ClassNotFoundException("Error occured while deserializing " +
			"LDAPModifyRequest object");

		LDAPModifyRequest tmp = (LDAPModifyRequest)readObject;
		String dn = tmp.getDN();
		LDAPModification[] mods = tmp.getModifications();
		tmp = null; //remove reference after getting properties

		RfcRequest operation =  new RfcModifyRequest(
				new RfcLDAPDN(dn),
				LDAPModifyRequest.encodeModifications(mods));
		message = new RfcLDAPMessage(operation, asn1Ctrls); 
//		Garbage collect the readObject from readDSML()..	
		readObject = null;
	}           
}

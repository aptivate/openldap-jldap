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

import com.novell.ldap.rfc2251.RfcAssertionValue;
import com.novell.ldap.rfc2251.RfcAttributeDescription;
import com.novell.ldap.rfc2251.RfcAttributeValueAssertion;
import com.novell.ldap.rfc2251.RfcCompareRequest;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPDN;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcRequest;

/**
 * Represents an LDAP Compare Request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *               entry           LDAPDN,
 *               ava             AttributeValueAssertion }
 */
public class LDAPCompareRequest extends LDAPMessage
{
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPCompareRequest()
	{
		super(LDAPMessage.COMPARE_REQUEST);
	}
    
    /**
     * Constructs an LDAPCompareRequest Object.
     *<br><br>
     *  @param dn      The distinguished name of the entry containing an
     *                 attribute to compare.
     *<br><br>
     *  @param name    The name of the attribute to compare.
     *<br><br>
     *  @param value    The value of the attribute to compare.
     *
     *<br><br>
     * @param cont Any controls that apply to the compare request,
     * or null if none.
     */
    public LDAPCompareRequest( String dn,
                               String name,
                               byte[] value,
                               LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.COMPARE_REQUEST,
               new RfcCompareRequest(
                   new RfcLDAPDN(dn),
                   new RfcAttributeValueAssertion(
                       new RfcAttributeDescription(name),
                       new RfcAssertionValue(value))),
               cont);
        return;
    }
    
    /**
     * Returns the LDAPAttribute associated with this request.
     *
     * @return the LDAPAttribute
     */
    public String getAttributeDescription()
    {
        RfcCompareRequest req = (RfcCompareRequest)getASN1Object().getRequest();
        return req.getAttributeValueAssertion().getAttributeDescription();
    }
    
    /**
     * Returns the LDAPAttribute associated with this request.
     *
     * @return the LDAPAttribute
     */
    public byte[] getAssertionValue()
    {
        RfcCompareRequest req = (RfcCompareRequest)getASN1Object().getRequest();
        return req.getAttributeValueAssertion().getAssertionValue();
    }
    
    /**
     * Returns of the dn of the entry to compare in the directory
     *
     * @return the dn of the entry to compare
     */
    public String getDN()
    {
        return getASN1Object().getRequestDN();
    }
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
		   throws IOException, ClassNotFoundException {
//			Check if it is the correct message type
		  if(!(readObject instanceof LDAPCompareRequest))
			throw new ClassNotFoundException("Error occured while deserializing " +
				"LDAPCompareRequest object");

			LDAPCompareRequest tmp = (LDAPCompareRequest)readObject;
			String dn = tmp.getDN();
			String name = tmp.getAttributeDescription();
			byte[] value = tmp.getAssertionValue();
			tmp = null; //remove reference after getting properties

			RfcRequest operation =  new RfcCompareRequest(
				   new RfcLDAPDN(dn),
				   new RfcAttributeValueAssertion(
					   new RfcAttributeDescription(name),
					   new RfcAssertionValue(value))); 	 
			message = new RfcLDAPMessage(operation, asn1Ctrls); 
//			Garbage collect the readObject from readDSML()..	
			readObject = null;
	   }       
}

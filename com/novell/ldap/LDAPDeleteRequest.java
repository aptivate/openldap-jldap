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

import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcDelRequest;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcRequest;

/**
 * Represents a request to delete an entry.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       DelRequest ::= [APPLICATION 10] LDAPDN
 */
public class LDAPDeleteRequest extends LDAPMessage
{
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPDeleteRequest()
	{
		super(LDAPMessage.DEL_REQUEST);
	}
    
    /**
     * Constructs a request to delete an entry from the directory
     *
     * @param dn the dn of the entry to delete.
     *
     * @param cont Any controls that apply to the abandon request
     * or null if none.
     */
    public LDAPDeleteRequest( String dn,
                              LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.DEL_REQUEST, new RfcDelRequest(dn), cont);
        return;
    }
    
    /**
     * Returns of the dn of the entry to delete from the directory
     *
     * @return the dn of the entry to delete
     */
    public String getDN()
    {
        return getASN1Object().getRequestDN();
    }        
    
    /**
     * Return an ASN1 representation of this delete request
     *
     * #return an ASN1 representation of this object
     */
    public String toString()
    {
        return getASN1Object().toString();
    }
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
	   throws IOException, ClassNotFoundException {
//	  Check if it is the correct message type
	  if(!(readObject instanceof LDAPDeleteRequest))
		throw new ClassNotFoundException("Error occured while deserializing " +
			"LDAPDeleteRequest object");

		LDAPDeleteRequest tmp = (LDAPDeleteRequest)readObject;
		String dn = tmp.getDN();
		tmp = null; //remove reference after getting properties

		RfcRequest operation =  new RfcDelRequest(dn); 	 
		message = new RfcLDAPMessage(operation, asn1Ctrls); 
//		Garbage collect the readObject from readDSML()..	
		readObject = null;
   }           
}

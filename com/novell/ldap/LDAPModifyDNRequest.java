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

import com.novell.ldap.asn1.ASN1Boolean;
import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPDN;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcLDAPSuperDN;
import com.novell.ldap.rfc2251.RfcModifyDNRequest;
import com.novell.ldap.rfc2251.RfcRelativeLDAPDN;
import com.novell.ldap.rfc2251.RfcRequest;

/**
 * Represents an LDAP ModifyDN request
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       ModifyDNRequest ::= [APPLICATION 12] SEQUENCE {
 *               entry           LDAPDN,
 *               newrdn          RelativeLDAPDN,
 *               deleteoldrdn    BOOLEAN,
 *               newSuperior     [0] LDAPDN OPTIONAL }
 */
public class LDAPModifyDNRequest extends LDAPMessage
{
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPModifyDNRequest()
	{
		super(LDAPMessage.MODIFY_RDN_REQUEST);
	}
    
    /**
     * Constructs a ModifyDN (rename) Request.
     *
     *  @param dn             The current distinguished name of the entry.
     *<br><br>
     *  @param newRdn         The new relative distinguished name for the entry.
     *<br><br>
     *  @param newParentdn    The distinguished name of an existing entry which
     *                        is to be the new parent of the entry.
     *<br><br>
     *  @param deleteOldRdn   If true, the old name is not retained as an
     *                        attribute value. If false, the old name is
     *                        retained as an attribute value.
     *<br><br>
     * @param cont            Any controls that apply to the modifyDN request,
     *                        or null if none.
     */
    public LDAPModifyDNRequest( String dn,
                                String newRdn,
                                String newParentdn,
                                boolean deleteOldRdn,
                                LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.MODIFY_RDN_REQUEST,
               new RfcModifyDNRequest(
                   new RfcLDAPDN(dn),
                   new RfcRelativeLDAPDN(newRdn),
                   new ASN1Boolean(deleteOldRdn),
                   (newParentdn != null) ?
                       new RfcLDAPSuperDN(newParentdn) : null),
               cont);
        return;
    }

    /**
     * Returns the dn of the entry to rename or move in the directory
     *
     * @return the dn of the entry to rename or move
     */
    public String getDN()
    {
        return getASN1Object().getRequestDN();
    }

    /**
     * Returns the newRDN of the entry to rename or move in the directory
     *
     * @return the newRDN of the entry to rename or move
     */
    public String getNewRDN()
    {
        // Get the RFC request object for this request
        RfcModifyDNRequest req = (RfcModifyDNRequest)getASN1Object().getRequest();
        RfcRelativeLDAPDN relDN = (RfcRelativeLDAPDN)req.toArray()[1];
        return relDN.stringValue();
    }

    /**
     * Returns the DeleteOldRDN flag that applies to the entry to rename or
     * move in the directory
     *
     * @return the DeleteOldRDN flag for the entry to rename or move
     */
    public boolean getDeleteOldRDN()
    {
        // Get the RFC request object for this request
        RfcModifyDNRequest req = (RfcModifyDNRequest)getASN1Object().getRequest();
        ASN1Boolean delOld = (ASN1Boolean)req.toArray()[2];
        return delOld.booleanValue();
    }

    /**
     * Returns the ParentDN for the entry move in the directory
     *
     * @return the ParentDN for the entry to move, or <dd>null</dd>
     * if the request is not a move.
     */
    public String getParentDN()
    {
        // Get the RFC request object for this request
        RfcModifyDNRequest req = (RfcModifyDNRequest)getASN1Object().getRequest();
        ASN1Object[] seq = req.toArray();
        if( (seq.length < 4)  || (seq[3] == null)) {
            return null;
        }
        RfcLDAPSuperDN parentDN = (RfcLDAPSuperDN)req.toArray()[3];
        return parentDN.stringValue();
    }

    /**
     * Return an ASN1 representation of this mod DN request
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
	  if(!(readObject instanceof LDAPModifyDNRequest))
		throw new ClassNotFoundException("Error occured while deserializing " +
			"LDAPModifyDNRequest object");

		LDAPModifyDNRequest tmp = (LDAPModifyDNRequest)readObject;
		String dn = tmp.getDN();
		String newRdn = tmp.getNewRDN();
		boolean deleteOldRdn = tmp.getDeleteOldRDN();
		String newParentdn = tmp.getParentDN();
		tmp = null; //remove reference after getting properties

		RfcRequest operation =  new RfcModifyDNRequest(
		new RfcLDAPDN(dn),
		new RfcRelativeLDAPDN(newRdn),
		new ASN1Boolean(deleteOldRdn),
		(newParentdn != null) ? new RfcLDAPSuperDN(newParentdn) : null);

		message = new RfcLDAPMessage(operation, asn1Ctrls); 
//		Garbage collect the readObject from readDSML()..	
		readObject = null;
   }           
}

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

import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

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
                       new RfcLDAPDN(newParentdn) : null),
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
        RfcLDAPDN parentDN = (RfcLDAPDN)req.toArray()[3];
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
}

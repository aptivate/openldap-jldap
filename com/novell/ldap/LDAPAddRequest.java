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

import java.util.Enumeration;

/**
 * Represents an LDAP Add Request.
 *
 * @see LDAPConnection#sendRequest
 *//*
 *       AddRequest ::= [APPLICATION 8] SEQUENCE {
 *               entry           LDAPDN,
 *               attributes      AttributeList }
 */
public class LDAPAddRequest extends LDAPMessage
{
    /**
     * Constructs a request to add an entry to the directory.
     *
     * @param entry The LDAPEntry to add to the directory.
     *
     * @param cont Any controls that apply to the add request,
     * or null if none.
     */
    public LDAPAddRequest( LDAPEntry entry,
                           LDAPControl[] cont)
        throws LDAPException
    {
        super( LDAPMessage.ADD_REQUEST,
               new RfcAddRequest(
                  new RfcLDAPDN(entry.getDN()), makeRfcAttrList( entry)), cont);
        return;
    }

    /**
     * Constructs an LDAPEntry that represents the add request
     *
     * @return an LDAPEntry that represents the add request.
     */
    public LDAPEntry getEntry() {
        RfcAddRequest addreq = (RfcAddRequest)getASN1Object().getRequest();

        LDAPAttributeSet attrs = new LDAPAttributeSet();

        // Build the list of attributes
        ASN1Object[] seqArray = addreq.getAttributes().toArray();
        for(int i = 0; i < seqArray.length; i++) {
            RfcAttributeTypeAndValues seq = (RfcAttributeTypeAndValues)seqArray[i];
            LDAPAttribute attr =
                new LDAPAttribute(((ASN1OctetString)seq.get(0)).stringValue());

            // Add the values to the attribute
            ASN1SetOf set = (ASN1SetOf)seq.get(1);
            Object[] setArray = set.toArray();
            for(int j = 0; j < setArray.length; j++) {
                attr.addValue(((ASN1OctetString)setArray[j]).byteValue());
            }
            attrs.add(attr);
        }

        return new LDAPEntry( getASN1Object().getRequestDN(), attrs);
    }

    /**
     * Build the attribuite list from an LDAPEntry.
     *
     * @param entry The LDAPEntry associated with this add request.
     */
    private static final RfcAttributeList makeRfcAttrList( LDAPEntry entry)
    {
        // convert Java-API LDAPEntry to RFC2251 AttributeList
        LDAPAttributeSet attrSet = entry.getAttributeSet();
        RfcAttributeList attrList = new RfcAttributeList(attrSet.size());
        Enumeration enum = attrSet.getAttributes();
        while(enum.hasMoreElements()) {
            LDAPAttribute attr = (LDAPAttribute)enum.nextElement();
            ASN1SetOf vals = new ASN1SetOf(attr.size());
            Enumeration attrEnum = attr.getByteValues();
            while(attrEnum.hasMoreElements()) {
                vals.add(new RfcAttributeValue((byte[])attrEnum.nextElement()));
            }
            attrList.add(new RfcAttributeTypeAndValues(
                new RfcAttributeDescription(attr.getName()), vals));
        }
        return attrList;
    }

    /**
     * Return an ASN1 representation of this add request.
     *
     * #return an ASN1 representation of this object.
     */
    public String toString()
    {
        return getASN1Object().toString();
    }
}

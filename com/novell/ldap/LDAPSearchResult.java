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
 *  Encapsulates a single search result that is in response to an asynchronous
 *  search operation.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/asynchronous/Searchas.java.html">Searchas.java</p>
 *
 * @see LDAPConnection#search
 */
public class LDAPSearchResult extends LDAPMessage
{

    private LDAPEntry entry = null;
    
    /**
     * Constructs an LDAPSearchResult object.
     *
     * @param message The RfcLDAPMessage with a search result.
     */
    /*package*/
    LDAPSearchResult(RfcLDAPMessage message)
    {
        super(message);
        return;
    }

    /**
     * Constructs an LDAPSearchResult object from an LDAPEntry.
     *
     * @param entry the LDAPEntry represented by this search result.
     */
    public LDAPSearchResult(LDAPEntry entry)
    {
        super();
        if( entry == null) {
            throw new IllegalArgumentException("Argument \"entry\" cannot be null");
        }    
        this.entry = entry;
        return;
    }

    /**
     * Returns the entry of a server's search response.
     *
     * @return The LDAPEntry associated with this LDAPSearchResult
     */
    public LDAPEntry getEntry()
    {
        if( entry == null) {
            LDAPAttributeSet attrs = new LDAPAttributeSet();

            ASN1Sequence attrList =
                ((RfcSearchResultEntry)message.getResponse()).getAttributes();

            ASN1Object[] seqArray = attrList.toArray();
            for(int i = 0; i < seqArray.length; i++) {
                ASN1Sequence seq = (ASN1Sequence)seqArray[i];
                LDAPAttribute attr =
                    new LDAPAttribute(((ASN1OctetString)seq.get(0)).stringValue());

                ASN1Set set = (ASN1Set)seq.get(1);
                Object[] setArray = set.toArray();
                for(int j = 0; j < setArray.length; j++) {
                    attr.addValue(((ASN1OctetString)setArray[j]).byteValue());
                }
                attrs.add(attr);
            }

            entry = new LDAPEntry(
                ((RfcSearchResultEntry)message.getResponse()).getObjectName().stringValue(),
                attrs);
        }            
        return entry;
    }

    /**
     * Return a String representation of this object.
     *
     * @return a String representing this object.
     */
    public String toString()
    {
        String str;
        if( entry == null) {
            str = super.toString();
        } else {
            str = entry.toString();
        }
        return str;
    }
}

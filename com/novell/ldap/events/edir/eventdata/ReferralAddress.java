/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999-2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.events.edir.eventdata;

import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;


/**
 * The class represents the Address(IP/IPX/IPV6 etc) datastructure for
 * Edir Events Notification.
 * 
 * <p>
 * The encoding for this class is as follows:- ReferralAddress := SEQUENCE {<br>
 * &nbsp;&nbsp;addressType        INTEGER,<br>
 * &nbsp;&nbsp;address            OCTET STRING<br>
 * &nbsp;&nbsp;}
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class ReferralAddress {
    private int addressType;
    private String address;

    /**
     * Default Constructor
     *
     * @param dseobject ASN1Sequence containing the encoded data.
     */
    public ReferralAddress(final ASN1Sequence dseobject) {
        super();

        addressType = ((ASN1Integer) dseobject.get(0)).intValue();
        address = ((ASN1OctetString) dseobject.get(1)).stringValue();
    }

    /**
     * Returns the Address as String.
     *
     * @return Address as String.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the Address Type.
     *
     * @return AddressType as integer.
     */
    public int getAddressType() {
        return addressType;
    }
}

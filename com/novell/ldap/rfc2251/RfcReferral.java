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
package com.novell.ldap.rfc2251;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import com.novell.ldap.LDAPUrl;
import com.novell.ldap.asn1.*;

/**
 * Represents an LDAP Referral.
 *
 *<pre>
 *        Referral ::= SEQUENCE OF LDAPURL
 *</pre>
 */
public class RfcReferral extends ASN1SequenceOf {

    //*************************************************************************
    // Constructor for Referral
    //*************************************************************************

    /**
     * The only time a Referral object is constructed, is when we are
     * decoding an RfcLDAPResult or COMPONENTS OF RfcLDAPResult.
     */
    public RfcReferral(ASN1Decoder dec, InputStream in, int len)
        throws IOException
    {
        super(dec, in, len);

        //convert from ASN1OctetString to RfcLDAPURL here (then look at
        // LDAPResponse.getReferrals())
    }

	/**
	 * This construtor is used when creating LDAPResponse directly and not from
	 *  the stream.
	 * @param value This array of Referrals [LDAPURL]
	 * @throws MalformedURLException
	 * <b>Note:Limitation</b> The ldapurl specified to this constructor should 
	 * have a dn part to it i.e ldap://www.nldap.com/cn=admin,o=acme  is 
	 * allowed but ldap://www.nldap.com is not allowed.
	 */
	public RfcReferral(String value[]) throws MalformedURLException {
		super(value.length + 1);
		for (int i = 0; i < value.length; i++) {
			LDAPUrl url = new LDAPUrl(value[i]);
			add(new ASN1OctetString(url.toString()));
		}

	}
    //*************************************************************************
    // Accessors
    //*************************************************************************

    // inherited from SequenceOf
}

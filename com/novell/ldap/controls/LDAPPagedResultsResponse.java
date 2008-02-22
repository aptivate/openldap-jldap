/* **************************************************************************
 *
 * Copyright (C) 2008 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap.controls;

import java.io.IOException;
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *  LDAPPagedResultsResponse - response to LDAPPagedResultsControl
 *  as defined in RFC2696.
 *
 */
public class LDAPPagedResultsResponse extends LDAPControl
{

    private int resultSize;
    private byte Cookie[];

    /**
     * This constructor is usually called by the SDK to instantiate an
     * a LDAPControl corresponding to the Server response to a LDAP
     * Control request.  Application programmers should not have
     * any reason to call the constructor.  This constructor besides
     * constructing a LDAPControl object parses the contents of the response
     * control.
     * <br>
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param values     The control-specific data.
     */
    public LDAPPagedResultsResponse(String oid, boolean critical, byte[] values)
                    throws IOException
    {
        super(oid, critical, values);

        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

        // We should get back an ASN1 Sequence object
        ASN1Object asnObj = decoder.decode(values);

        if ( (asnObj == null) || (!(asnObj instanceof ASN1Sequence)) )
            throw new IOException("Decoding error");

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.controls, "LDAPPagedResultsResponse Control Value =" + asnObj.toString());
        }

        /* Get the 1st element which should be an integer containing the
         * resultSize
         */
        ASN1Object asn1resultSize = ((ASN1Sequence)asnObj).get(0);
        if ( (asn1resultSize != null) && (asn1resultSize instanceof ASN1Integer) )
             resultSize =((ASN1Integer)asn1resultSize).intValue();
        else
            throw new IOException("Decoding error");

        /* Get the 2nd element which should be an octet string containing
         * the cookie for next invocation
         */
        ASN1Object asn1cookie = ((ASN1Sequence)asnObj).get(1);
        if ( (asn1cookie != null) && (asn1cookie instanceof ASN1OctetString) )
             Cookie =((ASN1OctetString)asn1cookie).byteValue();
        else
            throw new IOException("Decoding error");

        return;
    }

    /**
     * Returns the result size. According to RFC2696,
     * if server does not know this, it may contain 0.
     */
    public int getResultSize()
    {
        return resultSize;
    }

    /**
     * Returns the cookie that can be used in subsequent searches
     * to retrieve next page.
     */
    public byte[] getCookie()
    {
        return (Cookie.length == 0) ? null : Cookie;
    }

}


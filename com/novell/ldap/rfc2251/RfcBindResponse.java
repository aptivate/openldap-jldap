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
package com.novell.ldap.rfc2251;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;

/**
 * Represents and LDAP Bind Response.
 *
 *<pre>
 *       BindResponse ::= [APPLICATION 1] SEQUENCE {
 *
 *            COMPONENTS OF LDAPResult,
 *            serverSaslCreds    [7] OCTET STRING OPTIONAL }
 *</pre>
 */
public class RfcBindResponse extends ASN1Sequence implements RfcResponse {

    //*************************************************************************
    // Constructors for BindResponse
    //*************************************************************************

    /**
     * The only time a client will create a BindResponse is when it is
     * decoding it from an InputStream
     *
     * Note: If serverSaslCreds is included in the BindResponse, it does not
     *       need to be decoded since it is already an OCTET STRING.
     */
    public RfcBindResponse(ASN1Decoder dec, InputStream in, int len)
        throws IOException
    {
        super(dec, in, len);

        // Decode optional referral from ASN1OctetString to Referral.
        if(size() > 3) {
            ASN1Tagged obj = (ASN1Tagged)get(3);
            ASN1Identifier id = obj.getIdentifier();
            if(id.getTag() == RfcLDAPResult.REFERRAL) {
                byte[] content =
                    ((ASN1OctetString)obj.taggedValue()).byteValue();
                ByteArrayInputStream bais = new ByteArrayInputStream(content);
                set(3, new RfcReferral(dec, bais, content.length));
            }
        }

    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     *
     */
    public final ASN1Enumerated getResultCode()
    {
        return (ASN1Enumerated)get(0);
    }

    /**
     *
     */
    public final RfcLDAPDN getMatchedDN()
    {
        return new RfcLDAPDN(((ASN1OctetString)get(1)).byteValue());
    }

    /**
     *
     */
    public final RfcLDAPString getErrorMessage()
    {
        return new RfcLDAPString(((ASN1OctetString)get(2)).byteValue());
    }

    /**
     *
     */
    public final RfcReferral getReferral()
    {
        if( size() > 3) {
            ASN1Object obj = get(3);
            if(obj instanceof RfcReferral)
                return (RfcReferral)obj;
        }
        return null;
    }

    /**
     * Returns the OPTIONAL serverSaslCreds of a BindResponse if it exists
     * otherwise null.
     */
    public final ASN1OctetString getServerSaslCreds()
    {
        if(size() == 5)
            return (ASN1OctetString)((ASN1Tagged)get(4)).taggedValue();

        if(size() == 4) { // could be referral or serverSaslCreds
            ASN1Object obj = get(3);
            if(obj instanceof ASN1Tagged)
                return (ASN1OctetString)((ASN1Tagged)obj).taggedValue();
        }

        return null;
    }

    /**
     * Override getIdentifier to return an application-wide id.
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                   LDAPMessage.BIND_RESPONSE);
    }
}

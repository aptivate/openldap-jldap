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

package com.novell.ldap.asn1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This class encapsulates the OCTET STRING type.
 */
public class ASN1OctetString extends ASN1Object
{

    private byte[] content;

    /**
     * ASN.1 OCTET STRING tag definition.
     */
    public static final int TAG = 0x04;

    /**
     * ID is added for Optimization.
     * <p>Id needs only be one Value for every instance,
     * thus we create it only once.</p>
     */
    protected static final ASN1Identifier ID =
            new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
    /* Constructors for ASN1OctetString
     */

    /**
     * Call this constructor to construct an ASN1OctetString
     * object from a byte array.
     *
     * @param content A byte array representing the string that
     * will be contained in the this ASN1OctetString object
     */
    public ASN1OctetString(byte[] content)
    {
        super(ID);
        this.content = content;
        return;
    }


    /**
     * Call this constructor to construct an ASN1OctetString
     * object from a String object.
     *
     * @param content A string value that will be contained
     * in the this ASN1OctetString object
     */
    public ASN1OctetString(String content)
    {
        //content must be converted to utf8 data
        super(ID);
        try {
            this.content = content.getBytes("UTF8");
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException( uee.toString());
        }
        return;
    }


    /**
     * Constructs an ASN1OctetString object by decoding data from an
     * input stream.
     *
     * @param dec The decoder object to use when decoding the
     * input stream.  Sometimes a developer might want to pass
     * in his/her own decoder object<br>
     *
     * @param in A byte stream that contains the encoded ASN.1
     *
     */
    public ASN1OctetString(ASN1Decoder dec, InputStream in, int len)
            throws IOException
    {
        super(ID);
        content = (len>0) ? (byte[])dec.decodeOctetString(in, len) : new byte[0];
        return;
    }


    /* ASN1Object implementation
     */

    /**
     * Call this method to encode the current instance into the
     * specified output stream using the specified encoder object.
     *
     * @param enc Encoder object to use when encoding self.<br>
     *
     * @param out The output stream onto which the encoded byte
     * stream is written.
     */
    public final void encode(ASN1Encoder enc, OutputStream out)
            throws IOException
    {
        enc.encode(this, out);
        return;
    }


    /*ASN1OctetString specific methods
     */

    /**
     * Returns the content of this ASN1OctetString as a byte array.
     */
    public final byte[] byteValue()
    {
        return content;
    }


    /**
     * Returns the content of this ASN1OctetString as a String.
     */
    public final String stringValue()
    {
        String s = null;
        try {
            s = new String(content, "UTF8");
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException( uee.toString());
        }
        return s;
    }


    /**
     * Return a String representation of this ASN1Object.
     */
    public String toString()
    {
        return super.toString() + "OCTET STRING: " + stringValue();
    }
}

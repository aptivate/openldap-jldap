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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * This class encapsulates the ASN.1 BOOLEAN type.
 */
public class ASN1Boolean extends ASN1Object
{

    private boolean content;
 
    /**
     * ASN.1 BOOLEAN tag definition.
     */
    public static final int TAG = 0x01;

    /**
     * ID is added for Optimization.
     *
     * <p>ID needs only be one Value for every instance,
     * thus we create it only once.</p>
     */
     public static final ASN1Identifier ID =
            new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
    /* Constructors for ASN1Boolean
     */

    /**
     * Call this constructor to construct an ASN1Boolean
     * object from a boolean value.
     *
     * @param content The boolean value to be contained in the
     * this ASN1Boolean object
     */
    public ASN1Boolean(boolean content)
    {
        super(ID);
        this.content = content;
        return;
    }

    /**
     * Constructs an ASN1Boolean object by decoding data from an
     * input stream.
     *
     * @param dec The decoder object to use when decoding the
     * input stream.  Sometimes a developer might want to pass
     * in his/her own decoder object<br>
     *
     * @param in A byte stream that contains the encoded ASN.1
     *
     */
    public ASN1Boolean(ASN1Decoder dec, InputStream in, int len)
      throws IOException
    {
        super(ID);
        content = ((Boolean)dec.decodeBoolean(in, len)).booleanValue();
        return;
    }

    /* ASN1Object implementation
     */

    /**
     * Encode the current instance into the
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

    /* ASN1Boolean specific methods
     */

    /**
     * Returns the content of this ASN1Boolean as a boolean.
     */
    public final boolean booleanValue()
    {
        return content;
    }

    /**
     * Returns a String representation of this ASN1Boolean object.
     */
    public String toString()
    {
        return super.toString() + "BOOLEAN: " + content;
    }
}

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
import java.io.OutputStream;

/**
 * This class represents the ASN.1 NULL type.
 */
public class ASN1Null extends ASN1Object
{

    /**
     * ASN.1 NULL tag definition.
     */
    public static final int TAG = 0x05;

    /**
     * ID is added for Optimization.
    
     * <p>ID needs only be one Value for every instance,
     * thus we create it only once.</p>
     */
     public static final ASN1Identifier ID =
            new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
    /* Constructor for ASN1Null
     */

    /**
     * Call this constructor to construct a new ASN1Null
     * object.
     */
    public ASN1Null()
    {
        super(ID);
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

    /* ASN1Null specific methods
     */

    /**
     * Return a String representation of this ASN1Null object.
     */
    public String toString()
    {
        return super.toString() + "NULL: \"\"";
    }
}

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

/**
 * The ASN1Tagged class can hold a base ASN1Object with a distinctive tag
 * describing the type of that base object. It also maintains a boolean value
 * indicating whether the value should be encoded by EXPLICIT or IMPLICIT
 * means. (Explicit is true by default.)
 *
 * If the type is encoded IMPLICITLY, the base types form, length and content
 * will be encoded as usual along with the class type and tag specified in
 * the constructor of this ASN1Tagged class.
 *
 * If the type is to be encoded EXPLICITLY, the base type will be encoded as
 * usual after the ASN1Tagged identifier has been encoded.
 */
public class ASN1Tagged extends ASN1Object {

    private boolean explicit;
    private ASN1Object content;

    /* Constructors for ASN1Tagged
     */

    /**
     * Constructs an ASN1Tagged object using the provided 
     * AN1Identifier and the ASN1Object.
     *
     * The explicit flag defaults to true as per the spec.
     */
    public ASN1Tagged(ASN1Identifier identifier, ASN1Object object)
    {
        this(identifier, object, true);
        return;
    }

    /**
     * Constructs an ASN1Tagged object.
     */
    public ASN1Tagged(ASN1Identifier identifier, ASN1Object object,
                      boolean explicit)
    {
        super(identifier);
        this.content = object;
        this.explicit = explicit;

        if(!explicit  && content != null) {
            // replace object's id with new tag.
            content.setIdentifier(identifier);
        }
        return;
    }

    /**
     * Constructs an ASN1Tagged object by decoding data from an 
     * input stream.
     *
     * @param dec The decoder object to use when decoding the
     * input stream.  Sometimes a developer might want to pass
     * in his/her own decoder object<br>
     *
     * @param in A byte stream that contains the encoded ASN.1
     *
     */
    public ASN1Tagged(ASN1Decoder dec, InputStream in, int len,
                      ASN1Identifier identifier)
            throws IOException
    {
        super(identifier);

        // If we are decoding an implicit tag, there is no way to know at this
        // low level what the base type really is. We can place the content
        // into an ASN1OctetString type and pass it back to the application who
        // will be able to create the appropriate ASN.1 type for this tag.
        content = new ASN1OctetString(dec, in, len);
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

    /* ASN1Tagged specific methods
     */

    /**
     * Returns the ASN1Object stored in this ASN1Tagged object
     */
    public final ASN1Object taggedValue()
    {
        return content;
    }

    /**
     * Sets the ASN1Object tagged value
     */
    public final void setTaggedValue(ASN1Object content){
        this.content = content;
        if(!explicit && content != null) {
            // replace object's id with new tag.
            content.setIdentifier(this.getIdentifier());
        }
    }

    /**
     * Returns a boolean value indicating if this object uses
     * EXPLICIT tagging.
     */
    public final boolean isExplicit()
    {
        return explicit;
    }

    /**
     * Return a String representation of this ASN1Object.
     */
    public String toString()
    {
        if(explicit) {
            return super.toString() + content.toString();
        }
        // implicit tagging
        return content.toString();
    }
}

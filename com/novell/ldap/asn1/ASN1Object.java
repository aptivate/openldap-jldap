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

import java.io.Serializable;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This is the base class for all other ASN1 types.
 */
public abstract class ASN1Object implements Serializable {

    private ASN1Identifier id;

    public ASN1Object(ASN1Identifier id)
    {
        this.id = id;
        return;
    }
    
    /**
     * Abstract method that must be implemented by each child
     * class to encode itself ( an ASN1Object) directly intto 
     * a output stream.
     *
     * @param out The output stream onto which the encoded 
     * ASN1Object will be placed.
     */
    abstract public void encode(ASN1Encoder enc, OutputStream out)
        throws IOException;
        
    /**
     * Returns the identifier for this ASN1Object as an ASN1Identifier. 
     * This ASN1Identifier object will include the CLASS, FORM and TAG
     * for this ASN1Object.
     */
    public ASN1Identifier getIdentifier()
    {
        return id;
    }

    /**
     * Sets the identifier for this ASN1Object. This is helpful when 
     * creating implicit ASN1Tagged types.
     *
     * @param id An ASN1Identifier object representing the CLASS, 
     * FORM and TAG)
     */
    protected void setIdentifier(ASN1Identifier id)
    {
        this.id = id;
        return;
    }

    /**
     * This method returns a byte array representing the encoded
     * ASN1Object.  It in turn calls the encode method that is 
     * defined in ASN1Object but will usually be implemented
     * in the child ASN1 classses.
     */
    public final byte[] getEncoding(ASN1Encoder enc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            encode(enc, out);
        }
        catch(IOException e) {
            // Should never happen - the current ASN1Object does not have
            // a encode method. 
            throw new RuntimeException(
                "IOException while encoding to byte array: " + e.toString());
        }
        return out.toByteArray();
    }

    /**
     * Return a String representation of this ASN1Object.
     */
    public String toString()
    {
        String[] classTypes = {
            "[UNIVERSAL ", "[APPLICATION ", "[CONTEXT ", "[PRIVATE " };

        StringBuffer sb = new StringBuffer();
        ASN1Identifier id = getIdentifier(); // could be overridden.

        sb.append(classTypes[id.getASN1Class()]).append(id.getTag()).append("] ");

        return sb.toString();
    }
}

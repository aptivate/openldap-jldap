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
 * The ASN1Choice object represents the choice of any ASN1Object. All
 * ASN1Object methods are delegated to the object this ASN1Choice contains.
 */
 /* Can a CHOICE contain anything BUT a TAGGED Type?
 */
public class ASN1Choice extends ASN1Object
{

    private ASN1Object content;

    /* Constructors for ASN1Choice
     */

    /**
     * Constructs an ASN1Choice object using an ASN1Object value.
     *
     * @param content The ASN1Object that this ASN1Choice will
     * encode.  Since all ASN1 objects are derived from ASN1Object
     * any basic type can be passed in.
     */
    public ASN1Choice(ASN1Object content)
    {
        super(null);
        this.content = content;
        return;
    }

    /**
     * No arg Constructor. This is used by Filter, who subsequently sets the
     * content after parsing the RFC 2254 Search Filter String.
     */
    protected ASN1Choice()
    {
        super(null);
        this.content = null;
        return;
    }

    /* ASN1Object implementation
     */
    

    /**
     * Call this method to encode the contents of this ASN1Choice
     * instance into the specified output stream using the 
     * specified encoder object.
     *
     * @param enc Encoder object to use when encoding self.<br>
     *
     * @param out The output stream onto which the encoded byte 
     * stream is written.
     */
    public final void encode(ASN1Encoder enc, OutputStream out)
       throws IOException
    {
        content.encode(enc, out);
        return;
    }

    /* ASN1Choice specific methods
     */

    /**
     * Returns the CHOICE value stored in this ASN1Choice
     * as an ASN1Object. 
     */
    public final ASN1Object choiceValue()
    {
        return content;
    }

    /**
     * Sets the CHOICE value stored in this ASN1Choice.
     *
     * @param content The ASN1Object that this ASN1Choice will
     * encode.  Since all ASN1 objects are derived from ASN1Object
     * any basic type can be passed in.    
     */
    protected void setChoiceValue(ASN1Object content)
    {
        this.content = content;
        return;
    }

    /**
     * This method will return the ASN1Identifier of the 
     * encoded ASN1Object.We  override the parent method
     * as the identifier of an ASN1Choice depends on the 
     * type of the object encoded by this ASN1Choice.
     */
    public final ASN1Identifier getIdentifier()
    {
        return content.getIdentifier();
    }

    /**
     * Sets the identifier of the contained ASN1Object. We
     * override the parent method as the identifier of 
     * an ASN1Choice depends on the type of the object 
     * encoded by this ASN1Choice.
     */
    public final void setIdentifier(ASN1Identifier id)
    {
        content.setIdentifier(id);
        return;
    }

    /**
     * Return a String representation of this ASN1Object.
     */
    public String toString()
    {
        return content.toString();
    }
}

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

import com.novell.ldap.client.Debug;

/**
 * This class serves as the base type for all ASN.1
 * structured types.
 */
public abstract class ASN1Structured extends ASN1Object
{
    private ASN1Object[] content;
   
    private int contentIndex = 0;

    /*
     * Create a an ASN1 structured type with default size of 10
     *
     * @param the ASN1Identifier containing the tag for this structured type
     */
    protected ASN1Structured(ASN1Identifier id)
    {
        this(id, 10);
        return;
    }
    
    /*
     * Create a an ASN1 structured type with the designated size
     *
     * @param id the ASN1Identifier containing the tag for this structured type
     *
     * @param size the size to allocate
     */
    protected ASN1Structured(ASN1Identifier id, int size)
    {
        super(id);
        content = new ASN1Object[size];
        return;
    }
    
    /*
     * Create a an ASN1 structured type with default size of 10
     *
     * @param id the ASN1Identifier containing the tag for this structured type
     *
     * @param content an array containing the content
     *
     * @param size the number of items of content in the array
     */
    protected ASN1Structured( ASN1Identifier id,
                              ASN1Object[] newContent,
                              int size)
    {
        super(id);
        content = newContent;
        contentIndex = size;
        return;
    }
    
    /**
     * Encodes the contents of this ASN1Structured directly to an output
     * stream.
     */
    public final void encode(ASN1Encoder enc, OutputStream out)
                throws IOException
    {
        enc.encode(this, out);
        return;
    }

    /**
     * Decode an ASN1Structured type from an InputStream.
     */
    protected final void decodeStructured(ASN1Decoder dec, InputStream in, int len)
                throws IOException
    {
        int[] componentLen = new int[1]; // collects length of component

        while(len > 0) {
            add(dec.decode(in, componentLen));
            len -= componentLen[0];
        }
        return;    
    }

    /**
     * Returns an array containing the individual ASN.1 elements
     * of this ASN1Structed object.
     *
     * @return an array of ASN1Objects
     */
    public final ASN1Object[] toArray()
    {
        ASN1Object[] cloneArray = new ASN1Object[contentIndex];
        System.arraycopy( content, 0, cloneArray, 0, contentIndex);
        return cloneArray;
    }

    /**
     * Adds a new ASN1Object to the end of this ASN1Structured
     * object.
     *
     * @param value The ASN1Object to add to this ASN1Structured
     * object.
     */
    public final void add(ASN1Object value)
    {
        if( contentIndex == content.length) {
            // Array too small, need to expand it, double length
            int newSize = contentIndex + contentIndex;
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.asn1,
                    "ASN1Structured: Expanding Array from " + 
                    contentIndex + " to " + newSize);
            }
            ASN1Object[] newArray = new ASN1Object[newSize];
            System.arraycopy( content, 0, newArray, 0, contentIndex);
            content = newArray;
        }
        content[contentIndex++] = value;
        return;
    }

    /**
     * Replaces the ASN1Object in the specified index position of
     * this ASN1Structured object.
     *
     * @param index The index into the ASN1Structured object where
     * this new ANS1Object will be placed.
     *
     * @param value The ASN1Object to set in this ASN1Structured
     * object.
     */
    public final void set(int index, ASN1Object value)
    {
        if( (index >= contentIndex) || (index < 0)) {
            throw new IndexOutOfBoundsException("ASN1Structured: get: index " +
                    index + ", size " + contentIndex);
        }
        content[index] = value;
        return;
    }
    
    /**
     * Gets a specific ASN1Object in this structred object.
     *
     * @param index The index of the ASN1Object to get from
     * this ASN1Structured object.
     */
    public final ASN1Object get(int index)
    {
        if( (index >= contentIndex) || (index < 0)) {
            throw new IndexOutOfBoundsException("ASN1Structured: set: index " +
                    index + ", size " + contentIndex);
        }
        return content[index];
    }

    /**
     * Returns the number of ASN1Obejcts that have been encoded
     * into this ASN1Structured class.
     */
    public final int size()
    {
        return contentIndex;
    }

    /**
     * Creates a String representation of this ASN1Structured.
     * object.
     *
     * @param type the Type to put in the String representing this structured object
     *
     * @return the String representation of this object.
     */
    public String toString(String type)
    {
        StringBuffer sb = new StringBuffer();

        sb.append(type);

        for(int i=0; i < contentIndex; i++)
        {
            sb.append(content[i]);
            if(i != contentIndex-1)
                sb.append(", ");
        }
        sb.append(/*{*/ " }");

        return super.toString() + sb.toString();
    }
}

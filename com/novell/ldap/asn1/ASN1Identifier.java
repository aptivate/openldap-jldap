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
import java.io.EOFException;

/**
 * This class is used to encapsulate an ASN.1 Identifier.
 *
 * <p>An ASN1Identifier is composed of three parts:
 * <li> a class type,
 * <li> a form, and
 * <li> a tag.</p>
 *
 * <p>The class type is defined as:</p>
 *<pre>
 * bit 8 7 TAG CLASS
 * ------- -----------
 *     0 0 UNIVERSAL
 *     0 1 APPLICATION
 *     1 0 CONTEXT
 *     1 1 PRIVATE
 *</pre>
 *<p> The form is defined as:</p>
 *<pre>
 * bit 6 FORM
 * ----- --------
 *     0 PRIMITIVE
 *     1 CONSTRUCTED
 *</pre>
 *
 *<p> Note: CONSTRUCTED types are made up of other CONSTRUCTED or PRIMITIVE
 *       types.</p>
 *
 *<p> The tag is defined as:</p>
 *<pre>
 * bit 5 4 3 2 1 TAG
 * ------------- ---------------------------------------------
 *     0 0 0 0 0
 *     . . . . .
 *     1 1 1 1 0 (0-30) single octet tag
 *
 *     1 1 1 1 1 (> 30) multiple octet tag, more octets follow
 *</pre>
 */
public class ASN1Identifier extends Object implements Cloneable,
		java.io.Serializable {

    /**
     * Universal tag class.
     *
     * <p> UNIVERSAL = 0 </p>
     */
    public static final int UNIVERSAL = 0;
 
    /**
     * Application-wide tag class.
     *
     * <p> APPLICATION = 1 </p>
     */
    public static final int APPLICATION = 1;
 
    /**
     * Context-specific tag class.
     *
     * <p> CONTEXT = 2 </p>
     */
    public static final int CONTEXT = 2;
 
    /**
     * Private-use tag class.
     *
     * <p> PRIVATE = 3 </p>
     */
    public static final int PRIVATE = 3;
 
 
    /* Private variables
     */
 
    private int tagClass;
    private boolean constructed;
    private int tag;
    private int encodedLength;
 
    /* Constructors for ASN1Identifier
     */
 
    /**
     * Constructs an ASN1Identifier using the classtype, form and tag.
     *
     * @param tagClass As defined above.
     *
     * @param constructed Set to true if constructed and false if primitive.
     *
     * @param tag The tag of this identifier
     */
    public ASN1Identifier(int tagClass, boolean constructed, int tag)
    {
        this.tagClass = tagClass;
        this.constructed = constructed;
        this.tag = tag;
    }
 
    /**
     * Decode an ASN1Identifier directly from an InputStream and
     * save the encoded length of the ASN1Identifier.
     *
     * @param in The input stream to decode from.
     */
    public ASN1Identifier(InputStream in)
            throws IOException
    {
        int r = in.read();
        encodedLength++;
        if(r < 0)
            throw new EOFException("BERDecoder: decode: EOF in Identifier");
        tagClass = r >> 6;
        constructed = (r & 0x20) != 0;
        tag = r & 0x1F;      // if tag < 30 then its a single octet identifier.
        if(tag == 0x1F)      // if true, its a multiple octet identifier.
            tag = decodeTagNumber(in);
        return;
    }
 
    public ASN1Identifier()
    {
        return;
    }
 
    /**
     * Decode an ASN1Identifier directly from an InputStream and
     * save the encoded length of the ASN1Identifier, but reuse the object.
     *
     * @param in The input stream to decode from.
     */
    public final void reset(InputStream in)
            throws IOException
    {
        encodedLength = 0;
        int r = in.read();
        encodedLength++;
        if(r < 0)
            throw new EOFException("BERDecoder: decode: EOF in Identifier");
        tagClass = r >> 6;
        constructed = (r & 0x20) != 0;
        tag = r & 0x1F;      // if tag < 30 then its a single octet identifier.
        if(tag == 0x1F)      // if true, its a multiple octet identifier.
            tag = decodeTagNumber(in);
    }
 
    /**
     * In the case that we have a tag number that is greater than 30, we need
     * to decode a multiple octet tag number.
     */
    private int decodeTagNumber(InputStream in)
            throws IOException
    {
        int n = 0;
        while(true) {
            int r = in.read();
            encodedLength++;
            if(r < 0)
                throw new EOFException("BERDecoder: decode: EOF in tag number");
            n = (n<<7) + (r & 0x7F);
            if((r & 0x80) == 0)
                break;
        }
        return n;
    }
 
    /**
     * Returns the CLASS of this ASN1Identifier as an int value.
     *
     * @see #UNIVERSAL
     * @see #APPLICATION
     * @see #CONTEXT
     * @see #PRIVATE
     */
    public final int getASN1Class()
    {
        return tagClass;
    }
 
    /**
     * Return a boolean indicating if the constructed bit is set.
     *
     * @return true if constructed and false if primitive.
     */
    public final boolean getConstructed()
    {
        return constructed;
    }
 
    /**
     * Returns the TAG of this ASN1Identifier.
     */
    public final int getTag()
    {
        return tag;
    }
 
    /**
     * Returns the encoded length of this ASN1Identifier.
     */
    public final int getEncodedLength()
    {
        return encodedLength;
    }
 
    /* Convenience methods
     */
 
    /**
     * Returns a boolean value indicating whether or not this ASN1Identifier
     * has a TAG CLASS of UNIVERSAL.
     *
     * @see #UNIVERSAL
     */
    public final boolean isUniversal()
    {
        return tagClass == UNIVERSAL;
    }
 
    /**
     * Returns a boolean value indicating whether or not this ASN1Identifier
     * has a TAG CLASS of APPLICATION.
     *
     * @see #APPLICATION
     */
    public final boolean isApplication()
    {
        return tagClass == APPLICATION;
    }
 
    /**
     * Returns a boolean value indicating whether or not this ASN1Identifier
     * has a TAG CLASS of CONTEXT-SPECIFIC.
     *
     * @see #CONTEXT
     */
    public final boolean isContext()
    {
        return tagClass == CONTEXT;
    }
 
    /**
     * Returns a boolean value indicating whether or not this ASN1Identifier
     * has a TAG CLASS of PRIVATE.
     *
     * @see #PRIVATE
     */
    public final boolean isPrivate()
    {
        return tagClass == PRIVATE;
    }
 
    /**
     * Creates a duplicate, not a true clone, of this object and returns
     * a reference to the duplicate.
     *
     */
    public Object clone()
    {
        try {
            return super.clone();
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
    *  Writes the object state to a stream in standard Default Binary format
    *  This function wraps ObjectOutputStream' s defaultWriteObject() to write
    *  the non-static and non-transient fields of the current class to the stream
    *   
    *  @param objectOStrm  The OutputSteam where the Object need to be written
    */
    private void writeObject(java.io.ObjectOutputStream objectOStrm)
	    throws java.io.IOException {
		objectOStrm.defaultWriteObject();
    }
    
    /**
    *  Reads the serialized object from the underlying input stream.
    *  This function wraps ObjectInputStream's  defaultReadObject() function
    *
    *  @param objectIStrm  InputStream used to recover those objects previously serialized. 
    */
    private void readObject(java.io.ObjectInputStream objectIStrm)
         throws java.io.IOException, ClassNotFoundException
    {
	  objectIStrm.defaultReadObject();
    }
}

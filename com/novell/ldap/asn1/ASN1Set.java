
package org.ietf.asn1;

import java.io.*;
import java.util.Vector;

/**
 * The ASN1Set class can hold an unordered collection of components with
 * distinct type.
 */
public class ASN1Set extends ASN1Structured {

	//*************************************************************************
	// Constructors for ASN1Set
	//*************************************************************************

	/**
	 * Constructs an ASN1Set.
	 */
	public ASN1Set()
	{
		this(5);
	}

	/**
	 * Constructs an ASN1Set.
	 *
	 * @param size Specifies the initial size of the collection.
	 */
	public ASN1Set(int size)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SET);
		content = new Vector(size);
	}

	/**
	 * Constructs an ASN1Set object by decoding data from an input stream.
	 */
	public ASN1Set(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SET);
		decodeStructured(dec, in, len);
	}

	//*************************************************************************
	// ASN1Set specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString("SET: { ");
	}

}


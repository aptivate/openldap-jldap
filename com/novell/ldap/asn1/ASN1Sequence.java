
package com.novell.asn1;

import java.io.*;
import java.util.Vector;

/**
 * The ASN1Sequence class can hold an ordered collection of components with
 * distinct type.
 */
public class ASN1Sequence extends ASN1Structured {

	//*************************************************************************
	// Constructors for ASN1Sequence
	//*************************************************************************

	/**
	 * Constructs an ASN1Sequence.
	 */
	public ASN1Sequence()
	{
		this(5);
	}

	/**
	 * Constructs an ASN1Sequence.
	 *
	 * @param size Specifies the initial size of the collection.
	 */
	public ASN1Sequence(int size)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SEQUENCE);
		content = new Vector(size);
	}

	/**
	 * Constructs an ASN1Sequence object by decoding data from an input
	 * stream.
	 */
	public ASN1Sequence(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SEQUENCE);
		decodeStructured(dec, in, len);
	}

	//*************************************************************************
	// ASN1Sequence specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString("SEQUENCE: { ");
	}

}


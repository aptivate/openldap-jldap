
package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1Integer extends ASN1Numeric {

	//*************************************************************************
	// Constructors for ASN1Integer
	//*************************************************************************

	/**
	 * Constructs an ASN1Integer object using an int value.
	 */
	public ASN1Integer(int content)
	{
		this((long)content);
	}

	/**
	 * Constructs an ASN1Integer object using a long value.
	 */
	public ASN1Integer(long content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, INTEGER);
		this.content = new Long(content);
	}

	/**
	 * Constructs an ASN1Integer object by decoding data from an input stream.
	 */
	public ASN1Integer(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, INTEGER);
		content = (Long)dec.decodeNumeric(in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1Integer directly to an output
	 * stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1Integer specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "INTEGER: " + content;
	}

}


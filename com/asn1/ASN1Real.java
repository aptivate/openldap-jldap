
package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1Real extends ASN1Simple {

	private Double content;

	//*************************************************************************
	// Constructors for ASN1Real
	//*************************************************************************

	/**
	 * Constructs an ASN1Real object using a REAL value.
	 */
	public ASN1Real(Double content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, REAL);
		this.content = content;
	}

	/**
	 * Constructs an ASN1Real object by decoding data from an input stream.
	 */
	public ASN1Real(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, REAL);
		content = (Double)dec.decodeReal(in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this Real directly to an output stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1Real specific methods
	//*************************************************************************

	/**
	 * Returns an object representing this ASN1Real value.
	 */
	public Double getContent()
	{
		return content;
	}

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "REAL: "; // finish this
	}

}



package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1BitString extends ASN1Simple {

	private byte[] content;

	//*************************************************************************
	// Constructors for ASN1BitString
	//*************************************************************************

	/**
	 * Constructs an ASN1BitString object using a byte array value.
	 */
	public ASN1BitString(byte[] content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, BIT_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1BitString object by decoding data from an input
	 * stream.
	 */
	public ASN1BitString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, BIT_STRING);
		content = (byte[])dec.decodeBitString(in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1BitString directly to an output stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1BitString specific methods
	//*************************************************************************

	/**
	 * Returns the BIT_STRING value stored in this ASN1BitString as a byte[].
	 */
	public byte[] getContent()
	{
		return content;
	}

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "BIT STRING: "; // finish this
	}

}



package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1ObjectIdentifier extends ASN1Simple {

	private byte[] content;

	//*************************************************************************
	// Constructors for ASN1ObjectIdentifier
	//*************************************************************************

	/**
	 * Constructs an ASN1ObjectIdentifier object using an OBJECT_IDENTIFIER
	 * value.
	 */
	public ASN1ObjectIdentifier(byte[] content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     OBJECT_IDENTIFIER);
		this.content = content;
	}

	/**
	 * Constructs an ASN1ObjectIdentifier object by decoding data from an input
	 * stream.
	 */
	public ASN1ObjectIdentifier(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     OBJECT_IDENTIFIER);
		content = (byte[])dec.decodeObjectIdentifier(in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1ObjectIdentifier directly to an output
	 * stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1ObjectIdentifier specific methods
	//*************************************************************************

	/**
	 * Returns an object representing this ASN1ObjectIdentifier value.
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
		return super.toString() + "OBJECT IDENTIFIER: "; // finish this
	}

}


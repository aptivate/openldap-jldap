
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1OctetString extends ASN1Simple {

	private byte[] content;

	//*************************************************************************
	// Constructors for ASN1OctetString
	//*************************************************************************

	/**
	 * Constructs an ASN1OctetString object using a byte array value.
	 */
	public ASN1OctetString(byte[] content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     OCTET_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1OctetString object using a String value.
	 */
	public ASN1OctetString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     OCTET_STRING);
		this.content = content.getBytes();
	}

	/**
	 * Constructs an ASN1OctetString object by decoding data from an input
	 * stream.
	 */
	public ASN1OctetString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     OCTET_STRING);
		content = (byte[])dec.decodeOctetString(in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1OctetString directly to an output
	 * stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1OctetString specific methods
	//*************************************************************************

	/**
	 * Returns an object representing this ASN1OctetString value.
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
		return super.toString() + "OCTET STRING: " + new String(content);
	}

}


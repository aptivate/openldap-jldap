
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1Boolean extends ASN1Simple {

	private boolean content;

	//*************************************************************************
	// Constructors for ASN1Boolean
	//*************************************************************************

	/**
	 * Constructs an ASN1Boolean object using a boolean value.
	 */
	public ASN1Boolean(boolean content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, BOOLEAN);
		this.content = content;
	}

	/**
	 * Constructs an ASN1Boolean object by decoding data from an input stream.
	 */
	public ASN1Boolean(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, BOOLEAN);
		content = ((Boolean)dec.decodeBoolean(in, len)).booleanValue();
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1Boolean directly to an output stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1Boolean specific methods
	//*************************************************************************

	/**
	 * Returns the BOOLEAN value stored in this ASN1Boolean as a boolean.
	 * BOOLEAN     - Generic ASN.1 type.
	 * ASN1Boolean - Java representation of ASN.1 type.
	 * boolean     - Implementation of Generic type.
	 */
	public boolean getContent()
	{
		return content;
	}

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "BOOLEAN: " + content;
	}

}



package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1IA5String extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1IA5String
	//*************************************************************************

	/**
	 * Constructs an ASN1IA5String object using an IA5_STRING value.
	 */
	public ASN1IA5String(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, IA5_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1IA5String object by decoding data from an input
	 * stream.
	 */
	public ASN1IA5String(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, IA5_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1IA5String specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "IA5String: " + content;
	}

}


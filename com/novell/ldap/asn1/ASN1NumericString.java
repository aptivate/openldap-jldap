
package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1NumericString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1NumericString
	//*************************************************************************

	/**
	 * Constructs an ASN1NumericString object using a String value.
	 */
	public ASN1NumericString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     NUMERIC_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1NumericString object by decoding data from an input
	 * stream.
	 */
	public ASN1NumericString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     NUMERIC_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1NumericString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "NumericString: " + content;
	}

}


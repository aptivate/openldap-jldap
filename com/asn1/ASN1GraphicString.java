
package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1GraphicString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1GraphicString
	//*************************************************************************

	/**
	 * Constructs an ASN1GraphicString object using a String value.
	 */
	public ASN1GraphicString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     GRAPHIC_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1GraphicString object by decoding data from an input
	 * stream.
	 */
	public ASN1GraphicString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     GRAPHIC_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1GraphicString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "GraphicString: " + content;
	}

}



package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1GeneralString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1GeneralString
	//*************************************************************************

	/**
	 * Constructs an ASN1GeneralString object using a String value.
	 */
	public ASN1GeneralString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     GENERAL_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1GeneralString object by decoding data from an input
	 * stream.
	 */
	public ASN1GeneralString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     GENERAL_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1GeneralString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "GeneralString: " + content;
	}

}


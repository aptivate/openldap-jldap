
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1TeletexString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1TeletexString
	//*************************************************************************

	/**
	 * Constructs an ASN1TeletexString object using a String value.
	 */
	public ASN1TeletexString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     TELETEX_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1TeletexString object by decoding data from an input
	 * stream.
	 */
	public ASN1TeletexString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     TELETEX_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1TeletexString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "TeletexString: " + content;
	}

}


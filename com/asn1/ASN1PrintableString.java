
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1PrintableString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1PrintableString
	//*************************************************************************

	/**
	 * Constructs an ASN1PrintableString object using a String value.
	 */
	public ASN1PrintableString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     PRINTABLE_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1PrintableString object by decoding data from an input
	 * stream.
	 */
	public ASN1PrintableString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     PRINTABLE_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1PrintableString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "PrintableString: " + content;
	}

}


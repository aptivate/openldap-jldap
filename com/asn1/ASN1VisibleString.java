
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1VisibleString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1VideotexString
	//*************************************************************************

	/**
	 * Constructs an ASN1VisibleString object using a String value.
	 */
	public ASN1VisibleString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     VISIBLE_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1VisibleString object by decoding data from an input
	 * stream.
	 */
	public ASN1VisibleString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     VISIBLE_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1VisibleString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "VisibleString: " + content;
	}

}



package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public class ASN1VideotexString extends ASN1CharacterString {

	//*************************************************************************
	// Constructors for ASN1VideotexString
	//*************************************************************************

	/**
	 * Constructs an ASN1VideotexString object using a String value.
	 */
	public ASN1VideotexString(String content)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     VIDEOTEX_STRING);
		this.content = content;
	}

	/**
	 * Constructs an ASN1VideotexString object by decoding data from an input
	 * stream.
	 */
	public ASN1VideotexString(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false,
			                     VIDEOTEX_STRING);
		content = (String)dec.decodeCharacterString(in, len);
	}

	//*************************************************************************
	// ASN1VideotexString specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "VideotexString: " + content;
	}

}


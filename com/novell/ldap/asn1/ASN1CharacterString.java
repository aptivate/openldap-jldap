
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
abstract class ASN1CharacterString extends ASN1Simple {

	protected String content;

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes the contents of this ASN1CharacterString directly to an output
	 * stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1CharacterString specific methods
	//*************************************************************************

	/**
	 * Returns the String representing this ASN1CharacterString value.
	 */
	public String getContent()
	{
		return content;
	}

}



package com.novell.asn1;

import java.io.*;

/**
 *
 */
public class ASN1Null extends ASN1Simple {

	//*************************************************************************
	// Constructors for ASN1Null
	//*************************************************************************

	/**
	 * Constructs an ASN1Null object.
	 */
	public ASN1Null()
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, NULL);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encodes this Null directly to an output stream.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		enc.encode(this, out);
	}

	//*************************************************************************
	// ASN1Null specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString() + "NULL: \"\"";
	}

}


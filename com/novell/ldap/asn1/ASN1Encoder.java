
package org.ietf.asn1;

import java.io.*;

/**
 *
 */
public interface ASN1Encoder extends Serializable {

	//*************************************************************************
	// Encoders for ASN.1 simple types
	//*************************************************************************

	/**
	 * Encode an ASN1Boolean directly to a stream.
	 */
	public void encode(ASN1Boolean b, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1Numeric directly to a stream.
	 *
	 * Use a two's complement representation in the fewest number of octets
	 * possible.
	 *
	 * Will encode INTEGER and ENUMERATED values.
	 */
	public void encode(ASN1Numeric n, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1Real directly to a stream.
	 */
	public void encode(ASN1Real r, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1Null directly to a stream.
	 */
	public void encode(ASN1Null n, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1BitString directly to a stream.
	 */
	public void encode(ASN1BitString bs, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1OctetString directly to a stream.
	 */
	public void encode(ASN1OctetString os, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1ObjectIdentifier directly to a stream.
	 */
	public void encode(ASN1ObjectIdentifier oi, OutputStream out)
		throws IOException;

	/**
	 * Encode an ASN1CharacterString directly to a stream.
	 */
	public void encode(ASN1CharacterString cs, OutputStream out)
		throws IOException;

	//*************************************************************************
	// Encoder for ASN.1 structured types
	//*************************************************************************

	/**
	 * Encode an ASN1Structured directly to a stream.
	 */
	public void encode(ASN1Structured c, OutputStream out)
		throws IOException;

	//*************************************************************************
	// Encoders for ASN.1 useful types
	//*************************************************************************

	//*************************************************************************
	// Encoder for ASN.1 Identifier
	//*************************************************************************

	public void encodeIdentifier(ASN1Identifier id, OutputStream out)
		throws IOException;
}

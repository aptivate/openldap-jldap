
package com.novell.asn1;

import java.io.*;
import java.util.Vector;

/**
 * The ASN1Tagged class can hold a base ASN1Object with a distinctive tag
 * describing the type of that base object. It also maintains a boolean value
 * indicating whether the value should be encoded by EXPLICIT or IMPLICIT
 * means. (Explicit is true by default.)
 *
 * If the type is encoded IMPLICITLY, the base types form, length and content
 * will be encoded as usual along with the class type and tag specified in
 * the constructor of this ASN1Tagged class.
 *
 * If the type is to be encoded EXPLICITLY, the base type will be encoded as
 * usual after the ASN1Tagged identifier has been encoded.
 */
public class ASN1Tagged extends ASN1Object {

	private boolean explicit;
	private ASN1Object content;

	//*************************************************************************
	// Constructors for ASN1Tagged
	//*************************************************************************

	/**
	 * Constructs an ASN1Tagged object.
	 *
	 * The explicit flag defaults to true as per the spec.
	 */
	public ASN1Tagged(ASN1Identifier identifier, ASN1Object object)
	{
		this(identifier, object, true);
	}

	/**
	 * Constructs an ASN1Tagged object.
	 */
	public ASN1Tagged(ASN1Identifier identifier, ASN1Object object,
		               boolean explicit)
	{
		this.id = identifier;
		this.content = object;
		this.explicit = explicit;

		if(!explicit) {
			content.setIdentifier(id); // replace object's id with new tag.
		}

	}

	/**
	 * Constructs an ASN1Tagged object by decoding data from an input stream.
	 */
	public ASN1Tagged(ASN1Decoder dec, InputStream in, int len,
		               ASN1Identifier identifier)
		throws IOException
	{
		this.id = identifier;

		// If we are decoding an implicit tag, there is no way to know at this
		// low level what the base type really is. We can place the content
		// into an ASN1OctetString type and pass it back to the application who
		// will be able to create the appropriate ASN.1 type for this tag.
		content = new ASN1OctetString(dec, in, len);
	}

	//*************************************************************************
	// ASN1Object implementation
	//*************************************************************************

	/**
	 * Encode this ASN1Object directly to a stream.
	 *
	 * @param out The stream into which the encoding will go.
	 */
	public void encode(ASN1Encoder enc, OutputStream out)
		throws IOException
	{
		if(explicit) {
			enc.encodeIdentifier(id, out);
		}

		content.encode(enc, out);
	}

	//*************************************************************************
	// ASN1Tagged specific methods
	//*************************************************************************

	/**
	 * Returns the Tagged value stored in this ASN1Tagged.
	 */
	public ASN1Object getContent()
	{
		return content;
	}

	/**
	 * Returns a boolean value indicating if this object uses
	 * EXPLICIT tagging.
	 */
	public boolean isExplicit()
	{
		return explicit;
	}

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		if(explicit) {
			return super.toString() + content.toString();
		}
		else { // implicit tagging
			return content.toString();
		}
	}

}


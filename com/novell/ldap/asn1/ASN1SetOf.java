
package org.ietf.asn1;

import java.io.*;
import java.util.*;

/**
 * The ASN1SetOf class can hold an unordered collection of components with
 * identical type.
 */
public class ASN1SetOf extends ASN1Structured {

	//*************************************************************************
	// Constructors for ASN1SetOf
	//*************************************************************************

	/**
	 * Constructs an ASN1SetOf object.
	 */
	public ASN1SetOf()
	{
		this(5);
	}

	/**
	 * Constructs an ASN1SetOf object.
	 *
	 * @param size Specifies the initial size of the collection.
	 */
	public ASN1SetOf(int size)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SET_OF);
		content = new Vector(size);
	}

	/**
	 * A copy constructor which creates an ASN1SetOf from an
	 * instance of ASN1Set.
	 *
	 * Since SET and SET_OF have the same identifier, the decoder
	 * will always return a SET object when it detects that identifier.
	 * In order to take advantage of the ASN1SetOf type, we need to be
	 * able to construct this object when knowingly receiving an
	 * ASN1Set.
	 */
	public ASN1SetOf(ASN1Set set)
	{
		id = new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, SET_OF);
		content = new Vector(set.size());
		Enumeration e = set.elements();
		while(e.hasMoreElements()) {
			add((ASN1Object)e.nextElement());
		}
	}

	//*************************************************************************
	// ASN1SetOf specific methods
	//*************************************************************************

	/**
	 * Return a String representation of this ASN1Object.
	 */
	public String toString()
	{
		return super.toString("SET OF: { ");
	}

}


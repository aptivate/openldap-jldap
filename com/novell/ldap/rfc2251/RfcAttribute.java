
package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *       Attribute ::= SEQUENCE {
 *               type    AttributeDescription,
 *               vals    SET OF AttributeValue }
 */
public class Attribute extends ASN1Sequence {

	/**
	 *
	 */
	public Attribute(AttributeDescription type, ASN1SetOf vals) {
		super(2);
		setType(type);
		setVals(vals);
	}

	/**
	 *
	 */
	public Attribute(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	/**
	 *
	 */
	public void setType(AttributeDescription type)
	{
		set(0, type);
	}

	/**
	 *
	 */
	public void setVals(ASN1SetOf vals)
	{
		set(1, vals);
	}

	/**
	 *
	 */
	public AttributeDescription getType()
	{
		return (AttributeDescription)get(0);
	}

	/**
	 *
	 */
	public ASN1SetOf getVals()
	{
		return (ASN1SetOf)get(1);
	}
}


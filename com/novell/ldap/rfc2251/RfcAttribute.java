
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       Attribute ::= SEQUENCE {
 *               type    AttributeDescription,
 *               vals    SET OF AttributeValue }
 */
public class RfcAttribute extends ASN1Sequence {

	/**
	 *
	 */
	public RfcAttribute(RfcAttributeDescription type, ASN1SetOf vals) {
		super(2);
		setType(type);
		setVals(vals);
	}

	/**
	 *
	 */
	public RfcAttribute(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	/**
	 *
	 */
	public void setType(RfcAttributeDescription type)
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
	public RfcAttributeDescription getType()
	{
		return (RfcAttributeDescription)get(0);
	}

	/**
	 *
	 */
	public ASN1SetOf getVals()
	{
		return (ASN1SetOf)get(1);
	}
}


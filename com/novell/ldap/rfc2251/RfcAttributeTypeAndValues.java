
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *       AttributeTypeAndValues ::= SEQUENCE {
 *               type    AttributeDescription,
 *               vals    SET OF AttributeValue }
 */
public class AttributeTypeAndValues extends ASN1Sequence {

	//*************************************************************************
	// Constructor for AttributeTypeAndValues
	//*************************************************************************

	/**
	 *
	 */
	public AttributeTypeAndValues(AttributeDescription type, ASN1SetOf vals)
	{
		super(2);
		add(type);
		add(vals);
	}

}


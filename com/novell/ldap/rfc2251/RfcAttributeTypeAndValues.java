
package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       AttributeTypeAndValues ::= SEQUENCE {
 *               type    AttributeDescription,
 *               vals    SET OF AttributeValue }
 */
public class RfcAttributeTypeAndValues extends ASN1Sequence {

	//*************************************************************************
	// Constructor for AttributeTypeAndValues
	//*************************************************************************

	/**
	 *
	 */
	public RfcAttributeTypeAndValues(RfcAttributeDescription type, ASN1SetOf vals)
	{
		super(2);
		add(type);
		add(vals);
	}

}



package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *
 */
public class AttributeDescription extends RfcLDAPString {

	/**
	 *
	 */
	public AttributeDescription(String s)
	{
		super(s);
	}

	/**
	 *
	 */
	public AttributeDescription(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

}


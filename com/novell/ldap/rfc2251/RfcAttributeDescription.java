
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *
 */
public class AttributeDescription extends LDAPString {

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


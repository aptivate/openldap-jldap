
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *
 */
public class RfcAttributeDescription extends RfcLDAPString {

	/**
	 *
	 */
	public RfcAttributeDescription(String s)
	{
		super(s);
	}

	/**
	 *
	 */
	public RfcAttributeDescription(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

}



package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *
 */
public class MessageID extends ASN1Integer {

	private static int messageID;

	/**
	 *
	 */
	public MessageID()
	{
		super(++messageID); // should never exceed 2^31 - 1
	}

	/**
	 *
	 */
	public MessageID(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

}


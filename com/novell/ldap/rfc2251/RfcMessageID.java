
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *       MessageID ::= INTEGER (0 .. maxInt)
 *
 *       maxInt INTEGER ::= 2147483647 -- (2^^31 - 1) --
 */
public class MessageID extends ASN1Integer {

	private static int messageID;

	/**
	 * Creates a MessageID with an auto incremented ASN1Integer value.
	 */
	public MessageID()
	{
		super(++messageID); // should never exceed 2^31 - 1
	}

	/**
	 * Creates a MessageID with a specified int value.
	 */
	public MessageID(int i)
	{
		super(i);
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



package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *        MatchingRuleId ::= LDAPString
 */
public class MatchingRuleId extends LDAPString {

	/**
	 *
	 */
	public MatchingRuleId(String s)
	{
		super(s);
	}

	/**
	 *
	 */
	public MatchingRuleId(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

}


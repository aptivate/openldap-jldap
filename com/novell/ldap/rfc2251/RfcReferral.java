
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
 *        Referral ::= SEQUENCE OF LDAPURL
 */
public class Referral extends ASN1SequenceOf {

	//*************************************************************************
	// Constructor for Referral
	//*************************************************************************

	/**
	 * The only time a Referral object is constructed, is when we are
	 * decoding an LDAPResult or COMPONENTS OF LDAPResult.
	 */
	public Referral(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);

		//convert from ASN1OctetString to LDAPURL here (then look at
		// LDAPResponse.getReferrals())
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	// inherited from SequenceOf

}


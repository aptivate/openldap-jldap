
package com.novell.ldap.protocol;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *        Referral ::= SEQUENCE OF LDAPURL
 */
public class Referral extends ASN1SequenceOf {

	//*************************************************************************
	// Constructor for Referral
	//*************************************************************************

	/**
	 * The only time a Referral object is constructed, is when we are
	 * decoding an RfcLDAPResult or COMPONENTS OF RfcLDAPResult.
	 */
	public Referral(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);

		//convert from ASN1OctetString to RfcLDAPURL here (then look at
		// LDAPResponse.getReferrals())
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	// inherited from SequenceOf

}


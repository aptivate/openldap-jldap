
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/**
 * This interface represents RfcLDAPMessages that contain responses from a
 * server. If the protocol operation of the RfcLDAPMessage is of this type,
 * it contains at least an RfcLDAPResult.
 */
public interface RfcResponse {

	/**
	 *
	 */
	public ASN1Enumerated getResultCode();

	/**
	 *
	 */
	public RfcLDAPDN getMatchedDN();

	/**
	 *
	 */
	public RfcLDAPString getErrorMessage();

	/**
	 *
	 */
	public RfcReferral getReferral();

}



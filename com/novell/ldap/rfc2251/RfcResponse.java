
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 * This interface represents LDAPMessages that contain responses from a
 * server. If the protocol operation of the LDAPMessage is of this type,
 * it contains at least an LDAPResult.
 */
public interface Response {

	/**
	 *
	 */
	public ASN1Enumerated getResultCode();

	/**
	 *
	 */
	public LDAPDN getMatchedDN();

	/**
	 *
	 */
	public LDAPString getErrorMessage();

	/**
	 *
	 */
	public Referral getReferral();

}



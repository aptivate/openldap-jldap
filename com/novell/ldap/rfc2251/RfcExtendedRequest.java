
package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

/**
 *       ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *               requestName      [0] LDAPOID,
 *               requestValue     [1] OCTET STRING OPTIONAL }
 */
public class RfcExtendedRequest extends ASN1Sequence implements RfcRequest {

	/**
	 * Context-specific TAG for optional requestName.
	 */
	public final static int REQUEST_NAME = 0;
	/**
	 * Context-specific TAG for optional requestValue.
	 */
	public final static int REQUEST_VALUE = 1;

	//*************************************************************************
	// Constructors for ExtendedRequest
	//*************************************************************************

	/**
	 * Constructs an extended request.
	 *
	 * @param requestName The OID for this extended operation.
	 */
	public RfcExtendedRequest(RfcLDAPOID requestName)
	{
		this(requestName, null);
	}

	/**
	 * Constructs an extended request.
	 *
	 * @param requestName The OID for this extended operation.
	 * @param requestValue An optional request value.
	 */
	public RfcExtendedRequest(RfcLDAPOID requestName, ASN1OctetString requestValue)
	{
		super(2);
		add(new ASN1Tagged(
			new ASN1Identifier(ASN1Identifier.CONTEXT, false, REQUEST_NAME),
			                   requestName, false));
		if(requestValue != null)
			add(new ASN1Tagged(
				new ASN1Identifier(ASN1Identifier.CONTEXT, false, REQUEST_VALUE),
										 requestValue, false));
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 23.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
			                       RfcProtocolOp.EXTENDED_REQUEST);
	}

    public RfcRequest dupRequest(String base, String filter, Integer scope)
            throws LDAPException
    {
        throw new LDAPException(
                    LDAPExceptionMessageResource.NO_DUP_REQUEST,
                    new Object[] { "extended" },
                    LDAPException.LDAP_NOT_SUPPORTED);
    }
}

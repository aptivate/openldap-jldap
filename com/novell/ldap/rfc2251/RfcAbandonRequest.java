
package com.novell.ldap.protocol;

import com.novell.ldap.asn1.*;

/**
 *       AbandonRequest ::= [APPLICATION 16] MessageID
 */
public class RfcAbandonRequest extends RfcMessageID implements RfcRequest {

	//*************************************************************************
	// Constructor for AbandonRequest
	//*************************************************************************

	/**
	 *
	 */
	public RfcAbandonRequest(int msgId)
	{
		super(msgId);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 16. (0x50)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
			                       RfcProtocolOp.ABANDON_REQUEST);
	}

}


/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.rfc2251;

import java.io.IOException;
import java.io.InputStream;
import com.novell.ldap.asn1.*;

/**
 *       Attribute ::= SEQUENCE {
 *               type    AttributeDescription,
 *               vals    SET OF AttributeValue }
 */
public class RfcAttribute extends ASN1Sequence {

	/**
	 *
	 */
	public RfcAttribute(RfcAttributeDescription type, ASN1SetOf vals) {
		super(2);
		setType(type);
		setVals(vals);
	}

	/**
	 *
	 */
	public RfcAttribute(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	/**
	 *
	 */
	public void setType(RfcAttributeDescription type)
	{
		set(0, type);
	}

	/**
	 *
	 */
	public void setVals(ASN1SetOf vals)
	{
		set(1, vals);
	}

	/**
	 *
	 */
	public RfcAttributeDescription getType()
	{
		return (RfcAttributeDescription)get(0);
	}

	/**
	 *
	 */
	public ASN1SetOf getVals()
	{
		return (ASN1SetOf)get(1);
	}
}


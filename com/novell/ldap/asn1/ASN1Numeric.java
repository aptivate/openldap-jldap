
package com.novell.asn1;

import java.io.*;

/**
 *
 */
abstract class ASN1Numeric extends ASN1Simple {

	protected Long content;

	//*************************************************************************
	// ASN1Numeric specific methods
	//*************************************************************************

	/**
	 * Returns the INTEGER value stored in this ASN1Integer as an int.
	 * INTEGER     - Generic ASN.1 type.
	 * ASN1Integer - Java representation of ASN.1 type.
	 * int         - Implementation of Generic type.
	 */
	public int getInt()
	{
		return (int)getLong();
	}

	/**
	 * Returns the INTEGER value stored in this ASN1Integer as a long.
	 * INTEGER     - Generic ASN.1 type.
	 * ASN1Integer - Java representation of ASN.1 type.
	 * long        - Implementation of Generic type.
	 */
	public long getLong()
	{
		return content.longValue();
	}

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcControls.java,v 1.7 2000/11/09 23:50:55 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/

package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       Controls ::= SEQUENCE OF Control
 */
public class RfcControls extends ASN1SequenceOf {

   /**
    * Controls context specific tag
    */
   public final static int CONTROLS = 0;

	//*************************************************************************
	// Constructors for Controls
	//*************************************************************************

	/**
	 * Constructs a Controls object. This constructor is used in combination
	 * with the add() method to construct a set of Controls to send to the
	 * server.
	 */
	public RfcControls()
	{
		super(5);
	}

	/**
	 * Constructs a Controls object by decoding it from an InputStream.
	 */
	public RfcControls(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);

		// Convert each SEQUENCE element to a Control
		for(int i=0; i < size(); i++) {
            RfcControl tempControl = new RfcControl((ASN1Sequence)get(i));
            set (i, tempControl);
		}
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	/**
	 * Override add() of ASN1SequenceOf to only accept a Control type.
	 */
	public void add(RfcControl control)
	{
		
		super.add((ASN1Object)control);
	}

	/**
	 * Override set() of ASN1SequenceOf to only accept a Control type.
	 */
	public void set(int index, RfcControl control)
	{
		super.set(index, control);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return a context specific id.
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.CONTEXT, true, CONTROLS);
	}

}


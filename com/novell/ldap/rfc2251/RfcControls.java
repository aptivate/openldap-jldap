
package com.novell.asn1.ldap;

import com.novell.asn1.*;

/**
 *       Controls ::= SEQUENCE OF Control
 */
public class Controls extends ASN1SequenceOf {

	//*************************************************************************
	// Constructors for Controls
	//*************************************************************************

	/**
	 *
	 */
	public Controls()
	{
		super(5);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	/**
	 * Override add() of ASN1SequenceOf to only accept Control.
	 */
	public void add(Control control)
	{
		add(control);
	}

	/**
	 * Override set() of ASN1SequenceOf to only accept Control.
	 */
	public void set(int index, Control control)
	{
		set(index, control);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

}



package com.novell.asn1;

abstract class ASN1Simple extends ASN1Object {

	/**
	 * Universal, simple type, TAGS.
	 */
	public static final int BOOLEAN           = 0x01;
	public static final int INTEGER           = 0x02;
	public static final int BIT_STRING        = 0x03;
	public static final int OCTET_STRING      = 0x04;
	public static final int NULL              = 0x05;
	public static final int OBJECT_IDENTIFIER = 0x06;
	public static final int REAL              = 0x09;
	public static final int ENUMERATED        = 0x0a;
	public static final int NUMERIC_STRING    = 0x12;
	public static final int PRINTABLE_STRING  = 0x13;
	public static final int TELETEX_STRING    = 0x14;
	public static final int T61_STRING        = TELETEX_STRING;
	public static final int VIDEOTEX_STRING   = 0x15;
	public static final int IA5_STRING        = 0x16;
	public static final int GRAPHIC_STRING    = 0x19;
	public static final int VISIBLE_STRING    = 0x1a;
	public static final int ISO646_STRING     = VISIBLE_STRING;
	public static final int GENERAL_STRING    = 0x1b;

}


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
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.rfc2251;

import java.io.UnsupportedEncodingException;

import com.novell.ldap.asn1.ASN1Identifier;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Tagged;

/**
 * Represents an [0] LDAP DN OPTIONAL used as newSuperior attribute of
 * ModifyDNRequest (For more detail on this Syntax refer to rfc2251).
 */
public class RfcLDAPSuperDN extends ASN1Tagged {

	private byte[] content;
	
	/**
     * ASN.1 [0] LDAP DN OPTIONAL tag definition.
     */
    public static final int TAG = 0x00;

    /**
     * ID is added for Optimization.
     * <p>Id needs only be one Value for every instance,
     * thus we create it only once.</p>
     */
    protected static final ASN1Identifier ID =
         new ASN1Identifier(ASN1Identifier.CONTEXT, false, TAG);
   
    /**
     * Call this constructor to construct an RfcLDAPSuperDN
     * object from a String object.
     *
     * @param content A string value that will be contained
     * in the this RfcLDAPSuperDN object
     */
	public RfcLDAPSuperDN(String s) {
		super(ID, new ASN1OctetString(s), false); //type is encoded IMPLICITLY
		try {
            this.content = s.getBytes("UTF8");
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException( uee.toString());
        }
	}

	/**
     * Call this constructor to construct an RfcLDAPSuperDN
     * object from a byte array.
     *
     * @param content A byte array representing the string that
     * will be contained in the this RfcLDAPSuperDN object
     */
	public RfcLDAPSuperDN(byte[] ba) {
		super(ID, new ASN1OctetString(ba), false); //type is encoded IMPLICITLY 
		this.content = ba;
	}
	
	/**
     * Returns the content of this RfcLDAPSuperDN as a byte array.
     */
    public final byte[] byteValue()
    {
        return content;
    }

    /**
     * Returns the content of this RfcLDAPSuperDN as a String.
     */
    public final String stringValue()
    {
        String s = null;
        try {
            s = new String(content, "UTF8");
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException( uee.toString());
        }
        return s;
    }

    /**
     * Return a String representation of this ASN1Object.
     */
    public String toString()
    {
      	return super.toString();
    }
}

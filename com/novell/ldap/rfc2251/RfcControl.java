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

import java.io.IOException;
import java.io.InputStream;
import com.novell.ldap.asn1.*;

/**
 * Represents an LDAP Control.
 *
 *<pre>
 *        Control ::= SEQUENCE {
 *               controlType             LDAPOID,
 *               criticality             BOOLEAN DEFAULT FALSE,
 *               controlValue            OCTET STRING OPTIONAL }
 *</pre>
 */
public class RfcControl extends ASN1Sequence {

    //*************************************************************************
    // Constructors for Control
    //*************************************************************************

    /**
     *
     */
    public RfcControl(RfcLDAPOID controlType)
    {
        this(controlType, new ASN1Boolean(false), null);
    }

    /**
     *
     */
    public RfcControl(RfcLDAPOID controlType, ASN1Boolean criticality)
    {
        this(controlType, criticality, null);
    }

    /**
     *
     * Note: criticality is only added if true, as per RFC 2251 sec 5.1 part
     *       (4): If a value of a type is its default value, it MUST be
     *       absent.
     */
    public RfcControl(RfcLDAPOID controlType, ASN1Boolean criticality,
                    ASN1OctetString controlValue)
    {
        super(3);
        add(controlType);
        if(criticality.booleanValue() == true)
            add(criticality);
        if(controlValue != null)
            add(controlValue);
    }

    /**
     * Constructs a Control object by decoding it from an InputStream.
     */
    public RfcControl(ASN1Decoder dec, InputStream in, int len)
        throws IOException
    {
        super(dec, in, len);
    }

    /**
     * Constructs a Control object by decoding from an ASN1Sequence
     */
    public RfcControl(ASN1Sequence seqObj)
        throws IOException
    {
        super(3);
        int len = seqObj.size();
        for (int i = 0; i < len; i++)
            add(seqObj.get(i));
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     *
     */
    public final ASN1OctetString getControlType()
    {
        return (ASN1OctetString)get(0);
    }

    /**
     * Returns criticality.
     *
     * If no value present, return the default value of FALSE.
     */
    public final ASN1Boolean getCriticality()
    {
        if(size() > 1) { // MAY be a criticality
            ASN1Object obj = get(1);
            if(obj instanceof ASN1Boolean)
                return (ASN1Boolean)obj;
        }

        return new ASN1Boolean(false);
    }

    /**
     * Since controlValue is an OPTIONAL component, we need to check
     * to see if one is available. Remember that if criticality is of default
     * value, it will not be present.
     */
    public final ASN1OctetString getControlValue()
    {
        if(size() > 2) { // MUST be a control value
            return (ASN1OctetString)get(2);
        }
        else if(size() > 1) { // MAY be a control value
            ASN1Object obj = get(1);
            if(obj instanceof ASN1OctetString)
                return (ASN1OctetString)obj;
        }
        return null;

    }

   /**
     * Called to set/replace the ControlValue.  Will normally be called by
     * the child classes after the parent has been instantiated.
     */
    public final void setControlValue(ASN1OctetString controlValue)
    {

        if (controlValue == null)
            return;

        if(size() == 3) {
            // We already have a control value, replace it
            set(2, controlValue);
            return;

        }

        if (size() == 2) {

            // Get the second element
            ASN1Object obj = get(1);

            // Is this a control value
            if(obj instanceof ASN1OctetString) {

                // replace this one
                set(1, controlValue);
                return;
            }
            else {
                // add a new one at the end
                add(controlValue);
                return;
            }
        }
		else if (size() ==1 )
		{ // in case iscritical is false 
						// add a new one at the end
						add(controlValue);
						return;
		}
    }
}

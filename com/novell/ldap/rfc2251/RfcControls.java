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
 * Represents LDAP Contreols.
 *
 *</pre>
 *       Controls ::= SEQUENCE OF Control
 *<pre>
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
    public final void add(RfcControl control)
    {
        super.add(control);
    }

    /**
     * Override set() of ASN1SequenceOf to only accept a Control type.
     */
    public final void set(int index, RfcControl control)
    {
        super.set(index, control);
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return a context specific id.
     */
    public final ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.CONTEXT, true, CONTROLS);
    }
}

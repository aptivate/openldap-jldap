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

package com.novell.ldap.controls;

import java.io.IOException;
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *  LDAPSortResponse - will be added in newer version of LDAP
 *  Controls draft-- add descritption from draft here.
 */
public class LDAPSortResponse extends LDAPControl
{

    private String failedAttribute;
    private int resultCode;

    /**
     * This constructor is usually called by the SDK to instantiate an
     * a LDAPControl corresponding to the Server response to a LDAP
     * Sort Control request.  Application programmers should not have
     * any reason to call the constructor.  This constructor besides
     * constructing a LDAPControl object parses the contents of the response
     * control.
     * <br>
     * RFC 2891 defines this response control as follows:
     *
     * The controlValue is an OCTET STRING, whose
     * value is the BER encoding of a value of the following SEQUENCE:

     * SortResult ::= SEQUENCE {
         sortResult  ENUMERATED {
             success                   (0), -- results are sorted
             operationsError           (1), -- server internal failure
             timeLimitExceeded         (3), -- timelimit reached before
                                            -- sorting was completed
             strongAuthRequired        (8), -- refused to return sorted
                                            -- results via insecure
                                            -- protocol
             adminLimitExceeded       (11), -- too many matching entries
                                            -- for the server to sort
             noSuchAttribute          (16), -- unrecognized attribute
                                            -- type in sort key
             inappropriateMatching    (18), -- unrecognized or
                                            -- inappropriate matching
                                            -- rule in sort key
             insufficientAccessRights (50), -- refused to return sorted
                                            -- results to this client
             busy                     (51), -- too busy to process
             unwillingToPerform       (53), -- unable to sort
             other                    (80)
             },
         attributeType [0] AttributeDescription OPTIONAL }
     *
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param values     The control-specific data.
     */
    public LDAPSortResponse(String oid, boolean critical, byte[] values)
                    throws IOException
    {
        super(oid, critical, values);

        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

		// We should get back an enumerated type
        ASN1Object asnObj = decoder.decode(values);

        if ( (asnObj == null) || (!(asnObj instanceof ASN1Sequence)) )
            throw new IOException("Decoding error");

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.controls, "LDAPSortResponse controlvalue =" + asnObj.toString());
        }

        ASN1Object asn1Enum = ((ASN1Sequence)asnObj).get(0);
        if ( (asn1Enum != null) && (asn1Enum instanceof ASN1Enumerated) )
             resultCode =((ASN1Enumerated)asn1Enum).intValue();

        // Second element is the attributeType
        if ( ((ASN1Sequence)asnObj).size() > 1) {
            ASN1Object asn1String = ((ASN1Sequence)asnObj).get(1);
            if ( (asn1String != null) && (asn1String instanceof ASN1OctetString) )
                failedAttribute  = ((ASN1OctetString)asn1String).stringValue();
        }
        return;
    }

    /**
     *  If not null, this returns the attribute that caused the sort
     *  operation to fail.
     */
    public String getFailedAttribute()
    {
        return failedAttribute;
    }

    /**
     * Returns the result code from the sort
     */
    public int getResultCode()
    {
        return resultCode;
    }
}


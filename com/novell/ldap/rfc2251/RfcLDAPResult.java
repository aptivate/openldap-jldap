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
import java.io.ByteArrayInputStream;
import com.novell.ldap.asn1.*;

/**
 * Represents an LDAPResult.
 *
 *<pre>
 *        LDAPResult ::= SEQUENCE {
 *               resultCode      ENUMERATED {
 *                            success                      (0),
 *                            operationsError              (1),
 *                            protocolError                (2),
 *                            timeLimitExceeded            (3),
 *                            sizeLimitExceeded            (4),
 *                            compareFalse                 (5),
 *                            compareTrue                  (6),
 *                            authMethodNotSupported       (7),
 *                            strongAuthRequired           (8),
 *                                      -- 9 reserved --
 *                            referral                     (10),  -- new
 *                            adminLimitExceeded           (11),  -- new
 *                            unavailableCriticalExtension (12),  -- new
 *                            confidentialityRequired      (13),  -- new
 *                            saslBindInProgress           (14),  -- new
 *                            noSuchAttribute              (16),
 *                            undefinedAttributeType       (17),
 *                            inappropriateMatching        (18),
 *                            constraintViolation          (19),
 *                            attributeOrValueExists       (20),
 *                            invalidAttributeSyntax       (21),
 *                                       -- 22-31 unused --
 *                            noSuchObject                 (32),
 *                            aliasProblem                 (33),
 *                            invalidDNSyntax              (34),
 *                            -- 35 reserved for undefined isLeaf --
 *                            aliasDereferencingProblem    (36),
 *                                       -- 37-47 unused --
 *                            inappropriateAuthentication  (48),
 *
 *                            invalidCredentials           (49),
 *                            insufficientAccessRights     (50),
 *                            busy                         (51),
 *                            unavailable                  (52),
 *                            unwillingToPerform           (53),
 *                            loopDetect                   (54),
 *                                       -- 55-63 unused --
 *                            namingViolation              (64),
 *                            objectClassViolation         (65),
 *                            notAllowedOnNonLeaf          (66),
 *                            notAllowedOnRDN              (67),
 *                            entryAlreadyExists           (68),
 *                            objectClassModsProhibited    (69),
 *                                       -- 70 reserved for CLDAP --
 *                            affectsMultipleDSAs          (71), -- new
 *                                       -- 72-79 unused --
 *                            other                        (80) },
 *                            -- 81-90 reserved for APIs --
 *               matchedDN       LDAPDN,
 *               errorMessage    LDAPString,
 *               referral        [3] Referral OPTIONAL }
 *</pre>
 *
 */
public class RfcLDAPResult extends ASN1Sequence implements RfcResponse {

    /**
     * Context-specific TAG for optional Referral.
     */
    public final static int REFERRAL = 3;

    //*************************************************************************
    // Constructors for RfcLDAPResult
    //*************************************************************************

    /**
     * Constructs an RfcLDAPResult from parameters
     *
     * @param resultCode the result code of the operation
     *
     * @param matchedDN the matched DN returned from the server
     *
     * @param errorMessage the diagnostic message returned from the server
     */
    public RfcLDAPResult(ASN1Enumerated resultCode, RfcLDAPDN matchedDN,
                        RfcLDAPString errorMessage)
    {
        this(resultCode, matchedDN, errorMessage, null);
        return;
    }

    /**
     * Constructs an RfcLDAPResult from parameters
     *
     * @param resultCode the result code of the operation
     *
     * @param matchedDN the matched DN returned from the server
     *
     * @param errorMessage the diagnostic message returned from the server
     *
     * @param referral the referral(s) returned by the server
     */
    public RfcLDAPResult(ASN1Enumerated resultCode, RfcLDAPDN matchedDN,
                        RfcLDAPString errorMessage, RfcReferral referral)
    {
        super(4);
        add(resultCode);
        add(matchedDN);
        add(errorMessage);
        if(referral != null)
            add(referral);
        return;
    }

    /**




     * Constructs an RfcLDAPResult from the inputstream
     */
    public RfcLDAPResult(ASN1Decoder dec, InputStream in, int len)
                throws IOException
    {
        super(dec, in, len);

        // Decode optional referral from ASN1OctetString to Referral.
        if(size() > 3) {
            ASN1Tagged obj = (ASN1Tagged)get(3);
            ASN1Identifier id = obj.getIdentifier();
            if(id.getTag() == RfcLDAPResult.REFERRAL) {
                byte[] content =
                        ((ASN1OctetString)obj.taggedValue()).byteValue();
                ByteArrayInputStream bais = new ByteArrayInputStream(content);
                set(3, new RfcReferral(dec, bais, content.length));
            }
        }
        return;
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Returns the result code from the server
     *
     * @return the result code
     */
    public final ASN1Enumerated getResultCode()
    {
        return (ASN1Enumerated)get(0);
    }

    /**
     * Returns the matched DN from the server
     *
     * @return the matched DN
     */
    public final RfcLDAPDN getMatchedDN()
    {
        return new RfcLDAPDN(((ASN1OctetString)get(1)).byteValue());
    }

    /**
     * Returns the error message from the server
     *
     * @return the server error message
     */
    public final RfcLDAPString getErrorMessage()
    {
        return new RfcLDAPString(((ASN1OctetString)get(2)).byteValue());
    }

    /**
     * Returns the referral(s) from the server
     *
     * @return the referral(s)
     */
    public final RfcReferral getReferral()
    {
        return (size() > 3) ? (RfcReferral)get(3) : null;
    }
}

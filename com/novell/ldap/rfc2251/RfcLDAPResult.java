
package org.ietf.asn1.ldap;

import java.io.*;
import org.ietf.asn1.*;

/**
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
 *
 */
public class LDAPResult extends ASN1Sequence {

	/**
	 *
	 */
	public final static int REFERRAL = 3;

	//*************************************************************************
	// Constructors for LDAPResult
	//*************************************************************************

	/**
	 *
	 */
	public LDAPResult(ASN1Enumerated resultCode, LDAPDN matchedDN,
		               LDAPString errorMessage)
	{
		this(resultCode, matchedDN, errorMessage, null);
	}

	/**
	 *
	 */
	public LDAPResult(ASN1Enumerated resultCode, LDAPDN matchedDN,
	                  LDAPString errorMessage, Referral referral)
	{
		super(4);
		setResultCode(resultCode);
		setMatchedDN(matchedDN);
		setErrorMessage(errorMessage);
		if(referral != null)
			setReferral(referral);
	}

	/**
	 *
	 */
	public LDAPResult(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	/**
	 *
	 */
	public void setResultCode(ASN1Enumerated resultCode)
	{
		set(0, resultCode);
	}

	/**
	 *
	 */
	public void setMatchedDN(LDAPDN matchedDN)
	{
		set(1, matchedDN);
	}

	/**
	 *
	 */
	public void setErrorMessage(LDAPString errorMessage)
	{
		set(2, errorMessage);
	}

	/**
	 *
	 */
	public void setReferral(Referral referral)
	{
		set(3, referral);
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 *
	 */
	public ASN1Enumerated getResultCode()
	{
		return (ASN1Enumerated)get(0);
	}

	/**
	 *
	 */
	public LDAPDN getMatchedDN()
	{
		return (LDAPDN)get(1);
	}

	/**
	 *
	 */
	public LDAPString getErrorMessage()
	{
		return (LDAPString)get(2);
	}

	/**
	 *
	 */
	public Referral getReferral()
	{
		return (Referral)get(3);
	}
}


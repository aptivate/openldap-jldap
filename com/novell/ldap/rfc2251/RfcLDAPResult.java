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

/* 
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
public class RfcLDAPResult extends ASN1Sequence implements RfcResponse {

   /**
    * Context-specific TAG for optional Referral.
    */
   public final static int REFERRAL = 3;

   //*************************************************************************
   // Constructors for RfcLDAPResult
   //*************************************************************************

   /**
    *
    */
//   public RfcLDAPResult(ASN1Enumerated resultCode, RfcLDAPDN matchedDN,
//                     RfcLDAPString errorMessage)
//  {
//     this(resultCode, matchedDN, errorMessage, null);
//   }

   /**
    *
    */
//   public RfcLDAPResult(ASN1Enumerated resultCode, RfcLDAPDN matchedDN,
//                     RfcLDAPString errorMessage, Referral referral)
//   {
//      super(4);
//      setResultCode(resultCode);
//      setMatchedDN(matchedDN);
//      setErrorMessage(errorMessage);
//      if(referral != null)
//         setReferral(referral);
//   }

   /**
    *
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
               ((ASN1OctetString)obj.getContent()).getContent();
            ByteArrayInputStream bais = new ByteArrayInputStream(content);
            set(3, new RfcReferral(dec, bais, content.length));
         }
      }
   }

   //*************************************************************************
   // Mutators
   //*************************************************************************

   /**
    *
    */
//   public void setResultCode(ASN1Enumerated resultCode)
//   {
//      set(0, resultCode);
//   }

   /**
    *
    */
//   public void setMatchedDN(RfcLDAPDN matchedDN)
//   {
//      set(1, matchedDN);
//   }

   /**
    *
    */
//   public void setErrorMessage(RfcLDAPString errorMessage)
//   {
//      set(2, errorMessage);
//   }

   /**
    *
    */
//   public void setReferral(Referral referral)
//   {
//      set(3, referral);
//   }

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
   public RfcLDAPDN getMatchedDN()
   {
		return new RfcLDAPDN(((ASN1OctetString)get(1)).getString());
   }

   /**
    *
    */
   public RfcLDAPString getErrorMessage()
   {
		return new RfcLDAPString(((ASN1OctetString)get(2)).getString());
   }

   /**
    *
    */
   public RfcReferral getReferral()
   {
		return (size() > 3) ? (RfcReferral)get(3) : null;
   }
}


/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2001 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.extensions;

/*
 * public class LburpConstants
 */

/**
 * Contains a collection of constants used by the LBURP Classes
 * in Novell LDAP extensions.
 */
public class LburpConstants {

    /**
     * A constant for the EndLBURPRequest OID
     */
     public static final String LBURPEndReplReqOID = 
                                "2.16.840.1.113719.1.142.100.4";
                                
    /**
     * A constant for the EndLBURPResponse OID
     */
     public static final String LBURPEndReplResOID = 
                                "2.16.840.1.113719.1.142.100.5";
                                                                
    /**
     * A constant for the LBURPOperationRequest  OID
     */
     public static final String LBURPOperationReqOID = 
                                "2.16.840.1.113719.1.142.100.6";
                                
    /**
     * A constant for the LBURPOperationResponse  OID
     */
     public static final String LBURPOperationResOID = 
                                "2.16.840.1.113719.1.142.100.7";
    
    /**
     * A constant for the StartLBURPRequest   OID
     */
     public static final String LBURPStartReplReqOID = 
                                "2.16.840.1.113719.1.142.100.1";
    
    /**
     * A constant for the StartLBURPResponse   OID
     */
     public static final String LBURPStartReplResOID = 
                                "2.16.840.1.113719.1.142.100.2";
    
    /**
     * A constant for the LBURPFull Update Protocol   OID
     */
     public static final String LBURPFullUpdateOID = 
                                "2.16.840.1.113719.1.142.1.4.2";
    
    /**
     * A constant for the LBURPIncremental Update Protocol   OID
     */
     public static final String LBURPIncUpdateOID = 
                                "2.16.840.1.113719.1.142.1.4.1";
                                
        public LburpConstants()  {}
}

/* **************************************************************************
 * $Id: NamingContextConstants.java,v 1.2 2000/07/27 16:35:23 javed Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
package com.novell.ldap.ext; 

import com.novell.ldap.*;
import com.novell.ldap.client.protocol.lber.*;
import java.io.IOException;
 
/**
 * public class NamingContextConstants
 *
 */
public class NamingContextConstants {
   
    /**
     * Extended Operation Request and Repsonse OIDs
     *
     */
    public static final String CREATE_NAMING_CONTEXT_REQ    = "2.16.840.1.113719.1.27.100.3";
    public static final String CREATE_NAMING_CONTEXT_RES    = "2.16.840.1.113719.1.27.100.4";
    public static final String MERGE_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.5";
    public static final String MERGE_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.6";
    public static final String ADD_REPLICA_REQ              = "2.16.840.1.113719.1.27.100.7";
    public static final String ADD_REPLICA_RES              = "2.16.840.1.113719.1.27.100.8";
    
    
    
    
    public static final String DELETE_REPLICA_REQ           = "2.16.840.1.113719.1.27.100.11";
    public static final String DELETE_REPLICA_RES           = "2.16.840.1.113719.1.27.100.12";
    
    
    /**
     * Naming Context operation flags
     *
     */
    private static final int LDAP_ENSURE_SERVERS_UP = 1;
    
    
    /**
     * Replica Type Constants
     *
     */
    public static final int LDAP_RT_MASTER          = 0;
    public static final int LDAP_RT_SECONDARY       = 1;
    public static final int LDAP_RT_READONLY        = 2;
    public static final int LDAP_RT_SUBREF          = 3;
    public static final int LDAP_RT_SPARSE_WRITE    = 4;
    public static final int LDAP_RT_SPARSE_READ     = 5;    
    
    public NamingContextConstants()  {}   
		

}

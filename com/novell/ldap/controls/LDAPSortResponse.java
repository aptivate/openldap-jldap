/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPSortResponse.java,v 1.4 2000/11/10 16:50:05 vtag Exp $
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

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 * 3.3 public class LDAPSortResponse
 *                extends LDAPControl
 *
 *  LDAPSortResponse - will be added in newer version of LDAP
 *  Controls draft-- add descritption from draft here.
 */
public class LDAPSortResponse extends LDAPControl {
    
    private String failedAttribute;
    private int resultCode;
    
    public LDAPSortResponse(String id, boolean critical, byte[] vals)
    {
        super(id, critical, vals);
        
        // Parse the failed Attribute and the Result code here
    }

    
    // 3.3.2 getFailedAttribute

    /**
     *  If not null, this returns the attribute that caused the sort
     *  operation to fail.
     */
    public String getFailedAttribute()
    {
        return failedAttribute;
    }


    // 3.3.3 getResultCode

    /**
     * Returns the result code from the sort, as defined in [1], section
     * 4.1.10.
     */
    public int getResultCode()
    {
        return resultCode;
    }

}


/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchResultReference.java,v 1.16 2000/11/10 16:50:04 vtag Exp $
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
 
package com.novell.ldap;

import com.novell.ldap.rfc2251.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;
import java.io.IOException;
import java.util.*;

/**
 *
 *  Encapsulates a continuation reference from an asynchronous search operation.
 *  
 */
public class LDAPSearchResultReference extends LDAPMessage {

    private String[] srefs;
    private static Object nameLock = new Object(); // protect agentNum
    private static int refNum = 0;  // Debug, LDAPConnection number
    private String name;             // String name for debug
	/**
     * Constructs an LDAPSearchResultReference object.
     * 
     * @param message The LDAPMessage with a search reference.
	 */
	/*package*/ LDAPSearchResultReference(RfcLDAPMessage message)
	{
        super(message);
        if( Debug.LDAP_DEBUG) {
            synchronized(nameLock) {
                name = "SearchResultReference(" + ++refNum + "): ";
            }
            Debug.trace( Debug.referrals, name + "Created");
        }
        return;
	}

   /**
    * Returns any URLs in the object.
    *
    * @return The URLs.
    */
   public String[] getReferrals() {
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name + "Enter getReferrals");
        }
        RfcSearchResultReference sresref = (RfcSearchResultReference)message.getProtocolOp();
        Enumeration references = sresref.elements();
        srefs = new String[sresref.size()];
        for( int i=0; sresref.hasMoreElements(); i++) {
            srefs[i] = (String)sresref.nextElement(); 
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.referrals, name + "\t" + srefs[i] );
            }
        }
        return( srefs );
   }
}

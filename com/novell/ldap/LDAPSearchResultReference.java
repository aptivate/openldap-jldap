/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchResultReference.java,v 1.10 2000/09/29 22:47:18 vtag Exp $
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

import com.novell.ldap.protocol.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;
import java.io.IOException;
import java.util.*;

//import com.novell.ldap.client.protocol.lber.*;

/*
 * 4.34 public class LDAPSearchResultReference extends LDAPMessage
 */
 
/**
 *
 *  Encapsulates a continuation reference from an asynchronous search operation.
 *  
 */
public class LDAPSearchResultReference extends LDAPMessage {

//	private LberDecoder lber;
//	private Vector URLs; // referrals

    private String[] srefs;
    private String name = "LDAPSearchResultReference@" + Integer.toHexString(hashCode());
	/**
     * Constructs an LDAPSearchResultReference object.
     * 
     * @param message The LDAPMessage with a search reference.
	 */
	public LDAPSearchResultReference(com.novell.ldap.protocol.LDAPMessage message)
	{
        super(message);
        return;
	}

   /*
    * 4.8.1 getUrls
    */

   /**
    * Returns any URLs in the object.
    *
    * @return The URLs.
    */
   public String[] getUrls() {
        SearchResultReference sresref = (SearchResultReference)message.getProtocolOp();
        Enumeration references = sresref.elements();
        srefs = new String[sresref.size()];
        for( int i=0; sresref.hasMoreElements(); i++) {
            srefs[i] = (String)sresref.nextElement(); 
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name + srefs[i] );
            }
        }
        return( srefs );
   }
}

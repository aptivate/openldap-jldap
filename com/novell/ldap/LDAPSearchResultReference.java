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

package com.novell.ldap;

import java.io.IOException;
import java.net.MalformedURLException;

import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.client.Debug;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcSearchResultReference;

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
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPSearchResultReference()
	{
		super();
	}
    
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
   /** Constructs the Object from an array of referals, passed as string.
   * @param referals array of search referals.
   * @throws MalformedURLException When any referals url is not well-formed.
   */
  public LDAPSearchResultReference(String referals[]) throws MalformedURLException
	{
		super(new RfcLDAPMessage(new RfcSearchResultReference(referals)));
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
        ASN1Object[] references =
                    ((RfcSearchResultReference)message.getResponse()).toArray();
        srefs = new String[references.length];
        for( int i=0; i<references.length; i++) {
            srefs[i] = ((ASN1OctetString)(references[i])).stringValue(); 
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.referrals, name + "\t" + srefs[i] );
            }
        }
        return( srefs );
   }
     
   protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
	  throws IOException, ClassNotFoundException {
//	   Check if it is the correct message type
	 if(!(readObject instanceof LDAPSearchResultReference))
	   throw new ClassNotFoundException("Error occured while deserializing " +
		   "LDAPSearchResultReference object");

		LDAPSearchResultReference tmp = (LDAPSearchResultReference)readObject;
	
		String[] referals = tmp.getReferrals();
	    tmp = null; //remove reference after getting properties

	    message = new RfcLDAPMessage(new RfcSearchResultReference(referals)); 	 
//	    Garbage collect the readObject from readDSML()..	
	   readObject = null;
	  }          
}

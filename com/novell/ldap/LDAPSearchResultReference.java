/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/LDAPSearchResultReference.java,v 1.7 2000/08/28 22:18:59 vtag Exp $
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

import java.io.IOException;
import java.util.Vector;

//import com.novell.ldap.client.protocol.lber.*;

/**
 * 4.8 public class LDAPSearchResultReference extends LDAPMessage
 *
 *  An LDAPSearchResultReference object encapsulates a continuation
 *  reference from a search operation.
 */
public class LDAPSearchResultReference extends LDAPMessage {

//	private LberDecoder lber;
	private Vector URLs; // referrals

	/**
	 */
	public LDAPSearchResultReference(com.novell.ldap.protocol.LDAPMessage message)
	{
		super(message);
	}

/*
	public LDAPSearchResultReference(int messageID, LberDecoder lber,
		                              boolean isLdapv3)
		throws IOException
	{
		super(messageID, SEARCH_RESULT_REFERENCE);
		this.lber = lber;

		URLs = new Vector(5);

		// It is possible that some LDAP servers will
		// encode the SEQUENCE OF tag in the SearchResultRef
		if(lber.peekByte() ==
			(Lber.ASN_SEQUENCE | Lber.ASN_CONSTRUCTOR)) {
			lber.parseSeq(null);
		}

		while((lber.bytesLeft() > 0) &&
				(lber.peekByte() == Lber.ASN_OCTET_STR)) {

			URLs.addElement(lber.parseString(isLdapv3));
		}

		// parse any optional controls
		if(isLdapv3) parseControls();
	}

   private void parseControls()
		throws IOException
	{
      // handle LDAPv3 controls (if present)
      if((lber.bytesLeft() > 0) &&
			(lber.peekByte() == LDAP_CONTROLS)) {
         controls = new Vector(4);
         String controlOID;
         boolean criticality = false; // default
         byte[] controlValue = null;  // optional
         int[] seqlen = new int[1];

         lber.parseSeq(seqlen);
         int endseq = lber.getParsePosition() + seqlen[0];
         while((lber.getParsePosition() < endseq) &&
               (lber.bytesLeft() > 0)) {

            lber.parseSeq(null);
            controlOID = lber.parseString(true);

            if((lber.bytesLeft() > 0) &&
               (lber.peekByte() == Lber.ASN_BOOLEAN)) {
               criticality = lber.parseBoolean();
            }
            if((lber.bytesLeft() > 0) &&
               (lber.peekByte() == Lber.ASN_OCTET_STR)) {
               controlValue =
               lber.parseOctetString(Lber.ASN_OCTET_STR, null);
            }
            if(controlOID != null) {
               controls.addElement(
						new LDAPControl(controlOID, criticality, controlValue));
            }
         }
      }
   }
*/	

   /*
    * 4.8.1 getUrls
    */

   /**
    * Returns any URLs in the object.
    */
/*
   public String[] getUrls() {
		int urlCnt = URLs.size();
		String[] urls = new String[urlCnt];
		for(int i=0; i<urlCnt; i++) {
			urls[i] = (String)URLs.elementAt(i);
		}
		return urls;
   }
*/	

}

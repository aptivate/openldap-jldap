/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPSearchResult.java,v 1.5 2000/08/10 17:53:03 smerrill Exp $
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
 
package org.ietf.ldap;

import java.io.IOException;
import java.util.*;

//import com.novell.ldap.client.protocol.lber.*;

/**
 * 4.7 public class LDAPSearchResult extends LDAPMessage
 *
 *  An LDAPSearchResult object encapsulates a single search result.
 */
public class LDAPSearchResult extends LDAPMessage { // should extend LDAPResponse to pick up parseControls()

//	private LberDecoder lber;
	private boolean isLdapv3;
	private LDAPEntry entry;

   // Default list of binary attributes
/*
   private static Hashtable defaultBinaryAttrs = new Hashtable(23,0.75f);
   static {
      defaultBinaryAttrs.put("userpassword", Boolean.TRUE);      //2.5.4.35
      defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
      //1.3.6.1.4.1.42.2.27.4.1.8
      defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
      // 1.3.6.1.4.1.42.2.27.4.1.2
      defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
      //0.9.2342.19200300.100.1.60
      defaultBinaryAttrs.put("audio", Boolean.TRUE);  //0.9.2342.19200300.100.1.55
      defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
      //1.3.6.1.4.1.1466.101.120.35
      defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
      //1.3.6.1.4.1.1466.101.120.36
      defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);     //2.5.4.36
      defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);       //2.5.4.37
      defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
      //2.5.4.39
      defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE); //2.5.4.38
      defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);    //2.5.4.40
      defaultBinaryAttrs.put("photo", Boolean.TRUE);   //0.9.2342.19200300.100.1.7
      defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
      //0.9.2342.19200300.100.1.53
      defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE); //2.5.4.45
   }
*/	

	/**
	 */
	public LDAPSearchResult(org.ietf.asn1.ldap.LDAPMessage message)
	{
		super(message);
	}

/*
	public LDAPSearchResult(int messageID, LberDecoder lber, boolean isLdapv3)
		throws IOException
	{
		super(messageID, SEARCH_RESPONSE);
		this.lber = lber;
		this.isLdapv3 = isLdapv3;

		LDAPAttributeSet lattrs = new LDAPAttributeSet();
		String DN = lber.parseString(isLdapv3);
		entry = new LDAPEntry(DN, lattrs);
		int[] seqlen = new int[1];

		lber.parseSeq(seqlen);
		int endseq = lber.getParsePosition() + seqlen[0];
		while((lber.getParsePosition() < endseq) &&
				(lber.bytesLeft() > 0)) {
			LDAPAttribute la = parseAttribute();
			lattrs.add(la);
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

   private LDAPAttribute parseAttribute()
      throws IOException
	{
      int len[] = new int[1];
      int seq = lber.parseSeq(null);
      LDAPAttribute la = new LDAPAttribute(lber.parseString(isLdapv3)); // use type

      if((seq = lber.parseSeq(len)) == LDAPResponse.LBER_SET) {
         int attrlen = len[0];
         while(lber.bytesLeft() > 0 && attrlen > 0) {
            try {
               attrlen -= parseAttributeValue(la);
            }
            catch(IOException ex) {
               //System.err.println("Caught " + ex + ". Cannot get value for " + la.getID());
               lber.seek(attrlen);
               break;
            }
         }
      }
      else {
         // Skip the rest of the sequence because it is not what we want
         lber.seek(len[0]);
      }
      return la;
   }
*/	

	/**
    * returns number of bytes that were parsed. Adds the values to attr
	 */
/*
   private int parseAttributeValue(LDAPAttribute la)
      throws IOException
	{

      int len[] = new int[1];

      if(isBinary(la)) {
         la.addValue(lber.parseOctetString(lber.peekByte(), len));
      }
      else {
         la.addValue(lber.parseStringWithTag(Lber.ASN_SIMPLE_STRING, isLdapv3, len));
      }
      return len[0];
   }

   private boolean isBinary(LDAPAttribute la)
	{
      String id = la.getName().toLowerCase();

      return((id.indexOf(";binary") != -1) ||
             // defaultBinaryAttrs.containsKey(id) ||
             ((defaultBinaryAttrs != null) && (defaultBinaryAttrs.containsKey(id))));
   }
*/	

   /*
    * 4.7.1 getEntry
    */

   /**
    * Returns the entry of a server search response.
    */
   public LDAPEntry getEntry() {
		return entry;
   }

}

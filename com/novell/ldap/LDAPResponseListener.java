/**
 * 4.5 public class LDAPResponseListener
 *
 *  Represents the message queue associated with a particular LDAP
 *  operation or operations.
 */
package com.novell.ldap;

import com.novell.ldap.client.*;
import java.util.*;
import java.io.*;

public class LDAPResponseListener extends LDAPListener {

	/**
	 * Constructor
	 */
	public LDAPResponseListener(LDAPClient ldapClient) {
		this.ldapClient = ldapClient;
		this.isLdapv3 = ldapClient.isLdapv3();
		this.conn = ldapClient.getConn();
		this.queue = new LDAPMessageQueue();
		conn.addLDAPListener(this);
	}

   /*
    * 4.5.2 getResponse
    */

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response. It is the responsibility of the client to
	 * process the responses returned from a listener.
    */
   public LDAPResponse getResponse()
		throws LDAPException {

		LDAPMessage message = queue.getLDAPMessage();
		queue.removeMessageID(message.getMessageID());
		return (LDAPResponse)message;

/*
		BerDecoder replyBer = getReplyBer();

		LDAPResult result = new LDAPResult();
		result.resultCode = LDAPException.OPERATIONS_ERROR; // pessimistic

		try {
			// parse the replyber into an LDAPResponse
			replyBer.parseSeq(null);                 // init seq
			result.messageID = replyBer.parseInt();  // msg id
			result.type = replyBer.parseByte();      // response type

			replyBer.parseLength();

			// the following data items are defined in RFC2251 sec 4.1.10
			result.resultCode = replyBer.parseEnumeration();
			result.matchedDN = replyBer.parseString(isLdapv3);
			result.errorMessage = replyBer.parseString(isLdapv3);

			// handle LDAPv3 referrals (if present)
			if(isLdapv3 &&
				(replyBer.bytesLeft() > 0) &&
				(replyBer.peekByte() == LDAPClient.LDAP_REP_REFERRAL)) {

				Vector URLs = new Vector(4);
				int[] seqlen = new int[1];

				replyBer.parseSeq(seqlen);
				int endseq = replyBer.getParsePosition() + seqlen[0];
				while((replyBer.getParsePosition() < endseq) &&
						(replyBer.bytesLeft() > 0)) {

					URLs.addElement(replyBer.parseString(isLdapv3));
				}

				if(result.referrals == null) {
					result.referrals = new Vector(4);
				}
				result.referrals.addElement(URLs);
			}
			result.controls = isLdapv3 ? parseControls(replyBer) : null;
		}
		catch(IOException ioe) {
		}

		// remove the id from messageIDs int array
		removeMessageID(result.getMessageID());

		return result;
*/		
   }

} /* LDAPResponseListener */

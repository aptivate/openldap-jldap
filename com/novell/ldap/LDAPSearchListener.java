/**
 * 4.6 public class LDAPSearchListener
 *
 *  An LDAPSearchListener manages search results and references returned
 *  on one or more search requests.
 */
package com.novell.ldap;

import com.novell.ldap.client.*;
import java.util.*;
import java.io.*;

public class LDAPSearchListener extends LDAPListener {
 
	/**
	 * Constructor
	 */
	public LDAPSearchListener(LDAPClient ldapClient) {
		this.ldapClient = ldapClient;
		this.conn = ldapClient.getConn();
		this.isLdapv3 = ldapClient.isLdapv3();
		this.queue = new LDAPMessageQueue();
		this.exceptions = new Vector(5);
		conn.addLDAPListener(this);
	}

   /*
    * 4.6.2 getResponse
    */

   /**
    * Blocks until a response is available, or until all operations
    * associated with the object have completed or been canceled, and
    * returns the response. The response may be a search result, a search
    * reference, a search response, or null (if there are no more
    * outstanding requests). LDAPException is thrown on network errors.
	 *
	 * The only time this method should return a null is if there is no
	 * response in the message queue and there are no message ids pending.
    */
   public LDAPMessage getResponse()
		throws LDAPException {

		// network error exceptions... (LDAP_TIMEOUT for example)
		if(!exceptions.isEmpty()) {
			LDAPException e = (LDAPException)exceptions.firstElement();
			exceptions.removeElementAt(0);
			throw e;
		}

		LDAPMessage message = queue.getLDAPMessage();
		if(message != null && message.getType() == LDAPClient.LDAP_REP_RESULT) {
			queue.removeMessageID(message.getMessageID());
		}
		return message;

/*
		for(;;) {
			BerDecoder replyBer = getReplyBer(); // will return null if no reply

			// it could be that there is no reply, but still have message ids
			// pending
			if(replyBer == null && messageIDs.isEmpty())
				return null;

			if(replyBer != null) {
				try {
					// process search reply
					replyBer.parseSeq(null);              // init seq
					int msgId = replyBer.parseInt();      // msg id

					// Make sure message id has not been abandoned
					if(messageIDExists(msgId)) {
						int seq = replyBer.parseSeq(null);  // response type

						if(seq == LDAPClient.LDAP_REP_SEARCH) {
							// parse LDAPv3 search entries
							LDAPAttributeSet lattrs = new LDAPAttributeSet();
							String DN = replyBer.parseString(isLdapv3);
							LDAPEntry le = new LDAPEntry(DN, lattrs);
							int[] seqlen = new int[1];

							replyBer.parseSeq(seqlen);
							int endseq = replyBer.getParsePosition() + seqlen[0];
							while((replyBer.getParsePosition() < endseq) &&
									(replyBer.bytesLeft() > 0)) {
								LDAPAttribute la = parseAttribute(replyBer, binaryAttrs);
								lattrs.add(la);
							}

							// parse any optional controls
							LDAPControl[] ctrls = isLdapv3 ? parseControls(replyBer) : null;

							return new LDAPSearchResult(msgId, le, ctrls);
						}
						else if((seq == LDAPClient.LDAP_REP_SEARCH_REF) && isLdapv3) {

							// parse LDAPv3 search reference
							Vector URLs = new Vector(5);

							// It is possible that some LDAP servers will
							// encode the SEQUENCE OF tag in the SearchResultRef
							if(replyBer.peekByte() ==
								(Ber.ASN_SEQUENCE | Ber.ASN_CONSTRUCTOR)) {
								replyBer.parseSeq(null);
							}

							while((replyBer.bytesLeft() > 0) &&
									(replyBer.peekByte() == Ber.ASN_OCTET_STR)) {

								URLs.addElement(replyBer.parseString(isLdapv3));
							}

							// convert URLs from Vector to String[]
							int urlCnt = URLs.size();
							String[] urls = new String[urlCnt];
							for(int i=0; i<urlCnt; i++) {
								urls[i] = (String)URLs.elementAt(i);
							}

							// parse any optional controls
							LDAPControl[] ctrls = isLdapv3 ? parseControls(replyBer) : null;

							return new LDAPSearchResultReference(msgId, urls, ctrls);
						}
						else if(seq == LDAPClient.LDAP_REP_EXTENSION) {

							// needs work!!!
							// parseExtResponse(replyBer, (LDAPResult)searchResults); //%%% ignore for now

						}
						else if(seq == LDAPClient.LDAP_REP_RESULT) {
							// remove the id from messageIDs int array
							removeMessageID(msgId);

							LDAPResult result = new LDAPResult();
							result.resultCode = LDAPException.OPERATIONS_ERROR; // pessimistic

							try {
								result.messageID = msgId;
								result.type = seq; // !!!??? i think this needs to be adjusted

								// the following data items are defined in RFC2251 sec 4.1.10
								result.resultCode = replyBer.parseEnumeration();
								result.matchedDN = replyBer.parseString(isLdapv3);
								result.errorMessage = replyBer.parseString(isLdapv3);

								// parse any optional LDAPv3 referrals
								if(isLdapv3 &&
									(replyBer.bytesLeft() > 0) &&
									(replyBer.peekByte() == LDAPClient.LDAP_REP_REFERRAL)) {

									Vector URLs = new Vector(5);
									int[] seqlen = new int[1];

									replyBer.parseSeq(seqlen);
									int endseq = replyBer.getParsePosition() + seqlen[0];
									while((replyBer.getParsePosition() < endseq) &&
											(replyBer.bytesLeft() > 0)) {

										URLs.addElement(replyBer.parseString(isLdapv3));
									}

									if(result.referrals == null) {
										result.referrals = new Vector(5);
									}
									result.referrals.addElement(URLs);
								}
								result.controls = isLdapv3 ? parseControls(replyBer) : null;
							}
							catch(IOException ioe) {
							}

							return result;
						}
					}
				}
				catch(IOException ioe) {
					// problem Ber Decoding
				}
			}
		}
*/		

   }

} /* LDAPSearchListener */

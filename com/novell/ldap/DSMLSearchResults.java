/* **************************************************************************
 *
 * Copyright (C) 2004 Octet String, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM OCTET STRING, INC., 
 * COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap;

import java.io.IOException;

import com.novell.ldap.util.LDAPReader;

/**
 * @author Marc Boorshtein
 *
 * Used as a drop-in replacement for LDAPSerchResults
 */
public class DSMLSearchResults extends LDAPSearchResults {
	
	
	
	/** The returned controls */
	LDAPControl[] controls;
	
	/** The response pointer */
	int msgRespPtr;

	/** The Reader */
	private LDAPReader reader;
	
	/** The last read message */
	LDAPMessage lastread;
	
	/** Are there no results? */
	boolean empty;
	
	/** Have any results been read? */
	boolean wasRead;
	
	public DSMLSearchResults(LDAPReader reader) {
		this.reader = reader;
		this.empty = false;
		this.wasRead = true;
	}
	
	public DSMLSearchResults() {
		this.empty = true;
		this.wasRead = true;
	}
	
	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#abandon()
	 */
	void abandon() {
		
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#getCount()
	 */
	public int getCount() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#getResponseControls()
	 */
	public LDAPControl[] getResponseControls() {
		return controls;
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#hasMore()
	 */
	public boolean hasMore() {
		if (empty) return true;
		
		if (! wasRead) return (this.lastread != null);
		this.wasRead = false;
		try {
			this.lastread = this.reader.readMessage();
			if (lastread instanceof LDAPResponse) {
				if (lastread.getType() == LDAPMessage.SEARCH_RESULT) {
					lastread = this.reader.readMessage();
					if (lastread instanceof LDAPResponse) {
						this.lastread = null;
						return false;
					} else {
						return this.lastread != null;
					}
				} else {
					this.lastread = null;
					return false;
				}
			} else {
				return this.lastread != null;
			}
		} catch (LDAPException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return false;
		
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#next()
	 */
	public LDAPEntry next() throws LDAPException {
		if (empty) return null;
		this.wasRead = true;
		
		if (this.lastread != null) {
			if (lastread instanceof LDAPSearchResultReference) {
				LDAPReferralException ref = new LDAPReferralException("Referral",LDAPException.REFERRAL,"Referral encountered ");
				ref.setReferrals(((LDAPSearchResultReference) lastread).getReferrals());
				throw ref;
			}
			
			return ((LDAPSearchResult) this.lastread).getEntry();
		} else {
			return null;
		}
		
		
	}

	
}

/* **************************************************************************
 *
 * Copyright (C) 2005 Marc Boorshtein, Inc. All Rights Reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspml.message.Attribute;
import org.openspml.message.Identifier;
import org.openspml.message.SearchResult;

import com.novell.ldap.util.LDAPReader;

/**
 * @author Marc Boorshtein
 *
 * Used as a drop-in replacement for LDAPSerchResults
 */
public class SPMLSearchResults extends LDAPSearchResults {
	
	
	
	/** The returned controls */
	LDAPControl[] controls;
	
	/** The response pointer */
	int msgRespPtr;

	List results;
	
	/** The last read message */
	SearchResult lastread;
	
	/** Are there no results? */
	boolean empty;
	
	/** Have any results been read? */
	boolean wasRead;
	
	Iterator iter;
	
	public SPMLSearchResults(List results) {
		this.results = results;
		this.iter = results.iterator();
		this.empty = false;
		this.wasRead = true;
	}
	
	public SPMLSearchResults() {
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
		return this.iter.hasNext();
		
	}

	/* (non-Javadoc)
	 * @see com.novell.ldap.LDAPSearchResults#next()
	 */
	public LDAPEntry next() throws LDAPException {
		SearchResult res = (SearchResult) iter.next();
		
		Map attribs = res.getAttributeMap();
		Iterator it = attribs.keySet().iterator();
		
		LDAPAttributeSet attribSet = new LDAPAttributeSet();
		while (it.hasNext()) {
			String name = (String) it.next();
			Object o = attribs.get(name);
			if (o instanceof String) {
				String attrib = (String) attribs.get(name);
				LDAPAttribute ldapAttrib = new LDAPAttribute(name);
				if (attrib == null) {
					attrib = "";
				}
				ldapAttrib.addValue(attrib);
				attribSet.add(ldapAttrib);
			} else if (o instanceof List) {
				List l = (List) attribs.get(name);
				Iterator vals = l.iterator();
				LDAPAttribute ldapAttrib = new LDAPAttribute(name);
				while (vals.hasNext()) {
					String attrib = (String) vals.next();
					
					if (attrib == null) {
						attrib = "";
					}
					ldapAttrib.addValue(attrib);
				
				}
				attribSet.add(ldapAttrib);
			}
		
			
			
			
			
		}
		Identifier id = res.getIdentifier();
		
		LDAPAttribute ldapAttrib = new LDAPAttribute(id.getType().substring(id.getType().lastIndexOf('#') + 1));
		ldapAttrib.addValue(id.getId());
		
		attribSet.add(ldapAttrib);
		
		LDAPEntry ret = new LDAPEntry(id.getType().substring(id.getType().lastIndexOf('#') + 1) + "=" + id.getId(),attribSet);
		
		
		
		
		return ret;
		
		
	}

	
}

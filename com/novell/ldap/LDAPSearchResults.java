/**
 * 4.24 public class LDAPSearchResults
 *
 *  An LDAPSearchResults object is returned from a search operation. It
 *  implements Enumeration, thereby providing access to all entries
 *  retrieved during the operation.
 */
package com.novell.ldap;

import com.novell.ldap.client.*;
import java.util.*;
import java.io.*;

public class LDAPSearchResults implements Enumeration {

	private Vector entries;
	private Enumeration elements;
	private int batchSize;
	private boolean completed = false;
	private int count = 0;
	private LDAPControl[] controls;
	private LDAPSearchListener listener;

	public LDAPSearchResults(int batchSize, LDAPSearchListener listener)
	throws IOException, LDAPException {
		this.listener = listener;
		entries = new Vector((batchSize == 0) ? 64 : batchSize);
		this.batchSize = (batchSize == 0) ? Integer.MAX_VALUE : batchSize;
		completed = getBatchOfResults(); // initialize the enumeration
		elements = entries.elements();
	}

   /*
    * 4.24.1 getCount
    */

   /**
    * Returns a count of the entries in the search result. If the search is
    * asynchronous (batch size not 0), this reports the number of results
    * received so far.
    */
   public int getCount() {
		// when referrals are chased, they need to be added here!!!
		return count;
   }

   /*
    * 4.24.2 getResponseControls
    */

   /**
    * Returns the latest Server Controls returned by a Directory Server
    * in the context of this search request, or null
    * if no Server Controls were returned.
    */
   public LDAPControl[] getResponseControls() {
		return controls;
   }

   /*
    * 4.24.3 hasMoreElements
    */

   /**
    * Specifies whether or not there are more search results in the
    * enumeration. If true, there are more search results.
    */
   public boolean hasMoreElements() {
		if(elements.hasMoreElements() == true)
			return true;
		if(completed == false) { // reload the enumeration
			entries.setSize(0);
			completed = getBatchOfResults();
			elements = entries.elements();
			return elements.hasMoreElements();
		}
		return false;
   }

   /*
    * 4.24.4 next
    */

   /**
    * Returns the next result in the enumeration as an LDAPEntry. If
    * automatic referral following is disabled, and there are one or more
    * referrals among the search results, next() will throw an
    * LDAPReferralException the last time it is called, after all other
    * results have been returned.
    */
   public LDAPEntry next() throws LDAPException {
		Object element = elements.nextElement();
		if(element instanceof LDAPResult) {
			((LDAPResult)element).chkResultCode(); // will throw an exception
		}
      return (LDAPEntry)element;
   }

   /*
    * 4.24.5 nextElement
    */

   /**
    * Returns the next result in the enumeration as an Object. This is the
    * default implementation of Enumeration.nextElement(). The returned
    * value may be an LDAPEntry or an LDAPReferralException.
    */
   public Object nextElement() {
      return elements.nextElement();
   }

   /*
    * 4.24.6 sort
    */

   /**
    * Sorts all entries in the results using the provided comparison
    * object. If the object has been partially or completely enumerated,
    * only remaining elements are sorted. Sorting the results requires that
    * they all be present. This implies that
    * LDAPSearchResults.nextElement() will always block until all results
    * have been retrieved, after a sort operation.
    *
    * The LDAPCompareAttrNames class is provided to support the common need
    * to collate by a single or multiple attribute values, in ascending or
    * descending order.  Examples are:
    *
    *   res.sort(new LDAPCompareAttrNames("cn"));
    *
    *   res.sort(new LDAPCompareAttrNames("cn", false));
    *
    *   String[] attrNames = { "sn", "givenname" };
    *   res.sort(new LDAPCompareAttrNames(attrNames));
    *
    * Parameters are:
    *
    *  comp            An object that implements the LDAPEntryComparator
    *                  interface to compare two objects of type
    *                  LDAPEntry.
    */
   public void sort(LDAPEntryComparator comp) {
   }

	/**
	 *	@internal
	 *
	 * Will collect batchSize elements from an LDAPSearchListener message
	 * queue and place them in a Vector. If the last message from the server,
	 * the result message, contains an error, it will be stored in the Vector
	 * for nextElement to process. (although it does not increment the search
	 * result count) All search result entries will be placed in the Vector.
	 * If a null is returned from getResponse(), it is likely that the search
	 * was abandoned.
	 */
	private boolean getBatchOfResults() {
		LDAPMessage msg;
		for(int i=0; i<batchSize;) {
			try {
				if((msg = listener.getResponse()) != null) {
					controls = ((LDAPMessage)msg).getControls();
					if(msg instanceof LDAPSearchResult) {
						entries.addElement(((LDAPSearchResult)msg).getEntry());
						count++;
						i++;
					}
					else if(msg instanceof LDAPSearchResultReference) {
						// chase referrals???
					}
					else {
						LDAPResult result = (LDAPResult)msg;
						int resultCode = result.getResultCode();
						if(resultCode != LDAPException.SUCCESS) {
							entries.addElement((LDAPResult)msg);
						}
						return true; // search completed
					}
				}
				else {
					// new up a client timeout result
					LDAPResult result = new LDAPResult();
					result.resultCode = LDAPException.LDAP_TIMEOUT;
					entries.addElement(result);
					return true; // search has been abandoned
				}
			}
			catch(LDAPException e) {
				// new up a client timeout result
				LDAPResult result = new LDAPResult();
				result.resultCode = e.getLDAPResultCode();
				entries.addElement(result);
				return true; // search has been interrupted with an error
			}
		}
		return false; // search not completed
	}

	/**
	 *	The search can be abandoned in three ways:
	 * 1) By LDAPConnection.abandon()
	 * 2) By LDAPAsynchronousConnection.abandon()
	 * 3) By a client time out
	 */
	public void abandon() {
		completed = true;
	}

} /* LDAPSearchResults */

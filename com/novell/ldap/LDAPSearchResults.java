/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchResults.java,v 1.13 2000/09/28 15:28:45 vtag Exp $
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

import com.novell.ldap.client.*;
import java.util.*;
import java.io.*;

/*
 * 4.35 public class LDAPSearchResults
 */

/**
 *
 *  Implements an enumeration, thereby providing access to all entries
 *  retrieved during the synchronous search operation.
 */
public class LDAPSearchResults implements Enumeration
{

    private Vector entries;             // Search entries
    private Enumeration entryElements;  // Enumeration of entries
    
    private Vector searchRefs;          // Search Result References
    private Enumeration refElements;    // Enumeration of References
    
    private int batchSize;              // Application specified batch size
    private boolean completed = false;  // All entries received
    private int count = 0;              // Number of entries read
    private LDAPControl[] controls = null; // Last set of controls
    private LDAPSearchListener listener;

    /**
     * Constructs a listener object for search results.
     *
     * @param batchSize The maximum number of messages for the listener's queue.
     *<br><br>
     * @param listener The listener for the search results.
     */
    /* package */
    LDAPSearchResults(int batchSize, LDAPSearchListener listener)
    {
        int vectorIncr = (batchSize == 0) ? 64 : 0;
        this.listener = listener;
        entries = new Vector( (batchSize == 0) ? 64 : batchSize, vectorIncr );
        entries = new Vector( (batchSize == 0) ? 64 : batchSize, vectorIncr );
        this.batchSize = (batchSize == 0) ? Integer.MAX_VALUE : batchSize;
        completed = getBatchOfResults(); // initialize the enumeration
        entryElements = entries.elements();
    }

    /*
     * 4.35.1 getCount
     */

    /**
     * Returns a count of the entries in the search result. 
     *
     * <p>If the search is asynchronous (batch size not 0),
     *  this reports the number of results received so far. </p>
     *
     * @return The number of search results received so far.  
     */
    public int getCount()
    {
        return count;
    }

    /*
     * 4.35.2 getResponseControls
     */

    /**
     * Returns the latest server controls returned by a directory server
     * in the context of this search request, or null
     * if no server controls were returned.
     *
     * @return The server controls returned with the search request, or null
     *         if none were returned.
     */
    public LDAPControl[] getResponseControls()
    {
        return controls;
    }

    /*
     * 4.35.3 hasMoreElements
     */

    /**
     * Specifies whether or not there are more search results in the
     * enumeration. 
     *
     * @return True, if there are more search results; false, if there are none.
     */
    public boolean hasMoreElements()
    {
        if(entryElements.hasMoreElements() == true)
            return true;
        if(completed == false) { // reload the enumeration
            entries.setSize(0);
            completed = getBatchOfResults();
            entryElements = entries.elements();
            return entryElements.hasMoreElements();
        }
    return false;
    }

    /*
     * 4.35.4 next
     */

    /**
     * Returns the next result in the enumeration as an LDAPEntry. 
     *
     * <p>If automatic referral following is disabled and one or more 
     * referrals are among the search results, the next method will throw 
     * an LDAPReferralException the last time it is called, after all other
     * results have been returned.</p>
     *
     * @return The next search result as an LDAPEntry.
     *
     * @exception LDAPException A general exception which includes an error
     *                          message and an LDAP error code.
     */
    public LDAPEntry next() throws LDAPException
    {
        // Check if need to reload the enumeration
        if( ( completed == false) && (entryElements.hasMoreElements() == false) ) {
            entries.setSize(0);
            completed = getBatchOfResults();
            entryElements = entries.elements();
        }
        Object element = entryElements.nextElement();
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages,
                "LDAPSearchResults: next: found object " +
                element.getClass().getName());
        }
        if(element instanceof LDAPResponse) {         // Search done w/bad status
            ((LDAPResponse)element).chkResultCode(); // will throw an exception
        }
        return (LDAPEntry)element;
    }

    /*
     * 4.35.5 nextElement
     */

    /**
     * Returns the next result in the enumeration as an Object. 
     *
     * <p>The nextElement method is the default implementation of the
     * Enumeration.nextElement method. The returned value may be an LDAPEntry
     * or an LDAPReferralException. </p>
     *
     * @return The next element in the enumeration.
     */
    public Object nextElement()
    {
        // Check if need to reload the enumeration
        if( ( completed == false) && (entryElements.hasMoreElements() == false) ) {
            entries.setSize(0);
            completed = getBatchOfResults();
            entryElements = entries.elements();
        }
        Object element = entryElements.nextElement();
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages,
                "LDAPSearchResults: nextElement: found object " +
                element.getClass().getName());
        }
        return element;
    }

    /*
     * 4.35.6 sort
     */

    /**
     * Sorts all entries in the results using the provided comparison
     * object. 
     *
     * <p>If the object has been partially or completely enumerated,
     * only the remaining elements are sorted. Sorting the results requires that
     * they all be present. This implies that LDAPSearchResults.nextElement
     * method will always block until all results have been retrieved, 
     * after a sort operation.</p>
     *
     * <p>The LDAPCompareAttrNames class is provided to support the common need
     * to collate by a single or multiple attribute values, in ascending or
     * descending order.  Examples: </p>
     *<ul>
     *   <li>res.sort(new LDAPCompareAttrNames("cn"));</li>
     *
     *   <li>res.sort(new LDAPCompareAttrNames("cn", false));</li>
     *
     *   <li>String[] attrNames = { "sn", "givenname" };
     *   res.sort(new LDAPCompareAttrNames(attrNames));</li>
     *</ul>
     *
     *  @param comp     An object that implements the LDAPEntryComparator
     *                  interface to compare two objects of type
     *                  LDAPEntry.
     */
    public void sort(LDAPEntryComparator comp) {
        throw new RuntimeException("LDAPSearchResults: sort not implemented");
    }

    /**
     * @internal
     *
     * Will collect batchSize elements from an LDAPSearchListener message
     * queue and place them in a Vector. If the last message from the server,
     * the result message, contains an error, it will be stored in the Vector
     * for nextElement to process. (although it does not increment the search
     * result count) All search result entries will be placed in the Vector.
     * If a null is returned from getResponse(), it is likely that the search
     * was abandoned.
     */
    private boolean getBatchOfResults()
    {
        LDAPMessage msg;
        for(int i=0; i<batchSize;) {
            try {
                if((msg = listener.getResponse()) != null) {
                    // Only save controls if there are some
                    LDAPControl[] ctls = msg.getControls();
                    if( ctls != null )
                        controls = ctls;
                    if(msg instanceof LDAPSearchResult) { // Search Entry
                        entries.addElement(((LDAPSearchResult)msg).getEntry()); // can we optimize this?
                        if( Debug.LDAP_DEBUG )
                            Debug.trace( Debug.messages,
                            "LDAPSearchResults: getBatchOfResults: got LDAPSearchResult");
                        count++;
                        i++;
                    } else 
                    if(msg instanceof LDAPSearchResultReference) { // Search Ref
                          if( Debug.LDAP_DEBUG )
                              Debug.trace( Debug.messages,
                                  "LDAPSearchResults: getBatchOfResults: got LDAPSearchResultReference");
                    } else { // SearchResultDone
                        int resultCode = ((LDAPResponse)msg).getResultCode();
                        if( Debug.LDAP_DEBUG )
                            Debug.trace( Debug.messages,
                            "LDAPSearchResults: getBatchOfResults: got LDAPSearchResultDone, status " +
                            resultCode);

                        if(resultCode != LDAPException.SUCCESS) {
                            entries.addElement(msg);
                        }
                        return true; // search completed
                    }
                } else {
                    // how can we arrive here?
                    // we would have to have no responses, no message IDs and no
                    // exceptions
                    throw new RuntimeException(
                        "LDAPSearchResults: getBatchOfResults: getResponse returned null");
                }
            } catch(LDAPException e) { // network error
                // ?? Shouldn't exception be returned to application????
                // could be a client timeout result
                //          LDAPResponse response = new LDAPResponse(e.getLDAPResultCode());
                //          entries.addElement(response);
                return true; // search has been interrupted with an error
            }
        }
        return false; // search not completed
    }

    /**
     * Cancels the search request and clears the message and enumeration.
     */
    public void abandon() {
        // first, remove message ID and timer and any responses in the queue
        listener.abandonAll();

        // next, clear out enumeration
        entries.setSize(0);
        entryElements = entries.elements();
        completed = true;
    }
}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchResults.java,v 1.28 2000/12/05 17:53:25 cmorris Exp $
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

/**
 *
 *  The enumerable results of a synchronous search operation.
 */
public class LDAPSearchResults implements Enumeration
{

    private Vector entries;             // Search entries
    private int entryCount;             // # Search entries in vector
    private int entryIndex;             // Current position in vector
    private Vector references;          // Search Result References
    private int referenceCount;         // # Search Result Reference in vector
    private int referenceIndex;         // Current position in vector
    private int batchSize;              // Application specified batch size
    private boolean completed = false;  // All entries received
    private int count = 0;              // Number of entries read
    private LDAPControl[] controls = null; // Last set of controls
    private LDAPSearchListener listener;
    private static Object nameLock = new Object(); // protect resultsNum
    private static int resultsNum = 0;  // used for debug
    private String name;                // used for debug

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
        // setup entry Vector
        int vectorIncr = (batchSize == 0) ? 64 : 0;
        entries = new Vector( (batchSize == 0) ? 64 : batchSize, vectorIncr );
        entryCount = 0;
        entryIndex = 0;

        // setup search reference Vector
        references = new Vector( 5, 5);
        referenceCount = 0;
        referenceIndex = 0;

        this.listener = listener;
        this.batchSize = (batchSize == 0) ? Integer.MAX_VALUE : batchSize;

        if( Debug.LDAP_DEBUG ) {
            synchronized(nameLock) {
                name = "LDAPSearchResults(" + ++resultsNum + "): ";
            }
            Debug.trace( Debug.messages, name + " Object created, batch size " +
                this.batchSize );
        }

        completed = getBatchOfResults(); // initialize the vector
        return;
    }

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

    /**
     * Returns the latest server controls returned by the server
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

    /**
     * Specifies whether or not there are more search results in the
     * enumeration.
     *
     * @return True, if there are more search results; false, if there are no
     *         more search results.
     */
    public boolean hasMoreElements()
    {
        if( (entryIndex < entryCount) || (referenceIndex < referenceCount))
            return true;
        if(completed == false) { // reload the Vector
            resetVectors();
            return (entryIndex < entryCount) || (referenceIndex < referenceCount);
        }
        return false;
    }

    /*
     * One or more of the vectors was emptied,
     * get more data for them.
     */
    private void resetVectors()
    {
        if( (referenceIndex != 0) && (referenceIndex >= referenceCount) ) {
            references.setSize(0);
            referenceCount = 0;
            referenceIndex = 0;
        }
        if( (entryIndex != 0) && (entryIndex >= entryCount) ) {
            entries.setSize(0);
            entryCount = 0;
            entryIndex = 0;
       }
       completed = getBatchOfResults();
       return;
    }
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
        if( completed && (entryIndex >= entryCount) &&
                (referenceIndex >= referenceCount) ) {
            throw new NoSuchElementException(
                "LDAPSearchResults.next() no more results");
        }
        // Check if need to reload the enumeration
        // We want to receive all entries before we hand over references
        if( ! completed && (entryIndex >= entryCount) ) {
            resetVectors();
        }
        // Check for Search Entries and the Search Result
        Object element = null;
        if( entryIndex < entryCount ) {
            element = entries.elementAt( entryIndex++ );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".next: returns " +
                    element.getClass().getName() + "@" +
                    Integer.toHexString(element.hashCode()) );
            }
            if(element instanceof LDAPResponse) {         // Search done w/bad status
                ((LDAPResponse)element).chkResultCode(); // will throw an exception
            }
        } else
        // Check for Search References
        if( referenceIndex < referenceCount ) {
            String refs[] = (String[])(references.elementAt( referenceIndex++) );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".next: throws referral exception");
                for( int i = 0; i < refs.length; i++) {
                    Debug.trace( Debug.messages, name + " \t" + refs[i]);
                }
            }
            throw new LDAPReferralException(
                null,
                LDAPException.REFERRAL, refs);
        } else {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".next: No entry found and request incomplete\n" +
                    "\tentryIndex " + entryIndex +
                    ", entryCount " + entryCount +
                    ", referenceIndex " + referenceIndex +
                    ", referenceCount " + referenceCount );
            }
            throw new RuntimeException(
                "LDAPSearchResults.next(): No entry found and request incomplete");
        }
        return (LDAPEntry)element;
    }

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
        Object element;
        if( completed && (entryIndex >= entryCount) &&
                (referenceIndex >= referenceCount) ) {
            throw new NoSuchElementException(
                "LDAPSearchResults.nextElement() no more results");
        }
        // Check if need to reload the enumeration
        // We want to receive all entries before we hand over references
        if( ! completed && (entryIndex >= entryCount) ) {
            resetVectors();
        }
        // Check for Search Entries and the Search Response
        if( entryIndex < entryCount ) {
            element = entries.elementAt( entryIndex++ );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".nextElement: returns " +
                    element.getClass().getName() + "@" +
                    Integer.toHexString(element.hashCode()) );
            }
            if(element instanceof LDAPResponse) {             // Search done w/bad status
                LDAPException ex;
                ex = ((LDAPResponse)element).getResultException(); // get Exception object
                if( Debug.LDAP_DEBUG ) {
                    if( ex == null ) {
                        throw new RuntimeException(
                            name + ".nextElement: got success result from queue");
                    }
                }
                element = ex;
            }
        } else
        // Check for Search References
        if( referenceIndex < referenceCount ) {
            String refs[] = (String[])(references.elementAt( referenceIndex++) );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".next: returns referral exception");
                for( int i = 0; i < refs.length; i++) {
                    Debug.trace( Debug.messages, name + ":\t" + refs[i]);
                }
            }
            element = new LDAPReferralException(
                null, LDAPException.REFERRAL, refs);
        } else {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages,
                    name + ".next: No entry found and request incomplete\n" +
                    "\tentryIndex " + entryIndex +
                    ", entryCount " + entryCount +
                    ", referenceIndex " + referenceIndex +
                    ", referenceCount " + referenceCount );
            }
            throw new RuntimeException(
                "LDAPSearchResults.next(): No entry found and request incomplete");
        }
        return element;
    }

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
     * to collate by single or multiple attribute values, in ascending or
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
       if (!completed){
         batchSize = Integer.MAX_VALUE;
         if ( !getBatchOfResults() )//get all results and sort from this point on.
         {
            //we should run out of memory before this happens
            throw new RuntimeException("All results could not be stored in memory, sort failed");
         }
       }
       //ready to sort from index on.
        if (entryIndex < entries.size())  //if not all used up This replaces 'rangeCheck' in Java Source 1.2.2 of Arrays.sort(...comparator)
           mergeSort((Vector)entries.clone(), entries, entryIndex, entries.size(), comp);
   }

     /** Taken from Java 1.2.2 SourceCode for Arrays:<BR>
     * Sorts the specified range of the specified array of objects according
     * to the order induced by the specified comparator.  All elements in the
     * range must be <i>mutually comparable</i> by the specified comparator
     * (that is, <tt>c.compare(e1, e2)</tt> must not throw a
     * <tt>ClassCastException</tt> for any elements <tt>e1</tt> and
     * <tt>e2</tt> in the range).<p>
     *
     * This sort is guaranteed to be <i>stable</i>:  equal elements will
     * not be reordered as a result of the sort.<p>
     *
     * The sorting algorithm is a modified mergesort (in which the merge is
     * omitted if the highest element in the low sublist is less than the
     * lowest element in the high sublist).  This algorithm offers guaranteed
     * n*log(n) performance, and can approach linear performance on nearly
     * sorted lists.
     *
     * @param src the Vector to be sorted.
     * @param dest the result of Vector.
     * @param low the index of the first element (inclusive) to be
     *        sorted.
     * @param high the index of the last element (exclusive) to be sorted.
     * @param c the LDAPEntryComparator to determine the order of the Vector.
     * @throws ClassCastException if the array contains elements that are not
     *	       <i>LDAPEntry</i>.
     * @see LDAPComparator or LDAPCompareAttrNames
     */

    private static void mergeSort(Vector src, Vector dest,
                                  int low, int high, LDAPEntryComparator c) {
         int length = high - low;

      	// Insertion sort on smallest arrays
      	if (length < 7) {
      	    for (int i=low; i<high; i++)
      		for (int j=i; j>low && c.isGreater((LDAPEntry)dest.elementAt(j-1), (LDAPEntry)dest.elementAt(j)); j--)
      		    swap(dest, j, j-1);
      	    return;
      	}

        // Recursively sort halves of dest into src
        int mid = (low + high)/2;
        mergeSort(dest, src, low, mid, c);
        mergeSort(dest, src, mid, high, c);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
/*        if (!c.isGreater((LDAPEntry)src.elementAt(mid-1), (LDAPEntry)src.elementAt(mid))) {
           System.arraycopy(src, low, dest, low, length);

           return;
        }This optimizaion will be nice but first lets see if this works!*/

        // Merge sorted halves (now in src) into dest
        for(int i = low, p = low, q = mid; i < high; i++) {
            if (q>=high || p<mid && !c.isGreater((LDAPEntry)src.elementAt(p), (LDAPEntry)src.elementAt(q)))
                dest.setElementAt(src.elementAt(p++),i );
            else
                dest.setElementAt(src.elementAt(q++),i );
        }
    }

   /**
   * Used by MergeSort().  Swaps x[a] with x[b], except for Vectors.
   */
   private static void swap(Vector x, int a, int b) {
   	Object t = x.elementAt(a);
   	   x.setElementAt( x.elementAt(b),a);
   	x.setElementAt(t, b);
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

        for(int i=0; i<batchSize; ) {
            try {
                if((msg = listener.getResponse()) != null) {
                    // Only save controls if there are some
                    LDAPControl[] ctls = msg.getControls();
                    if( ctls != null )
                        controls = ctls;
                    if(msg instanceof LDAPSearchResult) { // Search Entry
                        Object entry = ((LDAPSearchResult)msg).getEntry();
                        entries.addElement( entry );
                        i++;
                        entryCount++;
                        count++;
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages,
                                name + ".read LDAPEntry@" +
                                Integer.toHexString(entry.hashCode()) +
                                " from LDAPMessage@" +
                                Integer.toHexString(msg.hashCode()) );
                        }
                    } else
                    if(msg instanceof LDAPSearchResultReference) { // Search Ref
                        Debug.trace( Debug.messages, "get references");
                        String[] refs = ((LDAPSearchResultReference)msg).getReferrals();
                        Debug.trace( Debug.messages, "got references " + refs );
                        Debug.trace( Debug.messages, "got references size " + refs.length);
                        references.addElement( refs );
                        referenceCount++;
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages,
                                name + ".read LDAPSearchResultReference@" +
                                Integer.toHexString(msg.hashCode()) );
                            for( int k=0; k < refs.length; k++ )
                            Debug.trace( Debug.messages,
                                name + ".read \t" + refs[k]);

                        }
                    } else { // LDAPResponse
                        int resultCode = ((LDAPResponse)msg).getResultCode();
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages,
                                name + ".read LDAPResponse@" +
                                Integer.toHexString(msg.hashCode()) +
                                ", result " + resultCode);
                        }
                        if(resultCode != LDAPException.SUCCESS) {
                            entries.addElement(msg);
                            entryCount++;
                        }
                        return true; // search completed
                    }
                } else {
                    // how can we arrive here?
                    // we would have to have no responses, no message IDs and no
                    // exceptions
                    throw new LDAPException( null, LDAPException.LDAP_TIMEOUT );
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
    /*package*/
    void abandon() {
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name + ".abandon: Entry");
        }
        // first, remove message ID and timer and any responses in the queue
        listener.getMessageAgent().abandonAll();

        // next, clear out enumeration
        resetVectors();
        completed = true;
    }
}

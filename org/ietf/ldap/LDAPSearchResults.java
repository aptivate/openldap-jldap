/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSearchResults.java,v 1.44 2001/05/30 17:07:04 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package org.ietf.ldap;

import java.util.Enumeration;

/**
 *  The enumerable results of a synchronous search operation.
 *
 * @see com.novell.ldap.LDAPSearchResults
 */
public class LDAPSearchResults implements Enumeration
{
    private com.novell.ldap.LDAPSearchResults results;

    /**
     * Constructs searchResults from a com.novell.ldap.LDAPSearchResults object
     */
    /* package */
    LDAPSearchResults( com.novell.ldap.LDAPSearchResults results)
    {
        this.results = results;
    }
    
    /**
     * get the com.novell.ldap.LDAPSearchResults object
     */
    /* package */
    com.novell.ldap.LDAPSearchResults getWrappedObject()
    {
        return results;
    }

    /**
     * Returns a count of the entries in the search result.
     *
     * @see com.novell.ldap.LDAPSearchResults#getCount()
     */
    public int getCount()
    {
        return results.getCount();
    }

    /**
     * Returns the latest server controls returned by the server
     * in the context of this search request, or null
     * if no server controls were returned.
     *
     * @see com.novell.ldap.LDAPSearchResults#getResponseControls()
     */
    public LDAPControl[] getResponseControls()
    {
        com.novell.ldap.LDAPControl[] controls = results.getResponseControls();
        if( controls == null) {
            return null;
        }

        LDAPControl[] ietfControls = new LDAPControl[controls.length];

        for( int i=0; i < controls.length; i++) {
            ietfControls[i] = new LDAPControl( controls[i]);
        }
        return ietfControls;
    }

    /**
     * Specifies whether or not there are more search results in the
     * enumeration.
     *
     * @see com.novell.ldap.LDAPSearchResults#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        return results.hasMoreElements();   
    }

    /**
     * Returns the next result in the enumeration as an LDAPEntry.
     *
     * @see com.novell.ldap.LDAPSearchResults#next()
     */
    public LDAPEntry next() throws LDAPException
    {
        try {
            return new LDAPEntry( results.next());
        } catch( com.novell.ldap.LDAPException ex) {
            if( ex instanceof com.novell.ldap.LDAPReferralException) {
                throw new LDAPReferralException(
                        (com.novell.ldap.LDAPReferralException)ex);
            } else {
                throw new LDAPException( ex);
            }
        }
    }

    /**
     * Returns the next result in the enumeration as an Object.
     *
     * @see com.novell.ldap.LDAPSearchResults#nextElement()
     */
    public Object nextElement()
    {
        Object ret = results.nextElement();
            
        if( ret instanceof com.novell.ldap.LDAPEntry) {
            return new LDAPEntry( (com.novell.ldap.LDAPEntry) ret);
        } else
        if( ret instanceof com.novell.ldap.LDAPReferralException) {
            return new LDAPReferralException(
                            (com.novell.ldap.LDAPReferralException)ret);
        } else
        if( ret instanceof com.novell.ldap.LDAPException) {
            return new LDAPException(
                            (com.novell.ldap.LDAPException)ret);
        } else {
            throw new RuntimeException("nextElement() unexpected return value");
        }
    }

    /**
     * Sorts all entries in the results using the provided comparison
     * object.
     *
     * @see com.novell.ldap.LDAPSearchResults#sort(LDAPEntryComparator)
     */
    public void sort(LDAPEntryComparator comp)
    {
        results.sort( new CompareSort( comp));
        return;
    }

    /**
     * Wrap a user's org.ietf.ldap.LDAPEntryComparator class
     */
    private class CompareSort implements com.novell.ldap.LDAPEntryComparator
    {
        org.ietf.ldap.LDAPEntryComparator comp;

        private
        CompareSort( LDAPEntryComparator comp)
        {
            this.comp = comp;
            return;
        }

        public boolean isGreater( com.novell.ldap.LDAPEntry entry1,
                                  com.novell.ldap.LDAPEntry entry2)
        {
            return comp.isGreater(new LDAPEntry(entry1), new LDAPEntry(entry2));
        }
    }
}

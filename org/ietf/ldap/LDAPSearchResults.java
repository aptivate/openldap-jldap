/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package org.ietf.ldap;

/**
 * <p>An LDAPSearchResults provides access to all results received during
 * the operation (entries and exceptions).</p>
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSearchResults.html">
            com.novell.ldap.LDAPSearchResults</a>
 */
public class LDAPSearchResults
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResults.html#getCount()">
            com.novell.ldap.LDAPSearchResults.getCount()</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResults.html#getResponseControls()">
            com.novell.ldap.LDAPSearchResults.getResponseControls()</a>
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
     * Reports if there are more search results.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResults.html#hasMore()">
            com.novell.ldap.LDAPSearchResults.hasMoreElements()</a>
     */
    public boolean hasMore()
    {
        return results.hasMore();   
    }

    /**
     * Returns the next result as an LDAPEntry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResults.html#next()">
            com.novell.ldap.LDAPSearchResults.next()</a>
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
}

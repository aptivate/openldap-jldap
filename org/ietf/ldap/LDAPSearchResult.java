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
 *  Encapsulates a single search result that is in response to an asynchronous
 *  search operation.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSearchResult.html">
            com.novell.ldap.LDAPSearchResult</a>
 */
public class LDAPSearchResult extends LDAPMessage
{
    private com.novell.ldap.LDAPSearchResult result;

    /**
     * Constructs an LDAPSearchResult object.
     */
    /*package*/
    LDAPSearchResult(com.novell.ldap.LDAPSearchResult result)
    {
        super(result);
        this.result = result;
        return;
    }

    /**
     * Returns the entry of a server's search response.
     *
    * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResult.html#getEntry()">
            com.novell.ldap.LDAPSearchResult.getEntry()</a>
     */
    public LDAPEntry getEntry()
    {
        return new LDAPEntry( result.getEntry());
    }
}

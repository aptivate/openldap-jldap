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
 *  An interface to support arbitrary sorting algorithms for entries returned
 *  by a search operation.
 *
 * @see <a href="../../../../doc/com/novell/ldap/LDAPEntryComparator.html">
            com.novell.ldap.LDAPEntryComparator</a>
 */
public interface LDAPEntryComparator
{
    /**
     * Returns true if entry1 is to be considered greater than or equal to
     * entry2, for the purpose of sorting.
     *
     * @see <a href="../../../../doc/com/novell/ldap/LDAPEntryComparator.html
            #isGreater(com.novell.ldap.LDAPEntry, com.novell.ldap.LDAPEntry)">
            com.novell.ldap.LDAPEntryComparator.isGreater(LDAPEntry,
            LDAPEntry)</a>
     */
    public boolean isGreater(LDAPEntry entry1, LDAPEntry entry2);
}

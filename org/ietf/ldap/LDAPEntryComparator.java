/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPEntryComparator.java,v 1.10 2001/03/01 00:29:49 cmorris Exp $
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

/**
 *  An interface to support arbitrary sorting algorithms for entries returned
 *  by a search operation.
 *
 *  @see com.novell.ldap.LDAPEntryComparator
 */
public interface LDAPEntryComparator
{
    /**
     * Returns true if entry1 is to be considered greater than or equal to
     * entry2, for the purpose of sorting.
     *
     *  @see com.novell.ldap.LDAPEntryComparator#isGreater(LDAPEntry,LDAPEntry)
     */
    public boolean isGreater(LDAPEntry entry1, LDAPEntry entry2);
}

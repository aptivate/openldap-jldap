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

package com.novell.ldap;

/**
 * An interface to support arbitrary sorting algorithms for entries returned
 * by a search operation.  
 * 
 * <p>The basic Java LDAP classes include one
 * imiplementaton: LDAPCompareAttrNames, to sort in ascending order based
 * on one or more attribute names.
 *
 * @see LDAPCompareAttrNames
 */
public interface LDAPEntryComparator {

   /**
    * Returns true if entry1 is to be considered greater than or equal to
    * entry2, for the purpose of sorting.
    *
    *  @param entry1         Target entry for comparison.
    *<br><br>
    *  @param entry2         Entry to be compared to.
    *
    * @return True if entry1 is greather than or equal to entry2; false, if
    *          entry2 is greater than entry1.
    */
   public boolean isGreater(LDAPEntry entry1, LDAPEntry entry2);

}

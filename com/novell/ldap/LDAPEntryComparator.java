/* **************************************************************************
 * $Novell: LDAPEntryComparator.java,v 1.2 2000/03/14 18:17:27 smerrill Exp $
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
 
package org.ietf.ldap;
 
/**
 * 4.12 public interface LDAPEntryComparator
 *
 *  An object of this class can implement arbitrary sorting algorithms
 *  for search results.
 */
public interface LDAPEntryComparator {

   /*
    * 4.12.1 isGreater
    */

   /**
    * Returns true if entry1 is to be considered greater than or equal to
    * entry2, for the purpose of sorting.
    *
    * Parameters are:
    *
    *  entry1         Target entry for comparison.
    *
    *  entry2         Entry to be compared to.
    */
   public boolean isGreater(LDAPEntry entry1, LDAPEntry entry2);

}





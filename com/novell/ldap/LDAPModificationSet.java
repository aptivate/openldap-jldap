/**
 * 4.16 public class LDAPModificationSet
 *
 *  A collection of modifications to be made to the attributes of a
 *  single entry.
 */
package com.novell.ldap;

import java.util.Vector;

public class LDAPModificationSet {

   private Vector modSet;

   /*
    * 4.16.1 Constructors
    */

   /**
    * Constructs a new, empty set of modifications.
    */
   public LDAPModificationSet() {
      modSet = new Vector();
   }

   /*
    * 4.16.2 add
    */

   /**
    * Specifies another modification to be added to the set of
    * modifications.
    *
    * Parameters are:
    *
    *  op             The type of modification to make, as described
    *                  for LDAPModification.
    *
    *  attr           The attribute (possibly with values) to be
    *                  modified.
    */
   public synchronized void add(int op, LDAPAttribute attr) {
      modSet.add(new LDAPModification(op, attr));
   }

	/**
	 *	This convenience method is not in the internet draft
	 */
   public synchronized void add(LDAPModification mod) {
      modSet.add(mod);
   }

   /*
    * 4.16.3 elementAt
    */

   /**
    * Retrieves a particular LDAPModification object at the position
    * specified by the index.
    *
    * Parameters are:
    *
    *  index          Index of the modification to get.
    */
   public LDAPModification elementAt(int index)
      throws ArrayIndexOutOfBoundsException {
      return (LDAPModification)modSet.elementAt(index);
   }

   /*
    * 4.16.4 remove
    */

   /**
    * Removes the first attribute with the specified name in the set of
    * modifications.
    *
    * Parameters are:
    *
    *  name           Name of the attribute to be removed.
    */
   public synchronized void remove(String name) {
		for(int i=0; i<modSet.size(); i++) {
			LDAPModification mod = (LDAPModification)modSet.elementAt(i);
			LDAPAttribute attr = mod.getAttribute();
			if(attr.getName().equalsIgnoreCase(name)) {
				modSet.removeElementAt(i);
			}
		}
   }

   /*
    * 4.16.5 removeElementAt
    */

   /**
    * Removes a particular LDAPModification object at the position
    * specified by the index.
    *
    * Parameters are:
    *
    *  index          Index of the modification to remove.
    */
   public void removeElementAt(int index)
      throws ArrayIndexOutOfBoundsException {
      modSet.removeElementAt(index);
   }

   /*
    * 4.16.6 size
    */

   /**
    * Retrieves the number of LDAPModification objects in this set.
    */
   public int size() {
      return modSet.size();
   }

}

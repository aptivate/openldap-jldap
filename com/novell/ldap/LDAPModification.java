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
 *
 * A single add, delete, or replace operation to an LDAPAttribute.
 *
 * <p>An LDAPModification contains information on the type of modification
 * being performed, the name of the attribute to be replaced and the new
 * value.  Multiple modifications are expressed as an array of modifications,
 * viz. <code>LDAPModification[]</code>.</p>
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/jldap_sample/ModifyAttrs.java.html">ModifyAttrs.java</p>
 *
 * @see LDAPConnection#modify
 * @see LDAPAttribute
 *
 */
public class LDAPModification {

   private int op;
   private LDAPAttribute attr;

   /**
    * Adds the listed values to the given attribute, creating
    * the attribute if it does not already exist.
    *
    *<p>ADD = 0</p>
    */
   public static final int ADD = 0;

   /**
    * Deletes the listed values from the given attribute,
    * removing the entire attribute (1) if no values are listed or
    * (2) if all current values of the attribute are listed for
    * deletion.
    *
    *<p>DELETE = 1</p>
    */
   public static final int DELETE = 1;

   /**
    * Replaces all existing values of the given attribute
    * with the new values listed, creating the attribute if it
    * does not already exist.
    *
    * <p> A replace with no value deletes the entire attribute if it
    *  exists, and is ignored if the attribute does not exist. </p>
    *
    *<p>REPLACE = 2</p>
    */
   public static final int REPLACE = 2;

   /**
    * Specifies a modification to be made to an attribute.
    *
    *  @param op       The type of modification to make, which can be
    *                  one of the following:
    *<ul>
    *         <li>LDAPModification.ADD - The value should be added to
    *                                    the attribute</li>
    *
    *         <li>LDAPModification.DELETE - The value should be removed
    *                                       from the attribute </li>
    *
    *         <li>LDAPModification.REPLACE - The value should replace all
    *                                        existing values of the
    *                                        attribute </li>
    *</ul><br>
    *  @param attr     The attribute to modify.
    *
    */
   public LDAPModification(int op, LDAPAttribute attr)
   {
      this.op = op;
      this.attr = attr;
   }

   /**
    * Returns the attribute to modify, with any existing values.
    *
    * @return The attribute to modify.
    */
   public LDAPAttribute getAttribute()
   {
      return attr;
   }

   /**
    * Returns the type of modification specified by this object.
    *
    * <p>The type is one of the following:</p>
    * <ul>
    *   <li>LDAPModification.ADD</li>
    *   <li>LDAPModification.DELETE</li>
    *   <li>LDAPModification.REPLACE</li>
    * </ul>
    *
    * @return The type of modification specified by this object.
    */
   public int getOp()
   {
      return op;
   }

}


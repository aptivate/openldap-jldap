/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPModification.java,v 1.6 2000/09/28 21:15:06 smerrill Exp $
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
 
/*
 * 4.20 public class LDAPModification
 */
 
/**
 *
 *  Represents a change specification for a single attribute.
 * 
 */
public class LDAPModification {

   private int op;
   private LDAPAttribute attr;

   /**
    * Adds the listed values to the given attribute, creating
    * the attribute if it does not already exist.
    */
   public static final int ADD = 0;

   /**
    * Deletes the listed values from the given attribute,
    * removing the entire attribute (1) if no values are listed or
    * (2) if all current values of the attribute are listed for
    * deletion.
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
    */
   public static final int REPLACE = 2;

   /*
    * 4.15.1 Constructors
    */

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

   /*
    * 4.15.2 getAttribute
    */

   /**
    * Returns the attribute to modify, with any existing values.
    *
    * @return The attribute to modify.
    */
   public LDAPAttribute getAttribute()
   {
      return attr;
   }

   /*
    * 4.15.3 getOp
    */

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


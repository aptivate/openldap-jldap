/* **************************************************************************
 * $Id$
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
 
/**
 * 4.17 public class LDAPObjectClassSchema
 *
 *  The LDAPObjectClassSchema class represents the definition of an
 *  object class. It is used to query the syntax of an object class, and
 *  to add or delete an object class definition in a Directory. See [2]
 *  for a description of object class representation in LDAP.
 */
public class LDAPObjectClassSchema {

   /*
    * 4.17.1 Constructors
    */

   /**
    * Constructs an object class definition for adding to or deleting from
    * a Directory.
    *
    * Parameters are:
    *
    *  name           Name of the object class.
    *
    *  oid            Unique Object Identifer of the object class - in
    *                  dotted numerical format.
    *
    *  description    Optional description of the object class.
    *
    *  superiors      The object classes this one derives from.
    *
    *  required       A list of attributes required for an entry with
    *                  this object class.
    *
    *  optional       A list of attributes acceptable but not required
    *                  for an entry with this object class.
    *
    *  type           One of ABSTRACT, AUXILIARY, or STRUCTURAL. These
    *                  are constants defined in LDAPObjectClassSchema.
    *
    *  aliases        Optional list of additional names by which the
    *                 object class may be known; null if there are no
    *                 aliases.
    *
    */
   public LDAPObjectClassSchema(String name,
                                String oid,
                                String[] superiors,
                                String description,
                                String[] required,
                                String[] optional,
                                int type,
                                String[] aliases) {
   }
                                                   
   /**
    * Constructs an object class definition from the raw String value
    * returned on a Directory query for "objectclasses".
    *
    * Parameters are:
    *
    *  raw            The raw String value returned on a Directory
    *                  query for "objectclasses".
    */
   public LDAPObjectClassSchema(String raw) {
   }

   /*
    * 4.17.2 getSuperiors
    */

   /**
    * Returns the object classes which this one derives from.
    */
   public String[] getSuperiors() {
      return null;
   }

   /*
    * 4.17.3 getRequiredAttributes
    */

   /**
    * Returns a list of attributes required of an entry with this object
    * class.
    */
   public String[] getRequiredAttributes() {
      return null;
   }

   /*
    * 4.17.4 getOptionalAttributes
    */

   /**
    * Returns a list of attributes acceptable but not required of an entry
    * with this object class.
    */
   public String[] getOptionalAttributes() {
      return null;
   }

   /*
    * 4.17.5 getType
    */

   /**
    * Returns one of ABSTRACT, AUXILIARY, or STRUCTURAL. These are
    * constants defined in LDAPObjectClassSchema.
    */
   public int getType() {
      return 0;
   }

}

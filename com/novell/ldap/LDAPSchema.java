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

import java.util.Enumeration;

/**
 * 4.21 public class LDAPSchema
 *
 *  The LDAPSchema supports querying a Directory Server for its schema,
 *  and obtaining definitions of individual schema elements.
 */
public class LDAPSchema {

   /*
    * 4.21.1 Constructors
    */

   /**
    * Constructs an empty LDAPSchema object.
    */
   public LDAPSchema() {
   }

   /*
    * 4.21.2 fetchSchema
    */

   /**
    * Retrieves the entire schema from a Directory Server.
    */
   public void fetchSchema(LDAPConnection ld) throws LDAPException {
   }

   /**
    * Retrieves the schema in effect at a particular entry in the Directory
    * Server.
    *
    * The fetchSchema methods are the only methods that interact with a
    * Directory Server. The other methods access information acquired
    * through fetchSchema. An LDAPException is thrown as for
    * LDAPConnection.search() (4.28.12) if the schema cannot be retrieved
    * with the specified connection.
    *
    * Parameters are:
    *
    *  ld             An open connection to a Directory Server.
    *
    *  dn             Distinguished name of the entry from which to
    *                  return schema. The subschemasubentry attribute of
    *                  the entry is queried to find the location of the
    *                  schema to be returned.
    */
   public void fetchSchema(LDAPConnection ld,
                           String dn) throws LDAPException {
   }

   /*
    * 4.21.3 getAttribute
    */

   /**
    * Returns a particular attribute definition, or null if not found.
    *
    * Parameters are:
    *
    *  name           Name of the attribute for which a definition is
    *                  to be returned.
    */
   public LDAPAttributeSchema getAttribute( String name ) {
      return null;
   }

   /*
    * 4.21.4 getObjectClass
    */

   /**
    * Returns a particular object class definition, or null if not found.
    *
    * Parameters are:
    *
    *  name           Name of the object class for which a definition
    *                  is to be returned.
    */
   public LDAPObjectClassSchema getObjectClass( String name ) {
      return null;
   }

   /*
    * 4.21.5 getMatchingRule
    */

   /**
    * Returns a particular matching rule definition, or null if not found.
    *
    * Parameters are:
    *
    *  name           Name of the matching rule for which a definition
    *                  is to be returned.
    */
   public LDAPMatchingRuleSchema getMatchingRule( String name ) {
      return null;
   }

   /*
    * 4.21.6 getAttributes
    */

   /**
    * Returns an enumeration of attribute definitions.
    */
   public Enumeration getAttributes() {
      return null;
   }

   /*
    * 4.21.7 getObjectClasses
    */

   /**
    * Returns an enumeration of object class definitions.
    */
   public Enumeration getObjectClasses() {
      return null;
   }

   /*
    * 4.21.8 getMatchingRules
    */

   /**
    * Returns an enumeration of matching rule definitions.
    */
   public Enumeration getMatchingRules() {
      return null;
   }

   /*
    * 4.21.9 getAttributeNames
    */

   /**
    * Returns an enumeration of attribute names.
    */
   public Enumeration getAttributeNames() {
      return null;
   }

   /*
    * 4.21.10 getObjectClassNames
    */

   /**
    * Returns an enumeration of object class names.
    */
   public Enumeration getObjectClassNames() {
      return null;
   }

   /*
    * 4.21.11 getMatchingRuleNames
    */

   /**
    * Returns an enumeration of matching rule names.
    */
   public Enumeration getMatchingRuleNames() {
      return null;
   }

}

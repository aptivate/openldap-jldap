/* **************************************************************************
 * $Novell: LDAPMatchingRuleSchema.java,v 1.2 2000/03/14 18:17:28 smerrill Exp $
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
 * 4.14 public class LDAPMatchingRuleSchema
 *                 extends LDAPSchemaElement
 *
 *  The LDAPMatchingRuleSchema class represents the definition of a
 *  matching rule. It is used to query matching rule syntax, and to add
 *  or delete a matching rule definition in a Directory. See [2] for a
 *  description of matching rule representation in LDAP.
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement {

   /*
    * 4.14.1 Constructors
    */

   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a Directory.
    *
    * Parameters are:
    *
    *  name               Name of the attribute.
    *
    *  oid                Unique Object Identifer of the attribute - in
    *                     dotted numerical format.
    *
    *  description        Optional description of the attribute.
    *
    *  attributes         OIDs of attributes to which the rule applies.
    *
    *  syntaxString       Unique Object Identifer of the syntax of the
    *                     attribute - in dotted numerical format.
    *
    *  aliases            Optional list of additional names by which the
    *                     matching rule may be known; null if there are
    *                     no aliases.
    */
   public LDAPMatchingRuleSchema(String name,
                                 String oid,
                                 String description,
                                 String[] attributes,
                                 String syntaxString,
                                 String[] aliases) {
   }


   /**
    * Constructs a matching rule definition from the raw String values
    * returned on a Directory query for "matchingRule" and for
    * "matchingRuleUse" for the same rule.
    *
    * Parameters are:
    *
    *  rawMatchingRule    The raw String value returned on a Directory
    *                     query for "matchingRule".
    *
    *
    *  rawMatchingRuleUse The raw String value returned on a Directory
    *                     query for "matchingRuleUse".
    */
   public LDAPMatchingRuleSchema(String rawMatchingRule,
                                 String rawMatchingRuleUse) {
   }

   /*
    * 4.14.2 getAttributes
    */

   /**
    * Returns the OIDs of the attributes to which this rule applies.
    */
   public String[] getAttributes() {
      return null;
   }

}

/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMatchingRuleSchema.java,v 1.6 2000/09/29 15:17:14 judy Exp $
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
 * 4.17 public class LDAPMatchingRuleSchema
 *                 extends LDAPSchemaElement
 */
 
/**
 *
 *  Represents the definition of a matching rule. It is used to query
 *  matching rule syntax, and to add or delete a matching rule definition 
 *  in a directory. 
 *
 * <p>NDS does not currently allow matching rules to be added or deleted from
 * the schema.</p>
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement {

   /*
    * 4.17.1 Constructors
    */

   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a directory.
    *
    *  @param name        Name of the attribute.
    *<br><br>
    *  @param oid         Unique object identifer of the attribute, in
    *                     dotted numerical format.
    *<br><br>
    *  @param description    Optional description of the attribute.
    *<br><br>
    *  @param attributes     OIDs of attributes to which the rule applies. All
    *                        attributes added to this array must use the same 
    *                        syntax.
    *<br><br>
    *  @param syntaxString   Unique object identifer of the syntax of the
    *                        attribute, in dotted numerical format.
    *<br><br>
    *  @param aliases     Optional list of additional names by which the
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
    * Constructs a matching rule definition from the raw string values
    * returned on a directory query for "matchingRule" and for
    * "matchingRuleUse" for the same rule.
    *
    *  @param rawMatchingRule    The raw string value returned on a directory
    *                            query for "matchingRule".
    *<br><br>
    *  @param rawMatchingRuleUse  The raw string value returned on a directory
    *                             query for "matchingRuleUse".
    */ 
   public LDAPMatchingRuleSchema(String rawMatchingRule,
                                 String rawMatchingRuleUse) {
   }

   /*
    * 4.17.2 getAttributes
    */

   /**
    * Returns the OIDs of the attributes to which this rule applies.
    *
    *@return The OIDs of the attributes to which this matching rule applies.
    */
   public String[] getAttributes() {
      return null;
   }

}

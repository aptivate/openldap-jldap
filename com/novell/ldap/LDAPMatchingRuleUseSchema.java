package com.novell.ldap;

/*
 * 4.18 public class LDAPMatchingRuleUseSchema
 *               extends LDAPSchemaElement
 */

/* The LDAPMatchingRuleUseSchema class represents the definition of a
 * matching rule use. It is used to discover or modify which attributes
 * are suitable for use with an extensible matching rule. It contains
 * the name and identifier of a matching rule, and a list off attributes
 * that it applies to.  See [2] for a description of matching rule use
 * representation in LDAP.
 */


public class LDAPMatchingRuleUseSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @param name        Name of the matching rule.</br></br>
     *
     * @param oid         Unique Object Identifier of the matching rule
     *                    in dotted numerical format.</br></br>
     *
     * @param description Optional description of the matching rule use.</br></br>
     *
     * @param obsolete    true if the matching rule use is obsolete.</br></br>
     *
     * @param attributes  List of attributes that this matching rule
     *                    applies to. These values may be either the
     *                    names or numeric oids of the attributes.</br></br>
     *
     * @param aliases     Optional list of additional names by which the
     *                    matching rule use may be known; null if there
     *                    are no aliases.</br></br>
     */
    public LDAPMatchingRuleUseSchema(String name,
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes,
                                     String[] aliases)
    {
        throw new RuntimeException("Class LDAPMatchingRuleUseSchema not implemented");
    }



    /**
     * Constructs a matching rule use definition from the raw String value
     * returned on a schema query for "matchingRuleUse".
     *
     * @param raw        The raw String value returned on a Directory
     *                   query for "matchingRuleUse".
     */
    public LDAPMatchingRuleUseSchema(String raw)
    {
        throw new RuntimeException("Class LDAPMatchingRuleUseSchema not implemented");
    }

    /**
     * Returns an array of all the attributes that this matching rule
     * applies to.
     *
     * @return an array of all the attributes this matching rule applies to.
     */
    public String[] getAttributes()
    {
        throw new RuntimeException("Method LDAPMatchingRuleUseSchema.getAttributes not implemented");
    }
}

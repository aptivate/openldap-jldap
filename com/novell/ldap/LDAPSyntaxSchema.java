package com.novell.ldap;

/* public class LDAPSyntaxSchema
 *              extends LDAPSchemaElement
 */

/**
 * The LDAPSyntaxSchema class represents the definition of a syntax. It
 * is used to discover the known set of syntaxes in effect for the
 * subschema. See RFC2252 for a description of syntax representation in
 * LDAP.</br></br>
 * Note that though this extends LDAPSchemaElement, it does not use the
 * name or obsolete members, subsequently calls to getName always return
 * null and isObsolete always returns false. There is also no matching
 * getSyntaxNames method in LDAPSchema.
 */

public class LDAPSyntaxSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a syntax for adding to or deleting from the schema. Note
     * that adding and removing syntaxes is not typically a supported
     * feature of LDAP servers.
     *
     * @param oid         Unique Object Identifier of the syntax - in
     *                    dotted numerical format.</br></br>
     *
     * @param description Optional description of the syntax.
     */
    public LDAPSyntaxSchema(String oid,
                           String description)
    {
        throw new RuntimeException("Object LDAPSyntaxSchema not implemented");
    }

    /**
     * Constructs a syntax from the raw String value returned on a schema
     * query for "LDAPSyntaxes".
     *
     * @param raw           The raw String value returned on a Directory
     *                      query for "ldapSyntaxes".
     */
    public LDAPSyntaxSchema(String raw)
    {
        throw new RuntimeException("Object LDAPSyntaxSchema not implemented");
    }
}

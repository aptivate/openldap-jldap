package com.novell.ldap;

/* public class LDAPSyntaxSchema
 *              extends LDAPSchemaElement
 */

/**
 * Represents the definition of a syntax. 
 *
 * <p>The LDAPSyntaxSchema class is used to discover the known set of syntaxes 
 * in effect for the subschema. </p>
 *
 * <p>Although this extends LDAPSchemaElement, it does not use the name or 
 * obsolete members. Therefore, calls to the getName method always return
 * null and to the isObsolete method always returns false. There is also no 
 * matching getSyntaxNames method in LDAPSchema. </p>
 */

public class LDAPSyntaxSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a syntax for adding to or deleting from the schema. 
     *
     * <p>Adding and removing syntaxes is not typically a supported
     * feature of LDAP servers. NDS does not allow syntaxes to be added
     * or removed.</p>
     *
     * @param oid         The unique object identifier of the syntax - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the syntax.
     */
    public LDAPSyntaxSchema(String oid,
                           String description)
    {
        throw new RuntimeException("Object LDAPSyntaxSchema not implemented");
    }

    /**
     * Constructs a syntax from the raw string value returned on a schema
     * query for LDAPSyntaxes.
     *
     * @param raw           The raw string value returned from a schema
     *                      query for ldapSyntaxes.
     */
    public LDAPSyntaxSchema(String raw)
    {
        throw new RuntimeException("Object LDAPSyntaxSchema not implemented");
    }
}

package com.novell.ldap;

/*
 * 4.22 public class LDAPNameFormSchema
 *               extends LDAPSchemaElement
 */

/**
 * The LDAPNameFormSchema class represents the definition of a Name
 * Form. It is used to discover or modify the allowed naming attributes
 * for a particular object class. See [2] for a description of same form
 * representation in LDAP.
 */

public class LDAPNameFormSchema
                extends LDAPSchemaElement
{

    /**
     * Constructs a name form for adding to or deleting from the schema.
     *
     * @param name        Name of the name form.</br></br>
     *
     * @param oid         Unique Object Identifier of the name form - in
     *                    dotted numerical format.</br></br>
     *
     * @param description Optional description of the name form.</br></br>
     *
     * @param obsolete    true if the name form is obsolete.</br></br>
     *
     * @param objectClass The object to which this name form applies.
     *                    This may either be specified by name or
     *                    numeric oid.</br></br>
     *
     * @param required    A list of the attributes that must be present
     *                    in the RDN of an entry that this name form
     *                    controls. These may either be specified by
     *                    name or numeric oid.</br></br>
     *
     * @param optional    A list of the attributes that may be present
     *                    in the RDN of an entry that this name form
     *                    controls. These may either be specified by
     *                    name or numeric oid.</br></br>
     *
     * @param aliases     Optional list of additional names by which the
     *                    name form may be known; null if there are no
     *                    aliases.</br></br>
     */
    public LDAPNameFormSchema(String name,
                              String oid,
                              String description,
                              boolean obsolete,
                              String objectClass,
                              String[] required,
                              String[] optional,
                              String[] aliases)
    {
        throw new RuntimeException("Class LDAPNameFormSchema not implemented");
    }

    /**
     * Constructs a DIT content rule from the raw String value returned on a
     * schema query for "nameForms".
     *
     * @param raw        The raw String value returned on a Directory
     *                   query for "nameForms".
     */
    public LDAPNameFormSchema(String raw)
    {
        throw new RuntimeException("Class LDAPNameFormSchema not implemented");
    }

    /**
     * Returns the name of the object class that this name form applies to.
     *
     * @return the name of the object class.
     */
    public String getObjectClass()
    {
        throw new RuntimeException("Method LDAPNameFormSchema.getObjectClass not implemented");
    }


    /**
     * Returns the list of required naming attributes for an entry
     * controlled by this name form.
     *
     * @return the list of required naming attributes.
     */
    public String[]getRequiredNamingAttributes()
    {
        throw new RuntimeException("Method LDAPNameFormSchema.getRequiredNamingAttributes not implemented");
    }

    /**
     * Returns the list of optional naming attributes for an entry
     * controlled by this content rule.
     *
     * @return the list of the optional naming attributes.
     */
    public String[]getOptionalNamingAttributes()
    {
        throw new RuntimeException("Method LDAPNameFormSchema.getOptionalNamingAttributes not implemented");
    }
}

package com.novell.ldap;

/*
 * 4.9 public class LDAPDITContentRuleSchema
 *              extends LDAPSchemaElement
 */

/**
 * The LDAPDITContentRuleSchema class represents the definition of a DIT
 * Content Rule. It is used to discover or modify additional auxiliary
 * classes, mandatory and optional attributes, and restricted attributes
 * in effect for an object class. See RFC2252 for a description of DIT
 * content rule representation in LDAP.
 */


public class LDAPDITContentRuleSchema
                extends LDAPSchemaElement
{
    /**
     * Constructs a DIT content rule for adding to or deleting from the
     * schema.
     *
     * @param name        Name of the content rule.
     *
     * @param oid         Unique Object Identifier of the content rule -
     *                    in dotted numerical format.
     *
     * @param description Optional description of the content rule.
     *
     * @param obsolete    true if the content rule is obsolete.
     *
     * @param auxiliary   A list of auxiliary object classes allowed for
     *                    an entry to which this content rule applies.
     *                    These may either be specified by name or
     *                    numeric oid.
     *
     * @param required    A list of user attribute types that an entry
     *                    to which this content rule applies must
     *                    contain in addition to its normal set of
     *                    mandatory attributes. These may either be
     *                    specified by name or numeric oid.
     *
     * @param optional    A list of user attribute types that an entry
     *                    to which this content rule applies may contain
     *                    in addition to its normal set of optional
     *                    attributes. These may either be specified by
     *                    name or numeric oid.
     *
     * @param precluded   A list, consisting of a subset of the optional
     *                    user attribute types of the structural and
     *                    auxiliary object classes which are precluded
     *                    from an entry to which this content rule
     *                    applies. These may either be specified by name
     *                    or numeric oid.
     *
     * @param aliases     Optional list of additional names by which the
     *                    content rule may be known; null if there are
     *                    no aliases.
     */
    public LDAPDITContentRuleSchema(String name,
                                    String oid,
                                    String description,
                                    boolean obsolete,
                                    String[] auxiliary,
                                    String[] required,
                                    String[] optional,
                                    String[] precluded,
                                    String[] aliases)
    {
        throw new RuntimeException("Class LDAPDITContentRuleSchema not implemented");
    }

    /**
     * Constructs a DIT content rule from the raw String value returned on a
     * schema query for "dITContentRules".
     *
     * @param raw         The raw String value returned on a Directory
     */
    public LDAPDITContentRuleSchema(String raw)
    {
        throw new RuntimeException("Class LDAPDITContentRuleSchema not implemented");
    }
    
    /**
     * Returns the list of allowed auxiliary classes.
     *
     * @return the list of allowed auxiliary classes.
     */
    public String[] getAuxiliaryClasses()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getAuxiliaryClasses not implemented");
    }

    /**
     * Returns the list of additional required attributes for an entry
     * controlled by this content rule.
     *
     * @return the list of additional required attributes
     */
    public String[] getRequiredAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getRequiredAttributes not implemented");
    }

    /**
     * Returns the list of additional optional attributes for an entry
     * controlled by this content rule.
     *
     * @return the list of additional optional attributes
     */
    public String[] getOptionalAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getOptionalAttributes not implemented");
    }

    /**
     * Returns the list of precluded attributes for an entry controlled by
     * this content rule.
     *
     * @return the list of precluded attributes
     */
    public String[]getPrecludedAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getPrecludedAttributes not implemented");
    }
}

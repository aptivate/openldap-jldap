package com.novell.ldap;

/**
 *  Represents ia specific DIT (Directory Information Tree) content rule
 *  in the directory schema.
 *
 *  <p>The LDAPDITContentRuleSchema class is used to discover or modify additional 
 *  auxiliary classes, mandatory and optional attributes, and restricted attributes
 *  in effect for an object class. </p>
 */


public class LDAPDITContentRuleSchema
                extends LDAPSchemaElement
{
    /**
     * Constructs a DIT content rule for adding to or deleting from the
     * schema.
     *
     * @param name        The name of the content rule.
     *<br><br>
     * @param oid         The unique object identifier of the content rule -
     *                    in dotted numerical format.
     *<br><br>
     * @param description The optional description of the content rule.
     *<br><br>
     * @param obsolete    True if the content rule is obsolete.
     *<br><br>
     * @param auxiliary   A list of auxiliary object classes allowed for
     *                    an entry to which this content rule applies.
     *                    These may either be specified by name or
     *                    numeric oid.
     *<br><br>
     * @param required    A list of attributes that an entry
     *                    to which this content rule applies must
     *                    contain in addition to its normal set of
     *                    mandatory attributes. These attributes may be
     *                    specified by either name or numeric oid.
     *<br><br>
     * @param optional    A list of attributes that an entry
     *                    to which this content rule applies may contain
     *                    in addition to its normal set of optional
     *                    attributes. These attributes may be specified by
     *                    either name or numeric oid.
     *<br><br>
     * @param precluded   A list, consisting of a subset of the optional
     *                    attributes of the structural and
     *                    auxiliary object classes which are precluded
     *                    from an entry to which this content rule
     *                    applies. These may be specified by either name
     *                    or numeric oid.
     *<br><br>
     * @param aliases     An optional list of additional names by which the
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
     * Constructs a DIT content rule from the raw string value returned from a
     * schema query for dITContentRules.
     *
     * @param raw         The raw string value returned from a schema query 
     *                    for content rules.
     */
    public LDAPDITContentRuleSchema(String raw)
    {
        throw new RuntimeException("Class LDAPDITContentRuleSchema not implemented");
    }
    
    /**
     * Returns the list of allowed auxiliary classes.
     *
     * @return The list of allowed auxiliary classes.
     */
    public String[] getAuxiliaryClasses()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getAuxiliaryClasses not implemented");
    }

    /**
     * Returns the list of additional required attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of additional required attributes.
     */
    public String[] getRequiredAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getRequiredAttributes not implemented");
    }

    /**
     * Returns the list of additional optional attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of additional optional attributes.
     */
    public String[] getOptionalAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getOptionalAttributes not implemented");
    }

    /**
     * Returns the list of precluded attributes for an entry controlled by
     * this content rule.
     *
     * @return The list of precluded attributes.
     */
    public String[]getPrecludedAttributes()
    {
        throw new RuntimeException("Method LDAPDITContentRuleSchema.getPrecludedAttributes not implemented");
    }
}
